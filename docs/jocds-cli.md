---
layout: page
title: CLI
permalink: /jocds-cli/
---

# jOCDS - Command Line Interface (CLI)

## Prerequisites & Installation

The only pre-requisite is [Java8](https://java.com/en/download/).

We continously publish versions of jOCDS on [Bintray](https://bintray.com/devgateway/jocds/jocds).
You can download the latest version of `jocds-cli` from the bottom of the page, in the "Downloads" section.

After downloading the jar file, you should be able to start it by double clicking on it, or from the command line using

```
java -jar jocds-cli-[version].jar
```

for example if you have downloaded `jocds-cli-0.4.0.jar` you should be able to start it using

```
java -jar jocds-cli-0.4.0.jar
```

Without any parameters, the tool will just print the usage info

```
java -jar jocds-cli-0.4.0.jar

jocds : The Java Open Contracting Data Standard Validator 0.4.0 : Copyright (c) 2017 Development Gateway, Inc
Usage:

$ jocds-cli [-operation=] -schema-type= [-version=] [-url=] [-file=]

-operation (optional) - the operation to perform. The default is validate. Accepted values: validate, show-supported-ocds, show-builtin-extensions
-schema-type - the input schema type. Accepted values: release, release-package
-version (optional) - the OCDS version to validate against. Can be autodetected for release-package schema-type.
-url (optional) - validate OCDS json from the given URL
-file (optional) - validate OCDS json from the given file on the file system
```


## Using the CLI

### Validating a local JSON file

To validate a json file saved locally you can use a command like the one below. We introduced an error in the original JSON taken
from the OCDS github page, more exactly used a string instead of a number in the `tender.minValue` field, resulting in an error:

```
java -jar jocds-cli-0.4.0.jar -operation=validate -schema-type=release-package -file=ocds-213czf-000-00001-02-tender.json
jocds : The Java Open Contracting Data Standard Validator 0.4.0 : Copyright (c) 2017 Development Gateway, Inc
jocds invoked with parameters: {-schema-type=release-package, -operation=validate, -file=/home/mihai/ocds-213czf-000-00001-02-tender.json}

[{"level":"error","schema":{"loadingURI":"http://standard.open-contracting.org/schema/1__1__1/release-schema.json#","pointer":"/definitions/Value/properties/amount"},"instance":{"pointer":"/releases/0/tender/minValue/amount"},"domain":"validation","keyword":"type","message":"instance type (string) does not match any allowed primitive type (allowed: [\"integer\",\"null\",\"number\"])","found":"string","expected":["integer","null","number"]}]

```

### Enforinng schema version

Just like with the REST API , we can enforce schema version when doing the check by using the `-version` parameter.
This works for schema types `release-package` and `release`. Example

```
java -jar jocds-cli-0.4.0.jar -operation=validate -version=1.0.2 -schema-type=release-package -file=ocds-213czf-000-00001-02-tender.json
```


### Querying the list of supported OCDS versions

We strive to keep jOCDS up to date and to support in offline mode the latest schemas released by the Open Contracting team.
To see the OCDS versions supported by a particular jOCDS release, use:

```
java -jar jocds-cli-0.4.0.jar -operation=show-supported-ocds -schema-type=release-package
jocds : The Java Open Contracting Data Standard Validator 0.4.0 : Copyright (c) 2017 Development Gateway, Inc
jocds invoked with parameters: {-schema-type=release-package, -operation=show-supported-ocds}

[{"level":"info","message":"1.0.0"},{"level":"info","message":"1.0.1"},{"level":"info","message":"1.0.2"},{"level":"info","message":"1.1.0"},{"level":"info","message":"1.1.1"}]

```

Notice jocds-cli-0.4.0 supports all OCDS versions released up to 1.1.3.

### Querying the list of supported offline core extensions

```
java -jar jocds-cli-0.4.0.jar -operation=show-builtin-extensions -schema-type=release-package
jocds : The Java Open Contracting Data Standard Validator 0.4.0 : Copyright (c) 2017 Development Gateway, Inc
jocds invoked with parameters: {-schema-type=release-package, -operation=show-builtin-extensions}

[{"level":"info","message":"ocds_additionalContactPoints_extension"},{"level":"info","message":"ocds_bid_extension/v1.1"},{"level":"info","message":"ocds_budget_breakdown_extension"},{"level":"info","message":"ocds_budget_projects_extension"},{"level":"info","message":"ocds_charges_extension"},{"level":"info","message":"ocds_contract_suppliers_extension"},{"level":"info","message":"ocds_documentation_extension"},{"level":"info","message":"ocds_enquiry_extension/v1.1"},{"level":"info","message":"ocds_extendsContractID_extension"},{"level":"info","message":"ocds_location_extension/v1.1"},{"level":"info","message":"ocds_lots_extension/v1.1"},{"level":"info","message":"ocds_milestone_documents_extension/v1.1"},{"level":"info","message":"ocds_participationFee_extension/v1.1"},{"level":"info","message":"ocds_process_title_extension/v1.1"}]
```

### Validating with extensions

Specifying extensions will enable validation of OCDS data plus extension data, based on the extension schema. The following command will validate a local json
file against two extensions: one that is builtin/offline, part of the OCDS core (`ocds_lots_extension/v1.1`)

```
java -jar jocds-cli-0.4.0.jar -operation=validate -schema-type=release-package -extensions=https://raw.githubusercontent.com/open-contracting/ocds_bid_extension/v1.1/extension.json,ocds_lots_extension/v1.1
-file=ocds-213czf-000-00001-02-tender.json

jocds : The Java Open Contracting Data Standard Validator 0.4.0 : Copyright (c) 2017 Development Gateway, Inc
jocds invoked with parameters: {-file=ocds-213czf-000-00001-02-tender.json, -extensions=https://raw.githubusercontent.com/open-contracting/ocds_bid_extension/v1.1/extension.json,ocds_lots_extension/v1.1, -schema-type=release-package, -operation=validate}

"OK"

```

