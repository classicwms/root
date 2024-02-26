USE WMS_ALMDEV
GO

-- String companyCodeId, String plantId, String languageId, String warehouseId
-- String refDocNumber, String preInboundNo, String goodsReceiptNo
-- Long statusId, String statusDescription, Date updatedOn
CREATE OR ALTER PROCEDURE grheader_status_update_proc 
	@companyCodeId nvarchar(5), 
	@plantId nvarchar(5), 
	@languageId nvarchar(5), 
	@warehouseId nvarchar(5), 
	@refDocNumber nvarchar(25),
	@preInboundNo nvarchar(25), 
	@goodsReceiptNo nvarchar(25),
	@statusId bigint,
	@statusDescription nvarchar(50),
	@updatedOn DATETIME
	
AS
BEGIN
	DECLARE @ROW_COUNT int, @ROW_COUNT_STATUS_ID_17 int, @IS_STATUS_ID_17 int = 0

	SELECT @ROW_COUNT = COUNT(REF_DOC_NO) FROM tblstagingline 
	WHERE C_ID = @companyCodeId AND 
	PLANT_ID = @plantId AND 
	LANG_ID = @languageId AND 
	WH_ID = @warehouseId AND 
	REF_DOC_NO = @refDocNumber AND
	PRE_IB_NO = @preInboundNo AND
	IS_DELETED = 0 
	GROUP BY REF_DOC_NO

	--SELECT @ROW_COUNT_STATUS_ID_17 = COUNT(REF_DOC_NO) FROM tblgrline 
	--WHERE C_ID = @companyCodeId AND 
	--PLANT_ID = @plantId AND 
	--LANG_ID = @languageId AND 
	--WH_ID = @warehouseId AND 
	--REF_DOC_NO = @refDocNumber AND
	--PRE_IB_NO = @preInboundNo AND
	--STATUS_ID = @statusId AND
	--IS_DELETED = 0 
	--GROUP BY REF_DOC_NO

	SELECT @ROW_COUNT_STATUS_ID_17 = COUNT(REF_DOC_NO) FROM (
	SELECT SUM(GR_QTY) GRQTY,ORD_QTY,REF_DOC_NO,IB_LINE_NO,ITM_CODE,MFR_NAME FROM TBLGRLINE 
	WHERE C_ID = @companyCodeId AND 
	PLANT_ID = @plantId AND 
	LANG_ID = @languageId AND 
	WH_ID = @warehouseId AND 
	REF_DOC_NO = @refDocNumber AND
	PRE_IB_NO = @preInboundNo AND
	--STATUS_ID = @statusId AND
	IS_DELETED = 0 
	GROUP BY IB_LINE_NO,ITM_CODE,MFR_NAME,REF_DOC_NO,ORD_QTY 
	HAVING SUM(GR_QTY) = ORD_QTY
	) X GROUP BY REF_DOC_NO

	IF @ROW_COUNT = @ROW_COUNT_STATUS_ID_17
	BEGIN
		SET @IS_STATUS_ID_17 = 1
	END	

	IF @IS_STATUS_ID_17 = 1
	BEGIN
		UPDATE tblgrheader
			SET STATUS_ID = @statusId,
			STATUS_TEXT = @statusDescription,
			GR_UTD_ON = @updatedOn,
			GR_CNF_ON = @updatedOn
			WHERE C_ID = @companyCodeId AND 
				PLANT_ID = @plantId AND 
				LANG_ID = @languageId AND 
				WH_ID = @warehouseId AND 
				REF_DOC_NO = @refDocNumber AND
				PRE_IB_NO = @preInboundNo AND
				IS_DELETED = 0
	END			
END				
GO