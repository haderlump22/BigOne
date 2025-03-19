--
-- PostgreSQL database dump
--

-- Dumped from database version 14.13 (Ubuntu 14.13-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.13 (Ubuntu 14.13-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: get_rest(integer, integer); Type: FUNCTION; Schema: public; Owner: domm
--

CREATE FUNCTION public.get_rest(ereignisid integer, kontoid integer) RETURNS numeric
    LANGUAGE plpgsql
    AS $$  
    Declare  
     SummeHaben DECIMAL;
     SummeSoll DECIMAL;
    Begin  
       select sum(betrag)   
       into SummeHaben  
       from transaktionen  
       where soll_haben = 'h' and konten_id = KontoId and ereigniss_id = EreignisId;  
       
       select sum(betrag)   
       into SummeSoll  
       from transaktionen  
       where soll_haben = 's' and konten_id = KontoId and ereigniss_id = EreignisId;  
       
       -- eventuell gibt es keine Datensätze, der Summe 0 zuweisen
       IF SummeHaben IS NULL THEN
          SummeHaben := 0;
       END IF;
       
       IF SummeSoll IS NULL THEN
          SummeSoll := 0;
       END IF;
       
       RETURN SummeHaben - SummeSoll;
    End;  
    $$;


ALTER FUNCTION public.get_rest(ereignisid integer, kontoid integer) OWNER TO domm;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: konten; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.konten (
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

COMMENT ON TABLE public.konten IS 'enhaelt alle konten bei banken oder aehnlichen instituten';


--
-- Name: COLUMN konten.standard; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.konten.standard IS 'legt das standardkonto fest';


--
-- Name: COLUMN konten.iban; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.konten.iban IS 'Deutsche IBAN';


--
-- Name: COLUMN konten.gueltig; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.konten.gueltig IS 'gibt an ob das konto noch aktiv ist';


--
-- Name: kreditinstitut; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.kreditinstitut (
    kreditinstitut_id integer NOT NULL,
    kreditinstitut character varying(60) DEFAULT ''::character varying NOT NULL,
    blz character varying(20) DEFAULT ''::character varying NOT NULL,
    gilt_bis date
);


ALTER TABLE public.kreditinstitut OWNER TO domm;

--
-- Name: personen; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.personen (
    personen_id integer NOT NULL,
    name character varying(45) DEFAULT ''::character varying NOT NULL,
    vorname character varying(45) DEFAULT ''::character varying NOT NULL,
    gueltig boolean
);


ALTER TABLE public.personen OWNER TO domm;

--
-- Name: TABLE personen; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE public.personen IS 'enthaelt alle personen welche mit konten verknuepft werden koennen';


--
-- Name: COLUMN personen.gueltig; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.personen.gueltig IS 'Feld fuer die Darstellung der Personen auf die sparbetraege aufgeteilt werden sollen. hat nichts mit der existens der person zu tun';


--
-- Name: Kontouebersicht; Type: VIEW; Schema: public; Owner: domm
--

CREATE VIEW public."Kontouebersicht" WITH (security_barrier='true') AS
 SELECT personen.name,
    personen.vorname,
    kreditinstitut.kreditinstitut,
    kreditinstitut.blz,
    konten.kontonummer,
    konten.bemerkung,
    konten.konten_id
   FROM public.konten,
    public.personen,
    public.kreditinstitut
  WHERE ((konten.kreditinstitut_id = kreditinstitut.kreditinstitut_id) AND (personen.personen_id = konten.personen_id) AND (konten.gueltig = true))
  ORDER BY personen.name, personen.vorname, kreditinstitut.kreditinstitut;


ALTER TABLE public."Kontouebersicht" OWNER TO domm;

--
-- Name: VIEW "Kontouebersicht"; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON VIEW public."Kontouebersicht" IS 'Uebersicht ueber alle konten und deren eigentuemer';


--
-- Name: abschluss; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.abschluss (
    abschluss_id integer NOT NULL,
    liqui_monat date NOT NULL,
    abgeschlossen boolean DEFAULT false NOT NULL
);


ALTER TABLE public.abschluss OWNER TO domm;

--
-- Name: TABLE abschluss; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE public.abschluss IS 'definiert die monate die abgeschlossen sind und an denen im Liqui nichts mehr geaendert werden kann (betraege zur aufteilung koennen dann z.B. nicht mehr von personen entfernt und ihnen zugewiesen werden)';


--
-- Name: COLUMN abschluss.liqui_monat; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.abschluss.liqui_monat IS 'Monat im format YYYY-MM-01 der als abgeschlossen gekennzeichnet werden soll';


--
-- Name: abschluss_abschluss_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.abschluss_abschluss_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.abschluss_abschluss_id_seq OWNER TO domm;

--
-- Name: abschluss_abschluss_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.abschluss_abschluss_id_seq OWNED BY public.abschluss.abschluss_id;


--
-- Name: aufteilung; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.aufteilung (
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

COMMENT ON COLUMN public.aufteilung.liqui IS 'gibt an ob der Liquimonat in der Tabelle Transanktionen fuer diesen\r\nDatensatz gilt';


--
-- Name: aufteilung_aufteilungs_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.aufteilung_aufteilungs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.aufteilung_aufteilungs_id_seq OWNER TO domm;

--
-- Name: aufteilung_aufteilungs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.aufteilung_aufteilungs_id_seq OWNED BY public.aufteilung.aufteilungs_id;


--
-- Name: buch_autor; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.buch_autor (
    id integer NOT NULL,
    autor character varying(100)
);


ALTER TABLE public.buch_autor OWNER TO domm;

--
-- Name: buch_autor_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.buch_autor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.buch_autor_id_seq OWNER TO domm;

--
-- Name: buch_autor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.buch_autor_id_seq OWNED BY public.buch_autor.id;


--
-- Name: buch_titel; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.buch_titel (
    id integer NOT NULL,
    autor_id integer NOT NULL,
    titel character varying(200)
);


ALTER TABLE public.buch_titel OWNER TO domm;

--
-- Name: buch_titel_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.buch_titel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.buch_titel_id_seq OWNER TO domm;

--
-- Name: buch_titel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.buch_titel_id_seq OWNED BY public.buch_titel.id;


--
-- Name: freistellungsauftraege; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.freistellungsauftraege (
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

COMMENT ON TABLE public.freistellungsauftraege IS 'auflistung von Freistellungsauftragen nach Bank und kontoinh';


--
-- Name: freistellungsauftraege_freistellung_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.freistellungsauftraege_freistellung_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.freistellungsauftraege_freistellung_id_seq OWNER TO domm;

--
-- Name: freistellungsauftraege_freistellung_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.freistellungsauftraege_freistellung_id_seq OWNED BY public.freistellungsauftraege.freistellung_id;


--
-- Name: ha_gehaltsgrundlagen; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.ha_gehaltsgrundlagen (
    id integer NOT NULL,
    partei_id smallint NOT NULL,
    betrag numeric NOT NULL,
    gilt_bis date,
    bemerkung character varying(500)
);


ALTER TABLE public.ha_gehaltsgrundlagen OWNER TO domm;

--
-- Name: TABLE ha_gehaltsgrundlagen; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE public.ha_gehaltsgrundlagen IS 'enthält die Gehaltsgrundlagen der in das Haushaltskonto Einbringenden Parteien';


--
-- Name: COLUMN ha_gehaltsgrundlagen.gilt_bis; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.ha_gehaltsgrundlagen.gilt_bis IS 'Datum des Monats bis zu dem einschliesslich der Wert gilt.
Dabei wird der Monat nur durch den 01. definiert gilt aber in gänze';


--
-- Name: ha_gehaltsgrundlagen_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.ha_gehaltsgrundlagen_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ha_gehaltsgrundlagen_id_seq OWNER TO domm;

--
-- Name: ha_gehaltsgrundlagen_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.ha_gehaltsgrundlagen_id_seq OWNED BY public.ha_gehaltsgrundlagen.id;


--
-- Name: income_per_person; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.income_per_person (
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

COMMENT ON TABLE public.income_per_person IS 'Income per Person to calculate the Profit per Person';


--
-- Name: COLUMN income_per_person.ipp_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.income_per_person.ipp_id IS 'income (i) per (p) person (p) id (index)';


--
-- Name: COLUMN income_per_person.split; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.income_per_person.split IS 'gibt an ob der Betrag ein gesplitteter ist, wenn ja gibt es einen teilbetrag der gleichen transaktions ID auch fuer einen andere Personen ID';


--
-- Name: COLUMN income_per_person.hinweis; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.income_per_person.hinweis IS 'hinweise zu zugewiesenen werten die sonst nicht so ohne weiteres sinn ergeben';


--
-- Name: ipp_ipp_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.ipp_ipp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ipp_ipp_id_seq OWNER TO domm;

--
-- Name: ipp_ipp_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.ipp_ipp_id_seq OWNED BY public.income_per_person.ipp_id;


--
-- Name: jahresausgaben; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.jahresausgaben (
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

COMMENT ON TABLE public.jahresausgaben IS 'enthaelt historisch gepflegte fixe Jahresausgaben um den mon';


--
-- Name: jahresausgaben_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.jahresausgaben_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.jahresausgaben_id_seq OWNER TO domm;

--
-- Name: jahresausgaben_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.jahresausgaben_id_seq OWNED BY public.jahresausgaben.id;


--
-- Name: kfz; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.kfz (
    kfz_id integer NOT NULL,
    fahrgestellnummer character varying(30) DEFAULT ''::character varying NOT NULL,
    kennzeichen character varying(12) DEFAULT ''::character varying NOT NULL,
    typ character varying(30) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.kfz OWNER TO domm;

--
-- Name: TABLE kfz; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE public.kfz IS 'Angabe ueber Banken und deren BLZ';


--
-- Name: kfz_kfz_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.kfz_kfz_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.kfz_kfz_id_seq OWNER TO domm;

--
-- Name: kfz_kfz_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.kfz_kfz_id_seq OWNED BY public.kfz.kfz_id;


--
-- Name: konten_konten_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.konten_konten_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.konten_konten_id_seq OWNER TO domm;

--
-- Name: konten_konten_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.konten_konten_id_seq OWNED BY public.konten.konten_id;


--
-- Name: kontenereignisse; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.kontenereignisse (
    ereigniss_id integer NOT NULL,
    ereigniss_krzbez character varying(50) DEFAULT ''::character varying NOT NULL,
    beschreibung character varying(250) DEFAULT ''::character varying NOT NULL,
    gueltig boolean
);


ALTER TABLE public.kontenereignisse OWNER TO domm;

--
-- Name: COLUMN kontenereignisse.gueltig; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.kontenereignisse.gueltig IS 'gibt an ob ein ereigniss noch eine Guelitgkeit besitz, damit kann es bei einigen abfragen aussen vorgelassen werden';


--
-- Name: kontenereignisse_ereigniss_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.kontenereignisse_ereigniss_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.kontenereignisse_ereigniss_id_seq OWNER TO domm;

--
-- Name: kontenereignisse_ereigniss_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.kontenereignisse_ereigniss_id_seq OWNED BY public.kontenereignisse.ereigniss_id;


--
-- Name: kraftstoffe; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.kraftstoffe (
    kraftstoff_id integer NOT NULL,
    bez character varying(30) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.kraftstoffe OWNER TO domm;

--
-- Name: TABLE kraftstoffe; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON TABLE public.kraftstoffe IS 'liste aller tankbaren Kraftstoffe';


--
-- Name: kraftstoffe_kraftstoff_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.kraftstoffe_kraftstoff_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.kraftstoffe_kraftstoff_id_seq OWNER TO domm;

--
-- Name: kraftstoffe_kraftstoff_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.kraftstoffe_kraftstoff_id_seq OWNED BY public.kraftstoffe.kraftstoff_id;


--
-- Name: kreditinstitut_kreditinstitut_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.kreditinstitut_kreditinstitut_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.kreditinstitut_kreditinstitut_id_seq OWNER TO domm;

--
-- Name: kreditinstitut_kreditinstitut_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.kreditinstitut_kreditinstitut_id_seq OWNED BY public.kreditinstitut.kreditinstitut_id;


--
-- Name: mtlausgaben; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.mtlausgaben (
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

COMMENT ON TABLE public.mtlausgaben IS 'monatliche fixausgaben';


--
-- Name: mtlausgaben_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.mtlausgaben_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mtlausgaben_id_seq OWNER TO domm;

--
-- Name: mtlausgaben_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.mtlausgaben_id_seq OWNED BY public.mtlausgaben.id;


--
-- Name: personen_personen_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.personen_personen_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.personen_personen_id_seq OWNER TO domm;

--
-- Name: personen_personen_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.personen_personen_id_seq OWNED BY public.personen.personen_id;


--
-- Name: tankdaten; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.tankdaten (
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

COMMENT ON TABLE public.tankdaten IS 'enthaelt die details zu betankungen durch ec und bar zahlung';


--
-- Name: COLUMN tankdaten.liter; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.tankdaten.liter IS 'getankte liter';


--
-- Name: COLUMN tankdaten.km; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.tankdaten.km IS 'gefahrene gesammtkilometer';


--
-- Name: COLUMN tankdaten.datum_bar; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.tankdaten.datum_bar IS 'betrag fuer bar bezahlte betankungen';


--
-- Name: tankdaten_tankdaten_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.tankdaten_tankdaten_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tankdaten_tankdaten_id_seq OWNER TO domm;

--
-- Name: tankdaten_tankdaten_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.tankdaten_tankdaten_id_seq OWNED BY public.tankdaten.tankdaten_id;


--
-- Name: transaktionen; Type: TABLE; Schema: public; Owner: domm
--

CREATE TABLE public.transaktionen (
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

COMMENT ON COLUMN public.transaktionen.transaktions_id IS 'fortlaufende Zahl (muss eindeutig sein)';


--
-- Name: COLUMN transaktionen.soll_haben; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.transaktionen.soll_haben IS 's fuer Soll h fuer Haben';


--
-- Name: COLUMN transaktionen.konten_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.transaktionen.konten_id IS 'verweist auf den Datensatz in der Tabelle Konten und von da kommt man zum Kreditinstitut';


--
-- Name: COLUMN transaktionen.datum; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.transaktionen.datum IS 'wertstellungstermin , erst ab mitte 2004 beruecksichtigt davor ausfuehrungszeitpunkt';


--
-- Name: COLUMN transaktionen.betrag; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.transaktionen.betrag IS 'menge die gutgeschrieben oder abgehoben wurde';


--
-- Name: COLUMN transaktionen.buchtext; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.transaktionen.buchtext IS 'hier kann man sich noch kurze notizen zu einer Buchung machen';


--
-- Name: COLUMN transaktionen.ereigniss_id; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.transaktionen.ereigniss_id IS 'hier ist die Verknuepfung zu einer Tabelle die ein bestimmtes\r\nEreigniss beziffert (z.B. kennzeichnet es alle ueberweisungen die zu eine bestimmten schuldenabzahlung gehoeren so kann man schnell feststellen wieviel man schon gezahlt hat)normalerweise 0';


--
-- Name: COLUMN transaktionen.liqui_monat; Type: COMMENT; Schema: public; Owner: domm
--

COMMENT ON COLUMN public.transaktionen.liqui_monat IS 'der Monat in dem der Betrag in die Liquiditaet eingerechnet wird z.B. 01.07.05 fuer Juli 05 (der Tag ist immer 01)';


--
-- Name: transaktionen_transaktions_id_seq; Type: SEQUENCE; Schema: public; Owner: domm
--

CREATE SEQUENCE public.transaktionen_transaktions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.transaktionen_transaktions_id_seq OWNER TO domm;

--
-- Name: transaktionen_transaktions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: domm
--

ALTER SEQUENCE public.transaktionen_transaktions_id_seq OWNED BY public.transaktionen.transaktions_id;


--
-- Name: abschluss abschluss_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.abschluss ALTER COLUMN abschluss_id SET DEFAULT nextval('public.abschluss_abschluss_id_seq'::regclass);


--
-- Name: aufteilung aufteilungs_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.aufteilung ALTER COLUMN aufteilungs_id SET DEFAULT nextval('public.aufteilung_aufteilungs_id_seq'::regclass);


--
-- Name: buch_autor id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.buch_autor ALTER COLUMN id SET DEFAULT nextval('public.buch_autor_id_seq'::regclass);


--
-- Name: buch_titel id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.buch_titel ALTER COLUMN id SET DEFAULT nextval('public.buch_titel_id_seq'::regclass);


--
-- Name: freistellungsauftraege freistellung_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.freistellungsauftraege ALTER COLUMN freistellung_id SET DEFAULT nextval('public.freistellungsauftraege_freistellung_id_seq'::regclass);


--
-- Name: ha_gehaltsgrundlagen id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.ha_gehaltsgrundlagen ALTER COLUMN id SET DEFAULT nextval('public.ha_gehaltsgrundlagen_id_seq'::regclass);


--
-- Name: income_per_person ipp_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.income_per_person ALTER COLUMN ipp_id SET DEFAULT nextval('public.ipp_ipp_id_seq'::regclass);


--
-- Name: jahresausgaben id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.jahresausgaben ALTER COLUMN id SET DEFAULT nextval('public.jahresausgaben_id_seq'::regclass);


--
-- Name: kfz kfz_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.kfz ALTER COLUMN kfz_id SET DEFAULT nextval('public.kfz_kfz_id_seq'::regclass);


--
-- Name: konten konten_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.konten ALTER COLUMN konten_id SET DEFAULT nextval('public.konten_konten_id_seq'::regclass);


--
-- Name: kontenereignisse ereigniss_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.kontenereignisse ALTER COLUMN ereigniss_id SET DEFAULT nextval('public.kontenereignisse_ereigniss_id_seq'::regclass);


--
-- Name: kraftstoffe kraftstoff_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.kraftstoffe ALTER COLUMN kraftstoff_id SET DEFAULT nextval('public.kraftstoffe_kraftstoff_id_seq'::regclass);


--
-- Name: kreditinstitut kreditinstitut_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.kreditinstitut ALTER COLUMN kreditinstitut_id SET DEFAULT nextval('public.kreditinstitut_kreditinstitut_id_seq'::regclass);


--
-- Name: mtlausgaben id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.mtlausgaben ALTER COLUMN id SET DEFAULT nextval('public.mtlausgaben_id_seq'::regclass);


--
-- Name: personen personen_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.personen ALTER COLUMN personen_id SET DEFAULT nextval('public.personen_personen_id_seq'::regclass);


--
-- Name: tankdaten tankdaten_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.tankdaten ALTER COLUMN tankdaten_id SET DEFAULT nextval('public.tankdaten_tankdaten_id_seq'::regclass);


--
-- Name: transaktionen transaktions_id; Type: DEFAULT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.transaktionen ALTER COLUMN transaktions_id SET DEFAULT nextval('public.transaktionen_transaktions_id_seq'::regclass);


--
-- Name: aufteilung aufteilungs_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.aufteilung
    ADD CONSTRAINT aufteilungs_id PRIMARY KEY (aufteilungs_id);


--
-- Name: kontenereignisse ereigniss_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.kontenereignisse
    ADD CONSTRAINT ereigniss_id PRIMARY KEY (ereigniss_id);


--
-- Name: freistellungsauftraege freistellung_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.freistellungsauftraege
    ADD CONSTRAINT freistellung_id PRIMARY KEY (freistellung_id);


--
-- Name: buch_autor id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.buch_autor
    ADD CONSTRAINT id PRIMARY KEY (id);


--
-- Name: buch_titel id_buch_titel; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.buch_titel
    ADD CONSTRAINT id_buch_titel PRIMARY KEY (id);


--
-- Name: jahresausgaben jahresausgaben_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.jahresausgaben
    ADD CONSTRAINT jahresausgaben_id PRIMARY KEY (id);


--
-- Name: abschluss key_abschluss; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.abschluss
    ADD CONSTRAINT key_abschluss PRIMARY KEY (abschluss_id);


--
-- Name: ha_gehaltsgrundlagen key_gehaltsgrundlagen; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.ha_gehaltsgrundlagen
    ADD CONSTRAINT key_gehaltsgrundlagen PRIMARY KEY (id);


--
-- Name: income_per_person key_ipp; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.income_per_person
    ADD CONSTRAINT key_ipp PRIMARY KEY (ipp_id);


--
-- Name: kfz kfz_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.kfz
    ADD CONSTRAINT kfz_id PRIMARY KEY (kfz_id);


--
-- Name: konten konten_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.konten
    ADD CONSTRAINT konten_id PRIMARY KEY (konten_id);


--
-- Name: kraftstoffe kraftstoff_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.kraftstoffe
    ADD CONSTRAINT kraftstoff_id PRIMARY KEY (kraftstoff_id);


--
-- Name: kreditinstitut kreditinstitut_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.kreditinstitut
    ADD CONSTRAINT kreditinstitut_id PRIMARY KEY (kreditinstitut_id);


--
-- Name: mtlausgaben mtlausgaben_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.mtlausgaben
    ADD CONSTRAINT mtlausgaben_id PRIMARY KEY (id);


--
-- Name: personen personen_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.personen
    ADD CONSTRAINT personen_id PRIMARY KEY (personen_id);


--
-- Name: tankdaten tankdaten_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.tankdaten
    ADD CONSTRAINT tankdaten_id PRIMARY KEY (tankdaten_id);


--
-- Name: transaktionen transaktions_id; Type: CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.transaktionen
    ADD CONSTRAINT transaktions_id PRIMARY KEY (transaktions_id);


--
-- Name: ha_gehaltsgrundlagen keyext_personen; Type: FK CONSTRAINT; Schema: public; Owner: domm
--

ALTER TABLE ONLY public.ha_gehaltsgrundlagen
    ADD CONSTRAINT keyext_personen FOREIGN KEY (partei_id) REFERENCES public.personen(personen_id) NOT VALID;


--
-- PostgreSQL database dump complete
--

