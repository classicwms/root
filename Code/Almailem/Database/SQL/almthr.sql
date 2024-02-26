USE WMS_ALMDEV
GO

CREATE OR ALTER PROCEDURE sp_thr
    @companyCodeId varchar(10),
    @plantId varchar(10),
    @languageId varchar(10),
    @warehouseId varchar(10),
    @itemCode varchar(255) = NULL,
	@manufacturerName varchar(255) = NULL,
	@openingStockDateFrom DATETIME,
    @openingStockDateTo DATETIME,
	@closingStockDateFrom DATETIME,
    @closingStockDateTo DATETIME
AS
BEGIN
    -- Validate the input parameters
    IF @companyCodeId IS NULL OR @companyCodeId = ''
    BEGIN
        RAISERROR('Invalid companyCodeId ', 16, 1)
        RETURN
    END
	
	IF @plantId IS NULL OR @plantId = ''
    BEGIN
        RAISERROR('Invalid plantId ', 16, 1)
        RETURN
    END
	
	IF @languageId IS NULL OR @languageId = ''
    BEGIN
        RAISERROR('Invalid languageId ', 16, 1)
        RETURN
    END
	
	IF @warehouseId IS NULL OR @warehouseId = ''
    BEGIN
        RAISERROR('Invalid warehouse id', 16, 1)
        RETURN
    END
	
	IF @openingStockDateFrom IS NULL OR @openingStockDateTo IS NULL
    BEGIN
        RAISERROR('Invalid date range', 16, 1)
        RETURN
    END
	
	IF @closingStockDateFrom IS NULL OR @closingStockDateTo IS NULL
    BEGIN
        RAISERROR('Invalid date range', 16, 1)
        RETURN
    END
    -- Set itemCode as Null when itemCode = 0 since Spring boot to stored procedure unable to send null value input
    IF @itemCode = '0'
	BEGIN
	SET @itemCode = NULL
	END

	-- Set manufacturerName as Null when manufacturerName = 0 since Spring boot to stored procedure unable to send null value input
    IF @manufacturerName = '0'
	BEGIN
	SET @manufacturerName = NULL
	END
	
		-- Create Temporary Table tbltransactionhistoryresults
	CREATE TABLE #THR
	(C_ID NVARCHAR(10),
	PLANT_ID NVARCHAR(10),
	LANG_ID NVARCHAR(10),
	WH_ID NVARCHAR(10),
	C_TEXT NVARCHAR(50),
	PLANT_TEXT NVARCHAR(50),
	WH_TEXT NVARCHAR(50),
	ITM_CODE NVARCHAR(200),
	MFR_NAME NVARCHAR(100),
	TEXT NVARCHAR(255),
	IS_OS_QTY FLOAT DEFAULT 0,
	PA_OS_QTY FLOAT DEFAULT 0,
	PI_OS_QTY FLOAT DEFAULT 0,
	IV_OS_QTY FLOAT DEFAULT 0,
	PA_CS_QTY FLOAT DEFAULT 0,
	PI_CS_QTY FLOAT DEFAULT 0,
	IV_CS_QTY FLOAT DEFAULT 0,
	IV_QTY FLOAT DEFAULT 0,
	PRIMARY KEY (C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_NAME));

	-- itemCode and Description from imbasicData1 to temp table 
	INSERT INTO #THR(C_ID,PLANT_ID,WH_ID,LANG_ID,ITM_CODE,TEXT,MFR_NAME,C_TEXT,PLANT_TEXT,WH_TEXT) 
	SELECT C_ID,PLANT_ID,WH_ID,LANG_ID,ITM_CODE,TEXT,MFR_PART,C_TEXT,PLANT_TEXT,WH_TEXT FROM TBLIMBASICDATA1 
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND IS_DELETED = 0 
	AND WH_ID = @warehouseId
	AND itm_code = ISNULL(@itemCode,itm_code)
	
	-- update the temp table with inventory qty and allocated qty from inventorystock table
	SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_NAME,INV_QTY,ALLOC_QTY,BIN_CL_ID 
	INTO #IVS FROM TBLINVENTORYSTOCK 
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND IS_DELETED = 0  
	AND BIN_CL_ID IN (1,7) 
	AND WH_ID=@warehouseId
	
	-- update the temp table with inventory qty and allocated qty from inventory table

	select max(inv_id) inventoryId into #inv from tblinventory 
    WHERE IS_DELETED = 0 group by itm_code,mfr_name,st_bin

	SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_NAME,INV_QTY,ALLOC_QTY,BIN_CL_ID,REF_FIELD_4 
	INTO #IV FROM TBLINVENTORY 
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND IS_DELETED = 0  
	AND BIN_CL_ID IN (1,7) 
	AND WH_ID=@warehouseId AND INV_ID IN (SELECT inventoryId from #inv)
	
	-- putaway line table to temp table pal
	SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_NAME,PA_CNF_QTY,PA_CTD_ON,STATUS_ID 
	INTO #PAL FROM TBLPUTAWAYLINE 
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND IS_DELETED = 0  
	AND STATUS_ID IN (24) 
	AND WH_ID=@warehouseId 
	AND PA_CTD_ON BETWEEN @openingStockDateFrom AND @closingStockDateTo
	
	-- pickup line table to temp table pul
	SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_NAME,PICK_CNF_QTY,PICK_CTD_ON,STATUS_ID 
	INTO #PUL FROM TBLPICKUPLINE 
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND IS_DELETED = 0  
	AND STATUS_ID IN (50,59) 
	AND WH_ID=@warehouseId 
	AND PICK_CTD_ON BETWEEN @openingStockDateFrom AND @closingStockDateTo

	-- inv movement table to temp table ivm
	SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_PART,MVT_QTY,IM_CTD_ON,MVT_TYP_ID,SUB_MVT_TYP_ID 
	INTO #IVM FROM TBLINVENTORYMOVEMENT 
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND IS_DELETED = 0  
	AND MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 
	AND WH_ID=@warehouseId AND IM_CTD_ON BETWEEN @openingStockDateFrom AND @closingStockDateTo
	
	-- IVS Temp Table to THR
	UPDATE TH SET TH.IS_OS_QTY = X.VALUE FROM #THR TH INNER JOIN
	(SELECT (SUM(COALESCE(INV_QTY,0)) + SUM(COALESCE(ALLOC_QTY,0))) VALUE, ITM_CODE, MFR_NAME, C_ID, PLANT_ID, WH_ID, LANG_ID FROM #IVS
	WHERE 
	C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND 
	BIN_CL_ID IN (1,7) AND 
	WH_ID=@warehouseId 
	GROUP BY ITM_CODE,MFR_NAME,C_ID,PLANT_ID,LANG_ID,WH_ID,LANG_ID) X ON 
	X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID AND X.ITM_CODE=TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME
	
	-- IV Temp Table to THR
	UPDATE TH SET TH.IV_QTY = X.VALUE FROM #THR TH INNER JOIN
	(SELECT SUM(REF_FIELD_4) VALUE, ITM_CODE, MFR_NAME, C_ID, PLANT_ID, WH_ID, LANG_ID FROM #IV
	WHERE 
	C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND 
	BIN_CL_ID IN (1,7) AND 
	WH_ID=@warehouseId 
	GROUP BY ITM_CODE,MFR_NAME,C_ID,PLANT_ID,LANG_ID,WH_ID,LANG_ID
	) X ON 
	X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID AND X.ITM_CODE=TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME
	
	-- PAL Temp Table to THR
	UPDATE TH SET TH.PA_OS_QTY = X.VALUE FROM #THR TH INNER JOIN
	(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE,MFR_NAME, C_ID, PLANT_ID, WH_ID, LANG_ID FROM #PAL 
	WHERE 
	C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND 
	STATUS_ID IN (24) AND 
	WH_ID=@warehouseId 
	AND PA_CTD_ON BETWEEN @openingStockDateFrom AND @openingStockDateTo 
	GROUP BY ITM_CODE,MFR_NAME,C_ID,PLANT_ID,LANG_ID,WH_ID,LANG_ID) X ON 
	X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID AND X.ITM_CODE=TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME

	-- PUL Temp Table to THR
	UPDATE TH SET TH.PI_OS_QTY = X.VALUE FROM #THR TH INNER JOIN
	(SELECT SUM(PICK_CNF_QTY) VALUE,ITM_CODE,MFR_NAME, C_ID, PLANT_ID, WH_ID, LANG_ID FROM #PUL 
	WHERE 
	C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND 
	STATUS_ID IN (50,59) AND
	WH_ID=@warehouseId 
	AND PICK_CTD_ON BETWEEN @openingStockDateFrom AND @openingStockDateTo 
	GROUP BY ITM_CODE,MFR_NAME,C_ID,PLANT_ID,LANG_ID,WH_ID,LANG_ID) X ON 
	X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID AND X.ITM_CODE=TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME

	-- IVM Temp Table to THR
	UPDATE TH SET TH.IV_OS_QTY = X.VALUE FROM #THR TH INNER JOIN
	(SELECT SUM(MVT_QTY) VALUE,ITM_CODE,MFR_PART, C_ID, PLANT_ID, WH_ID, LANG_ID FROM #IVM 
	WHERE 	
	C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND 
	MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND
	WH_ID=@warehouseId 
	AND IM_CTD_ON BETWEEN @openingStockDateFrom AND @openingStockDateTo 
	GROUP BY ITM_CODE,MFR_PART,C_ID,PLANT_ID,LANG_ID,WH_ID,LANG_ID) X ON 
	X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID AND X.ITM_CODE=TH.ITM_CODE AND X.MFR_PART = TH.MFR_NAME

	-- PAL Temp Table to THR (closingStock)
	UPDATE TH SET TH.PA_CS_QTY = X.VALUE FROM #THR TH INNER JOIN
	(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE,MFR_NAME, C_ID, PLANT_ID, WH_ID, LANG_ID FROM #PAL 
	WHERE 	
	C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND 
	STATUS_ID IN (24) AND
	WH_ID=@warehouseId 
	AND PA_CTD_ON BETWEEN @closingStockDateFrom AND @closingStockDateTo 
	GROUP BY ITM_CODE,MFR_NAME,C_ID,PLANT_ID,LANG_ID,WH_ID,LANG_ID) X ON 
	X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID AND X.ITM_CODE=TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME

	-- PUL Temp Table to THR (closingStock)
	UPDATE TH SET TH.PI_CS_QTY = X.VALUE FROM #THR TH INNER JOIN
	(SELECT SUM(PICK_CNF_QTY) VALUE,ITM_CODE,MFR_NAME, C_ID, PLANT_ID, WH_ID, LANG_ID FROM #PUL 
	WHERE 	
	C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND 
	STATUS_ID IN (50,59) AND
	WH_ID=@warehouseId 
	AND PICK_CTD_ON BETWEEN @closingStockDateFrom AND @closingStockDateTo 
	GROUP BY ITM_CODE,MFR_NAME,C_ID,PLANT_ID,LANG_ID,WH_ID,LANG_ID) X ON 
	X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID AND X.ITM_CODE=TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME

	-- IVM Temp Table to THR (closingStock)
	UPDATE TH SET TH.IV_CS_QTY = X.VALUE FROM #THR TH INNER JOIN
	(SELECT SUM(MVT_QTY) VALUE,ITM_CODE,MFR_PART, C_ID, PLANT_ID, WH_ID, LANG_ID FROM #IVM 
	WHERE 	 
	C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND 
	MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND
	WH_ID=@warehouseId 
	AND IM_CTD_ON BETWEEN @closingStockDateFrom AND @closingStockDateTo 
	GROUP BY ITM_CODE,MFR_PART,C_ID,PLANT_ID,LANG_ID,WH_ID,LANG_ID) X ON 
	X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID AND X.ITM_CODE=TH.ITM_CODE AND X.MFR_PART = TH.MFR_NAME
	
	TRUNCATE TABLE tbltransactionhistoryresults
	
	INSERT INTO tbltransactionhistoryresults(company_code_id,plant_id,language_id,warehouse_id,
	opening_stock,inbound_qty,outbound_qty,stock_adjustment_qty,system_inventory,item_code,description,C_TEXT,PLANT_TEXT,WH_TEXT,MFR_NAME,closing_stock,variance)

	(
	SELECT *, (closingStock - systemInventory) variance
	FROM
	(SELECT *,
	(openingStock+inboundQty+stockAdjustmentQty-outboundQty) closingStock
	FROM
	(SELECT
	c_id companyCodeId,
	plant_id plantId,
	lang_id languageId,
	wh_id warehouseId,
	((COALESCE(IS_OS_QTY,0)+COALESCE(PA_OS_QTY,0)+COALESCE(IV_OS_QTY,0))-COALESCE(PI_OS_QTY,0)) openingStock,
	COALESCE(PA_CS_QTY,0) inboundQty,
	COALESCE(PI_CS_QTY,0) outboundQty,
	COALESCE(IV_CS_QTY,0) stockAdjustmentQty,
	COALESCE(IV_QTY,0) systemInventory,
	ITM_CODE itemCode,
	TEXT description,
	C_TEXT,
	PLANT_TEXT,
	WH_TEXT,
	MFR_NAME
	FROM #THR) X) X1)
		
END

GO