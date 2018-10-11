/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ListReportProvider;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Keyword;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.github.fge.msgsimple.source.MapMessageSource;
import com.github.fge.msgsimple.source.MessageSource;
import com.vdurmont.semver4j.Requirement;
import com.vdurmont.semver4j.Semver;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.devgateway.jocds.jsonschema.Views;
import org.devgateway.jocds.jsonschema.checker.CodelistSyntaxChecker;
import org.devgateway.jocds.jsonschema.checker.DeprecatedSyntaxChecker;
import org.devgateway.jocds.jsonschema.checker.MergeStrategySyntaxChecker;
import org.devgateway.jocds.jsonschema.checker.OmitWhenMergedSyntaxChecker;
import org.devgateway.jocds.jsonschema.checker.OpenCodelistSyntaxChecker;
import org.devgateway.jocds.jsonschema.checker.VersionIdSyntaxChecker;
import org.devgateway.jocds.jsonschema.checker.WholeListMergeSyntaxChecker;
import org.devgateway.jocds.jsonschema.validator.CodelistValidator;
import org.devgateway.jocds.jsonschema.validator.DeprecatedValidator;
import org.devgateway.jocds.jsonschema.validator.factory.ReflectionKeywordValidationFactoryWithService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Created by mpostelnicu on 7/5/17.
 */
@Service
public class OcdsValidatorService {

    private final Logger logger = LoggerFactory.getLogger(OcdsValidatorService.class);

    private Map<String, JsonSchema> keySchema = new ConcurrentHashMap<>();

    private Map<String, String> schemaNamePrefix = new ConcurrentHashMap<>();

    private Map<String, JsonNode> extensionMeta = new ConcurrentHashMap<>();

    private Map<String, JsonMergePatch> extensionReleaseJson = new ConcurrentHashMap<>();

    private Map<String, Integer> majorLatestFullVersion = new ConcurrentHashMap<>();

    private Map<String, Set<String>> codeLists = new ConcurrentHashMap<>();


    @Autowired
    private ObjectMapper jacksonObjectMapper;

    private ValidationConfiguration validationConfiguration;

    private JsonNode getUnmodifiedSchemaNode(OcdsValidatorRequest request) {
        try {
            logger.debug("Loading unmodified schema node of type " + request.getSchemaType() + " version "
                    + request.getVersion());
            return JsonLoader.fromResource(schemaNamePrefix.get(request.getSchemaType()) + request.getVersion()
                    + OcdsValidatorConstants.SCHEMA_POSTFIX);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public String processingReportToJsonText(ProcessingReport report, OcdsValidatorRequest request) {
        try {
            return jacksonObjectMapper.writeValueAsString(processingReportToJsonNode(report, request));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonNode processingReportToJsonNode(ProcessingReport report, OcdsValidatorRequest request) {
        if (report.isSuccess() && request.getOperation().equals(OcdsValidatorConstants.Operations.VALIDATE)) {
            JsonNode r = ((ListProcessingReport) report).asJson();
            if (r.toString().length() == 2) {
                return TextNode.valueOf("OK");
            } else {
                return r;
            }
        }

        if (report instanceof ListProcessingReport) {
            return ((ListProcessingReport) report).asJson();
        }
        throw new RuntimeException("Unsupported non ListProcessingReport!");
    }

    private JsonNode applyExtensions(JsonNode schemaNode, OcdsValidatorRequest request) {
        if (ObjectUtils.isEmpty(request.getExtensions())) {
            logger.debug("No explicit schema extensions were requested.");
            return schemaNode;
        }

//        List<String> unrecognizedExtensions =
//                request.getExtensions().stream().filter(e -> !extensionMeta.containsKey(e))
//                        .collect(Collectors.toList());
//
//        if (!unrecognizedExtensions.isEmpty()) {
//            throw new RuntimeException("Unknown extensions by name: " + unrecognizedExtensions);
//        }

        JsonNode schemaResult = schemaNode;

        for (String ext : request.getExtensions()) {
            try {
                logger.debug("Applying schema extension " + ext);
                JsonNode nodeMeta = getExtensionMeta(request.getTrustSelfSignedCerts(), ext);
                if (!compatibleExtension(nodeMeta, request.getVersion())) {
                    throw new RuntimeException("Cannot apply extension " + ext + " due to version incompatibilities. "
                            + "Extension meta is " + nodeMeta.toString() + " requested schema version "
                            + request.getVersion());
                }
                schemaResult = getExtensionReleaseJson(request.getTrustSelfSignedCerts(), ext).apply(schemaResult);
            } catch (JsonPatchException e) {
                throw new RuntimeException(e);
            }
        }

        return schemaResult;
    }

    public Map<String, Set<String>> getCodeLists() {
        return codeLists;
    }

    private JsonNode getExtensionMeta(boolean trustSelfSignedCerts, String id) {

        //check if preloaded as extension
        if (extensionMeta.containsKey(id)) {
            return extensionMeta.get(id);
        }

        //attempt load via URL
        JsonNode jsonNode = readExtensionMetaURL(trustSelfSignedCerts, id);
        extensionMeta.put(id, jsonNode);
        return jsonNode;
    }

    private boolean compatibleExtension(JsonNode extensionNodeMeta, String fullVersion) {
        Assert.notNull(fullVersion, "OCDS Version cannot be null!");
        Assert.notNull(extensionNodeMeta, "Extension meta must not be null!");

        JsonNode compatNode = extensionNodeMeta.get(OcdsValidatorConstants.EXTENSION_META_COMPAT_PROPERTY);
        if (compatNode == null) {
            return true; //by default if we don't say anything about compatibility, it is compatible
        }

        Semver requestedVersion = new Semver(fullVersion, Semver.SemverType.NPM);
        if (compatNode.isArray()) { //support new array compatibility in 1.1.3
            boolean ret = false;
            for (final JsonNode node : compatNode) {
                ret |= requestedVersion.satisfies(
                        Requirement.buildNPM(patchSemverNPMWildcardNotWorking(node.asText())));
            }
            return ret;
        } else { //old 1.1.1 non array compat
            return requestedVersion.satisfies(
                    Requirement.buildNPM(patchSemverNPMWildcardNotWorking(compatNode.asText())));
        }
    }

    /**
     * see https://github.com/vdurmont/semver4j/issues/7
     *
     * @param version
     * @return
     */
    private String patchSemverNPMWildcardNotWorking(String version) {
        String[] split = version.split("\\.");
        if (split.length < 2) {
            throw new RuntimeException("Incompatible version format.");
        }

        if (split.length == 2) {
            version += ".0";
            if (version.contains(">")) {
                return version;
            } else {
                return ">=" + version;
            }
        }

        return version;
    }


    private JsonMergePatch getExtensionReleaseJson(boolean trustSelfSignedCerts, String id) {
        //check if preloaded as extension
        if (extensionReleaseJson.containsKey(id)) {
            return extensionReleaseJson.get(id);
        }

        //attempt load via URL
        String releaseUrl = id.replace(
                OcdsValidatorConstants.REMOTE_EXTENSION_META_POSTFIX,
                OcdsValidatorConstants.EXTENSION_RELEASE_JSON
        );
        JsonMergePatch patch = readExtensionReleaseJsonURL(trustSelfSignedCerts, releaseUrl);
        extensionReleaseJson.put(id, patch);
        return patch;
    }

    private JsonNode readExtensionMeta(String extensionName) {
        //reading meta
        try {
            logger.debug("Reading extension metadata for extension " + extensionName);
            return JsonLoader.fromResource(OcdsValidatorConstants.EXTENSIONS_PREFIX + extensionName + "/"
                    + OcdsValidatorConstants.EXTENSION_META);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode readExtensionMetaURL(boolean trustSelfSignedCerts, String url) {
        logger.debug("Reading extension metadata from URL " + url);
        return getJsonNodeFromUrl(trustSelfSignedCerts, url);
    }

    private JsonMergePatch readExtensionReleaseJsonURL(boolean trustSelfSignedCerts, String url) {
        //reading meta
        try {
            logger.debug("Reading extension JSON contents for extension " + url);
            JsonNode jsonMergePatch = getJsonNodeFromUrl(trustSelfSignedCerts, url);
            JsonMergePatch patch = JsonMergePatch.fromJson(jsonMergePatch);
            return patch;
        } catch (JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }


    private JsonMergePatch readExtensionReleaseJson(String extensionName) {
        //reading meta
        try {
            logger.debug("Reading extension JSON contents for extension " + extensionName);
            JsonNode jsonMergePatch = JsonLoader.fromResource(OcdsValidatorConstants.EXTENSIONS_PREFIX
                    + extensionName + "/" + OcdsValidatorConstants.EXTENSION_RELEASE_JSON);
            JsonMergePatch patch = JsonMergePatch.fromJson(jsonMergePatch);
            return patch;
        } catch (IOException | JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonSchema getSchema(OcdsValidatorRequest request) {
        if (keySchema.containsKey(request.getKey())) {
            logger.debug("Returning cached schema with extensions " + request.getKey());
            return keySchema.get(request.getKey());
        } else {
            JsonNode schemaNode = getUnmodifiedSchemaNode(request);
            schemaNode = applyExtensions(schemaNode, request);
            try {
                LogLevel logLevel = request.getVerbosity().equals(OcdsValidatorConstants.LogLevel.ERROR)
                        ? LogLevel.ERROR : LogLevel.WARNING;
                JsonSchema schema = JsonSchemaFactory.newBuilder()
                        .setValidationConfiguration(validationConfiguration)
                        .setReportProvider(new ListReportProvider(logLevel, LogLevel.NONE)).freeze()
                        .getJsonSchema(schemaNode);
                logger.debug("Saving to cache schema with extensions " + request.getKey());
                keySchema.put(request.getKey(), schema);
                return schema;
            } catch (ProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    private void initSchemaNamePrefix() {
        logger.debug("Initializing prefixes for all available schemas");
        schemaNamePrefix.put(OcdsValidatorConstants.Schemas.RELEASE, OcdsValidatorConstants.SchemaPrefixes.RELEASE);
        schemaNamePrefix.put(
                OcdsValidatorConstants.Schemas.RECORD_PACKAGE,
                OcdsValidatorConstants.SchemaPrefixes.RECORD_PACKAGE
        );
        schemaNamePrefix.put(
                OcdsValidatorConstants.Schemas.RELEASE_PACKAGE,
                OcdsValidatorConstants.SchemaPrefixes.RELEASE_PACKAGE
        );
        schemaNamePrefix.put(
                OcdsValidatorConstants.Schemas.VERSIONED_RELEASE_VALIDATION,
                OcdsValidatorConstants.SchemaPrefixes.VERSIONED_RELEASE_VALIDATION
        );
    }

    private void initExtensions() {
        logger.debug("Initializing predefined schema extensions");
        OcdsValidatorConstants.EXTENSIONS.forEach(e -> {
            logger.debug("Initializing schema extension " + e);
            extensionMeta.put(e, readExtensionMeta(e));
            extensionReleaseJson.put(e, readExtensionReleaseJson(e));
        });
    }

    private void initCodelists() {
        logger.debug("Preload codelists");
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resourcePatternResolver.getResources("/schema/codelists/*.csv");
            for (Resource resource : resources) {
                CSVParser parser = new CSVParser(new InputStreamReader(resource.getInputStream()),
                        CSVFormat.DEFAULT.withHeader());
                Integer codeIdx = parser.getHeaderMap().get("Code");
                codeLists.put(resource.getFilename(),
                        parser.getRecords().stream().map(r -> r.get(codeIdx)).collect(Collectors.toSet()));
                parser.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void initMajorLatestFullVersion() {

        for (int i = 0; i < OcdsValidatorConstants.Versions.ALL.length; i++) {
            String[] versions = OcdsValidatorConstants.Versions.ALL[i].split("\\.");
            String majorMinor = versions[0] + "." + versions[1];

            if (majorLatestFullVersion.containsKey(majorMinor)) {
                if (majorLatestFullVersion.get(majorMinor) < Integer.parseInt(versions[2])) {
                    majorLatestFullVersion.put(majorMinor, Integer.parseInt(versions[2]));
                }
            } else {
                majorLatestFullVersion.put(majorMinor, Integer.parseInt(versions[2]));
            }

        }
    }

    @PostConstruct
    private void init() {
        initSchemaNamePrefix();
        initMajorLatestFullVersion();
        initExtensions();
        initCodelists();
        initJsonSchemaFactory();
    }

    private CloseableHttpClient getHttpClient(boolean trustSelfSignedCerts) {
        if (trustSelfSignedCerts) {
            // use the TrustSelfSignedStrategy to allow Self Signed Certificates
            SSLContext sslContext = null;
            try {
                sslContext = SSLContextBuilder
                        .create()
                        .loadTrustMaterial(new TrustSelfSignedStrategy())
                        .build();
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                e.printStackTrace();
            }
            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
            SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
            return HttpClients.custom().setSSLSocketFactory(connectionFactory).build();
        } else {
            return HttpClients.createDefault();
        }
    }

    private Keyword createDeprecatedKeyword() {
        return Keyword.newBuilder(OcdsValidatorConstants.CustomSchemaKeywords.DEPRECATED)
                .withSyntaxChecker(DeprecatedSyntaxChecker.getInstance())
                .withIdentityDigester(NodeType.ARRAY, NodeType.OBJECT)
                .withValidatorClass(DeprecatedValidator.class)
                .freeze();
    }

    private Keyword createMergeStrategyKeyword() {
        return Keyword.newBuilder(OcdsValidatorConstants.CustomSchemaKeywords.MERGE_STRATEGY)
                .withSyntaxChecker(MergeStrategySyntaxChecker.getInstance())
                .freeze();
    }

    private Keyword createWholeListMergeKeyword() {
        return Keyword.newBuilder(OcdsValidatorConstants.CustomSchemaKeywords.WHOLE_LIST_MERGE)
                .withSyntaxChecker(WholeListMergeSyntaxChecker.getInstance())
                .freeze();
    }

    private Keyword createOmitWhenMergedKeyword() {
        return Keyword.newBuilder(OcdsValidatorConstants.CustomSchemaKeywords.OMIT_WHEN_MERGED)
                .withSyntaxChecker(OmitWhenMergedSyntaxChecker.getInstance())
                .freeze();
    }


    private Keyword createVersionIdKeyword() {
        return Keyword.newBuilder(OcdsValidatorConstants.CustomSchemaKeywords.VERSION_ID)
                .withSyntaxChecker(VersionIdSyntaxChecker.getInstance())
                .freeze();
    }


    private Keyword openCodelistKeyword() {
        return Keyword.newBuilder(OcdsValidatorConstants.CustomSchemaKeywords.OPEN_CODE_LIST)
                .withSyntaxChecker(OpenCodelistSyntaxChecker.getInstance())
                .freeze();
    }

    private Keyword codelistKeyword() {
        return Keyword.newBuilder(OcdsValidatorConstants.CustomSchemaKeywords.CODE_LIST)
                .withSyntaxChecker(CodelistSyntaxChecker.getInstance())
                .withIdentityDigester(NodeType.ARRAY, NodeType.OBJECT)
                .withValidatorFactory(
                        new ReflectionKeywordValidationFactoryWithService(
                                OcdsValidatorConstants.CustomSchemaKeywords.CODE_LIST,
                                CodelistValidator.class, this))
                .freeze();
    }


    /**
     * This method initializes the custom json schema factory
     */
    private void initJsonSchemaFactory() {
        /*
         * Build a library, based on the v4 library, with this new keyword
         */
        final Library library = DraftV4Library.get().thaw()
                .addKeyword(createDeprecatedKeyword())
                .addKeyword(createWholeListMergeKeyword())
                .addKeyword(createVersionIdKeyword())
                .addKeyword(openCodelistKeyword())
                .addKeyword(codelistKeyword())
                .addKeyword(createOmitWhenMergedKeyword())
                .addKeyword(createMergeStrategyKeyword())
                .freeze();

        validationConfiguration = ValidationConfiguration.newBuilder()
                .setDefaultLibrary(
                        "https://raw.githubusercontent"
                                + ".com/open-contracting/standard/1.1-dev/standard/schema/metaschema/json-schema"
                                + "-draft-4.json",
                        library
                )
                .setValidationMessages(createJsonSchemaCustomMessages()).freeze();

    }

    /**
     * These are custom messages used by the validator to report warnings and errors
     */
    private MessageBundle createJsonSchemaCustomMessages() {
        final String key = "warn.jocds.deprecatedValidator";
        final String value = "This element has been marked deprecated and will soon be removed from the OCDS standard !"
                + " This will be either due to limited use, or because they have been replaced by alternative fields "
                + "or codelists. Before a field or codelist value is removed, "
                + "it will be first marked as deprecated in a major or minor release (e.g. in 1.1), and removal will "
                + "only take place, subject to the governance process, in the next major version (e.g. 2.0).";

        final String codeListKey = "warn.jocds.codelistValidator";
        final String codeListValue = "Open codelist has values that are not defined within the standard. Make sure you"
                + "define and document their rationale!";

        final MessageSource source = MapMessageSource.newBuilder()
                .put(key, value).put(codeListKey, codeListValue).build();
        final MessageBundle bundle
                = MessageBundles.getBundle(JsonSchemaValidationBundle.class)
                .thaw().appendSource(source).freeze();
        return bundle;
    }

    public ProcessingReport validate(OcdsValidatorStringRequest request) {
        try {
            return validate(convertStringRequestToNodeRequest(request));
        } catch (RuntimeException e) {
            return logErrorAsReport(request, e);
        }
    }

    private ProcessingReport wrapLogReportInRequestInfo(ProcessingReport report, OcdsValidatorRequest request) {
        try {
            if (!report.isSuccess()) {
                report.error(new ProcessingMessage().setMessage("Error(s) found while processing request "
                        + jacksonObjectMapper.writerWithView(Views.Internal.class).writeValueAsString(request)));
            }
        } catch (ProcessingException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return report;
    }

    private ProcessingReport logErrorAsReport(OcdsValidatorRequest request, Exception e) {
        ProcessingReport report = new ListProcessingReport();
        try {
            wrapLogReportInRequestInfo(report, request);
            report.error(new ProcessingMessage().setMessage(e.getMessage()));
            return report;
        } catch (ProcessingException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        }
    }

    public ProcessingReport validate(OcdsValidatorUrlRequest request) {
        try {
            return validate(convertUrlRequestToNodeRequest(request));
        } catch (RuntimeException e) {
            return logErrorAsReport(request, e);
        }
    }

    public ProcessingReport validate(OcdsValidatorNodeRequest nodeRequest) {
        if (nodeRequest.getOperation().equals(OcdsValidatorConstants.Operations.VALIDATE)) {
            logger.debug("Running validation for api request for schema of type " + nodeRequest.getSchemaType()
                    + " and version " + nodeRequest.getVersion());

            if (nodeRequest.getSchemaType().equals(OcdsValidatorConstants.Schemas.RELEASE)
                    || nodeRequest.getSchemaType().equals(
                    OcdsValidatorConstants.Schemas.VERSIONED_RELEASE_VALIDATION)) {

                if (nodeRequest.getVersion() == null) {
                    throw new RuntimeException("Not allowed null version info for release validation!");
                }

                try {
                    return wrapLogReportInRequestInfo(validateRelease(nodeRequest), nodeRequest);
                } catch (RuntimeException e) {
                    return logErrorAsReport(nodeRequest, e);
                }
            }

            if (nodeRequest.getSchemaType().equals(OcdsValidatorConstants.Schemas.RECORD_PACKAGE)) {
                try {
                    return wrapLogReportInRequestInfo(validateRecordPackage(nodeRequest), nodeRequest);
                } catch (RuntimeException e) {
                    return logErrorAsReport(nodeRequest, e);
                }
            }

            if (nodeRequest.getSchemaType().equals(OcdsValidatorConstants.Schemas.RELEASE_PACKAGE)) {
                try {
                    return wrapLogReportInRequestInfo(validateReleasePackage(nodeRequest), nodeRequest);
                } catch (RuntimeException e) {
                    return logErrorAsReport(nodeRequest, e);
                }
            }
        }

        if (nodeRequest.getOperation().equals(OcdsValidatorConstants.Operations.SHOW_BUILTIN_EXTENSIONS)) {
            ListProcessingReport list = new ListProcessingReport();
            OcdsValidatorConstants.EXTENSIONS.forEach(e -> {
                        ProcessingMessage message = new ProcessingMessage();
                        message.setMessage(e);
                        try {
                            list.info(message);
                        } catch (ProcessingException e1) {
                            e1.printStackTrace();
                        }
                    }
            );
            return list;
        }


        if (nodeRequest.getOperation().equals(OcdsValidatorConstants.Operations.SHOW_SUPPORTED_OCDS)) {
            ListProcessingReport list = new ListProcessingReport();
            for (int i = 0; i < OcdsValidatorConstants.Versions.ALL.length; i++) {
                ProcessingMessage message = new ProcessingMessage();
                message.setMessage(OcdsValidatorConstants.Versions.ALL[i]);
                try {
                    list.info(message);
                } catch (ProcessingException e1) {
                    e1.printStackTrace();
                }
            }
            return list;
        }
        return null;

    }

    private JsonNode getJsonNodeFromString(String json) {
        try {
            return JsonLoader.fromString(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private JsonNode getJsonNodeFromUrl(boolean trustSelfSignedCerts, String url) {
        try {
            HttpGet request = new HttpGet(url);
            request.addHeader("accept", "application/json");
            HttpResponse response = getHttpClient(trustSelfSignedCerts).execute(request);
            return JsonLoader.fromReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Validates a release or an array of releases
     *
     * @param nodeRequest
     * @return
     */
    private ProcessingReport validateRelease(OcdsValidatorNodeRequest nodeRequest) {
        JsonSchema schema = getSchema(nodeRequest);
        try {
            return schema.validate(nodeRequest.getNode());
        } catch (ProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String constructFullVersion(String majorMinor, Integer bugfix) {
        return majorMinor + "." + bugfix;
    }


    private ProcessingReport validateEmbeddedReleases(OcdsValidatorNodeRequest nodeRequest, JsonNode releasesNode,
                                                      String releaseType)
            throws ProcessingException {
        ProcessingReport returnedReport = new ListProcessingReport();
        for (JsonNode release : releasesNode) {
            returnedReport.mergeWith(validateEmbeddedRelease(nodeRequest,
                    release, releaseType
            ));
        }
        return returnedReport;
    }

    private ProcessingReport validateEmbeddedRelease(OcdsValidatorNodeRequest nodeRequest, JsonNode node,
                                                     String releaseType)
            throws ProcessingException {
        ProcessingReport returnedReport = new ListProcessingReport();
        OcdsValidatorNodeRequest releaseValidationRequest = new OcdsValidatorNodeRequest(nodeRequest, node);
        releaseValidationRequest.setSchemaType(releaseType);
        ProcessingReport report = validateRelease(releaseValidationRequest);
        if (!report.isSuccess()) {
            ProcessingMessage msg = new ProcessingMessage();
            msg.setLogLevel(LogLevel.ERROR);
            String ocid = getOcidFromRelease(node);
            msg.setMessage("Error(s) in release with ocid=" + ((ocid == null) ? "" : " with ocid " + ocid));
            ProcessingReport wrapperReport = new ListProcessingReport();
            wrapperReport.error(msg);
            wrapperReport.mergeWith(report);
            returnedReport.mergeWith(wrapperReport);
            return returnedReport;
        }
        return returnedReport;
    }


    /**
     * Applies extensions read from nodeRequest
     *
     * @param nodeRequest
     */
    private void applyExtensions(OcdsValidatorNodeRequest nodeRequest) {
        //get release package extensions
        if (nodeRequest.getExtensions() == null && nodeRequest.getNode()
                .hasNonNull(OcdsValidatorConstants.EXTENSIONS_PROPERTY)) {
            nodeRequest.setExtensions(new TreeSet<>());
            for (JsonNode extension : nodeRequest.getNode().get(OcdsValidatorConstants.EXTENSIONS_PROPERTY)) {
                nodeRequest.getExtensions().add(extension.asText());
            }
        }
    }

    /**
     * Autodetect version from package
     *
     * @param nodeRequest
     */
    private void autodetectPackageVersion(OcdsValidatorNodeRequest nodeRequest) {
        if (nodeRequest.getVersion() == null) {
            //try autodetect using version in node
            if (nodeRequest.getNode() != null
                    && nodeRequest.getNode().hasNonNull(OcdsValidatorConstants.VERSION_PROPERTY)) {
                String majorMinor = nodeRequest.getNode().get(OcdsValidatorConstants.VERSION_PROPERTY).asText();
                if (!majorLatestFullVersion.containsKey(majorMinor)) {
                    throw new RuntimeException("Unrecognized package release version " + majorMinor);
                }
                nodeRequest.setVersion(constructFullVersion(majorMinor, majorLatestFullVersion.get(majorMinor)));
            } else {
                throw new RuntimeException("Version schema property has to be present!");
            }
        }
    }

    /**
     * Validates a release package
     *
     * @param nodeRequest
     * @return
     */
    private ProcessingReport validateReleasePackage(OcdsValidatorNodeRequest nodeRequest) {

        autodetectPackageVersion(nodeRequest);

        JsonSchema schema = getSchema(nodeRequest);
        try {
            ProcessingReport releasePackageReport = schema.validate(nodeRequest.getNode());
            if (!releasePackageReport.isSuccess()) {
                return releasePackageReport;
            }

            applyExtensions(nodeRequest);

            if (nodeRequest.getNode().hasNonNull(OcdsValidatorConstants.RELEASES_PROPERTY)) {
                releasePackageReport.mergeWith(validateEmbeddedReleases(
                        nodeRequest,
                        nodeRequest.getNode().get(OcdsValidatorConstants.RELEASES_PROPERTY),
                        OcdsValidatorConstants.Schemas.RELEASE
                ));
            } else {
                throw new RuntimeException("No releases were found during release package validation!");
            }

            return releasePackageReport;

        } catch (ProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Validates a record package
     *
     * @param nodeRequest
     * @return
     */
    private ProcessingReport validateRecordPackage(OcdsValidatorNodeRequest nodeRequest) {

        autodetectPackageVersion(nodeRequest);

        JsonSchema schema = getSchema(nodeRequest);
        try {

            //do a general non extension validation of entire json
            ProcessingReport recordPackageReport = schema.validate(nodeRequest.getNode());
            if (!recordPackageReport.isSuccess()) {
                return recordPackageReport;
            }

            //validate each release separately, after applying extensions
            applyExtensions(nodeRequest);

            //get all records
            if (nodeRequest.getNode().hasNonNull(OcdsValidatorConstants.RECORDS_PROPERTY)) {
                for (JsonNode record : nodeRequest.getNode().get(OcdsValidatorConstants.RECORDS_PROPERTY)) {

                    //validate compiled release
                    if (record.hasNonNull(OcdsValidatorConstants.COMPILED_RELEASE_PROPERTY)) {
                        recordPackageReport.mergeWith(validateEmbeddedRelease(
                                nodeRequest,
                                record.get(OcdsValidatorConstants.COMPILED_RELEASE_PROPERTY),
                                OcdsValidatorConstants.Schemas.RELEASE
                        ));
                    }

                    //validate versioned release
                    if (record.hasNonNull(OcdsValidatorConstants.VERSIONED_RELEASE_PROPERTY)) {
                        recordPackageReport.mergeWith(validateEmbeddedRelease(
                                nodeRequest,
                                record.get(OcdsValidatorConstants.VERSIONED_RELEASE_PROPERTY),
                                OcdsValidatorConstants.Schemas.VERSIONED_RELEASE_VALIDATION
                        ));
                    }

                    //validate releases array, this contains 'oneOf' keyword, so we need to distinguish b/w the schemas
                    if (record.hasNonNull(OcdsValidatorConstants.RELEASES_PROPERTY)) {
                        //decide if we are. If the first node contains the required 'url' property, this is a
                        //Linked release case, if not it is an embedded release case
                        boolean linkUrl = record.get(OcdsValidatorConstants.RELEASES_PROPERTY).get(0)
                                .hasNonNull(OcdsValidatorConstants.URL_PROPERTY);
                        if (linkUrl) {
                            for (JsonNode r : record.get(OcdsValidatorConstants.RELEASES_PROPERTY)) {
                                //we treat the url as a release link and invoke the validator on that url
                                OcdsValidatorUrlRequest urlRequest = new OcdsValidatorUrlRequest(
                                        nodeRequest,
                                        r.get(OcdsValidatorConstants.URL_PROPERTY).asText()
                                );
                                urlRequest.setSchemaType(OcdsValidatorConstants.Schemas.RELEASE);
                                recordPackageReport.mergeWith(validate(urlRequest));
                            }
                        } else {
                            //we treat each entry as an embedded release
                            recordPackageReport.mergeWith(validateEmbeddedReleases(
                                    nodeRequest, record.get(OcdsValidatorConstants.RELEASES_PROPERTY),
                                    OcdsValidatorConstants.Schemas.RELEASE
                            ));
                        }
                    }

                }
            }

            return recordPackageReport;

        } catch (ProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getOcidFromRelease(JsonNode release) {
        if (release.hasNonNull(OcdsValidatorConstants.OCID_PROPERTY)) {
            return release.get(OcdsValidatorConstants.OCID_PROPERTY).asText();
        }
        return null;
    }

    private OcdsValidatorNodeRequest convertStringRequestToNodeRequest(OcdsValidatorStringRequest request) {
        JsonNode node = null;
        if (!StringUtils.isEmpty(request.getJson())) {
            node = getJsonNodeFromString(request.getJson());
        }

        return new OcdsValidatorNodeRequest(request, node);
    }

    private OcdsValidatorNodeRequest convertUrlRequestToNodeRequest(OcdsValidatorUrlRequest request) {
        JsonNode node = null;
        if (!StringUtils.isEmpty(request.getUrl())) {
            node = getJsonNodeFromUrl(request.getTrustSelfSignedCerts(), request.getUrl());
        }

        return new OcdsValidatorNodeRequest(request, node);
    }

}
