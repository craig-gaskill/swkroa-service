UPDATE token
   SET used_ind    = :used_ind
      ,active_ind  = :active_ind
      ,updt_id     = :updt_id
      ,updt_dt_tm  = CURRENT_TIMESTAMP
      ,updt_cnt    = updt_cnt + 1
 WHERE token_id    = :token_id
   AND user_id     = :user_id
   AND updt_cnt    = :updt_cnt
