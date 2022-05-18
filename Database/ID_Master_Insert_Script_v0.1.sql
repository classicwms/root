
-- WMS - ID Master Insert scripts
----------------------------------------------------tblverticalid--------------------------------------------------

INSERT INTO `WMS`.`tblverticalid` (`VERT_ID`,`LANG_ID`,`VERTICAL`,`REMARK`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1,'EN','Retail','Test company1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblverticalid` (`VERT_ID`,`LANG_ID`,`VERTICAL`,`REMARK`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (2,'AB','Manufacturing','Test company2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblcompanyid--------------------------------------------------

INSERT INTO `WMS`.`tblcompanyid` (`C_ID`,`LANG_ID`,`C_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,'EN','Test company1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblcompanyid` (`C_ID`,`LANG_ID`,`C_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (2000,'AB','Test company2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblwarehouseid--------------------------------------------------

INSERT INTO `WMS`.`tblwarehouseid` (`C_ID`,`WH_ID`,`LANG_ID`,`PLANT_ID`,`WH_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,'WX1','EN',1001,'Central warehouse',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblwarehouseid` (`C_ID`,`WH_ID`,`LANG_ID`,`PLANT_ID`,`WH_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,'WX2','EN',1002,'Warehouse hub 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblplantid--------------------------------------------------

INSERT INTO `WMS`.`tblplantid` (`C_ID`,`PLANT_ID`,`LANG_ID`,`PLANT_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'EN','Plant 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblplantid` (`C_ID`,`PLANT_ID`,`LANG_ID`,`PLANT_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1002,'EN','Plant 2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblfloorid--------------------------------------------------

INSERT INTO `WMS`.`tblfloorid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`LANG_ID`,`FL_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL01','EN','Floor 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblfloorid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`LANG_ID`,`FL_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (2000,1002,'WX2','FL02','EN','Floor 2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstoragesectionid--------------------------------------------------

INSERT INTO `WMS`.`tblstorageSectionid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`ST_SEC`,`LANG_ID`,`ST_SEC_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL01',1,'SEC01','EN','Trading section',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstorageSectionid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`ST_SEC`,`LANG_ID`,`ST_SEC_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (2000,1002,'WX2','FL02',2,'SEC02','EN','FG Section',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

----------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblrowid----------------------------------------------------------------

INSERT INTO `WMS`.`tblrowid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`ROW_ID`,`LANG_ID`,`ROW_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL01',1,'R01','EN','Row 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblrowid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`ROW_ID`,`LANG_ID`,`ROW_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL02',1,'R02','EN','Row 2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblaisleid----------------------------------------------------------------

INSERT INTO `WMS`.`tblaisleid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`AISLE_ID`,`LANG_ID`,`AISLE_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL01',1,'A01','EN','Aisle 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblaisleid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`AISLE_ID`,`LANG_ID`,`AISLE_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL02',1,'A02','EN','Aisle 2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblspanid----------------------------------------------------------------

INSERT INTO `WMS`.`tblspanid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`AISLE_ID`,`ROW_ID`,`SPAN_ID`,`LANG_ID`,`SPAN_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL01',1,'A01','R01','S01','EN','Span 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblspanid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`AISLE_ID`,`ROW_ID`,`SPAN_ID`,`LANG_ID`,`SPAN_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL02',1,'A02','R02','S02','EN','Span 2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

----------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblshelfid----------------------------------------------------------------

INSERT INTO `WMS`.`tblshelfid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`AISLE_ID`,`ROW_ID`,`SPAN_ID`,`SHELF_ID`,`LANG_ID`,`SHELF_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL01',1,'A01','R01','S01','L01','EN','Shelf 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblshelfid` (`C_ID`,`PLANT_ID`,`WH_ID`,`FL_ID`,`ST_SEC_ID`,`AISLE_ID`,`ROW_ID`,`SPAN_ID`,`SHELF_ID`,`LANG_ID`,`SHELF_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','FL02',1,'A02','R02','S02','L02','EN','Shelf 2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstorageclassid----------------------------------------------------------------

INSERT INTO `WMS`.`tblstorageclassid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ST_CL_ID`,`LANG_ID`,`ST_CL_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Normal storage',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstorageclassid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ST_CL_ID`,`LANG_ID`,`ST_CL_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Cold Storage',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstoragetypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblstoragetypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ST_CL_ID`,`ST_TYP_ID`,`LANG_ID`,`ST_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,'EN','Capacaity based storage',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstoragetypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ST_CL_ID`,`ST_TYP_ID`,`LANG_ID`,`ST_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,2,'EN','Bulk storage',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstoragebintypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblstoragebintypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ST_CL_ID`,`ST_TYP_ID`,`ST_BIN_TYP_ID`,`LANG_ID`,`ST_BIN_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,1,'EN','BIN TYPE1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstoragebintypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ST_CL_ID`,`ST_TYP_ID`,`ST_BIN_TYP_ID`,`LANG_ID`,`ST_BIN_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,2,2,'EN','BIN TYPE2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbldoorid----------------------------------------------------------------

INSERT INTO `WMS`.`tbldoorid` (`C_ID`,`PLANT_ID`,`WH_ID`,`DOOR_ID`,`LANG_ID`,`DOOR_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','D01','EN','Door 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbldoorid` (`C_ID`,`PLANT_ID`,`WH_ID`,`DOOR_ID`,`LANG_ID`,`DOOR_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX2','D01','EN','Door 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

----------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblprocessid----------------------------------------------------------------

INSERT INTO `WMS`.`tblprocessid` (`C_ID`,`PLANT_ID`,`WH_ID`,`PROCESS_ID`,`LANG_ID`,`PROCESS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Item receipts',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblprocessid` (`C_ID`,`PLANT_ID`,`WH_ID`,`PROCESS_ID`,`LANG_ID`,`PROCESS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Putaway',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblusertypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblusertypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`USR_TYP_ID`,`LANG_ID`,`USR_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','PDA',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblusertypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`USR_TYP_ID`,`LANG_ID`,`USR_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','PORTAL',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbllanguageid----------------------------------------------------------------

INSERT INTO `WMS`.`tbllanguageid` (`LANG_ID`,`LANG_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('EN','English',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbllanguageid` (`LANG_ID`,`LANG_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('AB','Arabic',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

----------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbluomid----------------------------------------------------------------

INSERT INTO `WMS`.`tbluomid` (`C_ID`,`UOM_ID`,`LANG_ID`,`UOM_TEXT`,`UOM_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,'PC','EN','PIECE','STANDARD',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbluomid` (`C_ID`,`UOM_ID`,`LANG_ID`,`UOM_TEXT`,`UOM_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,'M','EN','METER','LENGTH',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblmoduleid----------------------------------------------------------------

INSERT INTO `WMS`.`tblmoduleid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MOD_ID`,`LANG_ID`,`MODULE_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Enterprise Setup',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblmoduleid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MOD_ID`,`LANG_ID`,`MODULE_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Master',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbladhocmoduleid----------------------------------------------------------------

INSERT INTO `WMS`.`tbladhocmoduleid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MOD_ID`,`ADHOC_MOD_ID`,`LANG_ID`,`ADHOC_MODULE_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,5,'EN','Doors',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbladhocmoduleid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MOD_ID`,`ADHOC_MOD_ID`,`LANG_ID`,`ADHOC_MODULE_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,9,'EN','Variant Management',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbllevelid----------------------------------------------------------------

INSERT INTO `WMS`.`tbllevelid` (`C_ID`,`PLANT_ID`,`WH_ID`,`LEVEL_ID`,`LANG_ID`,`LEVEL`,`LEVEL_REF`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','ITEM TYPE','FERT',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbllevelid` (`C_ID`,`PLANT_ID`,`WH_ID`,`LEVEL_ID`,`LANG_ID`,`LEVEL`,`LEVEL_REF`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','ITEM TYPE','RM',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblpalletizationlevelid----------------------------------------------------------------

INSERT INTO `WMS`.`tblpalletizationlevelid` (`C_ID`,`PLANT_ID`,`WH_ID`,`PAL_LVL_ID`,`PAL_LVL`,`LANG_ID`,`PAL_LVL_REF`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'LEVEL1','EN','PALLET/CASE/ITEM',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblpalletizationlevelid` (`C_ID`,`PLANT_ID`,`WH_ID`,`PAL_LVL_ID`,`PAL_LVL`,`LANG_ID`,`PAL_LVL_REF`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'LEVEL2','EN','CASE/ITEM',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------------


----------------------------------------------------tblapprovalprocessid----------------------------------------------------------------

INSERT INTO `WMS`.`tblapprovalprocessid` (`C_ID`,`PLANT_ID`,`WH_ID`,`APP_PROCESS_ID`,`LANG_ID`,`APP_PROCESS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','SCRAP',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblapprovalprocessid` (`C_ID`,`PLANT_ID`,`WH_ID`,`APP_PROCESS_ID`,`LANG_ID`,`APP_PROCESS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','WRITEOFF',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblcurrency_id----------------------------------------------------------------

INSERT INTO `WMS`.`tblcurrencyid` (`CURR_ID`,`LANG_ID`,`CURR_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('KWD','EN','Kuwait Dinar',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblcurrencyid` (`CURR_ID`,`LANG_ID`,`CURR_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('USD','EN','US Dollar',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblbarcodetypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblbarcodetypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`BAR_TYP_ID`,`LANG_ID`,`BAR_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','1D',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblbarcodetypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`BAR_TYP_ID`,`LANG_ID`,`BAR_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','2D',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblbarcodesubtypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblbarcodesubtypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`BAR_TYP_ID`,`BAR_SUB_ID`,`LANG_ID`,`BAR_SUB_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,'EN','GS1 (128)',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblbarcodesubtypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`BAR_TYP_ID`,`BAR_SUB_ID`,`LANG_ID`,`BAR_SUB_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,2,'EN','EAN13',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

----------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblemployeeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblemployeeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`EMP_ID`,`LANG_ID`,`EMP_NM`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','EMP001','EN','Abdul',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblemployeeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`EMP_ID`,`LANG_ID`,`EMP_NM`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','EMP002','EN','Noufal',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstrategyid----------------------------------------------------------------

INSERT INTO `WMS`.`tblstrategyid` (`C_ID`,`PLANT_ID`,`WH_ID`,`STR_TYP_ID`,`ST_NO`,`LANG_ID`,`STR_TYP_TEXT`,`ST_NO_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,'EN','Putaway','Fixed Bin',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstrategyid` (`C_ID`,`PLANT_ID`,`WH_ID`,`STR_TYP_ID`,`ST_NO`,`LANG_ID`,`STR_TYP_TEXT`,`ST_NO_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,2,'EN','Picking','FIFO',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblmenuid----------------------------------------------------------------

INSERT INTO `WMS`.`tblmenuid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MENU_ID`,`SUB_MENU_ID`,`AUT_OBJ_ID`,`AUT_OBJ_VALUE`,`LANG_ID`,`MENU_TEXT`,`SUB_MENU_TEXT`,`AUT_OBJ`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,1,1000,'EN','INBOUND','Item receipts','Company code',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblmenuid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MENU_ID`,`SUB_MENU_ID`,`AUT_OBJ_ID`,`AUT_OBJ_VALUE`,`LANG_ID`,`MENU_TEXT`,`SUB_MENU_TEXT`,`AUT_OBJ`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,2,2,'WX1','EN','INBOUND','Putaway','CWarehouse',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblbinsectionid----------------------------------------------------------------

INSERT INTO `WMS`.`tblbinsectionid` (`C_ID`,`PLANT_ID`,`WH_ID`,`BIN_SECTION_ID`,`LANG_ID`,`BIN_SEC`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','A01-S1-L1-001-01',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbldateformatid----------------------------------------------------------------

INSERT INTO `WMS`.`tbldateformatid` (`C_ID`,`PLANT_ID`,`WH_ID`,`DATE_FOR_ID`,`LANG_ID`,`DATE_FORMAT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','DD:MM:YYYY',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbldateformatid` (`C_ID`,`PLANT_ID`,`WH_ID`,`DATE_FOR_ID`,`LANG_ID`,`DATE_FORMAT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','YYYY:MM:DD',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbldecimalnotationid----------------------------------------------------------------

INSERT INTO `WMS`.`tbldecimalnotationid` (`C_ID`,`PLANT_ID`,`WH_ID`,`DEC_NOT_ID`,`LANG_ID`,`DECIMAL_NOTATION`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN',1234567.89,0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbldecimalnotationid` (`C_ID`,`PLANT_ID`,`WH_ID`,`DEC_NOT_ID`,`LANG_ID`,`DECIMAL_NOTATION`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN',1234567.89,0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstatusmessagesid----------------------------------------------------------------

INSERT INTO `WMS`.`tblstatusmessagesid` (`MESSAGE_ID`,`LANG_ID`,`MESSAGE_TYP`,`MESSAGE_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1,'EN','W','lanuage ID created successfully',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstatusmessagesid` (`MESSAGE_ID`,`LANG_ID`,`MESSAGE_TYP`,`MESSAGE_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (2,'EN','E','Capacity Not available',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblwarehousetypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblwarehousetypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`WH_TYP_ID`,`LANG_ID`,`WH_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Integration',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblwarehousetypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`WH_TYP_ID`,`LANG_ID`,`WH_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Standalone',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblmovementtypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblmovementtypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MVT_TYP_ID`,`LANG_ID`,`MVT_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','INBOUND',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblmovementtypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MVT_TYP_ID`,`LANG_ID`,`MVT_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','OUTBOUND',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblsubmovementtypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblsubmovementtypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MVT_TYP_ID`,`SUB_MVT_TYP_ID`,`LANG_ID`,`SUB_MVT_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,'EN','Goods Receipt',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblsubmovementtypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`MVT_TYP_ID`,`SUB_MVT_TYP_ID`,`LANG_ID`,`SUB_MVT_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,2,'EN','PUTAWAY',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbltransfertypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tbltransfertypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`TR_TYP_ID`,`LANG_ID`,`TR_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','BIN TO BIN',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbltransfertypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`TR_TYP_ID`,`LANG_ID`,`TR_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','WAREHOUSE TO WAREHOUSE',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstocktypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblstocktypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`STCK_TYP_ID`,`LANG_ID`,`STCK_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','ON HAND',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstocktypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`STCK_TYP_ID`,`LANG_ID`,`STCK_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','CONSIGNMENT',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblspecialstockindicatorid----------------------------------------------------------------

INSERT INTO `WMS`.`tblspecialstockindicatorid` (`C_ID`,`PLANT_ID`,`WH_ID`,`STCK_TYP_ID`,`SP_ST_IND_ID`,`LANG_ID`,`SP_ST_IND_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,'EN','BLOCK',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblspecialstockindicatorid` (`C_ID`,`PLANT_ID`,`WH_ID`,`STCK_TYP_ID`,`SP_ST_IND_ID`,`LANG_ID`,`SP_ST_IND_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,2,'EN','RESERVED',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblhandling_equipment_id----------------------------------------------------------------

INSERT INTO `WMS`.`tblhandlingequipmentid` (`C_ID`,`PLANT_ID`,`WH_ID`,`HE_ID`,`HE_NO`,`LANG_ID`,`HE_NO_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'HE10001','EN','Reach Truck 1 TON',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

----------------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblhandling_unit_id----------------------------------------------------------------

INSERT INTO `WMS`.`tblhandlingunitid` (`C_ID`,`PLANT_ID`,`WH_ID`,`HU_ID`,`HU_NO`,`LANG_ID`,`HU_NO_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'HU1001','EN','2*2 METER PALLET',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbldock_id----------------------------------------------------------------

INSERT INTO `WMS`.`tbldockid` (`C_ID`,`PLANT_ID`,`WH_ID`,`DK_ID`,`LANG_ID`,`DK_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','DK01','EN','Dock 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbldockid` (`C_ID`,`PLANT_ID`,`WH_ID`,`DK_ID`,`LANG_ID`,`DK_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','DK02','EN','Dock 2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblwork_center_id----------------------------------------------------------------

INSERT INTO `WMS`.`tblworkcenterid` (`C_ID`,`PLANT_ID`,`WH_ID`,`WRK_CTR_ID`,`LANG_ID`,`WRK_CTR_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','W01','EN','quality station 1',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblworkcenterid` (`C_ID`,`PLANT_ID`,`WH_ID`,`WRK_CTR_ID`,`LANG_ID`,`WRK_CTR_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1','W02','EN','quality station 2',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblcyclecounttypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblcyclecounttypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`CC_TYP_ID`,`LANG_ID`,`CC_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Perpetual',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblcyclecounttypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`CC_TYP_ID`,`LANG_ID`,`CC_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Annual',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblreturntypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblreturntypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`RETURN_TYP_ID`,`LANG_ID`,`RETURN_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Inbound',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblreturntypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`RETURN_TYP_ID`,`LANG_ID`,`RETURN_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Outbound',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbloutboundordertypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tbloutboundordertypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`OB_ORD_TYP_ID`,`LANG_ID`,`ROB_ORD_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','REPLENISHMENT',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbloutboundordertypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`OB_ORD_TYP_ID`,`LANG_ID`,`ROB_ORD_TYP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','ONLINE',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tbloutboundorderstatusid----------------------------------------------------------------

INSERT INTO `WMS`.`tbloutboundorderstatusid` (`C_ID`,`PLANT_ID`,`WH_ID`,`OB_ORD_STATUS_ID`,`LANG_ID`,`OB_ORD_STATUS_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','OPEN',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tbloutboundorderstatusid` (`C_ID`,`PLANT_ID`,`WH_ID`,`OB_ORD_STATUS_ID`,`LANG_ID`,`OB_ORD_STATUS_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','ALLOCATED',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblinboundordertypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblinboundordertypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`IB_ORD_TYP_ID`,`LANG_ID`,`IB_ORD_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','STANDARD',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblinboundordertypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`IB_ORD_TYP_ID`,`LANG_ID`,`IB_ORD_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','CONSIGNMENT',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblinboundorderstatusid----------------------------------------------------------------

INSERT INTO `WMS`.`tblinboundorderstatusid` (`C_ID`,`PLANT_ID`,`WH_ID`,`IB_ORD_STATUS_ID`,`LANG_ID`,`IB_ORD_STATUS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','OPEN',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblinboundorderstatusid` (`C_ID`,`PLANT_ID`,`WH_ID`,`IB_ORD_STATUS_ID`,`LANG_ID`,`IB_ORD_STATUS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','GR CREATED',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblbinclassid----------------------------------------------------------------

INSERT INTO `WMS`.`tblbinclassid` (`C_ID`,`PLANT_ID`,`WH_ID`,`BIN_CL_ID`,`LANG_ID`,`BIN_CLASS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Storage Bin',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblbinclassid` (`C_ID`,`PLANT_ID`,`WH_ID`,`BIN_CL_ID`,`LANG_ID`,`BIN_CLASS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Interim Bin(GR)',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblcontroltypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblcontroltypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`CTRL_TYP_ID`,`LANG_ID`,`CTRL_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Block',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblcontroltypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`CTRL_TYP_ID`,`LANG_ID`,`CTRL_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Release',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblapprovalid----------------------------------------------------------------

INSERT INTO `WMS`.`tblapprovalid` (`C_ID`,`PLANT_ID`,`WH_ID`,`APP_PROCESS_ID`,`LANG_ID`,`APP_LVL`,`APP_CODE`,`APP_PROCESS`,`APP_NM`,`DESIGNATION`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN',1,1,'SCRAP','RASHID','SuperVisor',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblapprovalid` (`C_ID`,`PLANT_ID`,`WH_ID`,`APP_PROCESS_ID`,`LANG_ID`,`APP_LVL`,`APP_CODE`,`APP_PROCESS`,`APP_NM`,`DESIGNATION`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN',1,2,'SCRAP','SATTAR','SuperVisor',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblcountryid----------------------------------------------------------------

INSERT INTO `WMS`.`tblcountryid` (`Country_ID`,`LANG_ID`,`COUNTRY_NM`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('IN','EN','INDIA',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblcountryid` (`Country_ID`,`LANG_ID`,`COUNTRY_NM`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('KW','EN','KUWAIT',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

----------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstateid----------------------------------------------------------------

INSERT INTO `WMS`.`tblstateid` (`COUNTRY_ID`,`STATE_ID`,`LANG_ID`,`STATE_NM`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('IN','TN','EN','TamilNadu',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstateid` (`COUNTRY_ID`,`STATE_ID`,`LANG_ID`,`STATE_NM`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('KW','HW','EN','Hawally',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblcityid----------------------------------------------------------------

INSERT INTO `WMS`.`tblcityid` (`COUNTRY_ID`,`STATE_ID`,`CITY_ID`,`ZIP_CD`,`LANG_ID`,`CITY_NM`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('IN','TN','CH',600062,'EN','Chennai',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblcityid` (`COUNTRY_ID`,`STATE_ID`,`CITY_ID`,`ZIP_CD`,`LANG_ID`,`CITY_NM`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('KW','HW','MQ',430001,'EN','Mirqab',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

--------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblvariantid----------------------------------------------------------------

INSERT INTO `WMS`.`tblvariantid` (`C_ID`,`PLANT_ID`,`WH_ID`,`VAR_ID`,`VAR_TYP`,`VAR_SUB_ID`,`VAR_ID_TEXT`,`VAR_SUB_ID_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'ATTRIBUTE',1,'COLOUR','RED',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblvariantid` (`C_ID`,`PLANT_ID`,`WH_ID`,`VAR_ID`,`VAR_TYP`,`VAR_SUB_ID`,`VAR_ID_TEXT`,`VAR_SUB_ID_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'ATTRIBUTE',2,'COLOUR','BLUE',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblitemtypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblitemtypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ITM_TYPE_ID`,`ITM_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'FERT',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblitemtypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ITM_TYPE_ID`,`ITM_TYP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'TRAD',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblitemgroupid----------------------------------------------------------------

INSERT INTO `WMS`.`tblitemgroupid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ITM_TYPE_ID`,`ITM_GRP_ID`,`IMT_GRP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,1,'GARDEN',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblitemgroupid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ITM_TYPE_ID`,`ITM_GRP_ID`,`IMT_GRP`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,2,'KITCHEN',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblsubitemgroupid----------------------------------------------------------------

INSERT INTO `WMS`.`tblsubitemgroupid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ITM_TYP_ID`,`ITM_GRP_ID`,`SUB_ITM_GRP_ID`,`SUB_ITM_GRP`,`LANG_ID`,`SUB_ITM_GRP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,2,1,'GRILL','EN','GRILL ACCESSORIES',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblsubitemgroupid` (`C_ID`,`PLANT_ID`,`WH_ID`,`ITM_TYP_ID`,`ITM_GRP_ID`,`SUB_ITM_GRP_ID`,`SUB_ITM_GRP`,`LANG_ID`,`SUB_ITM_GRP_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,2,2,'FRYPAN','EN','FRYPAN',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblstatusid----------------------------------------------------------------

INSERT INTO `WMS`.`tblstatusid` (`LANG_ID`,`C_ID`,`PLANT_ID`,`WH_ID`,`STATUS_ID`,`STATUS_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('EN',1000,1001,'WX1',1,'Open',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblstatusid` (`LANG_ID`,`C_ID`,`PLANT_ID`,`WH_ID`,`STATUS_ID`,`STATUS_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES ('EN',1000,1001,'WX1',2,'Order Created',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

---------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblrefdoctypeid----------------------------------------------------------------

INSERT INTO `WMS`.`tblrefdoctypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`REFDOCYTYPE_ID`,`REFDOC_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'ASN',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblrefdoctypeid` (`C_ID`,`PLANT_ID`,`WH_ID`,`REFDOCYTYPE_ID`,`REFDOC_TEXT`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'PO',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblcontrolprocessid----------------------------------------------------------------

INSERT INTO `WMS`.`tblcontrolprocessid` (`C_ID`,`PLANT_ID`,`WH_ID`,`CTRL_PROCESS_ID`,`LANG_ID`,`CTRL_PROCESS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,'EN','Putaway',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblcontrolprocessid` (`C_ID`,`PLANT_ID`,`WH_ID`,`CTRL_PROCESS_ID`,`LANG_ID`,`CTRL_PROCESS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',2,'EN','Picking',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-----------------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblsublevelid----------------------------------------------------------------

INSERT INTO `WMS`.`tblsublevelid` (`C_ID`,`PLANT_ID`,`WH_ID`,`LEVEL_ID`,`SUB_LEVEL_ID`,`LANG_ID`,`SUB_LEVEL`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,'EN','ITEM TYPE',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblsublevelid` (`C_ID`,`PLANT_ID`,`WH_ID`,`LEVEL_ID`,`SUB_LEVEL_ID`,`LANG_ID`,`SUB_LEVEL`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,2,'EN','ITEM TYPE',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

-------------------------------------------------------------------------------------------------------------------------------------

----------------------------------------------------tblprocesssequenceid----------------------------------------------------------------

INSERT INTO `WMS`.`tblprocesssequenceid` (`C_ID`,`PLANT_ID`,`WH_ID`,`PROCESS_ID`,`SUB_PROCESS_SEQ_ID`,`LANG_ID`,`PROCESS`,`SUBPROCESS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,1,'EN','Inbound','Preinbound',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

INSERT INTO `WMS`.`tblprocesssequenceid` (`C_ID`,`PLANT_ID`,`WH_ID`,`PROCESS_ID`,`SUB_PROCESS_SEQ_ID`,`LANG_ID`,`PROCESS`,`SUBPROCESS`,`IS_DELETED`,`CTD_BY`,`CTD_ON`,`UTD_BY`,`UTD_ON`)
VALUES (1000,1001,'WX1',1,2,'EN','Inbound','Container Receipt',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',CURDATE());

------------------------------------------------------------------------------------------------------------------------------------------

