
# --- !Ups

CREATE TABLE IF NOT EXISTS example.table1 (
  id uuid primary key,
  value text
);

INSERT INTO example.table1 (id,"value") VALUES (e07dd14a-a0ca-4766-9825-3ac9a53cb18d,'sample value');

# --- !Downs

DROP TABLE example.table1;
