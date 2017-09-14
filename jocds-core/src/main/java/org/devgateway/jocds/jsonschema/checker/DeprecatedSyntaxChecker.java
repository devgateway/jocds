/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
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
import java.util.Collection;
import org.devgateway.jocds.OcdsValidatorConstants;

public final class DeprecatedSyntaxChecker extends AbstractSyntaxChecker {

    private static final SyntaxChecker INSTANCE = new DeprecatedSyntaxChecker();

    public static SyntaxChecker getInstance() {
        return INSTANCE;
    }

    private DeprecatedSyntaxChecker() {
        super(OcdsValidatorConstants.CustomSchemaKeywords.DEPRECATED, NodeType.OBJECT);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
                              final MessageBundle bundle, final ProcessingReport report,
                              final SchemaTree tree)
            throws ProcessingException {
        System.out.println(tree);
    }
}