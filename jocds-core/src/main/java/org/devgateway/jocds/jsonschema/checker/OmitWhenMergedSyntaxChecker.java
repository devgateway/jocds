/*
 * Copyright (c) 2018. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.jsonschema.checker;

import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import org.devgateway.jocds.OcdsValidatorConstants;

import java.util.Collection;

public final class OmitWhenMergedSyntaxChecker extends AbstractSyntaxChecker {

    private static final SyntaxChecker INSTANCE = new OmitWhenMergedSyntaxChecker();

    public static SyntaxChecker getInstance() {
        return INSTANCE;
    }

    private OmitWhenMergedSyntaxChecker() {
        super(OcdsValidatorConstants.CustomSchemaKeywords.OMIT_WHEN_MERGED, NodeType.BOOLEAN);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
                              final MessageBundle bundle, final ProcessingReport report,
                              final SchemaTree tree)
            throws ProcessingException {

    }
}