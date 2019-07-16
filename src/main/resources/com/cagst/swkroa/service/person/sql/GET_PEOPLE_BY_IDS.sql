SELECT p.person_id
      ,p.title_cd
      ,p.name_last
      ,p.name_first
      ,p.name_middle
      ,p.locale_language
      ,p.locale_country
      ,p.time_zone
      ,p.active_ind
      ,p.updt_cnt
 FROM person p
WHERE p.person_id IN (:personIds)
