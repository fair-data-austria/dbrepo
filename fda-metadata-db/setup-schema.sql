CREATE USER root;
CREATE DATABASE root;

BEGIN;

CREATE
    TYPE gender AS ENUM ('F', 'M', 'T');
CREATE
    TYPE accesstype AS ENUM ('R', 'W');
CREATE
    TYPE image_environment_type AS ENUM ('USERNAME', 'PASSWORD', 'PRIVILEGED_USERNAME', 'PRIVILEGED_PASSWORD');
CREATE
    TYPE role_type AS ENUM ('ROLE_RESEARCHER', 'ROLE_DEVELOPER', 'ROLE_DATA_STEWARD');

CREATE
    CAST
    (character varying AS image_environment_type)
    WITH INOUT AS ASSIGNMENT;
CREATE
    CAST
    (character varying AS role_type)
    WITH INOUT AS ASSIGNMENT;

CREATE SEQUENCE public.mdb_images_environment_item_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_images_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_images_date_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_containers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_data_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_databases_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_tables_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_columns_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_columns_enum_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_view_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_identifiers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.mdb_creators_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS mdb_users
(
    UserID               bigint                      not null DEFAULT nextval('mdb_user_seq'),
    external_id          VARCHAR(255) UNIQUE,
    OID                  bigint,
    username             VARCHAR(255)                not null,
    First_name           VARCHAR(50),
    Last_name            VARCHAR(50),
    Gender               gender,
    Preceding_titles     VARCHAR(50),
    Postpositioned_title VARCHAR(50),
    Main_Email           VARCHAR(255)                not null,
    password             VARCHAR(255)                not null,
    created              timestamp without time zone NOT NULL DEFAULT NOW(),
    last_modified        timestamp without time zone,
    PRIMARY KEY (UserID),
    UNIQUE (username),
    UNIQUE (Main_Email),
    UNIQUE (OID)
);

CREATE TABLE public.mdb_images
(
    id            bigint                      NOT NULL DEFAULT nextval('mdb_images_seq'),
    repository    character varying(255)      NOT NULL,
    tag           character varying(255)      NOT NULL,
    default_port  integer                     NOT NULL,
    dialect       character varying(255)      NOT NULL,
    driver_class  character varying(255)      NOT NULL,
    jdbc_method   character varying(255)      NOT NULL,
    compiled      timestamp without time zone,
    logo          TEXT,
    hash          character varying(255),
    size          bigint,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp without time zone,
    PRIMARY KEY (id),
    UNIQUE (repository, tag)
);

CREATE TABLE public.mdb_images_date
(
    id              bigint                      NOT NULL DEFAULT nextval('mdb_images_date_seq'),
    iid             bigint                      NOT NULL,
    database_format character varying(255)      NOT NULL,
    unix_format     character varying(255)      NOT NULL,
    example         character varying(255)      NOT NULL,
    created_at      timestamp without time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id),
    FOREIGN KEY (iid) REFERENCES mdb_images (id),
    UNIQUE (database_format)
);

CREATE TABLE IF NOT EXISTS mdb_containers
(
    id            bigint                      NOT NULL DEFAULT nextval('mdb_containers_seq'),
    HASH          character varying(255)      NOT NULL,
    INTERNAL_NAME character varying(255)      NOT NULL,
    NAME          character varying(255)      NOT NULL,
    PORT          integer,
    image_id      bigint,
    ip_address    character varying(255),
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    created_by    bigint                      not null,
    LAST_MODIFIED timestamp without time zone,
    deleted       timestamp without time zone,
    PRIMARY KEY (id),
    FOREIGN KEY (image_id) REFERENCES mdb_images (id),
    FOREIGN KEY (created_by) REFERENCES mdb_users (UserID)
);

CREATE TABLE public.mdb_images_environment_item
(
    id            bigint                      NOT NULL DEFAULT nextval('mdb_images_environment_item_seq'),
    key           character varying(255)      NOT NULL,
    value         character varying(255)      NOT NULL,
    etype         image_environment_type      NOT NULL,
    iid           bigint                      NOT NULL,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp without time zone,
    PRIMARY KEY (id, iid),
    FOREIGN KEY (iid) REFERENCES mdb_images (id)
);

CREATE TABLE IF NOT EXISTS mdb_data
(
    ID           bigint DEFAULT nextval('mdb_data_seq'),
    PROVENANCE   TEXT,
    FileEncoding TEXT,
    FileType     VARCHAR(100),
    Version      TEXT,
    Seperator    TEXT,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS mdb_user_roles
(
    uid           bigint                      not null,
    role          varchar(255)                not null,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp without time zone,
    PRIMARY KEY (uid)
);

CREATE TABLE IF NOT EXISTS mdb_databases
(
    id            bigint                      NOT NULL DEFAULT nextval('mdb_databases_seq'),
    name          character varying(255)      NOT NULL,
    internal_name character varying(255)      NOT NULL,
    exchange      character varying(255)      NOT NULL,
    ResourceType  TEXT,
    Description   TEXT,
    Engine        VARCHAR(20)                          DEFAULT 'Postgres',
    Publisher     VARCHAR(50),
    Year          DATE                                 DEFAULT CURRENT_DATE,
    License       TEXT,
    is_public     BOOLEAN                     NOT NULL DEFAULT TRUE,
    Contactperson INTEGER REFERENCES mdb_USERS (UserID),
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    created_by    bigint                      not null,
    last_modified timestamp without time zone,
    deleted       timestamp without time zone NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES mdb_containers (id), /* currently we only support one-to-one */
    FOREIGN KEY (created_by) REFERENCES mdb_users (UserID)
);

CREATE TABLE IF NOT EXISTS mdb_tables
(
    ID            bigint                      NOT NULL DEFAULT nextval('mdb_tables_seq'),
    tDBID         bigint                      NOT NULL,
    internal_name character varying(255)      NOT NULL,
    topic         character varying(255)      NOT NULL,
    tName         VARCHAR(50),
    tDescription  TEXT,
    NumCols       INTEGER,
    NumRows       INTEGER,
    separator     CHAR(1),
    element_null  VARCHAR(50),
    skip_lines    BIGINT,
    element_true  VARCHAR(50),
    element_false VARCHAR(50),
    Version       TEXT,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    created_by    bigint                      NOT NULL,
    last_modified timestamp without time zone,
    PRIMARY KEY (tDBID, ID),
    FOREIGN KEY (tDBID) REFERENCES mdb_DATABASES (id),
    FOREIGN KEY (created_by) REFERENCES mdb_users (UserID)
);

CREATE TABLE IF NOT EXISTS mdb_COLUMNS
(
    ID                    bigint                               DEFAULT nextval('mdb_columns_seq'),
    cDBID                 bigint,
    tID                   bigint,
    dfID                  bigint,
    cName                 VARCHAR(100),
    internal_name         VARCHAR(100)                NOT NULL,
    Datatype              VARCHAR(50),
    ordinal_position      INTEGER,
    decimal_digits_before bigint,
    decimal_digits_after  bigint,
    is_primary_key        BOOLEAN,
    is_unique             BOOLEAN,
    auto_generated        BOOLEAN                              DEFAULT false,
    is_null_allowed       BOOLEAN,
    foreign_key           VARCHAR(255),
    reference_table       VARCHAR(255),
    check_expression      character varying(255),
    created               timestamp without time zone NOT NULL DEFAULT NOW(),
    created_by            bigint                      NOT NULL,
    last_modified         timestamp without time zone,
    PRIMARY KEY (cDBID, tID, ID),
    FOREIGN KEY (cDBID, tID) REFERENCES mdb_TABLES (tDBID, ID),
    FOREIGN KEY (created_by) REFERENCES mdb_users (UserID)
);

CREATE TABLE IF NOT EXISTS mdb_COLUMNS_ENUMS
(
    ID            bigint                      NOT NULL DEFAULT nextval('mdb_columns_enum_seq'),
    eDBID         bigint                      NOT NULL,
    tID           bigint                      NOT NULL,
    cID           bigint                      NOT NULL,
    enum_values   CHARACTER VARYING(255)      NOT NULL,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp without time zone,
    FOREIGN KEY (eDBID, tID, cID) REFERENCES mdb_COLUMNS (cDBID, tID, ID),
    PRIMARY KEY (ID, eDBID, tID, cID)
);

CREATE TABLE IF NOT EXISTS mdb_COLUMNS_nom
(
    cDBID         bigint,
    tID           bigint,
    cID           bigint,
    maxlength     INTEGER,
    last_modified timestamp without time zone,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    FOREIGN KEY (cDBID, tID, cID) REFERENCES mdb_COLUMNS (cDBID, tID, ID),
    PRIMARY KEY (cDBID, tID, cID)
);

CREATE TABLE IF NOT EXISTS mdb_COLUMNS_num
(
    cDBID         bigint,
    tID           bigint,
    cID           bigint,
    SIunit        TEXT,
    MaxVal        NUMERIC,
    MinVal        NUMERIC,
    Mean          NUMERIC,
    Median        NUMERIC,
    Sd            Numeric,
    Histogram     INTEGER[],
    last_modified timestamp without time zone,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    FOREIGN KEY (cDBID, tID, cID) REFERENCES mdb_COLUMNS (cDBID, tID, ID),
    PRIMARY KEY (cDBID, tID, cID)
);

CREATE TABLE IF NOT EXISTS mdb_COLUMNS_cat
(
    cDBID         bigint,
    tID           bigint,
    cID           bigint,
    num_cat       INTEGER,
    cat_array     TEXT[],
    last_modified timestamp without time zone,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    FOREIGN KEY (cDBID, tID, cID) REFERENCES mdb_COLUMNS (cDBID, tID, ID),
    PRIMARY KEY (cDBID, tID, cID)
);

CREATE TABLE IF NOT EXISTS mdb_concepts
(
    URI     TEXT,
    name    TEXT,
    created timestamp without time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (URI)
);

CREATE TABLE IF NOT EXISTS mdb_columns_concepts
(
    cDBID   bigint,
    tID     bigint,
    cID     bigint,
    URI     TEXT REFERENCES mdb_concepts (URI),
    created timestamp without time zone NOT NULL DEFAULT NOW(),
    FOREIGN KEY (cDBID, tID, cID) REFERENCES mdb_COLUMNS (cDBID, tID, ID),
    PRIMARY KEY (cDBID, tID, cID, URI)
);

CREATE TABLE IF NOT EXISTS mdb_VIEW
(
    id            bigint                      NOT NULL DEFAULT nextval('mdb_view_seq'),
    vName         VARCHAR(50),
    Query         TEXT,
    Public        BOOLEAN,
    NumCols       INTEGER,
    NumRows       INTEGER,
    InitialView   BOOLEAN,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    created_by    bigint                      NOT NULL,
    last_modified timestamp without time zone,
    PRIMARY KEY (id),
    FOREIGN KEY (created_by) REFERENCES mdb_users (UserID)
);

CREATE TABLE IF NOT EXISTS mdb_identifiers
(
    id            bigint                               DEFAULT nextval('mdb_identifiers_seq'),
    cid           bigint                      NOT NULL,
    dbid          bigint                      NOT NULL,
    qid           bigint                      NOT NULL,
    title         VARCHAR(255)                NOT NULL,
    description   TEXT                        NOT NULL,
    visibility    VARCHAR(10)                 NOT NULL DEFAULT 'SELF',
    doi           VARCHAR(255),
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    created_by    bigint                      NOT NULL,
    last_modified timestamp without time zone,
    deleted       timestamp without time zone,
    PRIMARY KEY (id), /* must be a single id from persistent identifier concept */
    FOREIGN KEY (cid) REFERENCES mdb_containers (id),
    FOREIGN KEY (dbid) REFERENCES mdb_databases (id),
    FOREIGN KEY (created_by) REFERENCES mdb_users (UserID),
    UNIQUE (cid, dbid, qid)
);

CREATE TABLE IF NOT EXISTS mdb_creators
(
    id            bigint                               DEFAULT nextval('mdb_creators_seq'),
    pid           bigint                      NOT NULL,
    firstname     VARCHAR(255)                NOT NULL,
    lastname      VARCHAR(255)                NOT NULL,
    created       timestamp without time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp without time zone NOT NULL,
    PRIMARY KEY (id, pid),
    FOREIGN KEY (pid) REFERENCES mdb_identifiers (id)
);

CREATE TABLE IF NOT EXISTS mdb_views_databases
(
    mdb_view_id  bigint REFERENCES mdb_VIEW (id),
    databases_id bigint REFERENCES mdb_DATABASES (id),
    created      timestamp without time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (mdb_view_id, databases_id)
);

CREATE TABLE IF NOT EXISTS mdb_feed
(
    fDBID   bigint,
    fID     bigint,
    fUserId INTEGER REFERENCES mdb_USERS (UserID),
    fDataID INTEGER REFERENCES mdb_DATA (ID),
    created timestamp without time zone NOT NULL DEFAULT NOW(),
    FOREIGN KEY (fDBID, fID) REFERENCES mdb_TABLES (tDBID, ID),
    PRIMARY KEY (fDBID, fID, fUserId, fDataID)
);

CREATE TABLE IF NOT EXISTS mdb_update
(
    uUserID INTEGER REFERENCES mdb_USERS (UserID),
    uDBID   bigint REFERENCES mdb_DATABASES (id),
    created timestamp without time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (uUserID, uDBID)
);

CREATE TABLE IF NOT EXISTS mdb_access
(
    aUserID  INTEGER REFERENCES mdb_USERS (UserID),
    aDBID    bigint REFERENCES mdb_DATABASES (id),
    attime   TIMESTAMP,
    download BOOLEAN,
    created  timestamp without time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (aUserID, aDBID)
);

CREATE TABLE IF NOT EXISTS mdb_have_access
(
    hUserID INTEGER REFERENCES mdb_USERS (UserID),
    hDBID   bigint REFERENCES mdb_DATABASES (id),
    hType   accesstype,
    created timestamp without time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (hUserID, hDBID)
);

CREATE TABLE IF NOT EXISTS mdb_owns
(
    oUserID INTEGER REFERENCES mdb_USERS (UserID),
    oDBID   bigint REFERENCES mdb_DATABASES (ID),
    created timestamp without time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (oUserID, oDBID)
);

COMMIT;
