
--
-- TOC entry 744 (class 1255 OID 1412518)
-- Dependencies: 1203 7 1065
-- Name: _split_track(geometry, integer, timestamp with time zone); Type: FUNCTION; Schema: public; Owner: -
--

/* 
 * Rozdeli track s gid bodem a casem
 */
CREATE FUNCTION maplog._split_track(point geometry, this_gid integer, this_time_stamp timestamp with time zone) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE

  track record;
  fraction float;
  subtrack1 geometry;
  subtrack2 geometry;
  newtrack  geometry;
  helper_time_before timestamp with time zone;
  helper_time_after timestamp with time zone;
  numpts int;
BEGIN

	SELECT * INTO track FROM units_tracks WHERE gid = this_gid;

	SELECT time_stamp INTO helper_time_before FROM units_positions 
		WHERE unit_id = track.unit_id
		AND time_stamp  < this_time_stamp ORDER BY time_stamp DESC LIMIT 1;
	SELECT time_stamp INTO helper_time_after FROM units_positions 
		WHERE unit_id = track.unit_id
		AND time_stamp  > this_time_stamp ORDER BY time_stamp LIMIT 1;
		

	IF this_time_stamp = track.track_end THEN
		-- pozice je na konci - staci umazat posledni
		SELECT st_removepoint(track.the_geom, 0) INTO newtrack;

	
		
		UPDATE units_tracks SET the_geom = newtrack, track_end = helper_time
		WHERE gid = this_gid;
		RETURN;
	ELSE IF this_time_stamp = track.track_start THEN

		-- pozice je na konci - staci umazat posledni
		SELECT ST_NumPoints(track.the_geom) INTO numpts;
		SELECT st_removepoint(track.the_geom, numpts) INTO newtrack;
		
		UPDATE units_tracks SET the_geom = newtrack, track_end = helper_time
		WHERE gid = this_gid;
		RETURN;
		END IF;
	END IF;

	SELECT Line_Locate_Point(track.the_geom, point) INTO fraction;

	SELECT line_substring(track.the_geom,0, fraction) INTO subtrack1;
	SELECT line_substring(track.the_geom,fraction, 1) INTO subtrack2;	

	
	UPDATE units_tracks SET the_geom = subtrack1, track_end = helper_time_before
	WHERE gid = this_gid;
	
	INSERT INTO units_tracks (the_geom, unit_id, track_start, track_end, is_closed)
	VALUES (subtrack2, track.unit_id, helper_time_after, track.track_end, track.is_closed );

  
 
END
$$;


--

CREATE FUNCTION maplog.add_alert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
this_alert_id bigint; 
description varchar;
BEGIN 

SELECT alert_id INTO this_alert_id FROM alerts WHERE alert_id = NEW.alert_id;

IF this_alert_id IS NULL THEN
   raise notice '% alert not found - adding automaticaly', NEW.alert_id;
   
   select into description 'default alert ' || EXTRACT(EPOCH FROM now())::varchar;
   INSERT INTO alerts(alert_id, alert_description) 
   values (NEW.alert_id, description);
   RETURN NEW;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;


--
-- TOC entry 23 (class 1255 OID 19024)
-- Dependencies: 7 1203
-- Name: add_gid_to_observation(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_gid_to_observation() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
last_unit_gid integer; 
this_last_pos record;
BEGIN 

SELECT * INTO this_last_pos FROM last_units_positions WHERE unit_id = NEW.unit_id;
    IF this_last_pos.time_stamp < NEW.time_stamp THEN
	NEW.gid:=this_last_pos.gid;	
    ELSE 
	SELECT gid INTO last_unit_gid FROM units_positions 
	WHERE units_positions.unit_id = NEW.unit_id AND
	units_positions.time_stamp < NEW.time_stamp
	ORDER BY units_positions.time_stamp DESC LIMIT 1;
        NEW.gid:=last_unit_gid;	
    END IF;
    RETURN NEW;    
END; 
$$;


--
-- TOC entry 746 (class 1255 OID 19025)
-- Dependencies: 7 1203
-- Name: add_last_unit_position(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_last_unit_position() RETURNS trigger
    LANGUAGE plpgsql
    AS $$declare
last_gid integer;
last_timestamp timestamp;
BEGIN 

SELECT gid INTO last_gid FROM last_units_positions WHERE unit_id = NEW.unit_id;
SELECT time_stamp INTO last_timestamp FROM last_units_positions WHERE unit_id = NEW.unit_id;

-- if no track then create new
IF last_gid  IS NULL THEN 
--insert new last position
	INSERT INTO last_units_positions values(NEW.gid, NEW.the_geom, NEW.unit_id, NEW.time_stamp);

ELSE IF (last_timestamp < NEW.time_stamp) THEN
-- update last position
        UPDATE last_units_positions
              SET the_geom = NEW.the_geom,
               time_stamp = NEW.time_stamp ,
               gid = NEW.gid,
               dop = NEW.dop,
               speed = NEW.speed
               WHERE unit_id = NEW.unit_id;	
END IF;
END IF;
RETURN NEW;
END; $$;


--
-- TOC entry 2996 (class 0 OID 0)
-- Dependencies: 746
-- Name: FUNCTION add_last_unit_position(); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION maplog.add_last_unit_position() IS 'Addes or update newly inserted position into last_units_positions';


--
-- TOC entry 24 (class 1255 OID 19026)
-- Dependencies: 7 1203
-- Name: add_phenomenon(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_phenomenon() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
this_phenomenon_id character varying(100); 

BEGIN 

SELECT phenomenon_id INTO this_phenomenon_id FROM phenomenons WHERE phenomenon_id = NEW.phenomenon_id;

IF this_phenomenon_id IS NULL THEN
   raise notice 'phenomenon not found - adding automaticaly ';
   INSERT INTO phenomenons(phenomenon_id, phenomenon_name, unit) 
   values (NEW.phenomenon_id, 'Generated - TODO', 'Generated - TODO');
   RETURN NEW;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;


--
-- TOC entry 26 (class 1255 OID 19027)
-- Dependencies: 1203 7
-- Name: add_sensor(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_sensor() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
this_sensor_id bigint; 

BEGIN 

SELECT sensor_id INTO this_sensor_id FROM sensors WHERE sensor_id = NEW.sensor_id;

IF this_sensor_id IS NULL THEN
   raise notice '% sensor not found - adding automaticaly', NEW.sensor_id;
   INSERT INTO sensors(sensor_id, phenomenon_id) 
   values (NEW.sensor_id, 999999);
   RETURN NEW;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;


--
-- TOC entry 27 (class 1255 OID 19028)
-- Dependencies: 1203 7
-- Name: add_unit(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_unit() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
this_unit_id bigint; 

BEGIN 

SELECT unit_id INTO this_unit_id FROM units WHERE unit_id = NEW.unit_id;
IF this_unit_id IS NULL THEN
   raise notice '% unit not found - adding automaticaly', NEW.unit_id;
   INSERT INTO units(unit_id, description) 
   values (NEW.unit_id, 'automaticaly added unit');
   RETURN NEW;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;


--
-- TOC entry 41 (class 1255 OID 19029)
-- Dependencies: 1203 7
-- Name: add_unit_conf(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_unit_conf() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
this_unit_id bigint; 

BEGIN 

SELECT unit_id INTO this_unit_id FROM units_conf WHERE unit_id = NEW.unit_id;
IF this_unit_id IS NULL THEN
   
   INSERT INTO units_conf(unit_id, min_distance, min_time_span) 
   values (NEW.unit_id, 20, '1 hour');
   RETURN NEW;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;


--
-- TOC entry 28 (class 1255 OID 80907)
-- Dependencies: 7 1203
-- Name: add_unit_to_admin(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_unit_to_admin() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
this_group_id int; 
admin_group_id int;
admin_name varchar;
BEGIN 
admin_name='admin';

SELECT group_id INTO this_group_id FROM units_to_groups WHERE unit_id = NEW.unit_id;
IF this_group_id IS NULL THEN

   SELECT id INTO this_group_id FROM groups 
   WHERE group_name = admin_name;

	IF this_group_id IS NULL THEN
		INSERT INTO groups(group_name)
		VALUES (admin_name);
		SELECT id INTO this_group_id FROM groups 
		WHERE group_name = admin_name;
	END IF;

   INSERT INTO units_to_groups(group_id, unit_id) 
   values (this_group_id, NEW.unit_id);
   RETURN NEW;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;


--
-- TOC entry 42 (class 1255 OID 19030)
-- Dependencies: 1203 7
-- Name: add_unit_to_sensor(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_unit_to_sensor() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

utos record;
BEGIN 

SELECT * INTO utos FROM units_to_sensors WHERE unit_id = NEW.unit_id
AND sensor_id = NEW.sensor_id;

IF utos IS NULL THEN    
   raise notice 'unit_to_sensor not found - adding automaticaly';  
   INSERT INTO units_to_sensors(unit_id, sensor_id) 
   values (NEW.unit_id, NEW.sensor_id);
   RETURN NEW;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;


--
-- TOC entry 43 (class 1255 OID 19031)
-- Dependencies: 1203 7
-- Name: add_unit_track_conf(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.add_unit_track_conf() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
this_unit_id bigint; 

BEGIN 

SELECT unit_id INTO this_unit_id FROM units_tracks_conf WHERE unit_id = NEW.unit_id;
IF this_unit_id IS NULL THEN
   
   INSERT INTO units_tracks_conf(unit_id, max_distance, max_time_span) 
   values (NEW.unit_id, 1000, '12 hours');
   RETURN NEW;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;




--
-- TOC entry 167 (class 1255 OID 90580)
-- Dependencies: 7 1203
-- Name: addpositiontotrack(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.addpositiontotrack(trackid integer, positionid integer, positionplace integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
declare

track_geom geometry;
this_position record;
this_track record;
trackPoints int;
BEGIN 

SELECT * INTO this_track FROM units_tracks WHERE gid = trackid;
SELECT * INTO this_position FROM units_positions WHERE gid = positionid;

IF positionplace = 0 AND this_track.track_start >= this_position.time_stamp THEN
        --dej na zacatek a nahrad pocatecni cas
	UPDATE units_tracks 
	SET 
	the_geom = addPoint(this_track.the_geom, this_position.the_geom, positionplace),
	track_start =  this_position.time_stamp
	WHERE gid=this_track.gid;
	return true;
ELSEIF this_track.track_end <= this_position.time_stamp 
	AND ST_NumPoints(this_track.the_geom)<=positionplace THEN
	--dej na konec a nahrad koncovej cas
	UPDATE units_tracks 
	SET 
	the_geom = addPoint(this_track.the_geom, this_position.the_geom),
	track_end =  this_position.time_stamp
	WHERE gid=this_track.gid;
	return true;

ELSEIF this_track.track_start < this_position.time_stamp AND 
	this_position.time_stamp < this_track.track_end 
	THEN
-- dej doprostred
        UPDATE units_tracks 
	SET 
	the_geom = addPoint(this_track.the_geom, this_position.the_geom, positionplace)	
	WHERE gid=this_track.gid;
	return true;
ELSE 
RETURN false;
END IF;
END; 
$$;


--
-- TOC entry 30 (class 1255 OID 19097)
-- Dependencies: 7 1203
-- Name: consider_insert_position(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.consider_insert_position() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

this_units_conf record;
this_position record;
dist double precision;
last_post geometry;
this_time_span interval;

BEGIN 

SELECT * INTO this_units_conf FROM units_conf WHERE unit_id = NEW.unit_id;
SELECT * INTO this_position FROM last_units_positions WHERE unit_id = NEW.unit_id;

dist = distance_sphere(this_position.the_geom, NEW.the_geom);
this_time_span = NEW.time_stamp - this_position.time_stamp;

IF (dist < this_units_conf.min_distance AND (this_time_span < this_units_conf.min_time_span))THEN
    raise notice 'Positiont of Unit % is too close ', NEW.unit_id;
   RETURN NULL;
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;


--
-- TOC entry 745 (class 1255 OID 1412167)
-- Dependencies: 7 1203
-- Name: consider_insert_position2(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.consider_insert_position2() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

this_units_conf record;
last_position record;
before_position record;
dist double precision;
dist2 double precision;
last_post geometry;
this_time_span interval;

BEGIN 

SELECT * INTO this_units_conf FROM units_conf WHERE unit_id = NEW.unit_id;
SELECT * INTO last_position FROM last_units_positions WHERE unit_id = NEW.unit_id;

dist = distance_sphere(last_position.the_geom, NEW.the_geom);

IF (dist < this_units_conf.min_distance AND NEW.speed<this_units_conf.min_speed)
   THEN
   SELECT * INTO before_position FROM units_positions WHERE 
   unit_id = NEW.unit_id 
   AND time_stamp < last_position.time_stamp ORDER BY time_stamp DESC limit 1;
   dist2 = distance_sphere(last_position.the_geom, before_position.the_geom);
	
	IF (dist2 < this_units_conf.min_distance AND 
	    before_position.speed < this_units_conf.min_speed AND
	    last_position.speed   < this_units_conf.min_speed) THEN
		UPDATE units_positions
		SET time_stamp = NEW.time_stamp
		WHERE gid = last_position.gid;
		RETURN NULL;
	ELSE
		RETURN NEW;
	END IF;
   
ELSE
-- if unit exist than do nothing
RETURN NEW;
END IF;
END; 
$$;



CREATE FUNCTION maplog.copy_ignition_status() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ DECLARE
 last_status record;
 tomorrow timestamp with time zone;
BEGIN
IF new.sensor_id = 330040000 THEN 
 SELECT * INTO last_status FROM public.last_ignition_status WHERE unit_id = NEW.unit_id;
 SELECT CURRENT_TIMESTAMP + INTERVAL '1 day' INTO  tomorrow;
 IF last_status IS NULL THEN
  INSERT INTO last_ignition_status VALUES (NEW.observation_id, NEW.gid, NEW.time_stamp, NEW.observed_value, NEW.unit_id);
 ELSE 
  IF last_status.time_stamp < NEW.time_stamp AND NEW.time_stamp < tomorrow THEN
    IF last_status.value <> NEW.observed_value THEN
      INSERT INTO ignition_status VALUES (last_status.observation_id, last_status.gid, last_status.time_stamp, last_status.value, last_status.unit_id);
      DELETE FROM last_ignition_status WHERE unit_id = last_status.unit_id;
      INSERT INTO last_ignition_status VALUES (NEW.observation_id, NEW.gid, NEW.time_stamp, NEW.observed_value, NEW.unit_id);
    ELSE
      UPDATE last_ignition_status SET observation_id = NEW.observation_id, gid = NEW.gid, time_stamp = NEW.time_stamp WHERE unit_id = NEW.unit_id;
    END IF;
  ELSE
    INSERT INTO ignition_status VALUES (NEW.observation_id, NEW.gid, NEW.time_stamp, NEW.observed_value, NEW.unit_id);
  END IF;
 END IF;
END IF;
RETURN NEW;
END; $$;




CREATE FUNCTION maplog.delete_sensor() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
BEGIN 

DELETE FROM observations WHERE sensor_id=OLD.sensor_id;
DELETE FROM units_to_sensors WHERE sensor_id=OLD.sensor_id;

RETURN OLD;

END; 
$$;


--
-- TOC entry 232 (class 1255 OID 19108)
-- Dependencies: 7 1203
-- Name: delete_unit(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.delete_unit(this_unit_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare
track_gid integer; 

BEGIN 

DELETE FROM running_tracks WHERE unit_id = this_unit_id;
DELETE FROM units_tracks WHERE unit_id = this_unit_id;
DELETE FROM observations WHERE unit_id = this_unit_id;
DELETE FROM last_units_positions WHERE unit_id = this_unit_id;
DELETE FROM units_positions WHERE unit_id = this_unit_id;
DELETE FROM units WHERE unit_id = this_unit_id;

END; 
$$;


CREATE FUNCTION maplog.do_track() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
track_gid integer; 
last_timestamp timestamp;
old_geom geometry;
line_geom geometry;

BEGIN 

SELECT time_stamp INTO last_timestamp FROM last_units_positions WHERE unit_id = NEW.unit_id;

track_gid := get_unit_running_track_gid(NEW.unit_id) ;
RAISE NOTICE 'found %', track_gid;
-- if no track then create new
IF track_gid IS NULL THEN 
	RAISE NOTICE 'no track';
	SELECT INTO track_gid nextval('units_tracks_gid_seq');
	line_geom = MakeLine(NEW.the_geom);

	INSERT INTO units_tracks(gid, the_geom, unit_id, track_start) 
	values (track_gid, line_geom, NEW.unit_id, NEW.time_stamp);

	RAISE NOTICE 'adding %', track_gid;
	
	INSERT INTO running_tracks(unit_id, gid)
	values(NEW.unit_id, track_gid);
	RETURN NEW;

ELSE IF (last_timestamp < NEW.time_stamp) THEN
-- track is already running
        SELECT the_geom INTO STRICT old_geom FROM units_tracks WHERE gid=track_gid;
	
	UPDATE units_tracks SET the_geom = addPoint(old_geom, NEW.the_geom) WHERE gid=track_gid;

RETURN NEW;
END IF;
END IF;
RETURN NEW;
END; 
$$;


--
-- TOC entry 44 (class 1255 OID 19117)
-- Dependencies: 7 1203
-- Name: do_track2(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.do_track2() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
position_from_end bigint;
track_gid integer; 
last_timestamp timestamp;
old_geom geometry;
line_geom geometry;
point_position integer;
point_number integer;

BEGIN 

--get position where to put new point
 
SELECT gid INTO track_gid FROM running_tracks WHERE unit_id=NEW.unit_id;



RAISE NOTICE 'found %', track_gid;
-- if no track then create new
IF track_gid IS NULL THEN 
	RAISE NOTICE 'no track';
	SELECT INTO track_gid nextval('units_tracks_gid_seq');
	line_geom = MakeLine(NEW.the_geom);

	INSERT INTO units_tracks(gid, the_geom, unit_id, track_start) 
	values (track_gid, line_geom, NEW.unit_id, NEW.time_stamp);

	RAISE NOTICE 'adding %', track_gid;
	
	INSERT INTO running_tracks(unit_id, gid)
	values(NEW.unit_id, track_gid);
	RETURN NEW;

ELSE 

-- track is already running
	SELECT count(gid) INTO position_from_end FROM units_positions 
	WHERE 
	time_stamp > NEW.time_stamp AND
	unit_id = NEW.unit_id;

	IF position_from_end = 0 THEN

		SELECT the_geom INTO STRICT old_geom FROM units_tracks WHERE gid=track_gid
		AND unit_id=NEW.unit_id;
		UPDATE units_tracks 
			SET the_geom = addPoint(old_geom, NEW.the_geom),
			    track_end = NEW.time_stamp
		 WHERE gid=track_gid;
     
	
	ELSE
-- calculate position
		SELECT st_numpoints(the_geom) INTO point_number FROM units_tracks 
		WHERE gid = track_gid AND unit_id=NEW.unit_id;

		point_position := point_number - position_from_end;
			IF point_position < 0 THEN 
				point_position := 0; 
			END IF;
	
		SELECT the_geom INTO STRICT old_geom FROM units_tracks WHERE gid=track_gid
		AND unit_id=NEW.unit_id;
	
		UPDATE units_tracks SET the_geom = addPoint(old_geom, NEW.the_geom, point_position) WHERE gid=track_gid;
	END IF;


RETURN NEW;

END IF;
RETURN NEW;
END; 
$$;


CREATE FUNCTION maplog.generate_positions() RETURNS void
    LANGUAGE plpgsql
    AS $$
declare
lastx double precision;
lasty double precision;
newx double precision;
newy double precision;
unit record;
timenow timestamp;
geom text;
BEGIN 

 FOR unit IN SELECT * FROM units LOOP
	SELECT x(the_geom) INTO lastx FROM last_units_positions WHERE unit_id = unit.unit_id;
	SELECT y(the_geom) INTO lasty FROM last_units_positions WHERE unit_id = unit.unit_id;
    
	newx := lastx + (random()*0.002) - (random()*0.002);
        newy := lasty + (random()*0.002) - (random()*0.002);
        SELECT now() INTO timenow;
	
	 geom := 'POINT(' || newx::text || ' ' || newy::text || ')'; 

        INSERT INTO units_positions(the_geom, unit_id, time_stamp) 
        values ( GeomFromText((geom),4326), unit.unit_id, timenow);
        raise notice 'Add Positiont for Unit %', unit.unit_id;
 END LOOP;


END ;
$$;


--
-- TOC entry 166 (class 1255 OID 22479)
-- Dependencies: 1203 7
-- Name: generate_units(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.generate_units(number_of_units integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare
this_unit_id integer;
x  double precision;
y  double precision;
geom text;
timenow timestamp;
BEGIN 
this_unit_id := 1;
 WHILE (this_unit_id < number_of_units) LOOP
	
	x := 14 + this_unit_id * 0.01;
        y := 50 ;
        SELECT now() INTO timenow;
	
	geom := 'POINT(' || x::text || ' ' || y::text || ')'; 

        INSERT INTO units_positions(the_geom, unit_id, time_stamp) 
        values ( GeomFromText((geom),4326), this_unit_id, timenow);
        raise notice 'Add Positiont for Unit %', this_unit_id;

         this_unit_id:=this_unit_id+1;
 END LOOP;


END ;
$$;



CREATE FUNCTION maplog.get_unit_running_track_gid(this_unit_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
declare
tracks_gid integer; 

BEGIN 
SELECT gid  INTO tracks_gid FROM running_tracks WHERE unit_id=this_unit_id LIMIT 1;
return tracks_gid;
END; 
$$;


--
-- TOC entry 293 (class 1255 OID 19196)
-- Dependencies: 1203 7
-- Name: get_unit_running_track_gid(bigint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.get_unit_running_track_gid(this_unit_id bigint) RETURNS integer
    LANGUAGE plpgsql
    AS $$
declare
tracks_gid integer; 

BEGIN 
SELECT gid  INTO tracks_gid FROM running_tracks WHERE unit_id=this_unit_id LIMIT 1;
return tracks_gid;
END; 
$$;


--
CREATE FUNCTION maplog.mergetracks(track1_gid integer, track2_gid integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
declare
track1 record;
track2 record;
newtrack geometry;
BEGIN 
SELECT * INTO track1 FROM units_tracks WHERE gid = track1_gid;	
SELECT * INTO track2 FROM units_tracks WHERE gid = track2_gid;	

if (track1.track_start < track2.track_end AND track1.track_end = track2.track_start) THEN

	SELECT linemerge(collect(the_geom)) INTO newtrack
	FROM
	(SELECT track1.the_geom AS the_geom
	UNION
	SELECT track2.the_geom AS the_geom) AS the_geom;

	UPDATE units_tracks
	SET
	the_geom=newtrack,
	track_start=track1.track_start,
	track_end= track2.track_end
	WHERE gid=track1_gid;

	DELETE FROM units_tracks WHERE  gid=track2_gid;
	return true;

END IF;
return false;
END; 
$$;


--
-- TOC entry 737 (class 1255 OID 1410624)
-- Dependencies: 1203 7
-- Name: ml_update_times(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.ml_update_times() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
this_first_obs timestamp; 
this_last_obs timestamp; 

BEGIN 

SELECT first_obs INTO this_first_obs FROM units_to_sensors WHERE sensor_id = NEW.sensor_id
AND unit_id = NEW.unit_id;

SELECT last_obs INTO this_last_obs FROM units_to_sensors WHERE sensor_id = NEW.sensor_id
AND unit_id = NEW.unit_id;

IF this_first_obs IS NULL OR this_last_obs IS NULL THEN
	IF this_first_obs IS NULL THEN
	raise notice '% sensor metadata not found - adding from observations', NEW.sensor_id;
		SELECT time_stamp INTO this_first_obs FROM observations 
		WHERE sensor_id = NEW.sensor_id AND unit_id = NEW.unit_id ORDER BY time_stamp limit 1;
	
	UPDATE units_to_sensors 
	SET first_obs = this_first_obs WHERE sensor_id = NEW.sensor_id AND unit_id = NEW.unit_id;  
	END IF;

	IF this_last_obs IS NULL THEN
	raise notice '% sensor metadata not found - adding from observations', NEW.sensor_id;
		SELECT time_stamp INTO this_last_obs FROM observations 
		WHERE sensor_id = NEW.sensor_id AND unit_id = NEW.unit_id ORDER BY time_stamp DESC limit 1;
	
	UPDATE units_to_sensors 
	SET last_obs = this_last_obs WHERE sensor_id = NEW.sensor_id AND unit_id = NEW.unit_id;  
	END IF;	
ELSE 
	IF NEW.time_stamp < this_first_obs THEN
	UPDATE units_to_sensors 
	SET first_obs = NEW.time_stamp WHERE sensor_id = NEW.sensor_id AND unit_id = NEW.unit_id;  
	END IF;


	IF NEW.time_stamp > this_last_obs THEN
	UPDATE units_to_sensors 
	SET last_obs = NEW.time_stamp WHERE sensor_id = NEW.sensor_id AND unit_id = NEW.unit_id;  
	END IF;
	RETURN NEW;
END IF;	
RETURN NEW;
END; 
$$;


--

CREATE FUNCTION maplog.on_delete_alert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

BEGIN 
DELETE FROM alert_events WHERE alert_id = OLD.alert_id;
DELETE FROM alert_queries WHERE alert_id = OLD.alert_id;

return OLD;
END; 
$$;


--
-- TOC entry 740 (class 1255 OID 1411912)
-- Dependencies: 7 1203
-- Name: on_delete_alert_query(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.on_delete_alert_query() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

BEGIN 
DELETE FROM alert_queries_to_units WHERE query_id = OLD.query_id;

return OLD;
END; 
$$;


--
-- TOC entry 742 (class 1255 OID 1412050)
-- Dependencies: 7 1203
-- Name: on_delete_driver(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.on_delete_driver() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

BEGIN 


DELETE FROM units_to_drivers WHERE driver_id = OLD.driver_id;
return OLD;
END; 
$$;


--
-- TOC entry 45 (class 1255 OID 90640)
-- Dependencies: 7 1203
-- Name: on_delete_group(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.on_delete_group() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

BEGIN 


DELETE FROM units_to_groups WHERE group_id = OLD.id;
UPDATE system_users
SET group_id=NULL WHERE group_id=OLD.id;
return OLD;
END; 
$$;


--
-- TOC entry 46 (class 1255 OID 80993)
-- Dependencies: 7 1203
-- Name: on_delete_position(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.on_delete_position() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

BEGIN 

DELETE FROM observations WHERE gid = OLD.gid;
DELETE FROM last_units_positions WHERE gid = OLD.gid;

return OLD;
END; 
$$;


--
-- TOC entry 741 (class 1255 OID 19290)
-- Dependencies: 7 1203
-- Name: on_delete_unit(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.on_delete_unit() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare

BEGIN 

DELETE FROM alert_events WHERE unit_id = OLD.unit_id;
DELETE FROM alert_queries_to_units WHERE unit_id = OLD.unit_id;
DELETE FROM units_tracks WHERE unit_id = OLD.unit_id;
DELETE FROM last_ignition_status WHERE unit_id = OLD.unit_id;
DELETE FROM ignition_status WHERE unit_id = OLD.unit_id;
DELETE FROM observations WHERE unit_id = OLD.unit_id;
DELETE FROM units_to_sensors WHERE unit_id = OLD.unit_id;
DELETE FROM last_units_positions WHERE unit_id = OLD.unit_id;
DELETE FROM units_conf WHERE unit_id = OLD.unit_id;
DELETE FROM units_tracks_conf WHERE unit_id = OLD.unit_id;
DELETE FROM units_positions WHERE unit_id = OLD.unit_id;
DELETE FROM observations_recent WHERE unit_id = OLD.unit_id;
DELETE FROM units_positions_recent WHERE unit_id = OLD.unit_id;
DELETE FROM units_to_groups WHERE unit_id = OLD.unit_id;
DELETE FROM units_to_drivers WHERE unit_id = OLD.unit_id;
return OLD;
END; 
$$;



CREATE FUNCTION maplog.sos_check_tracks(this_unit_id bigint, time_now timestamp without time zone) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
declare
track_gid integer; 
this_track_end timestamp;
this_max_time interval;
BEGIN 

SELECT gid INTO track_gid  
FROM running_tracks WHERE unit_id = this_unit_id;


SELECT track_end INTO this_track_end 
FROM units_tracks WHERE gid = track_gid;

SELECT max_time_span INTO this_max_time
FROM units_tracks_conf WHERE unit_id = this_unit_id;

IF this_max_time IS NOT NULL AND ((time_now - this_track_end ) > this_max_time) THEN 

	DELETE FROM running_tracks WHERE gid = track_gid;
RETURN TRUE;
END IF;
 RETURN FALSE;

END; 
$$;


--
-- TOC entry 429 (class 1255 OID 19355)
-- Dependencies: 7 1203
-- Name: sos_check_tracks(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.sos_check_tracks() RETURNS integer
    LANGUAGE plpgsql
    AS $$
declare
time_now timestamp;
i integer;
row record;

BEGIN 
i:=0;
time_now:= now();
FOR row IN SELECT * FROM units
LOOP
PERFORM sos_check_tracks(row.unit_id, time_now) ;
i:=i+1;
END LOOP;
RETURN i;
END; 
$$;




--
-- TOC entry 717 (class 1255 OID 90583)
-- Dependencies: 7 1203
-- Name: starttrack(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.starttrack(position_gid integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare
this_position record;
line_geom geometry;
BEGIN 
SELECT * INTO this_position FROM units_positions WHERE gid = position_gid;
	line_geom = MakeLine(this_position.the_geom);

	INSERT INTO units_tracks(the_geom, unit_id, track_start, track_end)
	values (line_geom, this_position.unit_id, this_position.time_stamp, this_position.time_stamp);

END; 
$$;


--
-- TOC entry 716 (class 1255 OID 90642)
-- Dependencies: 1203 7
-- Name: starttrack(timestamp without time zone, bigint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION maplog.starttrack(this_time_stamp timestamp without time zone, this_unit_id bigint) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
declare
this_position record;
line_geom geometry;
BEGIN 
SELECT * INTO this_position FROM units_positions 
WHERE time_stamp = this_time_stamp AND unit_id = this_unit_id;
	line_geom = MakeLine(this_position.the_geom);

	IF this_position.gid IS NOT NULL THEN
	INSERT INTO units_tracks(the_geom, unit_id, track_start, track_end)
	values (line_geom, this_position.unit_id, this_position.time_stamp, this_position.time_stamp);
	return true;
	ELSE
		return false;
	END IF;

END; 
$$;



CREATE FUNCTION maplog.update_group() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
super_group record;

BEGIN

IF NEW.parent_group_id IS NOT NULL THEN
	SELECT * INTO super_group FROM groups WHERE id = NEW.parent_group_id;
	UPDATE groups
               SET has_children = true
               WHERE groups.id = NEW.parent_group_id    ;  
      RETURN NEW;       
ELSE
RETURN NEW;
END IF;
END;

$$;

