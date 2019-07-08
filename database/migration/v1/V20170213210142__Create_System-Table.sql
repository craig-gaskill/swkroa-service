CREATE TABLE setting (
  setting_key            VARCHAR(25) NOT NULL,
  setting_name           VARCHAR(50) NOT NULL,
  setting_value          VARCHAR(50) NULL,
  active_ind             BOOLEAN DEFAULT 1 NOT NULL,
  create_dt_tm           DATETIME NOT NULL,
  create_id              BIGINT NOT NULL,
  updt_dt_tm             DATETIME NOT NULL,
  updt_id                BIGINT NOT NULL,
  updt_cnt               INT DEFAULT 0 NOT NULL,
  CONSTRAINT setting_pk PRIMARY KEY (setting_key)
) ENGINE = InnoDb;
