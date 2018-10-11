/*
 * Copyright (c) 2018. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.jsonschema.validator;

import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import org.devgateway.jocds.OcdsValidatorService;

/**
 * A {@link KeywordValidator} with an {@link OcdsValidatorService}
 */
public interface KeywordValidatorWithService extends KeywordValidator {

    OcdsValidatorService getService();
}
