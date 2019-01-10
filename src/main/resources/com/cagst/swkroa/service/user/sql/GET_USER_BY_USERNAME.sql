SELECT u.user_id
      ,u.person_id
      ,u.username
      ,u.password
      ,u.temporary_pwd_ind
      ,u.signin_attempts
      ,u.last_signin_dt_tm
      ,u.last_signin_ip
      ,u.account_locked_dt_tm
      ,u.account_expired_dt_tm
      ,u.password_changed_dt_tm
      ,u.user_type
      ,u.active_ind
      ,u.updt_cnt
      ,u.create_dt_tm
      ,u.updt_dt_tm
  FROM user u
 WHERE u.username = :username
