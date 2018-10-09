/*
 * Copyright (c) 2018. Development Gateway and contributors. All rights reserved.
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
import org.devgateway.jocds.OcdsValidatorService;

public final class CodelistValidator
        extends AbstractKeywordValidator implements KeywordValidatorWithService {

    private final JsonNode codelist;
    private final JsonNode openCodelist;
    private OcdsValidatorService service;

    public CodelistValidator(final JsonNode digest, OcdsValidatorService service) {
        super("codelist");
        this.service = service;
        codelist = digest.get(keyword);
        openCodelist = digest.get("openCodelist");
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
                         final ProcessingReport report, final MessageBundle bundle,
                         final FullData data)
            throws ProcessingException {

        if (codelist == null) {
            return;
        }

        System.out.println(data.getInstance().getNode());

    }

    @Override
    public String toString() {
        return keyword + ": ";
    }

    @Override
    public OcdsValidatorService getService() {
        return service;
    }
}
