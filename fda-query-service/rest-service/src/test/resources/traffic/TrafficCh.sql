/* https://www.kaggle.com/laa283/zurich-public-transport/version/2 */
CREATE SEQUENCE seq_traffic
    START 1;

CREATE TABLE traffic_zurich
(
    linie                bigint                                   null,
    richtung             bigint                                   null,
    betriebsdatum        date                                     null,
    fahrzeug             bigint                                   null,
    kurs                 bigint                                   null,
    seq_von              bigint                                   null,
    halt_diva_von        bigint                                   null,
    halt_punkt_diva_von  bigint                                   null,
    halt_kurz_von1       bigint                                   null,
    datum_von            date                                     null,
    soll_an_von          bigint                                   null,
    ist_an_von           bigint                                   null,
    soll_ab_von          bigint                                   null,
    seq_nach             bigint                                   null,
    halt_diva_nach       bigint                                   null,
    halt_punkt_diva_nach bigint                                   null,
    halt_kurz_nach1      bigint                                   null,
    datum_nach           date                                     null,
    soll_an_nach         bigint                                   null,
    ist_an_nach1         bigint                                   null,
    soll_ab_nach         bigint                                   null,
    ist_ab_nach          bigint                                   null,
    fahrt_id             bigint                                   null,
    fahrweg_id           bigint                                   null,
    fw_no                bigint                                   null,
    fw_typ               bigint                                   null,
    fw_kurz              bigint                                   null,
    fw_lang              varchar(255)                             null,
    umlauf_von           bigint                                   null,
    halt_id_von          bigint                                   null,
    halt_id_nach         bigint                                   null,
    halt_punkt_id_von    bigint                                   null,
    halt_punkt_id_nach   bigint                                   null,
    id                   bigint default nextval(`seq_traffic`) not null,
    primary key (id)
) with system versioning;