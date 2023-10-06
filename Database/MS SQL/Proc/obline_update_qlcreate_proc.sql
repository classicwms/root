USE WMS
GO
-- WarehouseId, PreOutboundNo, RefDocNumber, PartnerCode, LineNumber, ItemCode, DeliveryQty, DeliveryOrderNo, StatusId(57L);

CREATE OR ALTER PROCEDURE obline_update_qlcreate_proc 
	@warehouseId nvarchar(5), 
	@preOutboundNo nvarchar(20), 
	@refDocNumber nvarchar(20),
	@partnerCode nvarchar(20),
	@lineNumber int,
	@itmCode nvarchar(20),	
	@deliveryQty float(2),
	@deliveryOrderNo nvarchar(20),
	@statusId int
AS
	UPDATE tbloutboundline
	SET DLV_QTY = @deliveryQty,		
		DLV_ORD_NO = @deliveryOrderNo,
		STATUS_ID = @statusId
	WHERE WH_ID = @warehouseId 
	AND PRE_OB_NO = @preOutboundNo
	AND REF_DOC_NO = @refDocNumber
	AND PARTNER_CODE = @partnerCode
	AND OB_LINE_NO = @lineNumber
	AND ITM_CODE = @itmCode
GO
