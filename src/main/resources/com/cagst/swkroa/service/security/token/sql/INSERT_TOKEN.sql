INSERT INTO token (
  token_ident
 ,user_id
 ,expiry_dt_tm
 ,used_ind
 ,active_ind
 ,create_id
 ,create_dt_tm
 ,updt_id
 ,updt_dt_tm
) VALUES (
  :token_ident
 ,:user_id
 ,:expiry_dt_tm
 ,:used_ind
 ,:active_ind
 ,:create_id
 ,CURRENT_TIMESTAMP
 ,:updt_id
 ,CURRENT_TIMESTAMP)
