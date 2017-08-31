/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public class OcdsValidatorUrlRequest extends OcdsValidatorRequest {

    @NotNull(message = "Please provide an URL!")
    @URL
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
}
