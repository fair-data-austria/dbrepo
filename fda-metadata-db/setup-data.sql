BEGIN;

INSERT INTO mdb_images (default_port, dialect, driver_class, jdbc_method, repository, tag)
VALUES ('3306', 'org.hibernate.dialect.MariaDBDialect', 'org.mariadb.jdbc.Driver', 'mariadb', 'mariadb', '10.5');

INSERT INTO mdb_images_environment_item (iid, key, value, etype)
VALUES ('1', 'USERNAME', 'mariadb', 'USERNAME'),
       ('1', 'PASSWORD', 'mariadb', 'PASSWORD');

COMMIT;
