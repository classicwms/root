package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.service.PDFApacheExtractionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Validated
@Api(tags = {"PDFController" }, value = "PDFController Operations related to PDFController")
@SwaggerDefinition(tags = { @Tag(name = "PDF", description = "Operations related to PDF") })
@RequestMapping("/pdf")
@RestController
public class PdfController {

    @Autowired
    private PDFApacheExtractionService pdfApacheExtractionService;

    private Path fileStorageLocation = null;

    @PostMapping("/extract/v2")
    public ResponseEntity<?> extractPdfDetails(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String fileName,
                                               @RequestParam String loginUserId) throws Exception {
        try {
            // Extract details from the PDF
            pdfApacheExtractionService.pdfExtractDetails(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, fileName.trim(), loginUserId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}