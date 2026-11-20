package com.tekclover.wms.api.inbound.transaction.service;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkBookSheetDTO {

    private Workbook workbook;
    private Sheet sheet;
    private CellStyle style;
}
