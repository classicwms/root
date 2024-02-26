USE WMS_ALMDEV
GO

CREATE OR ALTER PROCEDURE obline_update_qlcreate_proc 
	@companyCodeId nvarchar(10), 
	@plantId nvarchar(10), 
	@languageId nvarchar(10), 
	@warehouseId nvarchar(5), 
	@preOutboundNo nvarchar(20), 
	@refDocNumber nvarchar(20),
	@partnerCode nvarchar(20),
	@lineNumber int,
	@itmCode nvarchar(20),	
	@deliveryQty float(2),
	@deliveryOrderNo nvarchar(20),
	@statusDescription nvarchar(25),
	@statusId int

AS
BEGIN

	UPDATE tbloutboundline
	SET DLV_QTY = @deliveryQty,		
		DLV_ORD_NO = @deliveryOrderNo,
		STATUS_TEXT = @statusDescription,
		STATUS_ID = @statusId
	WHERE C_ID = @companyCodeId 
	AND PLANT_ID = @plantId
	AND LANG_ID = @languageId
	AND WH_ID = @warehouseId
	AND PRE_OB_NO = @preOutboundNo
	AND REF_DOC_NO = @refDocNumber
	AND PARTNER_CODE = @partnerCode
	AND OB_LINE_NO = @lineNumber
	AND ITM_CODE = @itmCode			
END				
GO