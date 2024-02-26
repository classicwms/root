USE WMS_ALMDEV
GO

CREATE OR ALTER PROCEDURE staging_line_update_proc 
	@companyCodeId nvarchar(5), 
	@plantId nvarchar(5), 
	@languageId nvarchar(5),
	@warehouseId nvarchar(5), 
	@refDocNumber nvarchar(25),
	@preInboundNo nvarchar(25), 
	@itmCode nvarchar(50), 
	@manufacturerName nvarchar(20),
	@lineNumber bigint,
	@updatedOn DATETIME,
	@recAcceptQty float,
	@recDamageQty float
AS
BEGIN
	DECLARE @NEW_ACCEPT_QTY float, @NEW_DAMAGE_QTY float, @ACCEPT_QTY float, @DAMAGE_QTY float, @GR_STATUS_ID bigint, @GR_STATUS_TEXT nvarchar(30)

	SELECT @ACCEPT_QTY = rec_accept_qty, @DAMAGE_QTY = rec_damage_qty
	FROM tblstagingline
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND WH_ID = @warehouseId AND
	REF_DOC_NO = @refDocNumber AND PRE_IB_NO = @preInboundNo AND
	ITM_CODE = @itmcode AND MFR_NAME = @manufacturerName AND IB_LINE_NO = @lineNumber AND IS_DELETED = 0
	
	SELECT @GR_STATUS_ID = status_id, @GR_STATUS_TEXT = status_text
	FROM tblgrline
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND WH_ID = @warehouseId AND
	REF_DOC_NO = @refDocNumber AND PRE_IB_NO = @preInboundNo AND
	ITM_CODE = @itmcode AND MFR_NAME = @manufacturerName AND IB_LINE_NO = @lineNumber AND IS_DELETED = 0

	IF @ACCEPT_QTY IS NULL
	BEGIN
		SET @ACCEPT_QTY = 0
	END	

	IF @DAMAGE_QTY IS NULL
	BEGIN
		SET @DAMAGE_QTY = 0
	END	

	BEGIN
		SET @NEW_ACCEPT_QTY = (@ACCEPT_QTY + @recAcceptQty)
		SET @NEW_DAMAGE_QTY = (@DAMAGE_QTY + @recDamageQty)
	END

	UPDATE tblstagingline
	SET REC_ACCEPT_QTY = @NEW_ACCEPT_QTY, REC_DAMAGE_QTY = @NEW_DAMAGE_QTY, 
	ST_UTD_ON = @updatedOn, ST_CNF_ON = @updatedOn, 
	STATUS_ID = @GR_STATUS_ID, STATUS_TEXT = @GR_STATUS_TEXT
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND WH_ID = @warehouseId AND
	REF_DOC_NO = @refDocNumber AND PRE_IB_NO = @preInboundNo AND
	ITM_CODE = @itmcode AND MFR_NAME = @manufacturerName AND IB_LINE_NO = @lineNumber AND IS_DELETED = 0
END	
GO
