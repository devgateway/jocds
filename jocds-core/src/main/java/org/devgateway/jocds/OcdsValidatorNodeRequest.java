/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.devgateway.jocds.jsonschema.Views;

import java.util.TreeSet;

/**
 * Created by mpostelnicu on 7/6/17.
 */
public class OcdsValidatorNodeRequest extends OcdsValidatorRequest {

    @Schema(description = "The json to validate against OCDS schema")
    @JsonView(Views.Public.class)
    private JsonNode node;

    public OcdsValidatorNodeRequest(String version, TreeSet<String> extensions, String schemaType) {
        super(version, extensions, schemaType);
    }

    public OcdsValidatorNodeRequest() {

    }

    public OcdsValidatorNodeRequest(OcdsValidatorRequest request, JsonNode node) {
        super(request);
        this.node = node;
    }

    public JsonNode getNode() {
        return node;
    }

    public void setNode(JsonNode node) {
        this.node = node;
    }
}
