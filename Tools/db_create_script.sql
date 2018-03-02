--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: konten; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE konten (
    konten_id integer NOT NULL,
    personen_id integer DEFAULT 0 NOT NULL,
    kreditinstitut_id integer DEFAULT 0 NOT NULL,
    kontonummer character varying(20) DEFAULT NULL::character varying,
    bemerkung character varying(50) DEFAULT NULL::character varying,
    standard boolean,
    iban character varying(22),
    gueltig boolean
);


ALTER TABLE public.konten OWNER TO domm;

--
-- Name: TABLE konten; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE konten IS 'enhaelt alle konten bei banken oder aehnlichen instituten';


--
-- Name: COLUMN konten.standard; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN konten.standard IS 'legt das standardkonto fest';


--
-- Name: COLUMN konten.iban; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN konten.iban IS 'Deutsche IBAN';


--
-- Name: COLUMN konten.gueltig; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN konten.gueltig IS 'gibt an ob das konto noch aktiv ist';


--
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
-- Name: personen; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE personen (
    personen_id integer NOT NULL,
    name character varying(45) DEFAULT ''::character varying NOT NULL,
    vorname character varying(45) DEFAULT ''::character varying NOT NULL,
    gueltig boolean
);


ALTER TABLE public.personen OWNER TO domm;

--
-- Name: TABLE personen; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE personen IS 'enthaelt alle personen welche mit konten verknuepft werden koennen';


--
-- Name: COLUMN personen.gueltig; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN personen.gueltig IS 'Feld fuer die Darstellung der Personen auf die sparbetraege aufgeteilt werden sollen. hat nichts mit der existens der person zu tun';


--
-- Name: Kontouebersicht; Type: VIEW; Schema: public; Owner: domm
--

CREATE VIEW "Kontouebersicht" WITH (security_barrier='true') AS
 SELECT personen.name,
    personen.vorname,
    kreditinstitut.kreditinstitut,
    kreditinstitut.blz,
    konten.kontonummer,
    konten.bemerkung,
    konten.konten_id
   FROM konten,
    personen,
    kreditinstitut
  WHERE ((konten.kreditinstitut_id = kreditinstitut.kreditinstitut_id) AND (personen.personen_id = konten.personen_id))
  ORDER BY personen.name, personen.vorname, kreditinstitut.kreditinstitut;


ALTER TABLE public."Kontouebersicht" OWNER TO domm;

--
-- Name: VIEW "Kontouebersicht"; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON VIEW "Kontouebersicht" IS 'Uebersicht ueber alle konten und deren eigentuemer';


--
-- Name: abschluss; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE abschluss (
    abschluss_id integer NOT NULL,
    liqui_monat date NOT NULL,
    abgeschlossen boolean DEFAULT false NOT NULL
);


ALTER TABLE public.abschluss OWNER TO domm;

--
-- Name: TABLE abschluss; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE abschluss IS 'definiert die monate die abgeschlossen sind und an denen im Liqui nichts mehr geaendert werden kann (betraege zur aufteilung koennen dann z.B. nicht mehr von personen entfernt und ihnen zugewiesen werden)';


--
-- Name: COLUMN abschluss.liqui_monat; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN abschluss.liqui_monat IS 'Monat im format YYYY-MM-01 der als abgeschlossen gekennzeichnet werden soll';


--
-- Name: abschluss_abschluss_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE abschluss_abschluss_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.abschluss_abschluss_id_seq OWNER TO domm;

--
-- Name: abschluss_abschluss_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE abschluss_abschluss_id_seq OWNED BY abschluss.abschluss_id;


--
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
-- Name: COLUMN aufteilung.liqui; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN aufteilung.liqui IS 'gibt an ob der Liquimonat in der Tabelle Transanktionen fuer diesen\r\nDatensatz gilt';


--
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
-- Name: aufteilung_aufteilungs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE aufteilung_aufteilungs_id_seq OWNED BY aufteilung.aufteilungs_id;


--
-- Name: buch_autor; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE buch_autor (
    id integer NOT NULL,
    autor character varying(100)
);


ALTER TABLE public.buch_autor OWNER TO domm;

--
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
-- Name: buch_autor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE buch_autor_id_seq OWNED BY buch_autor.id;


--
-- Name: buch_titel; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE buch_titel (
    id integer NOT NULL,
    autor_id integer NOT NULL,
    titel character varying(200)
);


ALTER TABLE public.buch_titel OWNER TO domm;

--
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
-- Name: buch_titel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE buch_titel_id_seq OWNED BY buch_titel.id;


--
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
-- Name: TABLE freistellungsauftraege; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE freistellungsauftraege IS 'auflistung von Freistellungsauftragen nach Bank und kontoinh';


--
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
-- Name: freistellungsauftraege_freistellung_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE freistellungsauftraege_freistellung_id_seq OWNED BY freistellungsauftraege.freistellung_id;


--
-- Name: income_per_person; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE income_per_person (
    ipp_id integer NOT NULL,
    personen_id bigint NOT NULL,
    transaktions_id bigint NOT NULL,
    betrag numeric DEFAULT 0 NOT NULL,
    split boolean NOT NULL,
    hinweis character varying(100)
);


ALTER TABLE public.income_per_person OWNER TO domm;

--
-- Name: TABLE income_per_person; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE income_per_person IS 'Income per Person to calculate the Profit per Person';


--
-- Name: COLUMN income_per_person.ipp_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN income_per_person.ipp_id IS 'income (i) per (p) person (p) id (index)';


--
-- Name: COLUMN income_per_person.split; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN income_per_person.split IS 'gibt an ob der Betrag ein gesplitteter ist, wenn ja gibt es einen teilbetrag der gleichen transaktions ID auch fuer einen andere Personen ID';


--
-- Name: COLUMN income_per_person.hinweis; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN income_per_person.hinweis IS 'hinweise zu zugewiesenen werten die sonst nicht so ohne weiteres sinn ergeben';


--
-- Name: ipp_ipp_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE ipp_ipp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ipp_ipp_id_seq OWNER TO domm;

--
-- Name: ipp_ipp_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE ipp_ipp_id_seq OWNED BY income_per_person.ipp_id;


--
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
-- Name: TABLE jahresausgaben; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE jahresausgaben IS 'enthaelt historisch gepflegte fixe Jahresausgaben um den mon';


--
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
-- Name: jahresausgaben_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE jahresausgaben_id_seq OWNED BY jahresausgaben.id;


--
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
-- Name: TABLE kfz; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE kfz IS 'Angabe ueber Banken und deren BLZ';


--
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
-- Name: kfz_kfz_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE kfz_kfz_id_seq OWNED BY kfz.kfz_id;


--
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
-- Name: konten_konten_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE konten_konten_id_seq OWNED BY konten.konten_id;


--
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
-- Name: COLUMN kontenereignisse.gueltig; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN kontenereignisse.gueltig IS 'gibt an ob ein ereigniss noch eine Guelitgkeit besitz, damit kann es bei einigen abfragen aussen vorgelassen werden';


--
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
-- Name: kontenereignisse_ereigniss_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE kontenereignisse_ereigniss_id_seq OWNED BY kontenereignisse.ereigniss_id;


--
-- Name: kraftstoffe; Type: TABLE; Schema: public; Owner: domm; Tablespace: 
--

CREATE TABLE kraftstoffe (
    kraftstoff_id integer NOT NULL,
    bez character varying(30) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.kraftstoffe OWNER TO domm;

--
-- Name: TABLE kraftstoffe; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE kraftstoffe IS 'liste aller tankbaren Kraftstoffe';


--
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
-- Name: kraftstoffe_kraftstoff_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE kraftstoffe_kraftstoff_id_seq OWNED BY kraftstoffe.kraftstoff_id;


--
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
-- Name: kreditinstitut_kreditinstitut_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE kreditinstitut_kreditinstitut_id_seq OWNED BY kreditinstitut.kreditinstitut_id;


--
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
-- Name: TABLE mtlausgaben; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE mtlausgaben IS 'monatliche fixausgaben';


--
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
-- Name: mtlausgaben_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE mtlausgaben_id_seq OWNED BY mtlausgaben.id;


--
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
-- Name: personen_personen_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE personen_personen_id_seq OWNED BY personen.personen_id;


--
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
-- Name: TABLE tankdaten; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE tankdaten IS 'enthaelt die details zu betankungen durch ec und bar zahlung';


--
-- Name: COLUMN tankdaten.liter; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN tankdaten.liter IS 'getankte liter';


--
-- Name: COLUMN tankdaten.km; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN tankdaten.km IS 'gefahrene gesammtkilometer';


--
-- Name: COLUMN tankdaten.datum_bar; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN tankdaten.datum_bar IS 'betrag fuer bar bezahlte betankungen';


--
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
-- Name: tankdaten_tankdaten_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE tankdaten_tankdaten_id_seq OWNED BY tankdaten.tankdaten_id;


--
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
-- Name: COLUMN transaktionen.transaktions_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.transaktions_id IS 'fortlaufende Zahl (muss eindeutig sein)';


--
-- Name: COLUMN transaktionen.soll_haben; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.soll_haben IS 's fuer Soll h fuer Haben';


--
-- Name: COLUMN transaktionen.konten_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.konten_id IS 'verweist auf den Datensatz in der Tabelle Konten und von da kommt man zum Kreditinstitut';


--
-- Name: COLUMN transaktionen.datum; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.datum IS 'wertstellungstermin , erst ab mitte 2004 beruecksichtigt davor ausfuehrungszeitpunkt';


--
-- Name: COLUMN transaktionen.betrag; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.betrag IS 'menge die gutgeschrieben oder abgehoben wurde';


--
-- Name: COLUMN transaktionen.buchtext; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.buchtext IS 'hier kann man sich noch kurze notizen zu einer Buchung machen';


--
-- Name: COLUMN transaktionen.ereigniss_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.ereigniss_id IS 'hier ist die Verknuepfung zu einer Tabelle die ein bestimmtes\r\nEreigniss beziffert (z.B. kennzeichnet es alle ueberweisungen die zu eine bestimmten schuldenabzahlung gehoeren so kann man schnell feststellen wieviel man schon gezahlt hat)normalerweise 0';


--
-- Name: COLUMN transaktionen.liqui_monat; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN transaktionen.liqui_monat IS 'der Monat in dem der Betrag in die Liquiditaet eingerechnet wird z.B. 01.07.05 fuer Juli 05 (der Tag ist immer 01)';


--
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
-- Name: transaktionen_transaktions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE transaktionen_transaktions_id_seq OWNED BY transaktionen.transaktions_id;


--
-- Name: abschluss_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY abschluss ALTER COLUMN abschluss_id SET DEFAULT nextval('abschluss_abschluss_id_seq'::regclass);


--
-- Name: aufteilungs_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY aufteilung ALTER COLUMN aufteilungs_id SET DEFAULT nextval('aufteilung_aufteilungs_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY buch_autor ALTER COLUMN id SET DEFAULT nextval('buch_autor_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY buch_titel ALTER COLUMN id SET DEFAULT nextval('buch_titel_id_seq'::regclass);


--
-- Name: freistellung_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY freistellungsauftraege ALTER COLUMN freistellung_id SET DEFAULT nextval('freistellungsauftraege_freistellung_id_seq'::regclass);


--
-- Name: ipp_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY income_per_person ALTER COLUMN ipp_id SET DEFAULT nextval('ipp_ipp_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY jahresausgaben ALTER COLUMN id SET DEFAULT nextval('jahresausgaben_id_seq'::regclass);


--
-- Name: kfz_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY kfz ALTER COLUMN kfz_id SET DEFAULT nextval('kfz_kfz_id_seq'::regclass);


--
-- Name: konten_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY konten ALTER COLUMN konten_id SET DEFAULT nextval('konten_konten_id_seq'::regclass);


--
-- Name: ereigniss_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY kontenereignisse ALTER COLUMN ereigniss_id SET DEFAULT nextval('kontenereignisse_ereigniss_id_seq'::regclass);


--
-- Name: kraftstoff_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY kraftstoffe ALTER COLUMN kraftstoff_id SET DEFAULT nextval('kraftstoffe_kraftstoff_id_seq'::regclass);


--
-- Name: kreditinstitut_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY kreditinstitut ALTER COLUMN kreditinstitut_id SET DEFAULT nextval('kreditinstitut_kreditinstitut_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY mtlausgaben ALTER COLUMN id SET DEFAULT nextval('mtlausgaben_id_seq'::regclass);


--
-- Name: personen_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY personen ALTER COLUMN personen_id SET DEFAULT nextval('personen_personen_id_seq'::regclass);


--
-- Name: tankdaten_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY tankdaten ALTER COLUMN tankdaten_id SET DEFAULT nextval('tankdaten_tankdaten_id_seq'::regclass);


--
-- Name: transaktions_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY transaktionen ALTER COLUMN transaktions_id SET DEFAULT nextval('transaktionen_transaktions_id_seq'::regclass);


--
-- Name: aufteilungs_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY aufteilung
    ADD CONSTRAINT aufteilungs_id PRIMARY KEY (aufteilungs_id);


--
-- Name: ereigniss_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY kontenereignisse
    ADD CONSTRAINT ereigniss_id PRIMARY KEY (ereigniss_id);


--
-- Name: freistellung_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY freistellungsauftraege
    ADD CONSTRAINT freistellung_id PRIMARY KEY (freistellung_id);


--
-- Name: id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY buch_autor
    ADD CONSTRAINT id PRIMARY KEY (id);


--
-- Name: id_buch_titel; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY buch_titel
    ADD CONSTRAINT id_buch_titel PRIMARY KEY (id);


--
-- Name: jahresausgaben_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY jahresausgaben
    ADD CONSTRAINT jahresausgaben_id PRIMARY KEY (id);


--
-- Name: key_abschluss; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY abschluss
    ADD CONSTRAINT key_abschluss PRIMARY KEY (abschluss_id);


--
-- Name: key_ipp; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY income_per_person
    ADD CONSTRAINT key_ipp PRIMARY KEY (ipp_id);


--
-- Name: kfz_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY kfz
    ADD CONSTRAINT kfz_id PRIMARY KEY (kfz_id);


--
-- Name: konten_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY konten
    ADD CONSTRAINT konten_id PRIMARY KEY (konten_id);


--
-- Name: kraftstoff_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY kraftstoffe
    ADD CONSTRAINT kraftstoff_id PRIMARY KEY (kraftstoff_id);


--
-- Name: kreditinstitut_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY kreditinstitut
    ADD CONSTRAINT kreditinstitut_id PRIMARY KEY (kreditinstitut_id);


--
-- Name: mtlausgaben_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY mtlausgaben
    ADD CONSTRAINT mtlausgaben_id PRIMARY KEY (id);


--
-- Name: personen_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY personen
    ADD CONSTRAINT personen_id PRIMARY KEY (personen_id);


--
-- Name: tankdaten_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY tankdaten
    ADD CONSTRAINT tankdaten_id PRIMARY KEY (tankdaten_id);


--
-- Name: transaktions_id; Type: CONSTRAINT; Schema: public; Owner: domm; Tablespace: 
--

ALTER TABLE ONLY transaktionen
    ADD CONSTRAINT transaktions_id PRIMARY KEY (transaktions_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

