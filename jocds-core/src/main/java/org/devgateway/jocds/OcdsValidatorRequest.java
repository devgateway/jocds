/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The request object that is used throughout the validator. This is the base class for all request subclasses.
 * <p>
 * Created by mpostelnicu on 7/5/17.
 */
public class OcdsValidatorRequest {

    public OcdsValidatorRequest() {

    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public OcdsValidatorRequest(OcdsValidatorRequest request) {
        this.version = request.getVersion();
        this.extensions = request.getExtensions();
        this.schemaType = request.getSchemaType();
        this.operation = request.getOperation();
        this.verbosity = request.getVerbosity();
        this.trustSelfSignedCerts = request.getTrustSelfSignedCerts();
    }

    public OcdsValidatorRequest(String version, SortedSet<String> extensions, String schemaType) {
        this.version = version;
        this.extensions = extensions;
        this.schemaType = schemaType;
    }

    /**
     * This returns a unique key of the validator request based on the set contents , version and schemaType
     *
     * @return
     */
    @JsonIgnore
    @Schema(hidden = true)
    public String getKey() {
        return schemaType + "-" + version + "-" + extensions + "-" + verbosity;
    }

    @Schema(description =
            "This is the version of OCDS schema to validate against. Leaving this empty will enable schema"
            + " autodetection. This is helpful to test against another OCDS schema besides the one specified in "
            + "the incoming JSON.")
    /**
     * This is the version of OCDS schema to validate against. Leaving this empty will enable schema
     * autodetection. This is helpful to test against another OCDS schema besides the one specified in
     * the incoming JSON.
     */
    private String version;

    @Schema(description =
            "You can provide a set of OCDS extensions here to validate against. All OCDS core extensions are "
            + " supported, in offline mode, as well as any other OCDS extension given by URL")
    /**
     You can provide a set of OCDS extensions here to validate against. All OCDS core extensions are
     supported, in offline mode, as well as any other OCDS extension given by URL
     */
    private SortedSet<String> extensions = new TreeSet<>();

    @Pattern(regexp = OcdsValidatorConstants.Operations.VALIDATE + "|"
            + OcdsValidatorConstants.Operations.SHOW_BUILTIN_EXTENSIONS + "|"
            + OcdsValidatorConstants.Operations.SHOW_SUPPORTED_OCDS)
    @Schema(description = "Provides the operation that needs to be performed. The default is 'validate'."
            + "'show-supported-ocds' will list the supported ocds versions. show-builtin-extensions will list the "
            + "core OCDS extensions that are supported internally and in offline mode.")
    /**
     * Provides the operation that needs to be performed. The default is 'validate'.
     * 'show-supported-ocds' will list the supported ocds versions. show-builtin-extensions will list the
     * core OCDS extensions that are supported internally and in offline mode.
     */
    private String operation = OcdsValidatorConstants.Operations.VALIDATE;

    @Schema(description = "Trust self signed SSL certificates. Defaults to false")
    /**
     * Trust self signed SSL certificates. Defaults to false
     */
    private boolean trustSelfSignedCerts = false;

    @NotEmpty(message = "Please provide schemaType!")
    @Schema(description = "This is the schema type of the input JSON. Currently supported values are 'release' "
            + ", release-package, record-package", required = true)
    @Pattern(regexp = OcdsValidatorConstants.Schemas.RELEASE + "|"
            + OcdsValidatorConstants.Schemas.RELEASE_PACKAGE + "|" + OcdsValidatorConstants.Schemas.RECORD_PACKAGE)
    /**
     * This is the schema type of the input JSON. Currently supported values are 'release', release-package,
     * record-package
     */
    private String schemaType;

    @Schema(description = "Set the verbosity level of output. Default is 'error' but you can set this to "
            + "'warning'. On 'warning', the validator will print warning output for elements that are not part of the "
            + "schema or meta-schema, OCDS deprecated fields, etc. On 'error' it will only print validation"
            + " errors.")

    @Pattern(regexp = OcdsValidatorConstants.LogLevel.WARNING + "|" + OcdsValidatorConstants.LogLevel.ERROR)
    /**
     * Set the verbosity level of output. Default is 'error' but you can set this to 'warning'. On 'warning', the
     * validator will print warning output for elements that are not part of the
     * schema or meta-schema, OCDS deprecated fields, etc. On 'error' it will only print validation errors.
     */
    private String verbosity = OcdsValidatorConstants.LogLevel.ERROR;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SortedSet<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(SortedSet<String> extensions) {
        this.extensions = extensions;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public String getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(String verbosity) {
        this.verbosity = verbosity;
    }

    public boolean getTrustSelfSignedCerts() {
        return trustSelfSignedCerts;
    }

    public void setTrustSelfSignedCerts(boolean trustSelfSignedCerts) {
        this.trustSelfSignedCerts = trustSelfSignedCerts;
    }

    public boolean isTrustSelfSignedCerts() {
        return trustSelfSignedCerts;
    }
}
