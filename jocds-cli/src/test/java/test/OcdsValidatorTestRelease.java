/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package test;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.devgateway.jocds.OcdsValidatorConstants;
import org.devgateway.jocds.OcdsValidatorService;
import org.devgateway.jocds.OcdsValidatorStringRequest;
import org.devgateway.jocds.cli.ValidatorApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.TreeSet;

/**
 * Created by mpostelnicu on 7/7/17.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("integration")
@SpringBootTest(classes = {ValidatorApplication.class})
//@TestPropertySource("classpath:test.properties")
public class OcdsValidatorTestRelease {

    @Autowired
    private OcdsValidatorService ocdsValidatorService;

    @Test
    public void testReleaseValidation() {

        OcdsValidatorStringRequest request = new OcdsValidatorStringRequest(OcdsValidatorConstants.Versions.OCDS_1_1_0,
                OcdsValidatorConstants.EXTENSIONS, OcdsValidatorConstants.Schemas.RELEASE);
        request.setJson(getJsonFromResource("/full-release.json"));

        ProcessingReport processingReport = ocdsValidatorService.validate(request);
        if (!processingReport.isSuccess()) {
            System.out.println(processingReport);
        }

        Assert.assertTrue(processingReport.isSuccess());

    }

    private String getJsonFromResource(String resourceName) {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
        try {
            return StreamUtils.copyToString(resourceAsStream, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                resourceAsStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    @Test
    public void testReleasePackageValidation() {

        OcdsValidatorStringRequest request = new OcdsValidatorStringRequest(OcdsValidatorConstants.Versions.OCDS_1_1_3,
               new TreeSet<>(OcdsValidatorConstants.EXTENSIONS), OcdsValidatorConstants.Schemas.RELEASE_PACKAGE);

        request.setJson(getJsonFromResource("/release-package.json"));

        ProcessingReport processingReport = ocdsValidatorService.validate(request);
        if (!processingReport.isSuccess()) {
            System.out.println(processingReport);
        }

        Assert.assertTrue(processingReport.isSuccess());

    }

    @Test
    public void testReleasePackageValidationWithVersionAutodetect() {

        OcdsValidatorStringRequest request = new OcdsValidatorStringRequest(null,
                new TreeSet<>(OcdsValidatorConstants.EXTENSIONS), OcdsValidatorConstants.Schemas.RELEASE_PACKAGE);

        request.setJson(getJsonFromResource("/release-package.json"));

        ProcessingReport processingReport = ocdsValidatorService.validate(request);
        if (!processingReport.isSuccess()) {
            System.out.println(processingReport);
        }

        Assert.assertTrue(processingReport.isSuccess());

    }

}
