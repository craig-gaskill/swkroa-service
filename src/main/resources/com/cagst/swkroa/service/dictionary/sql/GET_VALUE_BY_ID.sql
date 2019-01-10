SELECT cv.codevalue_id
      ,cv.codeset_id
      ,cv.codevalue_display
      ,cv.codevalue_meaning
      ,cv.active_ind
      ,cv.updt_cnt AS codevalue_updt_cnt
  FROM codeset cs
 INNER JOIN codevalue cv ON (cv.codeset_id = cs.codeset_id)
 WHERE cs.codeset_meaning = :dictionaryMeaning
   AND cv.codevalue_id    = :dictionaryValueId
   AND cv.active_ind      = 1
