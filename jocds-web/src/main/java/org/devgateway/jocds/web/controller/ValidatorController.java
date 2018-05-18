/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiOperation;
import org.devgateway.jocds.OcdsValidatorNodeRequest;
import org.devgateway.jocds.OcdsValidatorService;
import org.devgateway.jocds.OcdsValidatorStringRequest;
import org.devgateway.jocds.OcdsValidatorUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Created by mpostelnicu on 16-May-17.
 */
@RestController
public class ValidatorController {

    @Autowired
    private OcdsValidatorService ocdsValidatorService;

    @ApiOperation(value = "Validates data against Open Contracting Data Standard using x-www-form-urlencoded "
            + "media type")
    @RequestMapping(value = "/api/validateFormInline", method = {RequestMethod.GET, RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<JsonNode> validateFormInline(@Valid @ModelAttribute OcdsValidatorStringRequest request)
            throws IOException {
        return new ResponseEntity<JsonNode>(ocdsValidatorService.
                processingReportToJsonNode(ocdsValidatorService.validate(request), request),
                HttpStatus.OK);
    }

    @ApiOperation(value = "Validates data against Open Contracting Data Standard using application/json "
            + "media type")
    @RequestMapping(value = "/api/validateJsonInline", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<JsonNode> validateJsonInline(@RequestBody @Valid
                                                               OcdsValidatorNodeRequest request)
            throws IOException {
        return new ResponseEntity<JsonNode>(ocdsValidatorService.processingReportToJsonNode(
                ocdsValidatorService.validate(request), request), HttpStatus.OK);
    }

    @ApiOperation(value = "Validates data against Open Contracting Data Standard using the "
            + "data fetched from a given URL")
    @RequestMapping(value = "/api/validateJsonUrl", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<JsonNode> validateJsonUrl(@RequestBody @Valid
                                                            OcdsValidatorUrlRequest request)
            throws IOException {
        return new ResponseEntity<JsonNode>(ocdsValidatorService.processingReportToJsonNode(
                ocdsValidatorService.validate(request), request), HttpStatus.OK);
    }



}
