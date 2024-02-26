USE WMS_ALMDEV
GO

-- String companyCodeId, String plantId, String languageId, String warehouseId
-- String itemCode, String manufacturerName, Long lineNo, Date updatedOn, 
-- String refDocNumber, String preOutboundNo, Long statusId, String statusDescription,
CREATE OR ALTER PROCEDURE outboundline_status_update_proc 
	@companyCodeId nvarchar(5), 
	@plantId nvarchar(5), 
	@languageId nvarchar(5),
	@warehouseId nvarchar(5), 
	@refDocNumber nvarchar(25),
	@preOutboundNo nvarchar(25), 
	@itmCode nvarchar(50), 
	@manufacturerName nvarchar(20),
	@partnerCode nvarchar(20),
	@handlingEquipment nvarchar(20),
	@assignedPickerId nvarchar(25) = NULL,
	@lineNumber bigint,
	@statusId bigint,
	@statusDescription nvarchar(50),
	@updatedOn DATETIME
	
AS
BEGIN

	-- Set assignedPickerId as Null when assignedPickerId = 0 since Spring boot to stored procedure unable to send null value input
    IF @assignedPickerId = '0'
	BEGIN
	SET @assignedPickerId = NULL
	END

	UPDATE tbloutboundline
	SET STATUS_ID = @statusId, STATUS_TEXT = @statusDescription, DLV_UTD_ON = @updatedOn, 
	HE_NO = @handlingEquipment, ass_picker_id = @assignedPickerId
	WHERE C_ID = @companyCodeId AND PLANT_ID = @plantId AND LANG_ID = @languageId AND WH_ID = @warehouseId AND 
			REF_DOC_NO = @refDocNumber AND PRE_OB_NO = @preOutboundNo AND ITM_CODE = @itmCode AND 
			MFR_NAME = @manufacturerName AND OB_LINE_NO = @lineNumber AND PARTNER_CODE = @partnerCode AND IS_DELETED = 0
END			
GO