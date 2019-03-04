SELECT count(*)
  FROM token
 WHERE token_ident = :token
   AND user_id     = :user_id
   AND active_ind  = 1
   AND expiry_dt_tm > CURRENT_TIMESTAMP
