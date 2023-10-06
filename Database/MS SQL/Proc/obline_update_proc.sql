USE WMS
GO
-- String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String itemCode

CREATE OR ALTER PROCEDURE obline_update_proc 
	@warehouseId nvarchar(5), 
	@preOutboundNo nvarchar(20), 
	@refDocNumber nvarchar(20),
	@partnerCode nvarchar(20),
	@lineNumber int,
	@itmCode nvarchar(20),
	@statusId int,
	@loginUserId nvarchar(20)
AS
	UPDATE tbloutboundline
	SET STATUS_ID = @statusId, DLV_UTD_BY = @loginUserId
	WHERE WH_ID = @warehouseId 
	AND PRE_OB_NO = @preOutboundNo
	AND REF_DOC_NO = @refDocNumber
	AND PARTNER_CODE = @partnerCode
	AND OB_LINE_NO = @lineNumber
	AND ITM_CODE = @itmCode
GO
