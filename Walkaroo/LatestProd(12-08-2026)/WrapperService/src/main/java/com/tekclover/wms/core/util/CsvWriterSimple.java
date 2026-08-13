package com.tekclover.wms.core.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tekclover.wms.core.model.transaction.Inventory;

public class CsvWriterSimple {
	public static void main(String[] args) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		try {
			// create a list of employee
			List<Inventory> list = inventoryList();
			Writer writer = new FileWriter("inventory.csv");
	        
	        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
	        		.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
	                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
	                .withOrderedResults(false)
	                .build();
	        beanToCsv.write(list);
	        writer.close();
	    } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * @return
	 */
	private static List<Inventory> inventoryList() {
		List<Inventory> listInventory = new ArrayList<>();
		Inventory dbInventory = new Inventory();
		dbInventory.setLanguageId("EN");
		dbInventory.setCompanyCodeId("1000");
		dbInventory.setPlantId("1001");
		dbInventory.setWarehouseId("110");
		dbInventory.setItemCode("21307001176");
		dbInventory.setPackBarcodes("TV00267922");
		dbInventory.setStorageBin("GG1GH05A05");
		dbInventory.setStockTypeId(1L);
		dbInventory.setSpecialStockIndicatorId(1L);
		dbInventory.setBinClassId(1L);
		dbInventory.setDescription("RESETTABLE COMBINATION PADLOCK 2-3/8' SHKL BRS671-49202");
		dbInventory.setInventoryQuantity(1D);
		dbInventory.setInventoryUom("PIECE");
		dbInventory.setDeletionIndicator(0L);
		dbInventory.setCreatedBy("admin");
		listInventory.add(dbInventory);

		Inventory dbInventory1 = new Inventory();
		dbInventory1.setLanguageId("EN");
		dbInventory1.setCompanyCodeId("1000");
		dbInventory1.setPlantId("1001");
		dbInventory1.setWarehouseId("110");
		dbInventory1.setItemCode("203012146");
		dbInventory1.setPackBarcodes("1060010346");
		dbInventory1.setStorageBin("GG1GH05A05");
		dbInventory1.setStockTypeId(1L);
		dbInventory1.setSpecialStockIndicatorId(1L);
		dbInventory1.setBinClassId(1L);
		dbInventory1.setDescription("SHREDDER 5in1");
		dbInventory1.setInventoryQuantity(2D);
		dbInventory1.setInventoryUom("PIECE");
		dbInventory1.setDeletionIndicator(0L);
		dbInventory1.setCreatedBy("admin");
		listInventory.add(dbInventory1);

		Inventory dbInventory2 = new Inventory();
		dbInventory2.setLanguageId("EN");
		dbInventory2.setCompanyCodeId("1000");
		dbInventory2.setPlantId("1001");
		dbInventory2.setWarehouseId("110");
		dbInventory2.setItemCode("203013219");
		dbInventory2.setPackBarcodes("202101200167");
		dbInventory2.setStorageBin("GG1GH05A05");
		dbInventory2.setStockTypeId(1L);
		dbInventory2.setSpecialStockIndicatorId(1L);
		dbInventory2.setBinClassId(1L);
		dbInventory2.setDescription("5PC SOFA SET (PART 1/2)");
		dbInventory2.setInventoryQuantity(1D);
		dbInventory2.setInventoryUom("PIECE");
		dbInventory2.setDeletionIndicator(0L);
		dbInventory2.setCreatedBy("admin");
		listInventory.add(dbInventory2);
		return listInventory;
	}
}