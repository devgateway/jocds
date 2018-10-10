/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

/**
 * This adds functionality for "deprecated" keyword.
 * http://standard.open-contracting.org/latest/en/schema/deprecation/
 * It will print warning messages when deprecated elements are found
 */
public final class DeprecatedValidator
        extends AbstractKeywordValidator {

    private final JsonNode deprecated;

    public DeprecatedValidator(final JsonNode digest) {
        super("deprecated");
        deprecated = digest.get(keyword);
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
                         final ProcessingReport report, final MessageBundle bundle,
                         final FullData data)
            throws ProcessingException {

        if (deprecated == null) {
            return;
        }

        report.warn(newMsg(data, bundle,
                "warn.jocds.deprecatedValidator"
        ).putArgument("deprecated", deprecated).putArgument("data", data.getInstance().getNode()));
    }

    @Override
    public String toString() {
        return keyword + ": ";
    }

}
