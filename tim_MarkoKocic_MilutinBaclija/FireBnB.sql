/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     4/16/2023 1:02:59 AM                         */
/*==============================================================*/


drop table if exists BAN;

drop table if exists KORISNIK;

drop table if exists LOKACIJA;

drop table if exists MESTO;

drop table if exists NAGRADE_LOJALNOSTI;

drop table if exists OSTVARENA_USLUGA;

drop table if exists PAKET_LOJALNOSTI;

drop table if exists PONUDJENA_USLUGA;

drop table if exists PRAVILO_CENE;

drop table if exists PRAVILO_NEDOSTUPNOSTI;

drop table if exists PRIJAVA;

drop table if exists PRIVILEGIJE_BORAVKA;

drop table if exists PRIVILEGIJE_USLUGA;

drop table if exists RECENZIJA;

drop table if exists REZERVACIJA;

drop table if exists SMESTAJ;

drop table if exists SUPPORT_TICKET;

drop table if exists TIP_TRANSAKCIJE;

drop table if exists TIP_USLUGE;

drop table if exists TRANSAKCIJA;

drop table if exists ZEMLJA;

/*==============================================================*/
/* Table: BAN                                                   */
/*==============================================================*/
create table BAN
(
   BAN_ID               int not null,
   KORISNIK_ID          int not null,
   RAZLOG               text not null,
   VREME                datetime not null,
   primary key (BAN_ID)
);

/*==============================================================*/
/* Table: KORISNIK                                              */
/*==============================================================*/
create table KORISNIK
(
   KORISNIK_ID          int not null,
   PAKET_LOJALNOSTI_ID  int not null,
   IME                  varchar(100) not null,
   PREZIME              varchar(100) not null,
   DATUM_RODJENJA       date not null,
   EMAIL                varchar(100) not null,
   POL                  varchar(1),
   POENI_LOJALNOSTI     int not null,
   primary key (KORISNIK_ID)
);

/*==============================================================*/
/* Table: LOKACIJA                                              */
/*==============================================================*/
create table LOKACIJA
(
   LOKACIJA_ID          int not null,
   MESTO_ID             int not null,
   ADRESA               varchar(400) not null,
   POSTANSKI_BROJ       varchar(100),
   primary key (LOKACIJA_ID)
);

/*==============================================================*/
/* Table: MESTO                                                 */
/*==============================================================*/
create table MESTO
(
   MESTO_ID             int not null,
   ZEMLJA_ID            int not null,
   IME                  varchar(100) not null,
   primary key (MESTO_ID)
);

/*==============================================================*/
/* Table: NAGRADE_LOJALNOSTI                                    */
/*==============================================================*/
create table NAGRADE_LOJALNOSTI
(
   DOGADJAJ             varchar(100) not null,
   POENI_LOJALNOSTI     int not null,
   primary key (DOGADJAJ)
);

/*==============================================================*/
/* Table: OSTVARENA_USLUGA                                      */
/*==============================================================*/
create table OSTVARENA_USLUGA
(
   TIP_USLUGE_ID        int not null,
   SMESTAJ_ID           int not null,
   CENOVNIK_USLUGE_ID   int not null,
   REZERVACIJA_ID       int not null,
   OSTVARENA_USLUGA_ID  int not null,
   primary key (TIP_USLUGE_ID, SMESTAJ_ID, CENOVNIK_USLUGE_ID, REZERVACIJA_ID, OSTVARENA_USLUGA_ID)
);

/*==============================================================*/
/* Table: PAKET_LOJALNOSTI                                      */
/*==============================================================*/
create table PAKET_LOJALNOSTI
(
   PAKET_LOJALNOSTI_ID  int not null,
   NIVO                 int not null,
   IME_PAKETA_LOJALNOSTI varchar(100) not null,
   USLOV_POENI_LOJALNOSTI int not null,
   primary key (PAKET_LOJALNOSTI_ID)
);

/*==============================================================*/
/* Table: PONUDJENA_USLUGA                                      */
/*==============================================================*/
create table PONUDJENA_USLUGA
(
   TIP_USLUGE_ID        int not null,
   SMESTAJ_ID           int not null,
   CENOVNIK_USLUGE_ID   int not null,
   CENA                 float not null,
   primary key (TIP_USLUGE_ID, SMESTAJ_ID, CENOVNIK_USLUGE_ID)
);

/*==============================================================*/
/* Table: PRAVILO_CENE                                          */
/*==============================================================*/
create table PRAVILO_CENE
(
   SMESTAJ_ID           int not null,
   PND_ID2              int not null,
   OD                   date not null,
   DO                   date not null,
   CENA_PO_NOCI         float not null,
   primary key (SMESTAJ_ID, PND_ID2)
);

/*==============================================================*/
/* Table: PRAVILO_NEDOSTUPNOSTI                                 */
/*==============================================================*/
create table PRAVILO_NEDOSTUPNOSTI
(
   SMESTAJ_ID           int not null,
   PND_ID               int not null,
   OD                   date not null,
   DO                   date not null,
   primary key (SMESTAJ_ID, PND_ID)
);

/*==============================================================*/
/* Table: PRIJAVA                                               */
/*==============================================================*/
create table PRIJAVA
(
   PRIJAVA_ID           int not null,
   KORISNIK_ID          int not null,
   KOR_KORISNIK_ID      int not null,
   TEKST_KORISNIKA      text not null,
   TEKST_DOMACINA       text not null,
   RESENJE              text not null,
   STATUS               varchar(20) not null,
   PODNOSILAC_PRIIJAVE  varchar(20) not null,
   primary key (PRIJAVA_ID)
);

/*==============================================================*/
/* Table: PRIVILEGIJE_BORAVKA                                   */
/*==============================================================*/
create table PRIVILEGIJE_BORAVKA
(
   PRIVILEGIJE_BORAVKA_ID int not null,
   PAKET_LOJALNOSTI_ID  int not null,
   POPUST               float not null,
   ROOM_UPGRADE         bool not null,
   primary key (PRIVILEGIJE_BORAVKA_ID)
);

/*==============================================================*/
/* Table: PRIVILEGIJE_USLUGA                                    */
/*==============================================================*/
create table PRIVILEGIJE_USLUGA
(
   PAKET_LOJALNOSTI_ID  int not null,
   TIP_USLUGE_ID        int not null,
   PRIVILEGIJA_USLUGA_ID int not null,
   POPUST               float not null,
   primary key (PAKET_LOJALNOSTI_ID, TIP_USLUGE_ID, PRIVILEGIJA_USLUGA_ID)
);

/*==============================================================*/
/* Table: RECENZIJA                                             */
/*==============================================================*/
create table RECENZIJA
(
   RECENZIJA_ID2        int not null,
   REZERVACIJA_ID       int not null,
   OCENA_SMESTAJA       int not null,
   OCENA_KORISNIKA      int not null,
   KOMENTAR_KORISNIKA   text not null,
   KOMENTAR_VLASNIKA    text not null,
   primary key (RECENZIJA_ID2)
);

/*==============================================================*/
/* Table: REZERVACIJA                                           */
/*==============================================================*/
create table REZERVACIJA
(
   REZERVACIJA_ID       int not null,
   KORISNIK_ID          int not null,
   SMESTAJ_ID           int not null,
   VREME_REZERVISANJA   datetime not null,
   POCETAK_REZERVACIJE  datetime not null,
   KRAJ_REZERVACIJE     datetime not null,
   ODKAZAN              bool not null,
   primary key (REZERVACIJA_ID)
);

/*==============================================================*/
/* Table: SMESTAJ                                               */
/*==============================================================*/
create table SMESTAJ
(
   SMESTAJ_ID           int not null,
   KORISNIK_ID          int,
   LOKACIJA_ID          int not null,
   KVADRATURA           int not null,
   BROJ_SOBA            int not null,
   primary key (SMESTAJ_ID)
);

/*==============================================================*/
/* Table: SUPPORT_TICKET                                        */
/*==============================================================*/
create table SUPPORT_TICKET
(
   SUPPORT_TICKET_ID    int not null,
   KORISNIK_ID          int not null,
   TEKST_KORISNIKA      text not null,
   RESENJE              text,
   STATUS               varchar(20),
   primary key (SUPPORT_TICKET_ID)
);

/*==============================================================*/
/* Table: TIP_TRANSAKCIJE                                       */
/*==============================================================*/
create table TIP_TRANSAKCIJE
(
   TIP_TRANSAKCIJE_ID   int not null,
   NAZIV_TIPA           varchar(100) not null,
   primary key (TIP_TRANSAKCIJE_ID)
);

/*==============================================================*/
/* Table: TIP_USLUGE                                            */
/*==============================================================*/
create table TIP_USLUGE
(
   TIP_USLUGE_ID        int not null,
   NAZIV_TIPA_USLUGE    varchar(200),
   primary key (TIP_USLUGE_ID)
);

/*==============================================================*/
/* Table: TRANSAKCIJA                                           */
/*==============================================================*/
create table TRANSAKCIJA
(
   TRANSAKCIJA_ID       int not null,
   TIP_TRANSAKCIJE_ID   int not null,
   REZERVACIJA_ID       int not null,
   VREME                datetime not null,
   KOLICINA_U_DIN       float not null,
   primary key (TRANSAKCIJA_ID)
);

/*==============================================================*/
/* Table: ZEMLJA                                                */
/*==============================================================*/
create table ZEMLJA
(
   ZEMLJA_ID            int not null,
   IME                  varchar(100) not null,
   KOD                  varchar(10) not null,
   primary key (ZEMLJA_ID)
);

alter table BAN add constraint FK_BANOVANI_KORISNIK foreign key (KORISNIK_ID)
      references KORISNIK (KORISNIK_ID) on delete restrict on update cascade;

alter table KORISNIK add constraint FK_PAKET_KORISNIKA foreign key (PAKET_LOJALNOSTI_ID)
      references PAKET_LOJALNOSTI (PAKET_LOJALNOSTI_ID) on delete restrict on update cascade;

alter table LOKACIJA add constraint FK_RELATIONSHIP_23 foreign key (MESTO_ID)
      references MESTO (MESTO_ID) on delete restrict on update cascade;

alter table MESTO add constraint FK_NALAZI_U_ZEMLJI foreign key (ZEMLJA_ID)
      references ZEMLJA (ZEMLJA_ID) on delete restrict on update cascade;

alter table OSTVARENA_USLUGA add constraint FK_OSTVARENA_U_TOKU_REZERVACIJE foreign key (REZERVACIJA_ID)
      references REZERVACIJA (REZERVACIJA_ID) on delete restrict on update cascade;

alter table OSTVARENA_USLUGA add constraint FK_USLUGA foreign key (TIP_USLUGE_ID, SMESTAJ_ID, CENOVNIK_USLUGE_ID)
      references PONUDJENA_USLUGA (TIP_USLUGE_ID, SMESTAJ_ID, CENOVNIK_USLUGE_ID) on delete restrict on update cascade;

alter table PONUDJENA_USLUGA add constraint FK_CENA_ZA_SMESTAJ foreign key (SMESTAJ_ID)
      references SMESTAJ (SMESTAJ_ID) on delete restrict on update cascade;

alter table PONUDJENA_USLUGA add constraint FK_TIP_PONUDJENE_USLUGE foreign key (TIP_USLUGE_ID)
      references TIP_USLUGE (TIP_USLUGE_ID) on delete restrict on update cascade;

alter table PRAVILO_CENE add constraint FK_IZADAVANI_SMESTAJ foreign key (SMESTAJ_ID)
      references SMESTAJ (SMESTAJ_ID) on delete restrict on update cascade;

alter table PRAVILO_NEDOSTUPNOSTI add constraint FK_NEDOSTUPNI_SMESTAJ foreign key (SMESTAJ_ID)
      references SMESTAJ (SMESTAJ_ID) on delete restrict on update cascade;

alter table PRIJAVA add constraint FK_DOMACIN_PRIJAVE foreign key (KOR_KORISNIK_ID)
      references KORISNIK (KORISNIK_ID) on delete restrict on update cascade;

alter table PRIJAVA add constraint FK_GOST_PRIJAVE foreign key (KORISNIK_ID)
      references KORISNIK (KORISNIK_ID) on delete restrict on update cascade;

alter table PRIVILEGIJE_BORAVKA add constraint FK_PAKET_PRIVILEGIJE_BORAVKA foreign key (PAKET_LOJALNOSTI_ID)
      references PAKET_LOJALNOSTI (PAKET_LOJALNOSTI_ID) on delete restrict on update cascade;

alter table PRIVILEGIJE_USLUGA add constraint FK_PAKET_PRIVILEGIJE_USLUGE foreign key (PAKET_LOJALNOSTI_ID)
      references PAKET_LOJALNOSTI (PAKET_LOJALNOSTI_ID) on delete restrict on update cascade;

alter table PRIVILEGIJE_USLUGA add constraint FK_TIP_PRIVILEGOVANE_USLUGE foreign key (TIP_USLUGE_ID)
      references TIP_USLUGE (TIP_USLUGE_ID) on delete restrict on update cascade;

alter table RECENZIJA add constraint FK_OCENJENA_REZERVACIJA foreign key (REZERVACIJA_ID)
      references REZERVACIJA (REZERVACIJA_ID) on delete restrict on update cascade;

alter table REZERVACIJA add constraint FK_GOST foreign key (KORISNIK_ID)
      references KORISNIK (KORISNIK_ID) on delete restrict on update cascade;

alter table REZERVACIJA add constraint FK_SMESTAJ foreign key (SMESTAJ_ID)
      references SMESTAJ (SMESTAJ_ID) on delete restrict on update cascade;

alter table SMESTAJ add constraint FK_SE_NALAZI foreign key (LOKACIJA_ID)
      references LOKACIJA (LOKACIJA_ID) on delete restrict on update cascade;

alter table SMESTAJ add constraint FK_VLASNIK foreign key (KORISNIK_ID)
      references KORISNIK (KORISNIK_ID) on delete set null on update cascade;

alter table SUPPORT_TICKET add constraint FK_KORISNIK_KOJI_TRAZI_POMOC foreign key (KORISNIK_ID)
      references KORISNIK (KORISNIK_ID) on delete restrict on update cascade;

alter table TRANSAKCIJA add constraint FK_TIP_TRANSAKCIJE foreign key (TIP_TRANSAKCIJE_ID)
      references TIP_TRANSAKCIJE (TIP_TRANSAKCIJE_ID) on delete restrict on update cascade;

alter table TRANSAKCIJA add constraint FK_TRANSAKCIJA_REZERAVIJE foreign key (REZERVACIJA_ID)
      references REZERVACIJA (REZERVACIJA_ID) on delete restrict on update cascade;

