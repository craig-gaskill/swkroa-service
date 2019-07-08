SELECT token_id
      ,token_ident
      ,user_id
      ,expiry_dt_tm
      ,used_ind
      ,active_ind
      ,updt_cnt
  FROM token
 WHERE token_ident = :token_ident
   AND user_id     = :user_id
   AND used_ind    = 0
   AND active_ind  = 1
   AND expiry_dt_tm > CURRENT_TIMESTAMP
