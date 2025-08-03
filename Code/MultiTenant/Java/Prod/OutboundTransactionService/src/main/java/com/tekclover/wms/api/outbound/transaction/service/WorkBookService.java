package com.tekclover.wms.api.outbound.transaction.service;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class WorkBookService {

	/**
	 * 
	 * @param sheet_name
	 * @return
	 */
	public WorkBookSheetDTO createWorkBookWithSheet(String sheet_name) {
		WorkBookSheetDTO workBookSheetDTO = new WorkBookSheetDTO();

		XSSFWorkbook workbook = new XSSFWorkbook();
		// create excel xls sheet
		Sheet sheet = workbook.createSheet(sheet_name);
		sheet.setDefaultColumnWidth(15);

		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Calibri");
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		font.setBold(true);
		font.setColor(HSSFColor.BLACK.index);
		style.setFont(font);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		workBookSheetDTO.setWorkbook(workbook);
		workBookSheetDTO.setSheet(sheet);
		workBookSheetDTO.setStyle(style);
		return workBookSheetDTO;
	}

	/**
	 * 
	 * @param workbook
	 * @return
	 */
	public CellStyle createLineCellStyle(Workbook workbook) {
		// create style for header cells
		CellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setBold(true);// Make font bold
//        cellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
//        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		font.setColor(HSSFColor.BLACK.index);
		cellStyle.setFont(font);// set it to bold
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		return cellStyle;
	}
}
