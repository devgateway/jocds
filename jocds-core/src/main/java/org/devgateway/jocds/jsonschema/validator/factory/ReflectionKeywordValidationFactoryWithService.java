/*
 * Copyright (c) 2018. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.jsonschema.validator.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.validator.KeywordValidatorFactory;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.devgateway.jocds.OcdsValidatorService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionKeywordValidationFactoryWithService implements KeywordValidatorFactory {
    private static final String ERRMSG = "failed to build keyword validator";
    private static final MessageBundle BUNDLE
            = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);
    private OcdsValidatorService service;

    private final Constructor<? extends KeywordValidator> constructor;

    public ReflectionKeywordValidationFactoryWithService(String name,
                                                         Class<? extends KeywordValidator> clazz,
                                                         OcdsValidatorService service) {
        this.service = service;
        try {
            constructor = clazz.getConstructor(JsonNode.class, OcdsValidatorService.class);
        } catch (NoSuchMethodException ignored) {
            throw new IllegalArgumentException(BUNDLE.printf(
                    "noAppropriateConstructor", name, clazz.getCanonicalName()
            ));
        }
    }

    @Override
    public KeywordValidator getKeywordValidator(JsonNode node)
            throws ProcessingException {
        try {
            return constructor.newInstance(node, service);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ProcessingException(ERRMSG, e);
        }
    }


}
