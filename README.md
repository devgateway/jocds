# jOCDS
jOCDS - The Java Open Contracting Data Standard Validator

[![Build Status](https://travis-ci.org/devgateway/jocds.svg?branch=master)](https://travis-ci.org/devgateway/jocds)
[ ![Download](https://api.bintray.com/packages/devgateway/jocds/jocds/images/download.svg) ](https://bintray.com/devgateway/jocds/jocds/_latestVersion)

The [Open Contracting Data Standard (OCDS)](http://standard.open-contracting.org) is a standards development initiative issued by
the Omidyar Network and the World Bank [which commenced in November 2014](http://standard.open-contracting.org/latest/en/support/history_and_development/).

The standard facilitates publishing data about public procurement of goods, works
and services and can be easily extended to be used in other contexts.

It has been adopted in more than 25 countries and cities working on open contracting.

The purpose of adopting the standard is structuring the internal data in a format using JSON and following a structure template called
as schema (JSON Schema). The schema is developed and maintained by [Open Contracting Partnership (OCP)](https://www.open-contracting.org/).
After the data is structured in this way, it can be made public, for reuse by third parties.

It is critical that the data being published follows the schema, meaning it is "valid". This becomes a more delicate
problem because the schema itself is a living growing structure that has versions of its own.
The OCDS schema in 2015, when the 1.0 version of OCDS was published, is different than the schema available now (OCDS 1.1.1).
The OCP tries to keep the subsequent schema versions backwards compliant, but that is not 100% possible all the time. This
means there are now multiple versions of the OCDS schema available. Obviously OCP always will advertise and encourage the use
of the latest published schema, but the institutions that sign on to become OCDS compliant will do that at a certain moment in time, and in many cases will not have readily available funds to keep their OCDS export updated to work with the latest OCDS schema. This means there is a large and growing list of publishers out there that use older OCDS schema versions.

It is very important that tools are made available to allow validation of data in any OCDS schema version that exists, old or new. The issue is made more difficult by the existence of schema extensions, that have their own versions in some cases, and which have to be applied on top of the original OCDS schema, to leverage many aspects of the growing datasets complexity that is seen in the wild. Extensions can be official (maintained by the OCP), community maintained or custom made by the publisher.

Another issue is as the standard adoption grows, content producers, editors will need to consume data produced by the standard or generate data in diverse environments sometimes with their own tech challanges. In some developing countries, Internet access is not always possible all the time and there is a great need for tools that work offline to the most extent possible.


# Features

jOCDS is collection of tools that makes validation of the OCDS JSON data easy. It aims to achieve:
- full compliance with all OCDS schema versions released, covering major, minor or maintenance updates
- offline support for schema validation
- offline support for validation of schema extension data for the core supported extensions
- command line interface (CLI) tool that is easy to install and use
- REST API tool that can be easily started and allows validation in the browser or using a REST client
- standalone server deployment - deploy the tool as a separate online application
- do NOT store incoming OCDS data uploaded by the users
- online support for any OCDS extensions that are given by URL
- caching of schema with the applied extensions for fast validation or large datasets
- built on open source technologies, and fully open source code with a very permissible license


# jOCDS Web - REST API

Complete REST API documentation can be find on the [REST API module page](https://github.com/devgateway/jocds/tree/master/jocds-web)

# jOCDS CLI - Command Line Interface

The command line interface version of the tool has a separate [CLI module page](https://github.com/devgateway/jocds/tree/master/jocds-cli)


## Thanks!

![YourKit](https://www.yourkit.com/images/yklogo.png)

jOCDS uses YourKit Java Profiler for Performance Tuning.

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.
