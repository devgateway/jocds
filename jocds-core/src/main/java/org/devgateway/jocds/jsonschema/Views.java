/*
 * Copyright (c) 2018. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.jsonschema;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.devgateway.jocds.OcdsValidatorRequest;

/**
 * Used to restrict export to json programattically for some internal cases
 * @see org.devgateway.jocds.OcdsValidatorService#wrapLogReportInRequestInfo(ProcessingReport, OcdsValidatorRequest)
 */
public class Views {
    public static class Public {
    }
    public static class Internal {
    }

}