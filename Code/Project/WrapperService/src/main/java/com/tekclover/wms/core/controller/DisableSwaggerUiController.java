package com.tekclover.wms.core.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Profile("!swagger")
@RestController
@Slf4j
public class DisableSwaggerUiController {
//
//    @RequestMapping(value = "swagger-ui.html", method = RequestMethod.GET)
//    public void getSwagger(HttpServletResponse httpResponse) throws IOException {
//        httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
//    }
}
