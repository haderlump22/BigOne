--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.9
-- Dumped by pg_dump version 9.3.9
-- Started on 2015-07-22 22:33:22 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 2134 (class 1262 OID 16385)
-- Name: bigone; Type: DATABASE; Schema: -; Owner: domm
--

CREATE DATABASE bigone WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'de_DE.UTF-8' LC_CTYPE = 'de_DE.UTF-8';


ALTER DATABASE bigone OWNER TO domm;

\connect bigone

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 198 (class 3079 OID 11789)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2137 (class 0 OID 0)
-- Dependencies: 198
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 170 (class 1259 OID 16386)
-- Name: aufteilung; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE aufteilung (
    aufteilungs_id integer NOT NULL,
    transaktions_id integer DEFAULT 0 NOT NULL,
    betrag numeric DEFAULT (0)::numeric NOT NULL,
    ereigniss_id smallint DEFAULT (0)::smallint NOT NULL,
    liqui boolean DEFAULT false NOT NULL
);


ALTER TABLE public.aufteilung OWNER TO domm;

--
-- TOC entry 2138 (class 0 OID 0)
-- Dependencies: 170
-- Name: COLUMN aufteilung.liqui; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN aufteilung.liqui IS 'gibt an ob der Liquimonat in der Tabelle Transanktionen fuer diesen\r\nDatensatz gilt';


--
-- TOC entry 171 (class 1259 OID 16396)
-- Name: aufteilung_aufteilungs_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE aufteilung_aufteilungs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.aufteilung_aufteilungs_id_seq OWNER TO domm;

--
-- TOC entry 2139 (class 0 OID 0)
-- Dependencies: 171
-- Name: aufteilung_aufteilungs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE aufteilung_aufteilungs_id_seq OWNED BY aufteilung.aufteilungs_id;


--
-- TOC entry 172 (class 1259 OID 16398)
-- Name: buch_autor; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE buch_autor (
    id integer NOT NULL,
    autor character varying(100)
);


ALTER TABLE public.buch_autor OWNER TO domm;

--
-- TOC entry 173 (class 1259 OID 16401)
-- Name: buch_autor_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE buch_autor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.buch_autor_id_seq OWNER TO domm;

--
-- TOC entry 2140 (class 0 OID 0)
-- Dependencies: 173
-- Name: buch_autor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE buch_autor_id_seq OWNED BY buch_autor.id;


--
-- TOC entry 174 (class 1259 OID 16403)
-- Name: buch_titel; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE buch_titel (
    id integer NOT NULL,
    autor_id integer NOT NULL,
    titel character varying(200)
);


ALTER TABLE public.buch_titel OWNER TO domm;

--
-- TOC entry 175 (class 1259 OID 16406)
-- Name: buch_titel_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE buch_titel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.buch_titel_id_seq OWNER TO domm;

--
-- TOC entry 2141 (class 0 OID 0)
-- Dependencies: 175
-- Name: buch_titel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE buch_titel_id_seq OWNED BY buch_titel.id;


--
-- TOC entry 176 (class 1259 OID 16408)
-- Name: freistellungsauftraege; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE freistellungsauftraege (
    freistellung_id integer NOT NULL,
    personen_id integer DEFAULT 0 NOT NULL,
    kreditinstitut_id integer DEFAULT 0 NOT NULL,
    betrag numeric DEFAULT (0)::numeric NOT NULL,
    gestellt_am date NOT NULL,
    gilt_von date NOT NULL,
    gilt_bis date
);


ALTER TABLE public.freistellungsauftraege OWNER TO domm;

--
-- TOC entry 2142 (class 0 OID 0)
-- Dependencies: 176
-- Name: TABLE freistellungsauftraege; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE freistellungsauftraege IS 'auflistung von Freistellungsauftragen nach Bank und kontoinh';


--
-- TOC entry 177 (class 1259 OID 16417)
-- Name: freistellungsauftraege_freistellung_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE freistellungsauftraege_freistellung_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.freistellungsauftraege_freistellung_id_seq OWNER TO domm;

--
-- TOC entry 2143 (class 0 OID 0)
-- Dependencies: 177
-- Name: freistellungsauftraege_freistellung_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE freistellungsauftraege_freistellung_id_seq OWNED BY freistellungsauftraege.freistellung_id;


--
-- TOC entry 178 (class 1259 OID 16419)
-- Name: jahresausgaben; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE jahresausgaben (
    id integer NOT NULL,
    betrag numeric DEFAULT (0)::numeric NOT NULL,
    bez character varying(50),
    gilt_ab date NOT NULL,
    gilt_bis date NOT NULL
);


ALTER TABLE public.jahresausgaben OWNER TO domm;

--
-- TOC entry 2144 (class 0 OID 0)
-- Dependencies: 178
-- Name: TABLE jahresausgaben; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE jahresausgaben IS 'enthaelt historisch gepflegte fixe Jahresausgaben um den mon';


--
-- TOC entry 179 (class 1259 OID 16426)
-- Name: jahresausgaben_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE jahresausgaben_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.jahresausgaben_id_seq OWNER TO domm;

--
-- TOC entry 2145 (class 0 OID 0)
-- Dependencies: 179
-- Name: jahresausgaben_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE jahresausgaben_id_seq OWNED BY jahresausgaben.id;


--
-- TOC entry 180 (class 1259 OID 16428)
-- Name: kfz; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE kfz (
    kfz_id integer NOT NULL,
    fahrgestellnummer character varying(30) DEFAULT ''::character varying NOT NULL,
    kennzeichen character varying(12) DEFAULT ''::character varying NOT NULL,
    typ character varying(30) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.kfz OWNER TO domm;

--
-- TOC entry 2146 (class 0 OID 0)
-- Dependencies: 180
-- Name: TABLE kfz; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE kfz IS 'Angabe ueber Banken und deren BLZ';


--
-- TOC entry 181 (class 1259 OID 16434)
-- Name: kfz_kfz_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE kfz_kfz_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.kfz_kfz_id_seq OWNER TO domm;

--
-- TOC entry 2147 (class 0 OID 0)
-- Dependencies: 181
-- Name: kfz_kfz_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE kfz_kfz_id_seq OWNED BY kfz.kfz_id;


--
-- TOC entry 182 (class 1259 OID 16436)
-- Name: konten; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE konten (
    konten_id integer NOT NULL,
    personen_id integer DEFAULT 0 NOT NULL,
    kreditinstitut_id integer DEFAULT 0 NOT NULL,
    kontonummer character varying(20) DEFAULT NULL::character varying,
    bemerkung character varying(50) DEFAULT NULL::character varying,
    standard boolean
);


ALTER TABLE public.konten OWNER TO domm;

--
-- TOC entry 2148 (class 0 OID 0)
-- Dependencies: 182
-- Name: TABLE konten; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE konten IS 'enhaelt alle konten bei banken oder aehnlichen instituten';


--
-- TOC entry 2149 (class 0 OID 0)
-- Dependencies: 182
-- Name: COLUMN konten.standard; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN konten.standard IS 'legt das standardkonto fest';


--
-- TOC entry 183 (class 1259 OID 16443)
-- Name: konten_konten_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE konten_konten_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.konten_konten_id_seq OWNER TO domm;

--
-- TOC entry 2150 (class 0 OID 0)
-- Dependencies: 183
-- Name: konten_konten_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE konten_konten_id_seq OWNED BY konten.konten_id;


--
-- TOC entry 184 (class 1259 OID 16445)
-- Name: kontenereignisse; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE kontenereignisse (
    ereigniss_id integer NOT NULL,
    ereigniss_krzbez character varying(50) DEFAULT ''::character varying NOT NULL,
    beschreibung character varying(250) DEFAULT ''::character varying NOT NULL,
    gueltig boolean
);


ALTER TABLE public.kontenereignisse OWNER TO domm;

--
-- TOC entry 2151 (class 0 OID 0)
-- Dependencies: 184
-- Name: COLUMN kontenereignisse.gueltig; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN kontenereignisse.gueltig IS 'gibt an ob ein ereigniss noch eine Guelitgkeit besitz, damit kann es bei einigen abfragen aussen vorgelassen werden';


--
-- TOC entry 185 (class 1259 OID 16450)
-- Name: kontenereignisse_ereigniss_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE kontenereignisse_ereigniss_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.kontenereignisse_ereigniss_id_seq OWNER TO domm;

--
-- TOC entry 2152 (class 0 OID 0)
-- Dependencies: 185
-- Name: kontenereignisse_ereigniss_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE kontenereignisse_ereigniss_id_seq OWNED BY kontenereignisse.ereigniss_id;


--
-- TOC entry 186 (class 1259 OID 16452)
-- Name: kraftstoffe; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE kraftstoffe (
    kraftstoff_id integer NOT NULL,
    bez character varying(30) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.kraftstoffe OWNER TO domm;

--
-- TOC entry 2153 (class 0 OID 0)
-- Dependencies: 186
-- Name: TABLE kraftstoffe; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE kraftstoffe IS 'liste aller tankbaren Kraftstoffe';


--
-- TOC entry 187 (class 1259 OID 16456)
-- Name: kraftstoffe_kraftstoff_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE kraftstoffe_kraftstoff_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.kraftstoffe_kraftstoff_id_seq OWNER TO domm;

--
-- TOC entry 2154 (class 0 OID 0)
-- Dependencies: 187
-- Name: kraftstoffe_kraftstoff_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE kraftstoffe_kraftstoff_id_seq OWNED BY kraftstoffe.kraftstoff_id;


--
-- TOC entry 188 (class 1259 OID 16458)
-- Name: kreditinstitut; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE kreditinstitut (
    kreditinstitut_id integer NOT NULL,
    kreditinstitut character varying(60) DEFAULT ''::character varying NOT NULL,
    blz character varying(20) DEFAULT ''::character varying NOT NULL,
    gilt_bis date
);


ALTER TABLE public.kreditinstitut OWNER TO domm;

--
-- TOC entry 189 (class 1259 OID 16463)
-- Name: kreditinstitut_kreditinstitut_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE kreditinstitut_kreditinstitut_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.kreditinstitut_kreditinstitut_id_seq OWNER TO domm;

--
-- TOC entry 2155 (class 0 OID 0)
-- Dependencies: 189
-- Name: kreditinstitut_kreditinstitut_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE kreditinstitut_kreditinstitut_id_seq OWNED BY kreditinstitut.kreditinstitut_id;


--
-- TOC entry 190 (class 1259 OID 16465)
-- Name: mtlausgaben; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE mtlausgaben (
    id integer NOT NULL,
    betrag numeric DEFAULT (0)::numeric NOT NULL,
    ereigniss_id integer DEFAULT 0 NOT NULL,
    gilt_ab date NOT NULL,
    gilt_bis date NOT NULL,
    hart smallint DEFAULT (0)::smallint NOT NULL
);


ALTER TABLE public.mtlausgaben OWNER TO domm;

--
-- TOC entry 2156 (class 0 OID 0)
-- Dependencies: 190
-- Name: TABLE mtlausgaben; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE mtlausgaben IS 'monatliche fixausgaben';


--
-- TOC entry 191 (class 1259 OID 16474)
-- Name: mtlausgaben_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE mtlausgaben_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mtlausgaben_id_seq OWNER TO domm;

--
-- TOC entry 2157 (class 0 OID 0)
-- Dependencies: 191
-- Name: mtlausgaben_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE mtlausgaben_id_seq OWNED BY mtlausgaben.id;


--
-- TOC entry 192 (class 1259 OID 16476)
-- Name: personen; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE personen (
    personen_id integer NOT NULL,
    name character varying(45) DEFAULT ''::character varying NOT NULL,
    vorname character varying(45) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.personen OWNER TO domm;

--
-- TOC entry 2158 (class 0 OID 0)
-- Dependencies: 192
-- Name: TABLE personen; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE personen IS 'enthaelt alle personen welche mit konten verknuepft werden koennen';


--
-- TOC entry 193 (class 1259 OID 16481)
-- Name: personen_personen_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE personen_personen_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.personen_personen_id_seq OWNER TO domm;

--
-- TOC entry 2159 (class 0 OID 0)
-- Dependencies: 193
-- Name: personen_personen_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE personen_personen_id_seq OWNED BY personen.personen_id;


--
-- TOC entry 194 (class 1259 OID 16483)
-- Name: tankdaten; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE tankdaten (
    tankdaten_id integer NOT NULL,
    transaktions_id integer DEFAULT 0 NOT NULL,
    liter numeric DEFAULT (0)::numeric NOT NULL,
    km integer DEFAULT 0 NOT NULL,
    kraftstoff_id integer DEFAULT 0 NOT NULL,
    datum_bar date,
    betrag_bar numeric DEFAULT (0)::numeric,
    kfz_id integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.tankdaten OWNER TO domm;

--
-- TOC entry 2160 (class 0 OID 0)
-- Dependencies: 194
-- Name: TABLE tankdaten; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE tankdaten IS 'enthaelt die details zu betankungen durch ec und bar zahlung';


--
-- TOC entry 2161 (class 0 OID 0)
-- Dependencies: 194
-- Name: COLUMN tankdaten.liter; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN tankdaten.liter IS 'getankte liter';


--
-- TOC entry 2162 (class 0 OID 0)
-- Dependencies: 194
-- Name: COLUMN tankdaten.km; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN tankdaten.km IS 'gefahrene gesammtkilometer';


--
-- TOC entry 2163 (class 0 OID 0)
-- Dependencies: 194
-- Name: COLUMN tankdaten.datum_bar; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN tankdaten.datum_bar IS 'betrag fuer bar bezahlte betankungen';


--
-- TOC entry 195 (class 1259 OID 16495)
-- Name: tankdaten_tankdaten_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE tankdaten_tankdaten_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tankdaten_tankdaten_id_seq OWNER TO domm;

--
-- TOC entry 2164 (class 0 OID 0)
-- Dependencies: 195
-- Name: tankdaten_tankdaten_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE tankdaten_tankdaten_id_seq OWNED BY tankdaten.tankdaten_id;


--
-- TOC entry 196 (class 1259 OID 16497)
-- Name: transaktionen; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE transaktionen (
    transaktions_id integer NOT NULL,
    soll_haben character varying(1) DEFAULT ''::character varying NOT NULL,
    konten_id integer DEFAULT 0 NOT NULL,
    datum date NOT NULL,
    betrag numeric DEFAULT (0)::numeric NOT NULL,
    buchtext character varying(500) DEFAULT ''::character varying NOT NULL,
    ereigniss_id integer DEFAULT 0 NOT NULL,
    liqui_monat date
);


ALTER TABLE public.transaktionen OWNER TO domm;

--
-- TOC entry 2165 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN transaktionen.transaktions_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.transaktions_id IS 'fortlaufende Zahl (muss eindeutig sein)';


--
-- TOC entry 2166 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN transaktionen.soll_haben; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.soll_haben IS 's fuer Soll h fuer Haben';


--
-- TOC entry 2167 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN transaktionen.konten_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.konten_id IS 'verweist auf den Datensatz in der Tabelle Konten und von da kommt man zum Kreditinstitut';


--
-- TOC entry 2168 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN transaktionen.datum; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.datum IS 'wertstellungstermin , erst ab mitte 2004 beruecksichtigt davor ausfuehrungszeitpunkt';


--
-- TOC entry 2169 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN transaktionen.betrag; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.betrag IS 'menge die gutgeschrieben oder abgehoben wurde';


--
-- TOC entry 2170 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN transaktionen.buchtext; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.buchtext IS 'hier kann man sich noch kurze notizen zu einer Buchung machen';


--
-- TOC entry 2171 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN transaktionen.ereigniss_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.ereigniss_id IS 'hier ist die Verknuepfung zu einer Tabelle die ein bestimmtes\r\nEreigniss beziffert (z.B. kennzeichnet es alle ueberweisungen die zu eine bestimmten schuldenabzahlung gehoeren so kann man schnell feststellen wieviel man schon gezahlt hat)normalerweise 0';


--
-- TOC entry 2172 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN transaktionen.liqui_monat; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.liqui_monat IS 'der Monat in dem der Betrag in die Liquiditaet eingerechnet wird z.B. 01.07.05 fuer Juli 05 (der Tag ist immer 01)';


--
-- TOC entry 197 (class 1259 OID 16508)
-- Name: transaktionen_transaktions_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE transaktionen_transaktions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.transaktionen_transaktions_id_seq OWNER TO domm;

--
-- TOC entry 2173 (class 0 OID 0)
-- Dependencies: 197
-- Name: transaktionen_transaktions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE transaktionen_transaktions_id_seq OWNED BY transaktionen.transaktions_id;


--
-- TOC entry 1949 (class 2604 OID 16510)
-- Name: aufteilungs_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY aufteilung ALTER COLUMN aufteilungs_id SET DEFAULT nextval('aufteilung_aufteilungs_id_seq'::regclass);


--
-- TOC entry 1950 (class 2604 OID 16511)
-- Name: id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY buch_autor ALTER COLUMN id SET DEFAULT nextval('buch_autor_id_seq'::regclass);


--
-- TOC entry 1951 (class 2604 OID 16512)
-- Name: id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY buch_titel ALTER COLUMN id SET DEFAULT nextval('buch_titel_id_seq'::regclass);


--
-- TOC entry 1955 (class 2604 OID 16513)
-- Name: freistellung_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY freistellungsauftraege ALTER COLUMN freistellung_id SET DEFAULT nextval('freistellungsauftraege_freistellung_id_seq'::regclass);


--
-- TOC entry 1957 (class 2604 OID 16514)
-- Name: id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY jahresausgaben ALTER COLUMN id SET DEFAULT nextval('jahresausgaben_id_seq'::regclass);


--
-- TOC entry 1961 (class 2604 OID 16515)
-- Name: kfz_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY kfz ALTER COLUMN kfz_id SET DEFAULT nextval('kfz_kfz_id_seq'::regclass);


--
-- TOC entry 1966 (class 2604 OID 16516)
-- Name: konten_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY konten ALTER COLUMN konten_id SET DEFAULT nextval('konten_konten_id_seq'::regclass);


--
-- TOC entry 1969 (class 2604 OID 16517)
-- Name: ereigniss_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY kontenereignisse ALTER COLUMN ereigniss_id SET DEFAULT nextval('kontenereignisse_ereigniss_id_seq'::regclass);


--
-- TOC entry 1971 (class 2604 OID 16518)
-- Name: kraftstoff_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY kraftstoffe ALTER COLUMN kraftstoff_id SET DEFAULT nextval('kraftstoffe_kraftstoff_id_seq'::regclass);


--
-- TOC entry 1974 (class 2604 OID 16519)
-- Name: kreditinstitut_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY kreditinstitut ALTER COLUMN kreditinstitut_id SET DEFAULT nextval('kreditinstitut_kreditinstitut_id_seq'::regclass);


--
-- TOC entry 1978 (class 2604 OID 16520)
-- Name: id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY mtlausgaben ALTER COLUMN id SET DEFAULT nextval('mtlausgaben_id_seq'::regclass);


--
-- TOC entry 1981 (class 2604 OID 16521)
-- Name: personen_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY personen ALTER COLUMN personen_id SET DEFAULT nextval('personen_personen_id_seq'::regclass);


--
-- TOC entry 1988 (class 2604 OID 16522)
-- Name: tankdaten_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY tankdaten ALTER COLUMN tankdaten_id SET DEFAULT nextval('tankdaten_tankdaten_id_seq'::regclass);


--
-- TOC entry 1994 (class 2604 OID 16523)
-- Name: transaktions_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY transaktionen ALTER COLUMN transaktions_id SET DEFAULT nextval('transaktionen_transaktions_id_seq'::regclass);


--
-- TOC entry 1996 (class 2606 OID 16525)
-- Name: aufteilungs_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY aufteilung
    ADD CONSTRAINT aufteilungs_id PRIMARY KEY (aufteilungs_id);


--
-- TOC entry 2010 (class 2606 OID 16527)
-- Name: ereigniss_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY kontenereignisse
    ADD CONSTRAINT ereigniss_id PRIMARY KEY (ereigniss_id);


--
-- TOC entry 2002 (class 2606 OID 16529)
-- Name: freistellung_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY freistellungsauftraege
    ADD CONSTRAINT freistellung_id PRIMARY KEY (freistellung_id);


--
-- TOC entry 1998 (class 2606 OID 16531)
-- Name: id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY buch_autor
    ADD CONSTRAINT id PRIMARY KEY (id);


--
-- TOC entry 2000 (class 2606 OID 16533)
-- Name: id_buch_titel; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY buch_titel
    ADD CONSTRAINT id_buch_titel PRIMARY KEY (id);


--
-- TOC entry 2004 (class 2606 OID 16535)
-- Name: jahresausgaben_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY jahresausgaben
    ADD CONSTRAINT jahresausgaben_id PRIMARY KEY (id);


--
-- TOC entry 2006 (class 2606 OID 16537)
-- Name: kfz_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY kfz
    ADD CONSTRAINT kfz_id PRIMARY KEY (kfz_id);


--
-- TOC entry 2008 (class 2606 OID 16539)
-- Name: konten_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY konten
    ADD CONSTRAINT konten_id PRIMARY KEY (konten_id);


--
-- TOC entry 2012 (class 2606 OID 16541)
-- Name: kraftstoff_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY kraftstoffe
    ADD CONSTRAINT kraftstoff_id PRIMARY KEY (kraftstoff_id);


--
-- TOC entry 2014 (class 2606 OID 16543)
-- Name: kreditinstitut_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY kreditinstitut
    ADD CONSTRAINT kreditinstitut_id PRIMARY KEY (kreditinstitut_id);


--
-- TOC entry 2016 (class 2606 OID 16545)
-- Name: mtlausgaben_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY mtlausgaben
    ADD CONSTRAINT mtlausgaben_id PRIMARY KEY (id);


--
-- TOC entry 2018 (class 2606 OID 16547)
-- Name: personen_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY personen
    ADD CONSTRAINT personen_id PRIMARY KEY (personen_id);


--
-- TOC entry 2020 (class 2606 OID 16549)
-- Name: tankdaten_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY tankdaten
    ADD CONSTRAINT tankdaten_id PRIMARY KEY (tankdaten_id);


--
-- TOC entry 2022 (class 2606 OID 16551)
-- Name: transaktions_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY transaktionen
    ADD CONSTRAINT transaktions_id PRIMARY KEY (transaktions_id);


--
-- TOC entry 2136 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2015-07-22 22:33:22 CEST

--
-- PostgreSQL database dump complete
--

