CREATE TABLE token (
  token_ident  BINARY(16) NOT NULL,
  user_id      BIGINT UNSIGNED NOT NULL,
  expiry_dt_tm DATETIME NOT NULL,
  used_ind     BOOLEAN DEFAULT 0 NOT NULL,
  active_ind   BOOLEAN DEFAULT 1 NOT NULL,
  create_dt_tm DATETIME NOT NULL,
  create_id    BIGINT NOT NULL,
  updt_dt_tm   DATETIME NOT NULL,
  updt_id      BIGINT NOT NULL,
  updt_cnt     INT DEFAULT 0 NOT NULL,
  CONSTRAINT token_pk PRIMARY KEY (token_ident),
  CONSTRAINT token_user_fk FOREIGN KEY (user_id) REFERENCES user (user_id)
) ENGINE = InnoDb;
