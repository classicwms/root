USE WMS_ALMPRD
GO

-- String companyCodeId, String plantId, String languageId, String warehouseId
-- String itemCode, String manufacturerName, Long lineNo, Date updatedOn, 
-- String refDocNumber, String preInboundNo, Long statusId, String statusDescription,
CREATE OR ALTER PROCEDURE alm_inboundline_status_update_proc_v2 
	@companyCodeId nvarchar(5), 
	@plantId nvarchar(5), 
	@languageId nvarchar(5),
	@warehouseId nvarchar(5), 
	@refDocNumber nvarchar(25),
	@preInboundNo nvarchar(25), 	
	@lineNumber int,
	@itmCode nvarchar(50),	
	@mfrName nvarchar(20),
	@statusId bigint,
	@statusDescription nvarchar(50),
	@updatedOn DATETIME
		
AS
BEGIN

	UPDATE IBL SET IBL.STATUS_ID = @statusId, IBL.STATUS_TEXT = @statusDescription, IBL.UTD_ON = @updatedOn, IBL.IB_CNF_ON = @updatedOn 
	FROM tblinboundline IBL INNER JOIN
	(SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_IB_NO,IB_LINE_NO,ITM_CODE,MFR_NAME FROM tblgrline
	WHERE IS_DELETED = 0 AND status_id = @statusId AND C_ID = @companyCodeId AND PLANT_ID = @plantId AND 
			LANG_ID = @languageId AND WH_ID = @warehouseId AND REF_DOC_NO = @refDocNumber AND PRE_IB_NO = @preInboundNo
			AND IB_LINE_NO = @lineNumber AND ITM_CODE = @itmCode AND MFR_NAME = @mfrName
	) X ON
	IBL.C_ID = X.C_ID AND IBL.PLANT_ID = X.PLANT_ID AND IBL.LANG_ID = X.LANG_ID AND IBL.WH_ID = X.WH_ID AND 
	IBL.REF_DOC_NO = X.REF_DOC_NO AND IBL.PRE_IB_NO = X.PRE_IB_NO AND IBL.ITM_CODE = X.ITM_CODE AND 
	IBL.MFR_NAME = X.MFR_NAME AND IBL.IB_LINE_NO = X.IB_LINE_NO AND IBL.IS_DELETED = 0 AND IBL.STATUS_ID != 20

END			
GO