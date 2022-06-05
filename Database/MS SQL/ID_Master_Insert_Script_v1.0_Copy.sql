
-- WMS - ID Master Insert scripts

INSERT INTO tblusermanagement (usr_id, c_id, ctd_by, cur_decimal, date_for_id, is_deleted, mail_id, fst_nm, lang_id, lst_nm, password, plant_id, status_id, user_nm, usr_role_id, usr_typ_id, wh_id) 
VALUES ('wms', '1000', 'raj', '1', '1', '0', 'raj@tekclover.com', 'Raj', 'EN', 'Raj', '$2a$10$2vBZ88KzAgCZlcbp33jkyOtX/vB4AdheVeNitofJfYtxBnEWWyb8G', '1001', '1', 'raj', '1', '1', '110');
GO

INSERT INTO tblverticalid (VERT_ID,LANG_ID,VERTICAL,REMARK,IS_DELETED,CTD_BY,CTD_ON,UTD_BY,UTD_ON)
VALUES (1,'EN','Retail','Remarks',0,'SUPERADMIN',CURDATE(),'SUPERADMIN',GETDATE());
GO

