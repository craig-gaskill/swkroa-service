INSERT INTO token (
  token_ident
 ,user_id
 ,expiry_dt_tm
 ,active_ind
) VALUES (
  :token
 ,:user_id
 ,:expiry_dt_tm
 ,:active_ind
)
