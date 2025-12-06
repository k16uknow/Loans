--
-- PostgreSQL database dump
--


-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

-- Started on 2025-12-02 01:50:51

--SET statement_timeout = 0;
--SET lock_timeout = 0;
--SET idle_in_transaction_session_timeout = 0;
--SET transaction_timeout = 0;
--SET client_encoding = 'UTF8';
--SET standard_conforming_strings = on;
--SELECT pg_catalog.set_config('search_path', '', false);
--SET check_function_bodies = false;
--SET xmloption = content;
--SET client_min_messages = warning;
--SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--



--
-- TOC entry 234 (class 1255 OID 24576)
-- Name: calculate_total_payments(integer); Type: FUNCTION; Schema: public; Owner: loans_user
--

CREATE FUNCTION public.calculate_total_payments(p_loan_id integer) RETURNS numeric
    LANGUAGE plpgsql
    AS $$
DECLARE 
	total numeric(12,5);
BEGIN
	
	SELECT SUM(amount) INTO total FROM payment WHERE loan_id = p_loan_id;
	RETURN COALESCE(total, 0);
END;
$$;


ALTER FUNCTION public.calculate_total_payments(p_loan_id integer) OWNER TO loans_user;

--
-- TOC entry 235 (class 1255 OID 32830)
-- Name: update_installment_status(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_installment_status() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  -- handle insert
  IF TG_OP = 'INSERT' THEN
    UPDATE installment i
    SET paid_amount = COALESCE(i.paid_amount, 0) + NEW.amount,
        status = CASE 
                   WHEN COALESCE(i.paid_amount, 0) + NEW.amount >= i.amount 
                   THEN 'PAID' 
                   ELSE 'PENDING'
                 END
    WHERE i.id = NEW.installment_id AND i.loan_id = NEW.loan_id;
  END IF;

  -- (similar logic for UPDATE/DELETE if needed)

  RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_installment_status() OWNER TO loans_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 225 (class 1259 OID 16449)
-- Name: borrower; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.borrower (
    id integer NOT NULL,
    first_name character varying(50) NOT NULL,
    paternal_last character varying(50) NOT NULL,
    maternal_last character varying(50) NOT NULL,
    phone character varying(15) NOT NULL,
    address character varying(200) NOT NULL,
    occupation character varying(100) NOT NULL,
    workplace character varying(200) NOT NULL,
    status character varying(10) NOT NULL,
    rating integer DEFAULT 1 NOT NULL
);


ALTER TABLE public.borrower OWNER TO loans_user;

--
-- TOC entry 224 (class 1259 OID 16448)
-- Name: borrower_id_seq; Type: SEQUENCE; Schema: public; Owner: loans_user
--

CREATE SEQUENCE public.borrower_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER SEQUENCE public.borrower_id_seq OWNER TO loans_user;

--
-- TOC entry 5067 (class 0 OID 0)
-- Dependencies: 224
-- Name: borrower_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: loans_user
--

ALTER SEQUENCE public.borrower_id_seq OWNED BY public.borrower.id;


--
-- TOC entry 229 (class 1259 OID 16545)
-- Name: installment; Type: TABLE; Schema: public; Owner: loans_user
--

CREATE TABLE public.installment (
    id integer NOT NULL,
    loan_id integer NOT NULL,
    plan_version integer DEFAULT 1 NOT NULL,
    installment_no integer NOT NULL,
    due_date date NOT NULL,
    amount numeric(12,4) NOT NULL,
    total_payments numeric(12,4) NOT NULL,
    statement_balance numeric(12,4) NOT NULL,
    status character varying(10),
    paid_amount numeric(12,4) DEFAULT 0.0000
);


ALTER TABLE public.installment OWNER TO loans_user;

--
-- TOC entry 5068 (class 0 OID 0)
-- Dependencies: 229
-- Name: TABLE installment; Type: COMMENT; Schema: public; Owner: loans_user
--

COMMENT ON TABLE public.installment IS 'Total amount payable: A term often used in finance agreements (like car finance) for the full amount you commit to paying back, which includes the initial loan amount, deposit, interest, and all charges.

Statement balance: The total amount owed on a specific statement date, including all posted transactions at that time. ';


--
-- TOC entry 5069 (class 0 OID 0)
-- Dependencies: 229
-- Name: COLUMN installment.plan_version; Type: COMMENT; Schema: public; Owner: loans_user
--

COMMENT ON COLUMN public.installment.plan_version IS 'Each time installments are regenerated (for example, after an extra payment, refinancing, or rate change), you create a new batch of installments tied to the same loan.
Old installments remain stored but are inactive, archived, or superseded.';


--
-- TOC entry 5070 (class 0 OID 0)
-- Dependencies: 229
-- Name: COLUMN installment.status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.installment.status IS '
INACTIVE (no more schedule needed)
REPLACED (old schedule versioned out)
ACTIVE
PAID';


--
-- TOC entry 228 (class 1259 OID 16544)
-- Name: installment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.installment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER SEQUENCE public.installment_id_seq OWNER TO loans_user;

--
-- TOC entry 5071 (class 0 OID 0)
-- Dependencies: 228
-- Name: installment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: loans_user
--

ALTER SEQUENCE public.installment_id_seq OWNED BY public.installment.id;


--
-- TOC entry 233 (class 1259 OID 32825)
-- Name: installment_payments; Type: VIEW; Schema: public; Owner: loans_user
--

CREATE VIEW public.installment_payments AS
SELECT
    NULL::integer AS id,
    NULL::integer AS loan_id,
    NULL::date AS due_date,
    NULL::integer AS installment_no,
    NULL::numeric(12,4) AS amount,
    NULL::numeric AS payment_amount;


ALTER VIEW public.installment_payments OWNER TO loans_user;

--
-- TOC entry 227 (class 1259 OID 16500)
-- Name: loan; Type: TABLE; Schema: public; Owner: loans_user
--

CREATE TABLE public.loan (
    id integer NOT NULL,
    majority_partner_id integer NOT NULL,
    minority_partner_id integer NOT NULL,
    borrower_id integer NOT NULL,
    principal numeric(12,2) NOT NULL,
    number_of_payments integer NOT NULL,
    interest_rate numeric(5,2) NOT NULL,
    future_value numeric(12,2) NOT NULL,
    release_date date NOT NULL,
    first_payment_date date NOT NULL,
    last_payment_date date NOT NULL,
    gross_profit numeric(12,2) NOT NULL,
    majority_partner_pct numeric(5,2) NOT NULL,
    majority_partner_profit numeric(12,2) NOT NULL,
    minority_partner_pct numeric(5,2) NOT NULL,
    minority_partner_profit numeric(12,2) NOT NULL,
    concept_required character varying NOT NULL,
    axen numeric(12,2) NOT NULL,
    insert_date timestamp without time zone NOT NULL,
    comments text,
    plan_version integer DEFAULT 1 CONSTRAINT loan_plan_verion_not_null NOT NULL,
    status character varying(10) DEFAULT 'ACTIVE'::character varying NOT NULL
);


ALTER TABLE public.loan OWNER TO loans_user;

--
-- TOC entry 226 (class 1259 OID 16499)
-- Name: loan_id_seq; Type: SEQUENCE; Schema: public; Owner: loans_user
--

CREATE SEQUENCE public.loan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER SEQUENCE public.loan_id_seq OWNER TO loans_user;

--
-- TOC entry 5072 (class 0 OID 0)
-- Dependencies: 226
-- Name: loan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.loan_id_seq OWNED BY public.loan.id;


--
-- TOC entry 221 (class 1259 OID 16391)
-- Name: partner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.partner (
    id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.partner OWNER TO loans_user;

--
-- TOC entry 232 (class 1259 OID 24582)
-- Name: loan_overview; Type: VIEW; Schema: public; Owner: loans_user
--

CREATE VIEW public.loan_overview AS
 SELECT l.id AS loan_id,
    l.majority_partner_id,
    p_maj.name AS majority_partner,
    l.minority_partner_id,
    p_min.name AS minority_partner,
    l.borrower_id,
    concat(b.first_name, ' ', b.paternal_last, ' ', b.maternal_last) AS borrower_name,
    l.principal,
    l.number_of_payments,
    l.interest_rate,
    l.future_value,
    public.calculate_total_payments(l.id) AS total_payments,
    l.release_date,
    l.first_payment_date,
    l.last_payment_date,
    l.gross_profit,
    l.majority_partner_pct,
    l.majority_partner_profit,
    l.minority_partner_pct,
    l.minority_partner_profit,
    l.concept_required,
    l.axen,
    l.insert_date,
    l.comments,
    l.plan_version
   FROM (((public.loan l
     JOIN public.borrower b ON ((l.borrower_id = b.id)))
     JOIN public.partner p_maj ON ((l.majority_partner_id = p_maj.id)))
     JOIN public.partner p_min ON ((l.minority_partner_id = p_min.id)));


ALTER VIEW public.loan_overview OWNER TO loans_user;

--
-- TOC entry 220 (class 1259 OID 16390)
-- Name: partner_id_seq; Type: SEQUENCE; Schema: public; Owner: loans_user
--

CREATE SEQUENCE public.partner_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.partner_id_seq OWNER TO loans_user;

--
-- TOC entry 5073 (class 0 OID 0)
-- Dependencies: 220
-- Name: partner_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.partner_id_seq OWNED BY public.partner.id;


--
-- TOC entry 230 (class 1259 OID 16584)
-- Name: payment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payment (
    id integer NOT NULL,
    loan_id integer CONSTRAINT "payment_loan.id_not_null" NOT NULL,
    installment_id integer NOT NULL,
    amount numeric(12,5) NOT NULL,
    insert_date timestamp without time zone NOT NULL,
    payment_date date NOT NULL
);


ALTER TABLE public.payment OWNER TO loans_user;

--
-- TOC entry 231 (class 1259 OID 16603)
-- Name: payment_id_seq; Type: SEQUENCE; Schema: public; Owner: loans_user
--

CREATE SEQUENCE public.payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER SEQUENCE public.payment_id_seq OWNER TO loans_user;

--
-- TOC entry 5074 (class 0 OID 0)
-- Dependencies: 231
-- Name: payment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: loans_user
--

ALTER SEQUENCE public.payment_id_seq OWNED BY public.payment.id;


--
-- TOC entry 4892 (class 2604 OID 16452)
-- Name: borrower id; Type: DEFAULT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.borrower ALTER COLUMN id SET DEFAULT nextval('public.borrower_id_seq'::regclass);


--
-- TOC entry 4897 (class 2604 OID 16548)
-- Name: installment id; Type: DEFAULT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.installment ALTER COLUMN id SET DEFAULT nextval('public.installment_id_seq'::regclass);


--
-- TOC entry 4894 (class 2604 OID 16503)
-- Name: loan id; Type: DEFAULT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.loan ALTER COLUMN id SET DEFAULT nextval('public.loan_id_seq'::regclass);


--
-- TOC entry 4891 (class 2604 OID 16394)
-- Name: partner id; Type: DEFAULT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.partner ALTER COLUMN id SET DEFAULT nextval('public.partner_id_seq'::regclass);


--
-- TOC entry 4900 (class 2604 OID 16604)
-- Name: payment id; Type: DEFAULT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.payment ALTER COLUMN id SET DEFAULT nextval('public.payment_id_seq'::regclass);


--
-- TOC entry 4904 (class 2606 OID 16467)
-- Name: borrower borrower_pkey; Type: CONSTRAINT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.borrower
    ADD CONSTRAINT borrower_pkey PRIMARY KEY (id);


--
-- TOC entry 4906 (class 2606 OID 16595)
-- Name: loan loan_pkey; Type: CONSTRAINT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.loan
    ADD CONSTRAINT loan_pkey PRIMARY KEY (id, borrower_id, principal, majority_partner_id, minority_partner_id, interest_rate, number_of_payments, future_value, release_date, first_payment_date, last_payment_date, insert_date, axen, concept_required, minority_partner_profit, minority_partner_pct, majority_partner_profit, majority_partner_pct, gross_profit);


--
-- TOC entry 4908 (class 2606 OID 16556)
-- Name: installment loan_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.installment
    ADD CONSTRAINT loan_schedule_pkey PRIMARY KEY (id);


--
-- TOC entry 4902 (class 2606 OID 16398)
-- Name: partner partner_pkey; Type: CONSTRAINT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.partner
    ADD CONSTRAINT partner_pkey PRIMARY KEY (id);


--
-- TOC entry 4910 (class 2606 OID 16593)
-- Name: payment payment_pkey; Type: CONSTRAINT; Schema: public; Owner: loans_user
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);


--
-- TOC entry 5060 (class 2618 OID 32828)
-- Name: installment_payments _RETURN; Type: RULE; Schema: public; Owner: loans_user
--

CREATE OR REPLACE VIEW public.installment_payments AS
 SELECT i.id,
    i.loan_id,
    i.due_date,
    i.installment_no,
    i.amount,
    COALESCE(sum(p.amount), (0)::numeric) AS payment_amount
   FROM (public.installment i
     LEFT JOIN public.payment p ON ((p.installment_id = i.id)))
  WHERE ((i.status)::text = 'ACTIVE'::text)
  GROUP BY i.id, p.installment_id, i.due_date
 HAVING ((max(i.plan_version) = ( SELECT max(ii.plan_version) AS max
           FROM public.installment ii
          WHERE (ii.loan_id = i.loan_id)
         LIMIT 1)) AND (COALESCE(sum(p.amount), (0)::numeric) < i.amount))
  ORDER BY i.installment_no;


--
-- TOC entry 4911 (class 2620 OID 32832)
-- Name: payment trg_update_installment_status; Type: TRIGGER; Schema: public; Owner: loans_user
--

CREATE TRIGGER trg_update_installment_status AFTER INSERT ON public.payment FOR EACH ROW EXECUTE FUNCTION public.update_installment_status();


-- Completed on 2025-12-02 01:50:51

--
-- PostgreSQL database dump complete
--

