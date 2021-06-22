/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.SortedSet;

/**
 * Created by mpostelnicu on 7/6/17.
 */
public class OcdsValidatorStringRequest extends OcdsValidatorRequest {

    @Schema(description = "The json to validate against OCDS schema, given as text.")
    private String json;


    public OcdsValidatorStringRequest(String version, SortedSet<String> extensions, String schemaType) {
        super(version, extensions, schemaType);
    }

    public OcdsValidatorStringRequest() {
        super();
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}
