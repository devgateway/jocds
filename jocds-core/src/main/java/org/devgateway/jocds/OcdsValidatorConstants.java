/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds;

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by mpostelnicu on 7/5/17.
 */
public final class OcdsValidatorConstants {

    private OcdsValidatorConstants() {

    }

    public static final class Versions {
        public static final String OCDS_1_0_0 = "1.0.0";
        public static final String OCDS_1_0_1 = "1.0.1";
        public static final String OCDS_1_0_2 = "1.0.2";
        public static final String OCDS_1_0_3 = "1.0.3";
        public static final String OCDS_1_1_0 = "1.1.0";
        public static final String OCDS_1_1_1 = "1.1.1";
        public static final String OCDS_1_1_2 = "1.1.2";
        public static final String OCDS_1_1_3 = "1.1.3";

        public static final String[] ALL = {OCDS_1_0_0, OCDS_1_0_1, OCDS_1_0_2, OCDS_1_0_3, OCDS_1_1_0,
                OCDS_1_1_1, OCDS_1_1_2, OCDS_1_1_3};

    }

    public static final class CustomSchemaKeywords {
        /**
         * See http://standard.open-contracting.org/latest/en/schema/deprecation/
         */
        public static final String DEPRECATED = "deprecated";
        public static final String WHOLE_LIST_MERGE = "wholeListMerge";
        public static final String OMIT_WHEN_MERGED = "omitWhenMerged";
        public static final String MERGE_STRATEGY = "mergeStrategy";
        public static final String VERSION_ID = "versionId";
        public static final String OPEN_CODE_LIST = "openCodelist";
        public static final String CODE_LIST = "codelist";
    }

    public static final class Schemas {
        public static final String RELEASE = "release";
        public static final String RELEASE_PACKAGE = "release-package";
        public static final String RECORD_PACKAGE = "record-package";
        public static final String[] ALL = {RELEASE, RELEASE_PACKAGE, RECORD_PACKAGE};
    }

    public static final class LogLevel {
        public static final String WARNING = "warning";
        public static final String ERROR = "error";
        public static final String[] ALL = {WARNING, ERROR};
    }

    public static final class SchemaPrefixes {
        public static final String RELEASE = "/schema/release/release-schema-";
        public static final String RELEASE_PACKAGE = "/schema/release-package/release-package-schema-";
        public static final String RECORD_PACKAGE = "/schema/record-package/record-package-schema-";
    }

    public static final String SCHEMA_POSTFIX = ".json";

    public static final class Operations {
        public static final String VALIDATE = "validate";
        public static final String SHOW_SUPPORTED_OCDS = "show-supported-ocds";
        public static final String SHOW_BUILTIN_EXTENSIONS = "show-builtin-extensions";
    }

    public static final class Extensions {
        //OFFICIAL
        public static final String OCDS_BID_EXTENSION_V1_1 = "ocds_bid_extension/v1.1";
        public static final String OCDS_ENQUIRY_EXTENSION_V1_1 = "ocds_enquiry_extension/v1.1";
        public static final String OCDS_LOCATION_EXTENSION_V1_1 = "ocds_location_extension/v1.1";
        public static final String OCDS_LOTS_EXTENSION_V1_1 = "ocds_lots_extension/v1.1";
        public static final String OCDS_MILESTONE_DOCUMENTS_EXTENSION_V1_1 = "ocds_milestone_documents_extension/v1.1";
        public static final String OCDS_PARTICIPATION_FEE_EXTENSION_V1_1 = "ocds_participationFee_extension/v1.1";
        public static final String OCDS_PROCESS_TITLE_EXTENSION_V1_1 = "ocds_process_title_extension/v1.1";

        //COMMUNITY
        public static final String OCDS_ADDITIONAl_CONTACT_POINTS_EXTENSION = "ocds_additionalContactPoints_extension";
        public static final String OCDS_BUDGET_BREAKDOWN_EXTENSION = "ocds_budget_breakdown_extension";
        public static final String OCDS_BUDGET_PROJECTS_EXTENSION = "ocds_budget_projects_extension";
        public static final String OCDS_CHARGES_EXTENSION = "ocds_charges_extension";
        public static final String OCDS_CONTRACT_SUPPLIERS_EXTENSION = "ocds_contract_suppliers_extension";
        public static final String OCDS_DOCUMENTATION_EXTENSION = "ocds_documentation_extension";
        public static final String OCDS_EXTENDS_CONTRACTID_EXTENSION = "ocds_extendsContractID_extension";

        public static final String[] ALL = {OCDS_BID_EXTENSION_V1_1, OCDS_ENQUIRY_EXTENSION_V1_1,
                OCDS_LOCATION_EXTENSION_V1_1, OCDS_LOTS_EXTENSION_V1_1, OCDS_MILESTONE_DOCUMENTS_EXTENSION_V1_1,
                OCDS_PARTICIPATION_FEE_EXTENSION_V1_1, OCDS_PROCESS_TITLE_EXTENSION_V1_1,

                OCDS_ADDITIONAl_CONTACT_POINTS_EXTENSION, OCDS_BUDGET_BREAKDOWN_EXTENSION,
                OCDS_BUDGET_PROJECTS_EXTENSION, OCDS_CHARGES_EXTENSION, OCDS_CONTRACT_SUPPLIERS_EXTENSION,
                OCDS_DOCUMENTATION_EXTENSION, OCDS_EXTENDS_CONTRACTID_EXTENSION};
    }

    public static final String EXTENSIONS_PREFIX = "/schema/extensions/";
    public static final String REMOTE_EXTENSION_META_POSTFIX = "extension.json";

    public static final String EXTENSIONS_PROPERTY = "extensions";
    public static final String RELEASES_PROPERTY = "releases";
    public static final String OCID_PROPERTY = "ocid";
    public static final String VERSION_PROPERTY = "version";

    public static final SortedSet<String> EXTENSIONS = Collections.unmodifiableSortedSet(new TreeSet<>(Arrays.asList(
            Extensions.ALL)));


    public static final String EXTENSION_META = "extension.json";
    public static final String EXTENSION_META_COMPAT_PROPERTY = "compatibility";
    public static final String EXTENSION_RELEASE_JSON = "release-schema.json";
}
