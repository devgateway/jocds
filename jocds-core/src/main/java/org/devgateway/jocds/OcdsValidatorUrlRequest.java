/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;

/**
 * Used to validate OCDS json provided by URL
 */
public class OcdsValidatorUrlRequest extends OcdsValidatorRequest {

    @NotEmpty(message = "Please provide an URL!")
    @URL
    @ApiModelProperty(value = "The URL of the OCDS JSON to validate.", required = true)
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public OcdsValidatorUrlRequest(OcdsValidatorRequest request, String url) {
        super(request);
        this.url = url;
    }

    public OcdsValidatorUrlRequest() {

    }
}
