USE WMS_ALMDEV
GO

-- String companyCodeId, String plantId, String languageId, String warehouseId
-- String itemCode, String manufacturerName, Long lineNo, Date updatedOn, 
-- String refDocNumber, String preInboundNo, Long statusId, String statusDescription,
CREATE OR ALTER PROCEDURE inboundline_status_update_proc 
	@companyCodeId nvarchar(5), 
	@plantId nvarchar(5), 
	@languageId nvarchar(5),
	@warehouseId nvarchar(5), 
	@refDocNumber nvarchar(25),
	@preInboundNo nvarchar(25), 
	@itmCode nvarchar(50), 
	@manufacturerName nvarchar(20),
	@lineNumber bigint,
	@statusId bigint,
	@statusDescription nvarchar(50),
	@updatedOn DATETIME
		
AS
BEGIN
	UPDATE tblinboundline
	SET STATUS_ID = @statusId, STATUS_TEXT = @statusDescription, UTD_ON = @updatedOn, IB_CNF_ON = @updatedOn
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND WH_ID = @warehouseId AND 
			REF_DOC_NO = @refDocNumber AND PRE_IB_NO = @preInboundNo AND ITM_CODE = @itmCode AND 
			MFR_NAME = @manufacturerName AND IB_LINE_NO = @lineNumber AND IS_DELETED = 0
END			
GO