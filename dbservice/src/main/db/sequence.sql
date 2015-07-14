
CREATE SEQUENCE maplog.alert_events_alert_event_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 2477 (class 1259 OID 1411880)
-- Dependencies: 2812 2813 2814 7
-- Name: alert_events; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.alert_events (
    alert_event_id integer DEFAULT nextval('maplog.alert_events_alert_event_id_seq'::regclass) NOT NULL,
    time_stamp timestamp with time zone NOT NULL,
    solved boolean DEFAULT false,
    alert_id integer NOT NULL,
    unit_id bigint NOT NULL,
    gid integer,
    solving boolean DEFAULT false NOT NULL
);


--
-- TOC entry 2473 (class 1259 OID 1410750)
-- Dependencies: 7
-- Name: alert_queries_query_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.alert_queries_query_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2478 (class 1259 OID 1411913)
-- Dependencies: 2815 7
-- Name: alert_queries; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.alert_queries (
    query_id integer DEFAULT nextval('maplog.alert_queries_query_id_seq'::regclass) NOT NULL,
    query_string character varying(200),
    alert_id integer NOT NULL
);


--
-- TOC entry 2474 (class 1259 OID 1410752)
-- Dependencies: 7
-- Name: alert_queries_to_units_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.alert_queries_to_units_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2479 (class 1259 OID 1411925)
-- Dependencies: 2816 7
-- Name: alert_queries_to_units; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.alert_queries_to_units (
    id integer DEFAULT nextval('maplog.alert_queries_to_units_id_seq'::regclass) NOT NULL,
    query_id integer NOT NULL,
    unit_id bigint NOT NULL,
    last_status_alert_query boolean,
    last_status_time_stamp timestamp with time zone
);


--
-- TOC entry 2475 (class 1259 OID 1410754)
-- Dependencies: 7
-- Name: alerts_alert_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.alerts_alert_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2476 (class 1259 OID 1411870)
-- Dependencies: 2811 7
-- Name: alerts; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.alerts (
    alert_id integer DEFAULT nextval('maplog.maplog.alerts_alert_id_seq'::regclass) NOT NULL,
    alert_description character varying(100)
);





--
-- TOC entry 2426 (class 1259 OID 19757)
-- Dependencies: 7 1065
-- Name: geom_alerts_conf; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.geom_alerts_conf (
    geom_alerts_conf_id integer NOT NULL,
    unit_id integer NOT NULL,
    the_geom geometry NOT NULL,
    relation character varying(3)
);


--
-- TOC entry 2427 (class 1259 OID 19763)
-- Dependencies: 7 2426
-- Name: geom_alerts_conf_geom_alerts_conf_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.geom_alerts_conf_geom_alerts_conf_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3008 (class 0 OID 0)
-- Dependencies: 2427
-- Name: geom_alerts_conf_geom_alerts_conf_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.geom_alerts_conf_geom_alerts_conf_id_seq OWNED BY maplog.geom_alerts_conf.geom_alerts_conf_id;


SET default_with_oids = true;

--
-- TOC entry 2428 (class 1259 OID 19765)
-- Dependencies: 7
-- Name: geometry_columns; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.geometry_columns (
    f_table_catalog character varying(256) NOT NULL,
    f_table_schema character varying(256) NOT NULL,
    f_table_name character varying(256) NOT NULL,
    f_geometry_column character varying(256) NOT NULL,
    coord_dimension integer NOT NULL,
    srid integer NOT NULL,
    type character varying(30) NOT NULL
);


SET default_with_oids = false;

--
-- TOC entry 2429 (class 1259 OID 19771)
-- Dependencies: 2778 7
-- Name: groups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.groups (
    id integer NOT NULL,
    group_name character varying(100),
    parent_group_id integer,
    has_children boolean DEFAULT false
);


--
-- TOC entry 2430 (class 1259 OID 19775)
-- Dependencies: 2429 7
-- Name: groups_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.groups_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3009 (class 0 OID 0)
-- Dependencies: 2430
-- Name: groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.groups_id_seq OWNED BY maplog.groups.id;


--
-- TOC entry 2481 (class 1259 OID 1412029)
-- Dependencies: 7
-- Name: ignition_status; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.ignition_status (
    observation_id integer NOT NULL,
    gid integer,
    time_stamp timestamp with time zone NOT NULL,
    value double precision NOT NULL,
    unit_id bigint NOT NULL
);


--
-- TOC entry 2480 (class 1259 OID 1412011)
-- Dependencies: 7
-- Name: last_ignition_status; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.last_ignition_status (
    observation_id integer NOT NULL,
    gid integer,
    time_stamp timestamp with time zone NOT NULL,
    value double precision NOT NULL,
    unit_id bigint NOT NULL
);


--
-- TOC entry 2431 (class 1259 OID 19777)
-- Dependencies: 1065 7
-- Name: last_units_positions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.last_units_positions (
    gid integer NOT NULL,
    the_geom geometry NOT NULL,
    unit_id bigint NOT NULL,
    time_stamp timestamp with time zone NOT NULL,
    speed double precision,
    dop double precision
);


--
-- TOC entry 2432 (class 1259 OID 19783)
-- Dependencies: 2431 7
-- Name: last_units_positions_gid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.last_units_positions_gid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3010 (class 0 OID 0)
-- Dependencies: 2432
-- Name: last_units_positions_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.last_units_positions_gid_seq OWNED BY maplog.last_units_positions.gid;


CREATE TABLE maplog.obs_alerts_conf (
    obs_alerts_conf_id integer NOT NULL,
    unit_id bigint NOT NULL,
    phenomenon_id character varying(100) NOT NULL,
    value double precision NOT NULL,
    relation character varying(3)
);


--
-- TOC entry 2434 (class 1259 OID 19788)
-- Dependencies: 2433 7
-- Name: obs_alerts_conf_obs_alerts_conf_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.obs_alerts_conf_obs_alerts_conf_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3012 (class 0 OID 0)
-- Dependencies: 2434
-- Name: obs_alerts_conf_obs_alerts_conf_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.obs_alerts_conf_obs_alerts_conf_id_seq OWNED BY maplog.obs_alerts_conf.obs_alerts_conf_id;


--
-- TOC entry 2492 (class 1259 OID 1472758)
-- Dependencies: 7
-- Name: obs_to_obs; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.obs_to_obs (
    main_obs_id integer NOT NULL,
    sec_obs_id integer NOT NULL
);


--
-- TOC entry 2435 (class 1259 OID 19790)
-- Dependencies: 7
-- Name: observations; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.observations (
    observation_id integer NOT NULL,
    gid integer,
    time_stamp timestamp with time zone NOT NULL,
    observed_value double precision NOT NULL,
    sensor_id bigint,
    unit_id bigint NOT NULL
);


--
-- TOC entry 2436 (class 1259 OID 19793)
-- Dependencies: 7 2435
-- Name: observations_observation_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.observations_observation_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3013 (class 0 OID 0)
-- Dependencies: 2436
-- Name: observations_observation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.observations_observation_id_seq OWNED BY maplog.observations.observation_id;



CREATE SEQUENCE maplog.phenomenons_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2438 (class 1259 OID 19798)
-- Dependencies: 2783 7
-- Name: phenomenons; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.phenomenons (
    phenomenon_id character varying(100) DEFAULT nextval('maplog.phenomenons_id_seq'::regclass) NOT NULL,
    phenomenon_name character varying(100) NOT NULL,
    unit character varying(30) NOT NULL
);


--
-- TOC entry 2486 (class 1259 OID 1412299)
-- Dependencies: 7
-- Name: rights; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.rights (
    rights_id integer NOT NULL,
    user_role character varying(20),
    note_cz character varying(100)
);




--
-- TOC entry 2440 (class 1259 OID 19804)
-- Dependencies: 7
-- Name: sensors; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.sensors (
    sensor_id bigint NOT NULL,
    sensor_name character varying(20),
    sensor_type character varying(100),
    phenomenon_id character varying(100) NOT NULL
);


--
-- TOC entry 2441 (class 1259 OID 19807)
-- Dependencies: 2440 7
-- Name: sensors_sensor_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.sensors_sensor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3014 (class 0 OID 0)
-- Dependencies: 2441
-- Name: sensors_sensor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.sensors_sensor_id_seq OWNED BY maplog.sensors.sensor_id;


--
-- TOC entry 2468 (class 1259 OID 113867)
-- Dependencies: 2810 7
-- Name: sessions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.sessions (
    session_id character varying(100) NOT NULL,
    system_user_id integer NOT NULL,
    ip character(100),
    time_stamp timestamp with time zone DEFAULT now()
);



--
-- TOC entry 2452 (class 1259 OID 19852)
-- Dependencies: 2801 7 1065
-- Name: units_positions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units_positions (
    gid integer NOT NULL,
    the_geom geometry NOT NULL,
    unit_id bigint NOT NULL,
    time_stamp timestamp with time zone DEFAULT now() NOT NULL,
    dop double precision,
    speed double precision
);



--
-- TOC entry 2458 (class 1259 OID 19873)
-- Dependencies: 2804 7 1065
-- Name: units_tracks; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units_tracks (
    gid integer NOT NULL,
    the_geom geometry NOT NULL,
    unit_id bigint NOT NULL,
    track_start timestamp with time zone DEFAULT now(),
    track_end timestamp with time zone,
    is_closed boolean
);



CREATE TABLE maplog.system_users (
    user_id integer NOT NULL,
    user_name text NOT NULL,
    user_real_name text,
    user_password text,
    group_id integer,
    rights_id integer DEFAULT 0,
    audio boolean DEFAULT false,
    module_administrator boolean DEFAULT false,
    module_log_book boolean DEFAULT false,
    lang text DEFAULT 'cz'::text
);


--
-- TOC entry 2444 (class 1259 OID 19821)
-- Dependencies: 7 2443
-- Name: system_users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.system_users_user_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3015 (class 0 OID 0)
-- Dependencies: 2444
-- Name: system_users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.system_users_user_id_seq OWNED BY maplog.system_users.user_id;


--
-- TOC entry 2482 (class 1259 OID 1412057)
-- Dependencies: 7
-- Name: unit_drivers_driver_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.unit_drivers_driver_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2483 (class 1259 OID 1412059)
-- Dependencies: 2817 7
-- Name: unit_drivers; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.unit_drivers (
    driver_id integer DEFAULT nextval('maplog.unit_drivers_driver_id_seq'::regclass) NOT NULL,
    holder_id integer,
    title_prefix character varying(35),
    fname character varying(24),
    lname character varying(35),
    title character varying(10),
    phone character varying(16)
);


--
-- TOC entry 2445 (class 1259 OID 19823)
-- Dependencies: 7
-- Name: unit_holders; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.unit_holders (
    holder_id integer NOT NULL,
    phone character varying(16),
    icon_id integer,
    holder_name character varying(255) NOT NULL,
    address character varying(255),
    email character varying(100),
    www character varying(100)
);


--
-- TOC entry 2446 (class 1259 OID 19826)
-- Dependencies: 7
-- Name: units; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units (
    unit_id bigint NOT NULL,
    holder_id integer,
    description character varying(100)
);


--
-- TOC entry 2447 (class 1259 OID 19829)
-- Dependencies: 2792 2793 2794 2795 2796 7
-- Name: units_conf; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units_conf (
    unit_id bigint NOT NULL,
    min_distance double precision,
    min_time_span interval,
    is_active boolean DEFAULT true NOT NULL,
    store_interval interval DEFAULT '7 days'::interval,
    max_time_span interval DEFAULT '00:10:00'::interval NOT NULL,
    provide_alerts boolean DEFAULT true NOT NULL,
    min_speed double precision DEFAULT 2
);


--
-- TOC entry 2448 (class 1259 OID 19834)
-- Dependencies: 2797 7 1065
-- Name: units_positions_recent; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units_positions_recent (
    gid integer NOT NULL,
    the_geom geometry NOT NULL,
    unit_id bigint NOT NULL,
    time_stamp timestamp with time zone DEFAULT now() NOT NULL,
    dop double precision,
    polygon_gid integer
);


CREATE SEQUENCE maplog.units_positions_gid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3018 (class 0 OID 0)
-- Dependencies: 2453
-- Name: units_positions_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.units_positions_gid_seq OWNED BY maplog.units_positions.gid;


--
-- TOC entry 2463 (class 1259 OID 27198)
-- Dependencies: 7
-- Name: units_to_auth_group_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.units_to_auth_group_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2484 (class 1259 OID 1412073)
-- Dependencies: 7
-- Name: units_to_drivers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.units_to_drivers_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2485 (class 1259 OID 1412075)
-- Dependencies: 2818 7
-- Name: units_to_drivers; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units_to_drivers (
    id integer DEFAULT nextval('maplog.units_to_drivers_id_seq'::regclass) NOT NULL,
    unit_id bigint,
    driver_id integer
);


--
-- TOC entry 2454 (class 1259 OID 19861)
-- Dependencies: 7
-- Name: units_to_groups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units_to_groups (
    id integer NOT NULL,
    group_id integer,
    unit_id bigint
);


--
-- TOC entry 2455 (class 1259 OID 19864)
-- Dependencies: 7 2454
-- Name: units_to_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.units_to_groups_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3019 (class 0 OID 0)
-- Dependencies: 2455
-- Name: units_to_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.units_to_groups_id_seq OWNED BY maplog.units_to_groups.id;


--
-- TOC entry 2456 (class 1259 OID 19866)
-- Dependencies: 7
-- Name: units_to_sensors; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units_to_sensors (
    sensor_id bigint NOT NULL,
    unit_id bigint NOT NULL,
    first_obs timestamp with time zone,
    last_obs timestamp with time zone
);


--
-- TOC entry 2457 (class 1259 OID 19869)
-- Dependencies: 2568 7
-- Name: units_to_phenomenons; Type: VIEW; Schema: public; Owner: -
--

--
-- TOC entry 2459 (class 1259 OID 19880)
-- Dependencies: 2806 2807 7
-- Name: units_tracks_conf; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE maplog.units_tracks_conf (
    units_tracks_settings_id integer NOT NULL,
    unit_id bigint,
    max_time_span interval DEFAULT '1 day'::interval,
    max_distance double precision DEFAULT 20
);


--
-- TOC entry 2460 (class 1259 OID 19885)
-- Dependencies: 2459 7
-- Name: units_tracks_conf_units_tracks_settings_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.units_tracks_conf_units_tracks_settings_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3020 (class 0 OID 0)
-- Dependencies: 2460
-- Name: units_tracks_conf_units_tracks_settings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.units_tracks_conf_units_tracks_settings_id_seq OWNED BY maplog.units_tracks_conf.units_tracks_settings_id;


--
-- TOC entry 2461 (class 1259 OID 19887)
-- Dependencies: 2458 7
-- Name: units_tracks_gid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.units_tracks_gid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3021 (class 0 OID 0)
-- Dependencies: 2461
-- Name: units_tracks_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.units_tracks_gid_seq OWNED BY maplog.units_tracks.gid;


--
-- TOC entry 2462 (class 1259 OID 19889)
-- Dependencies: 2445 7
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE maplog.users_user_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 3022 (class 0 OID 0)
-- Dependencies: 2462
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE maplog.users_user_id_seq OWNED BY maplog.unit_holders.holder_id;
