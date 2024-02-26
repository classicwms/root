USE WMS_ALMDEV
GO

-- String companyCodeId, String plantId, String languageId, String warehouseId
-- String itemCode, String manufacturerName, Date updatedOn, 
-- String refDocNumber, String preOutboundNo, Long statusId, String statusDescription,
CREATE OR ALTER PROCEDURE pickupheader_status_update_proc 
	@companyCodeId nvarchar(5), 
	@plantId nvarchar(5), 
	@languageId nvarchar(5),
	@warehouseId nvarchar(5), 
	@refDocNumber nvarchar(25),
	@preOutboundNo nvarchar(25), 
	@itmCode nvarchar(50), 
	@manufacturerName nvarchar(20),
	@partnerCode nvarchar(20),
	@pickupNumber nvarchar(25),
	@statusId bigint,
	@statusDescription nvarchar(50),
	@updatedBy nvarchar(50),
	@updatedOn DATETIME
		
AS
BEGIN
	UPDATE tblpickupheader
	SET STATUS_ID = @statusId, STATUS_TEXT = @statusDescription, 
	PICK_UTD_ON = @updatedOn, PICK_CNF_ON = @updatedOn, PICK_UTD_BY = @updatedBy, PICK_CNF_BY = @updatedBy
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND WH_ID = @warehouseId AND 
			REF_DOC_NO = @refDocNumber AND PRE_OB_NO = @preOutboundNo AND ITM_CODE = @itmCode AND 
			MFR_NAME = @manufacturerName AND PU_NO = @pickupNumber AND PARTNER_CODE = @partnerCode AND IS_DELETED = 0
END			
GO