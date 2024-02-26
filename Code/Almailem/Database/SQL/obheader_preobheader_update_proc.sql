USE WMS_ALMDEV
GO

CREATE OR ALTER PROCEDURE obheader_preobheader_update_proc 
	@companyCodeId nvarchar(5), 
	@plantId nvarchar(5), 
	@languageId nvarchar(5),
	@warehouseId nvarchar(5), 
	@refDocNumber nvarchar(25),
	@preOutboundNo nvarchar(25), 
	@updatedOn DATETIME,
	@updatedBy nvarchar(50),
	@statusId47 bigint,
	@statusId50 bigint,
	@statusId51 bigint,
	@statusDescription50 nvarchar(50),
	@statusDescription51 nvarchar(50)
AS
BEGIN
	DECLARE @ROW_COUNT int, @ROW_COUNT_STATUS_ID_50 int, @ROW_COUNT_STATUS_ID_51 int, @IS_STATUS_ID_50 int = 0, @IS_STATUS_ID_51 int = 0

	SELECT @ROW_COUNT = COUNT(REF_DOC_NO) FROM tbloutboundline 
	WHERE C_ID = @companyCodeId AND 
	PLANT_ID = @plantId AND 
	LANG_ID = @languageId AND 
	WH_ID = @warehouseId AND 
	REF_DOC_NO = @refDocNumber AND
	PRE_OB_NO = @preOutboundNo AND
	IS_DELETED = 0 
	GROUP BY REF_DOC_NO

	SELECT @ROW_COUNT_STATUS_ID_51 = COUNT(REF_DOC_NO) FROM tbloutboundline 
	WHERE C_ID = @companyCodeId AND 
	PLANT_ID = @plantId AND 
	LANG_ID = @languageId AND 
	WH_ID = @warehouseId AND 
	REF_DOC_NO = @refDocNumber AND
	PRE_OB_NO = @preOutboundNo AND
	STATUS_ID in (@statusId47, @statusId51) AND
	IS_DELETED = 0 
	GROUP BY REF_DOC_NO
	
	SELECT @ROW_COUNT_STATUS_ID_50 = COUNT(REF_DOC_NO) FROM tbloutboundline 
	WHERE C_ID = @companyCodeId AND 
	PLANT_ID = @plantId AND 
	LANG_ID = @languageId AND 
	WH_ID = @warehouseId AND 
	REF_DOC_NO = @refDocNumber AND
	PRE_OB_NO = @preOutboundNo AND
	STATUS_ID = @statusId50 AND
	IS_DELETED = 0 
	GROUP BY REF_DOC_NO

	IF @ROW_COUNT = @ROW_COUNT_STATUS_ID_50
	BEGIN
		SET @IS_STATUS_ID_50 = 1
	END	
	
	IF @ROW_COUNT = @ROW_COUNT_STATUS_ID_51
	BEGIN
		SET @IS_STATUS_ID_51 = 1
	END	

	IF @IS_STATUS_ID_51 = 1
	BEGIN
		UPDATE tbloutboundheader
			SET STATUS_ID = @statusId51,
			STATUS_TEXT = @statusDescription51,
			DLV_UTD_ON = @updatedOn,
			DLV_UTD_BY = @updatedBy
			WHERE C_ID = @companyCodeId AND 
				PLANT_ID = @plantId AND 
				LANG_ID = @languageId AND 
				WH_ID = @warehouseId AND 
				REF_DOC_NO = @refDocNumber AND
				PRE_OB_NO = @preOutboundNo AND
				IS_DELETED = 0
			
			UPDATE tblpreoutboundheader
			SET STATUS_ID = @statusId51,
			STATUS_TEXT = @statusDescription51,
			PRE_OB_UTD_ON = @updatedOn,
			PRE_OB_UTD_BY = @updatedBy
			WHERE C_ID = @companyCodeId AND 
				PLANT_ID = @plantId AND 
				LANG_ID = @languageId AND 
				WH_ID = @warehouseId AND 
				REF_DOC_NO = @refDocNumber AND
				PRE_OB_NO = @preOutboundNo AND
				IS_DELETED = 0
	END
	
	IF @IS_STATUS_ID_50 = 1
	BEGIN
		UPDATE tbloutboundheader
			SET STATUS_ID = @statusId50,
			STATUS_TEXT = @statusDescription50,
			DLV_UTD_ON = @updatedOn,
			DLV_UTD_BY = @updatedBy
			WHERE C_ID = @companyCodeId AND 
				PLANT_ID = @plantId AND 
				LANG_ID = @languageId AND 
				WH_ID = @warehouseId AND 
				REF_DOC_NO = @refDocNumber AND
				PRE_OB_NO = @preOutboundNo AND
				IS_DELETED = 0
	END			
END				
GO