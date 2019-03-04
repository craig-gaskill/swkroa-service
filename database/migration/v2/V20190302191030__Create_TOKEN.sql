CREATE TABLE token (
  token_ident  BINARY(16) NOT NULL,
  user_id      BIGINT UNSIGNED NOT NULL,
  expiry_dt_tm DATETIME NOT NULL,
  active_ind   BOOLEAN DEFAULT 1 NOT NULL,
  CONSTRAINT token_pk PRIMARY KEY (token_ident),
  CONSTRAINT token_user_fk FOREIGN KEY (user_id) REFERENCES user (user_id)
) ENGINE = InnoDb;
