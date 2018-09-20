---
layout: page
title: Dockerized
permalink: /docker/
---

# Dockerized builds

jOCDS is available as a dockerized build published on https://hub.docker.com.

We publish two images: one for the CLI version, the other for the Web version of the tool.


## CLI image

The CLI docker image of jOCDS was built using `maven:3.5-jdk-8-alpine` and repackaged from `openjdk:8-jre-alpine`
as a Docker [multi-stage build](https://docs.docker.com/develop/develop-images/multistage-build/).

You may find more info about the CLI build on its hub.docker.com page [available here](https://hub.docker.com/r/jocds/jocds-cli/) .


### Running the CLI image

The latest version of the dockerized cli build can be fetched and run using:

```docker run jocds/jocds-cli:latest```

You may pass the parameter options directly after these commands.
For example the following line downloads/invokes the container and runs it to validate an OCDS example data fetchde from given url:

```docker run jocds/jocds-cli:latest -verbosity=error -operation=validate -schema-type=release-package -url=https://raw.githubusercontent.com/open-contracting/sample-data/master/fictional-example/1.1/ocds-213czf-000-00001-02-tender.json -trustSelfSignedCerts=true```

## Web Server image


The Web Server docker image of jOCDS was built using `maven:3.5-jdk-8-alpine` and repackaged from `openjdk:8-jre-alpine`
as a Docker [multi-stage build](https://docs.docker.com/develop/develop-images/multistage-build/).

### Running the Web Server image

The latest version of the dockerized cli build can be fetched and run using:

```docker run -p8080:8080 jocds/jocds-web:latest```

This will start the web server and expose it on port `8080`. You may then access it in your favourite browser
by navigating to the address: [http://localhost:8080/](http://localhost:8080/)