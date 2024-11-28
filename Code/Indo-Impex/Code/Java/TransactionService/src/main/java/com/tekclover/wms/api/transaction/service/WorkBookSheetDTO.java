package com.tekclover.wms.api.transaction.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@Getter
@Setter
public class WorkBookSheetDTO {

    private Workbook workbook;
    private Sheet sheet;
    private CellStyle style;
}