---
layout: page
title: Examples
permalink: /examples/
---


# OC Bandung

Bandung city provides open access to its tenders through their [Birms platform](https://birms.bandung.go.id/beta/).
Raw OCDS data is also available.

## Validating release package data from OC Bandung

Release packages are envelopes of releases, with provenance data. See more info on [OCDS page](http://standard.open-contracting.org/latest/en/schema/release_package/).

We will use [the docker version of the cli module](jocds/jocds-cli), to validate a release package from OC Bandung.
The data is online, so we will use the `-uri` method:

```
docker run jocds/jocds-cli:latest -verbosity=error -operation=validate -schema-type=release-package -url=https://birms.bandung.go.id/beta/api/package/ocds-afzrfb-s-2016-3662192
```

This command will result in an error:

```
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:397) ~[na:1.8.0_171]
	at sun.security.validator.PKIXValidator.engineValidate(PKIXValidator.java:302) ~[na:1.8.0_171]
	at sun.security.validator.Validator.validate(Validator.java:260) ~[na:1.8.0_171]
	at sun.security.ssl.X509TrustManagerImpl.validate(X509TrustManagerImpl.java:324) ~[na:1.8.0_171]
	at sun.security.ssl.X509TrustManagerImpl.checkTrusted(X509TrustManagerImpl.java:229) ~[na:1.8.0_171]
	at sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:124) ~[na:1.8.0_171]
	at sun.security.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1596) ~[na:1.8.0_171]
	... 35 common frames omitted
Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	at sun.security.provider.certpath.SunCertPathBuilder.build(SunCertPathBuilder.java:141) ~[na:1.8.0_171]
	at sun.security.provider.certpath.SunCertPathBuilder.engineBuild(SunCertPathBuilder.java:126) ~[na:1.8.0_171]
	at java.security.cert.CertPathBuilder.build(CertPathBuilder.java:280) ~[na:1.8.0_171]
	at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:392) ~[na:1.8.0_171]
```

This happens because the OC Bandung server has a self-generated SSL certificate. These are considered unsafe and jOCDS will crash if a certificate
cannot be validated. In order to overcome this problem you need to provide a separate parameter `-trustSelfSignedCerts=true` so jOCDS
will ignore the error

```
docker run jocds/jocds-cli:latest -verbosity=error -operation=validate -schema-type=release-package -url=https://birms.bandung.go.id/beta/api/package/ocds-afzrfb-s-2016-3662192 -trustSelfSignedCerts=true
```

... will result in jOCDS downloading the json and validating it. In this case there are no errors

```
$docker run jocds/jocds-cli:latest -verbosity=error -operation=validate -schema-type=release-package -url=https://birms.bandung.go.id/beta/api/package/ocds-afzrfb-s-2016-3662192 -trustSelfSignedCerts=true
jocds : The Java Open Contracting Data Standard Validator 0.4.0 : Copyright (c) 2018 Development Gateway, Inc
jocds invoked with parameters: {-url=https://birms.bandung.go.id/beta/api/package/ocds-afzrfb-s-2016-3662192, -verbosity=error, -schema-type=release-package, -operation=validate, -trustSelfSignedCerts=true}

"OK"
```

## Validating releases from OC Bandung

Bandung open data platform also provides an endpoint for directly fetching releases. We can use this new url with `schema-type=release`.

```
docker run jocds/jocds-cli:latest -verbosity=error -operation=validate -schema-type=release -url=https://birms.bandung.go.id/beta/api/newcontract/ocds-afzrfb-s-2016-3662192 -trustSelfSignedCerts=true
```

However if we use this command, we will get an error:

```
Caused by: java.lang.RuntimeException: Not allowed null version info for release validation!
	at org.devgateway.jocds.OcdsValidatorService.validate(OcdsValidatorService.java:488) ~[jocds-core-0.3.2-SNAPSHOT.jar!/:na]
	at org.devgateway.jocds.OcdsValidatorService.validate(OcdsValidatorService.java:477) ~[jocds-core-0.3.2-SNAPSHOT.jar!/:na]
	at org.devgateway.jocds.cli.CliRunner.run(CliRunner.java:145) ~[classes!/:na]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:800) ~[spring-boot-2.0.4.RELEASE.jar!/:2.0.4.RELEASE]
	... 11 common frames omitted

```

This is because OCDS releases do not have the OCDS version specified, unlike release-packages. So the OCDS version to be used
must be provided manually using `-version` parameter. For this, it would be useful for jOCDS to print
all the available versions it can validate against. This can be done using `-operation=show-supported-ocds`:

```
docker run jocds/jocds-cli:latest -operation=show-supported-ocds

[ {
  "level" : "info",
  "message" : "1.0.0"
}, {
  "level" : "info",
  "message" : "1.0.1"
}, {
  "level" : "info",
  "message" : "1.0.2"
}, {
  "level" : "info",
  "message" : "1.0.3"
}, {
  "level" : "info",
  "message" : "1.1.0"
}, {
  "level" : "info",
  "message" : "1.1.1"
}, {
  "level" : "info",
  "message" : "1.1.2"
}, {
  "level" : "info",
  "message" : "1.1.3"
} ]

```

So our jocds supports the latest 1.1.3 OCDS release. Let's use that to validate a release url

```
docker run jocds/jocds-cli:latest -verbosity=error -operation=validate -schema-type=release -url=https://birms.bandung.go.id/beta/api/newcontract/ocds-afzrfb-s-2016-3662192 -trustSelfSignedCerts=true -version=1.1.3

jocds : The Java Open Contracting Data Standard Validator 0.4.0 : Copyright (c) 2018 Development Gateway, Inc
jocds invoked with parameters: {-url=https://birms.bandung.go.id/beta/api/newcontract/ocds-afzrfb-s-2016-3662192, -verbosity=error, -version=1.1.3, -schema-type=release, -operation=validate, -trustSelfSignedCerts=true}

"OK"

```