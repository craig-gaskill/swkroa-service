SELECT codeset_id
      ,codeset_display
      ,codeset_meaning
      ,active_ind
      ,updt_cnt
  FROM codeset
 WHERE codeset_id = :dictionaryId
