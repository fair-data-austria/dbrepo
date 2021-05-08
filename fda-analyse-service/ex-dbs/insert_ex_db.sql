--INSERT CSV DATA INTO TABLES
COPY aka_name
FROM '/ex-dbs/tables/aka_name.csv'
CSV;

COPY aka_title 
FROM '/ex-dbs/tables/aka_title.csv'
CSV; 

COPY cast_info 
FROM '/ex-dbs/tables/cast_info.csv'
CSV; 

COPY char_name
FROM '/ex-dbs/tables/char_name.csv'
CSV;

COPY comp_cast_type
FROM '/ex-dbs/tables/comp_cast_type.csv'
CSV;

COPY company_name
FROM '/ex-dbs/tables/company_name.csv'
CSV;

COPY company_type 
FROM '/ex-dbs/tables/company_type.csv'
CSV;

COPY complete_cast 
FROM '/ex-dbs/tables/complete_cast.csv'
CSV;

COPY info_type
FROM '/ex-dbs/tables/info_type.csv'
CSV;

COPY keyword
FROM '/ex-dbs/tables/keyword.csv'
CSV;

COPY kind_type  
FROM '/ex-dbs/tables/kind_type.csv'
CSV;

COPY link_type
FROM '/ex-dbs/tables/link_type.csv'
CSV;

COPY movie_companies 
FROM '/ex-dbs/tables/movie_companies.csv'
CSV;

COPY movie_info
FROM '/ex-dbs/tables/movie_info.csv'
CSV;

COPY movie_info_idx 
FROM '/ex-dbs/tables/movie_info_idx.csv'
CSV;

COPY movie_keyword
FROM '/ex-dbs/tables/movie_keyword.csv'
CSV;

COPY movie_link
FROM '/ex-dbs/tables/movie_link.csv'
CSV;

COPY name
FROM '/ex-dbs/tables/name.csv'
CSV;

COPY person_info
FROM '/ex-dbs/tables/person_info.csv'
CSV;

COPY role_type
FROM '/ex-dbs/tables/role_type.csv'
CSV;

COPY title
FROM '/ex-dbs/tables/title.csv'
CSV;
