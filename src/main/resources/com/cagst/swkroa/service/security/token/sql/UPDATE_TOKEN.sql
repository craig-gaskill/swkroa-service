UPDATE token
   SET used_ind    = :used
      ,active_ind  = :active
 WHERE token_ident = :token
   AND user_id     = :user_id
