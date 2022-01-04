CREATE USER 'cluvio_readonly'@'localhost' IDENTIFIED BY 'secret_password';
GRANT SELECT ON *.* TO 'cluvio_readonly'@'localhost';
FLUSH PRIVILEGES;