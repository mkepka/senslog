
SET search_path = maplog;

ALTER TABLE maplog.geom_alerts_conf ALTER COLUMN geom_alerts_conf_id SET DEFAULT nextval('maplog.geom_alerts_conf_geom_alerts_conf_id_seq'::regclass);


--
-- TOC entry 2779 (class 2604 OID 19892)
-- Dependencies: 2430 2429
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.groups ALTER COLUMN id SET DEFAULT nextval('maplog.groups_id_seq'::regclass);


--
-- TOC entry 2780 (class 2604 OID 19893)
-- Dependencies: 2432 2431
-- Name: gid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.last_units_positions ALTER COLUMN gid SET DEFAULT nextval('maplog.last_units_positions_gid_seq'::regclass);


--
-- TOC entry 2781 (class 2604 OID 19894)
-- Dependencies: 2434 2433
-- Name: obs_alerts_conf_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.obs_alerts_conf ALTER COLUMN obs_alerts_conf_id SET DEFAULT nextval('maplog.obs_alerts_conf_obs_alerts_conf_id_seq'::regclass);


--
-- TOC entry 2782 (class 2604 OID 19895)
-- Dependencies: 2436 2435
-- Name: observation_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.observations ALTER COLUMN observation_id SET DEFAULT nextval('maplog.observations_observation_id_seq'::regclass);


--
-- TOC entry 2784 (class 2604 OID 19896)
-- Dependencies: 2441 2440
-- Name: sensor_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.sensors ALTER COLUMN sensor_id SET DEFAULT nextval('maplog.sensors_sensor_id_seq'::regclass);


--
-- TOC entry 2785 (class 2604 OID 19897)
-- Dependencies: 2444 2443
-- Name: user_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.system_users ALTER COLUMN user_id SET DEFAULT nextval('maplog.system_users_user_id_seq'::regclass);


--
-- TOC entry 2791 (class 2604 OID 19898)
-- Dependencies: 2462 2445
-- Name: holder_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.unit_holders ALTER COLUMN holder_id SET DEFAULT nextval('maplog.users_user_id_seq'::regclass);


--
-- TOC entry 2802 (class 2604 OID 19900)
-- Dependencies: 2453 2452
-- Name: gid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.units_positions ALTER COLUMN gid SET DEFAULT nextval('maplog.units_positions_gid_seq'::regclass);




--
-- TOC entry 2803 (class 2604 OID 19902)
-- Dependencies: 2455 2454
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.units_to_groups ALTER COLUMN id SET DEFAULT nextval('maplog.units_to_groups_id_seq'::regclass);


--
-- TOC entry 2805 (class 2604 OID 19903)
-- Dependencies: 2461 2458
-- Name: gid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.units_tracks ALTER COLUMN gid SET DEFAULT nextval('maplog.units_tracks_gid_seq'::regclass);


--
-- TOC entry 2808 (class 2604 OID 19904)
-- Dependencies: 2460 2459
-- Name: units_tracks_settings_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE maplog.units_tracks_conf ALTER COLUMN units_tracks_settings_id SET DEFAULT nextval('maplog.units_tracks_conf_units_tracks_settings_id_seq'::regclass);


--
-- TOC entry 2897 (class 2606 OID 1411887)
-- Dependencies: 2477 2477
-- Name: alert_event_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.alert_events
    ADD CONSTRAINT alert_event_pk PRIMARY KEY (alert_event_id);


--
-- TOC entry 2893 (class 2606 OID 1411875)
-- Dependencies: 2476 2476
-- Name: alert_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.alerts
    ADD CONSTRAINT alert_pk PRIMARY KEY (alert_id);


--
-- TOC entry 2902 (class 2606 OID 1411930)
-- Dependencies: 2479 2479
-- Name: alert_queries_to_units_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.alert_queries_to_units
    ADD CONSTRAINT alert_queries_to_units_pkey PRIMARY KEY (id);


--
-- TOC entry 2899 (class 2606 OID 1411918)
-- Dependencies: 2478 2478
-- Name: alert_query_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.alert_queries
    ADD CONSTRAINT alert_query_pk PRIMARY KEY (query_id);


--
-- TOC entry 2895 (class 2606 OID 1411877)
-- Dependencies: 2476 2476
-- Name: alerts_alert_description_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.alerts
    ADD CONSTRAINT alerts_alert_description_key UNIQUE (alert_description);


--
-- TOC entry 2831 (class 2606 OID 19906)
-- Dependencies: 2433 2433
-- Name: alerts_conf_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.obs_alerts_conf
    ADD CONSTRAINT alerts_conf_pk PRIMARY KEY (obs_alerts_conf_id);



--
-- TOC entry 2821 (class 2606 OID 19908)
-- Dependencies: 2426 2426
-- Name: geom_alerts_conf_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.geom_alerts_conf
    ADD CONSTRAINT geom_alerts_conf_pk PRIMARY KEY (geom_alerts_conf_id);


--
-- TOC entry 2823 (class 2606 OID 19910)
-- Dependencies: 2428 2428 2428 2428 2428
-- Name: geometry_columns_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.geometry_columns
    ADD CONSTRAINT geometry_columns_pk PRIMARY KEY (f_table_catalog, f_table_schema, f_table_name, f_geometry_column);


--
-- TOC entry 2825 (class 2606 OID 19912)
-- Dependencies: 2429 2429
-- Name: group_id_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.groups
    ADD CONSTRAINT group_id_pk PRIMARY KEY (id);


--
-- TOC entry 2904 (class 2606 OID 1412015)
-- Dependencies: 2480 2480
-- Name: last_status_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.last_ignition_status
    ADD CONSTRAINT last_status_pk PRIMARY KEY (observation_id);


--
-- TOC entry 2827 (class 2606 OID 19914)
-- Dependencies: 2431 2431
-- Name: last_units_positions_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.last_units_positions
    ADD CONSTRAINT last_units_positions_pk PRIMARY KEY (gid);


--
-- TOC entry 2924 (class 2606 OID 1472762)
-- Dependencies: 2492 2492 2492
-- Name: obs_to_obs_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.obs_to_obs
    ADD CONSTRAINT obs_to_obs_pkey PRIMARY KEY (main_obs_id, sec_obs_id);




--
-- TOC entry 2834 (class 2606 OID 19918)
-- Dependencies: 2435 2435
-- Name: observation_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.observations
    ADD CONSTRAINT observation_pk PRIMARY KEY (observation_id);


--
-- TOC entry 2838 (class 2606 OID 1443286)
-- Dependencies: 2435 2435 2435 2435
-- Name: observations_time_stamp_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.observations
    ADD CONSTRAINT observations_time_stamp_key UNIQUE (time_stamp, sensor_id, unit_id);


--
-- TOC entry 2846 (class 2606 OID 19920)
-- Dependencies: 2438 2438
-- Name: phenomenon_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.phenomenons
    ADD CONSTRAINT phenomenon_pk PRIMARY KEY (phenomenon_id);


--
-- TOC entry 2916 (class 2606 OID 1412303)
-- Dependencies: 2486 2486
-- Name: rights_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.rights
    ADD CONSTRAINT rights_pkey PRIMARY KEY (rights_id);


--
-- TOC entry 2918 (class 2606 OID 1412305)
-- Dependencies: 2486 2486
-- Name: rights_role_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.maplog.rights
    ADD CONSTRAINT rights_role_key UNIQUE (user_role);



--
-- TOC entry 2851 (class 2606 OID 19924)
-- Dependencies: 2440 2440
-- Name: sensor_id; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.sensors
    ADD CONSTRAINT sensor_id PRIMARY KEY (sensor_id);


--
-- TOC entry 2891 (class 2606 OID 113872)
-- Dependencies: 2468 2468
-- Name: sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.sessions
    ADD CONSTRAINT sessions_pkey PRIMARY KEY (session_id);



--
-- TOC entry 2908 (class 2606 OID 1412033)
-- Dependencies: 2481 2481
-- Name: status_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.ignition_status
    ADD CONSTRAINT status_pk PRIMARY KEY (observation_id);


--
-- TOC entry 2854 (class 2606 OID 19928)
-- Dependencies: 2440 2440
-- Name: unique_name; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.sensors
    ADD CONSTRAINT unique_name UNIQUE (sensor_name);


--
-- TOC entry 2910 (class 2606 OID 1412066)
-- Dependencies: 2483 2483 2483
-- Name: unit_drivers_fname_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.unit_drivers
    ADD CONSTRAINT unit_drivers_fname_key UNIQUE (fname, lname);


--
-- TOC entry 2912 (class 2606 OID 1412064)
-- Dependencies: 2483 2483
-- Name: unit_drivers_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.unit_drivers
    ADD CONSTRAINT unit_drivers_pkey PRIMARY KEY (driver_id);


--
-- TOC entry 2860 (class 2606 OID 1412287)
-- Dependencies: 2445 2445
-- Name: unit_holders_holder_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.unit_holders
    ADD CONSTRAINT unit_holders_holder_name_key UNIQUE (holder_name);


--
-- TOC entry 2829 (class 2606 OID 19930)
-- Dependencies: 2431 2431
-- Name: unit_id_unique; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.last_units_positions
    ADD CONSTRAINT unit_id_unique UNIQUE (unit_id);


--
-- TOC entry 2864 (class 2606 OID 19932)
-- Dependencies: 2446 2446
-- Name: unit_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units
    ADD CONSTRAINT unit_pk PRIMARY KEY (unit_id);


--
-- TOC entry 2872 (class 2606 OID 19934)
-- Dependencies: 2452 2452
-- Name: unit_position_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_positions
    ADD CONSTRAINT unit_position_pk PRIMARY KEY (gid);


--
-- TOC entry 2878 (class 2606 OID 19936)
-- Dependencies: 2454 2454 2454
-- Name: unit_to_group; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_to_groups
    ADD CONSTRAINT unit_to_group UNIQUE (group_id, unit_id);


--
-- TOC entry 2866 (class 2606 OID 19938)
-- Dependencies: 2447 2447
-- Name: units_conf_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_conf
    ADD CONSTRAINT units_conf_pk PRIMARY KEY (unit_id);


--
-- TOC entry 2868 (class 2606 OID 19940)
-- Dependencies: 2448 2448
-- Name: units_history_points_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_positions_recent
    ADD CONSTRAINT units_history_points_pk PRIMARY KEY (gid);



--
-- TOC entry 2914 (class 2606 OID 1412080)
-- Dependencies: 2485 2485
-- Name: units_to_drivers_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_to_drivers
    ADD CONSTRAINT units_to_drivers_pkey PRIMARY KEY (id);


--
-- TOC entry 2880 (class 2606 OID 19944)
-- Dependencies: 2454 2454
-- Name: units_to_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_to_groups
    ADD CONSTRAINT units_to_groups_pkey PRIMARY KEY (id);


--
-- TOC entry 2882 (class 2606 OID 1410607)
-- Dependencies: 2456 2456 2456
-- Name: units_to_sensors_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_to_sensors
    ADD CONSTRAINT units_to_sensors_pkey PRIMARY KEY (sensor_id, unit_id);


--
-- TOC entry 2889 (class 2606 OID 19946)
-- Dependencies: 2459 2459
-- Name: units_tracks_conf_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_tracks_conf
    ADD CONSTRAINT units_tracks_conf_pk PRIMARY KEY (units_tracks_settings_id);


--
-- TOC entry 2887 (class 2606 OID 19948)
-- Dependencies: 2458 2458
-- Name: units_tracks_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.units_tracks
    ADD CONSTRAINT units_tracks_pk PRIMARY KEY (gid);


--
-- TOC entry 2858 (class 2606 OID 19950)
-- Dependencies: 2443 2443
-- Name: user_id_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.system_users
    ADD CONSTRAINT user_id_pk PRIMARY KEY (user_id);


--
-- TOC entry 2862 (class 2606 OID 19952)
-- Dependencies: 2445 2445
-- Name: user_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY maplog.unit_holders
    ADD CONSTRAINT user_pk PRIMARY KEY (holder_id);


--
-- TOC entry 2883 (class 1259 OID 90474)
-- Dependencies: 2458
-- Name: end_index; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX end_index ON maplog.units_tracks USING btree (track_end);


--
-- TOC entry 2900 (class 1259 OID 1411943)
-- Dependencies: 2478
-- Name: fki_alert_id_FK; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX "fki_alert_id_FK" ON maplog.alert_queries USING btree (alert_id);


--
-- TOC entry 2876 (class 1259 OID 19953)
-- Dependencies: 2454
-- Name: group_id_index; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX group_id_index ON maplog.units_to_groups USING btree (group_id);


--
-- TOC entry 2905 (class 1259 OID 1412044)
-- Dependencies: 2481
-- Name: ignition_i_gid; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX ignition_i_gid ON maplog.ignition_status USING btree (gid DESC NULLS LAST);


--
-- TOC entry 2906 (class 1259 OID 1412045)
-- Dependencies: 2481
-- Name: ignition_i_timestamp; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX ignition_i_timestamp ON maplog.ignition_status USING btree (time_stamp DESC NULLS LAST);


--
-- TOC entry 2832 (class 1259 OID 19954)
-- Dependencies: 2435
-- Name: observation_gid_index; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX observation_gid_index ON maplog.observations USING btree (gid);



--
-- TOC entry 2835 (class 1259 OID 19957)
-- Dependencies: 2435
-- Name: observation_sensor_id_index; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX observation_sensor_id_index ON maplog.observations USING btree (sensor_id);







--
-- TOC entry 2836 (class 1259 OID 19960)
-- Dependencies: 2435
-- Name: observations_index; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX observations_index ON maplog.observations USING btree (time_stamp);


--
-- TOC entry 2839 (class 1259 OID 19961)
-- Dependencies: 2435
-- Name: observations_unit_id; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX observations_unit_id ON maplog.observations USING btree (unit_id);




--
-- TOC entry 2852 (class 1259 OID 19963)
-- Dependencies: 2440
-- Name: sensors_phenomenon_id_index; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX sensors_phenomenon_id_index ON maplog.sensors USING btree (phenomenon_id);


--
-- TOC entry 2884 (class 1259 OID 90473)
-- Dependencies: 2458
-- Name: start_index; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX start_index ON maplog.units_tracks USING btree (track_start);


--
-- TOC entry 2885 (class 1259 OID 19964)
-- Dependencies: 2458
-- Name: tracks_gid_index; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX tracks_gid_index ON maplog.units_tracks USING btree (gid);


--
-- TOC entry 2873 (class 1259 OID 19965)
-- Dependencies: 2452
-- Name: unit_position_time_stamp; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX unit_position_time_stamp ON maplog.units_positions USING btree (time_stamp);


--
-- TOC entry 2874 (class 1259 OID 19966)
-- Dependencies: 2056 2452
-- Name: units_positins_gid; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX units_positins_gid ON maplog.units_positions USING gist (the_geom);


--
-- TOC entry 2875 (class 1259 OID 19967)
-- Dependencies: 2452
-- Name: units_positions_unit_id; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX units_positions_unit_id ON maplog.units_positions USING btree (unit_id);


--
-- TOC entry 2988 (class 2620 OID 1411903)
-- Dependencies: 739 2477
-- Name: add_alert; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_alert
    BEFORE INSERT ON alert_events
    FOR EACH ROW
    EXECUTE PROCEDURE add_alert();


--
-- TOC entry 2978 (class 2620 OID 19968)
-- Dependencies: 41 2446
-- Name: add_conf_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_conf_trigger
    AFTER INSERT ON units
    FOR EACH ROW
    EXECUTE PROCEDURE add_unit_conf();


--
-- TOC entry 2989 (class 2620 OID 1411904)
-- Dependencies: 23 2477
-- Name: add_gid_to_observation; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_gid_to_observation
    BEFORE INSERT ON maplog.alert_events
    FOR EACH ROW
    EXECUTE PROCEDURE add_gid_to_observation();


--
-- TOC entry 2969 (class 2620 OID 19969)
-- Dependencies: 23 2435
-- Name: add_gid_to_observation_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_gid_to_observation_trigger
    BEFORE INSERT OR UPDATE ON observations
    FOR EACH ROW
    EXECUTE PROCEDURE add_gid_to_observation();


--
-- TOC entry 2976 (class 2620 OID 19970)
-- Dependencies: 24 2440
-- Name: add_phenomenon_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_phenomenon_trigger
    BEFORE INSERT OR UPDATE ON sensors
    FOR EACH ROW
    EXECUTE PROCEDURE add_phenomenon();


--
-- TOC entry 2970 (class 2620 OID 19971)
-- Dependencies: 26 2435
-- Name: add_sensor; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_sensor
    BEFORE INSERT OR UPDATE ON observations
    FOR EACH ROW
    EXECUTE PROCEDURE add_sensor();


--
-- TOC entry 2981 (class 2620 OID 80912)
-- Dependencies: 2446 28
-- Name: add_to_admin; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_to_admin
    AFTER INSERT OR UPDATE ON units
    FOR EACH ROW
    EXECUTE PROCEDURE add_unit_to_admin();


--
-- TOC entry 2979 (class 2620 OID 19972)
-- Dependencies: 2446 43
-- Name: add_track_conf_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_track_conf_trigger
    AFTER INSERT ON units
    FOR EACH ROW
    EXECUTE PROCEDURE add_unit_track_conf();


--
-- TOC entry 2971 (class 2620 OID 19973)
-- Dependencies: 2435 27
-- Name: add_unit_triger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER add_unit_triger
    BEFORE INSERT OR UPDATE ON observations
    FOR EACH ROW
    EXECUTE PROCEDURE add_unit();


--
-- TOC entry 2975 (class 2620 OID 1412142)
-- Dependencies: 2435 743
-- Name: copy_ignition_status; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER copy_ignition_status
    AFTER INSERT ON observations
    FOR EACH ROW
    EXECUTE PROCEDURE copy_ignition_status();





--
-- TOC entry 2990 (class 2620 OID 1411924)
-- Dependencies: 740 2478
-- Name: delete_alert_query_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER delete_alert_query_trigger
    BEFORE DELETE ON alert_queries
    FOR EACH ROW
    EXECUTE PROCEDURE on_delete_alert_query();


--
-- TOC entry 2987 (class 2620 OID 1411878)
-- Dependencies: 738 2476
-- Name: delete_alert_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER delete_alert_trigger
    BEFORE DELETE ON alerts
    FOR EACH ROW
    EXECUTE PROCEDURE on_delete_alert();


--
-- TOC entry 2991 (class 2620 OID 1412067)
-- Dependencies: 2483 742
-- Name: delete_driver_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER delete_driver_trigger
    BEFORE DELETE ON unit_drivers
    FOR EACH ROW
    EXECUTE PROCEDURE on_delete_driver();


--
-- TOC entry 2980 (class 2620 OID 19976)
-- Dependencies: 2446 741
-- Name: delete_unit_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER delete_unit_trigger
    BEFORE DELETE ON units
    FOR EACH ROW
    EXECUTE PROCEDURE on_delete_unit();


--
-- TOC entry 2985 (class 2620 OID 80996)
-- Dependencies: 46 2452
-- Name: on_delete; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER on_delete
    BEFORE DELETE ON units_positions
    FOR EACH ROW
    EXECUTE PROCEDURE on_delete_position();


--
-- TOC entry 2968 (class 2620 OID 90641)
-- Dependencies: 2429 45
-- Name: on_delete; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER on_delete
    BEFORE DELETE ON groups
    FOR EACH ROW
    EXECUTE PROCEDURE on_delete_group();


--
-- TOC entry 2977 (class 2620 OID 19978)
-- Dependencies: 2440 35
-- Name: on_delete_sensor; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER on_delete_sensor
    BEFORE DELETE ON sensors
    FOR EACH ROW
    EXECUTE PROCEDURE delete_sensor();


--
-- TOC entry 2983 (class 2620 OID 19979)
-- Dependencies: 2452 746
-- Name: trig_add_last_pos; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trig_add_last_pos
    AFTER INSERT OR UPDATE ON units_positions
    FOR EACH ROW
    EXECUTE PROCEDURE add_last_unit_position();


--
-- TOC entry 2986 (class 2620 OID 1412168)
-- Dependencies: 2452 745
-- Name: trig_cosider_add_pos2; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trig_cosider_add_pos2
    BEFORE INSERT ON units_positions
    FOR EACH ROW
    EXECUTE PROCEDURE consider_insert_position2();


--
-- TOC entry 2982 (class 2620 OID 19981)
-- Dependencies: 27 2452
-- Name: trig_unit; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trig_unit
    BEFORE INSERT OR UPDATE ON units_positions
    FOR EACH ROW
    EXECUTE PROCEDURE add_unit();


--
-- TOC entry 2972 (class 2620 OID 19982)
-- Dependencies: 42 2435
-- Name: unit_to_sensor_triger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER unit_to_sensor_triger
    BEFORE INSERT OR UPDATE ON observations
    FOR EACH ROW
    EXECUTE PROCEDURE add_unit_to_sensor();


--
-- TOC entry 2967 (class 2620 OID 19983)
-- Dependencies: 122 2429
-- Name: update_is_parent; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_is_parent
    AFTER INSERT OR UPDATE ON groups
    FOR EACH ROW
    EXECUTE PROCEDURE update_group();


--
-- TOC entry 2974 (class 2620 OID 1410625)
-- Dependencies: 2435 737
-- Name: update_sensor_time; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_sensor_time
    AFTER INSERT OR UPDATE ON observations
    FOR EACH ROW
    EXECUTE PROCEDURE ml_update_times();


--
-- TOC entry 2952 (class 2606 OID 1411888)
-- Dependencies: 2477 2476 2892
-- Name: alert_event_to_alert_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.alert_events
    ADD CONSTRAINT alert_event_to_alert_id_fk FOREIGN KEY (alert_id) REFERENCES alerts(alert_id);


--
-- TOC entry 2953 (class 2606 OID 1411893)
-- Dependencies: 2477 2452 2871
-- Name: alert_event_to_gid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.alert_events
    ADD CONSTRAINT alert_event_to_gid_fk FOREIGN KEY (gid) REFERENCES units_positions(gid);


--
-- TOC entry 2954 (class 2606 OID 1411898)
-- Dependencies: 2477 2446 2863
-- Name: alert_event_to_unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.alert_events
    ADD CONSTRAINT alert_event_to_unit_id_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2955 (class 2606 OID 1411919)
-- Dependencies: 2478 2476 2892
-- Name: alert_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.alert_queries
    ADD CONSTRAINT "alert_id_FK" FOREIGN KEY (alert_id) REFERENCES alerts(alert_id);


--
-- TOC entry 2929 (class 2606 OID 19984)
-- Dependencies: 2433 2438 2845
-- Name: alerts_conf_to_phenomenon_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.obs_alerts_conf
    ADD CONSTRAINT alerts_conf_to_phenomenon_id_fk FOREIGN KEY (phenomenon_id) REFERENCES phenomenons(phenomenon_id);


--
-- TOC entry 2930 (class 2606 OID 19989)
-- Dependencies: 2863 2446 2433
-- Name: alerts_conf_to_unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.obs_alerts_conf
    ADD CONSTRAINT alerts_conf_to_unit_id_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);




--
-- TOC entry 2925 (class 2606 OID 19994)
-- Dependencies: 2426 2863 2446
-- Name: geom_alerts_conf_to_unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.geom_alerts_conf
    ADD CONSTRAINT geom_alerts_conf_to_unit_id_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2945 (class 2606 OID 19999)
-- Dependencies: 2454 2429 2824
-- Name: group_id_pk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_to_groups
    ADD CONSTRAINT group_id_pk FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- TOC entry 2927 (class 2606 OID 20004)
-- Dependencies: 2452 2871 2431
-- Name: last_pos_gid; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.last_units_positions
    ADD CONSTRAINT last_pos_gid FOREIGN KEY (gid) REFERENCES units_positions(gid);


--
-- TOC entry 2958 (class 2606 OID 1412016)
-- Dependencies: 2452 2480 2871
-- Name: last_status_to_gid; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.last_ignition_status
    ADD CONSTRAINT last_status_to_gid FOREIGN KEY (gid) REFERENCES units_positions(gid);


--
-- TOC entry 2959 (class 2606 OID 1412021)
-- Dependencies: 2863 2480 2446
-- Name: last_status_to_unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.last_ignition_status
    ADD CONSTRAINT last_status_to_unit_id_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2928 (class 2606 OID 20009)
-- Dependencies: 2431 2446 2863
-- Name: last_units_positions_to_unit_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.last_units_positions
    ADD CONSTRAINT last_units_positions_to_unit_name_fkey FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2966 (class 2606 OID 1472778)
-- Dependencies: 2833 2492 2435
-- Name: obs_to_obs_main_obs_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.obs_to_obs
    ADD CONSTRAINT obs_to_obs_main_obs_id_fkey FOREIGN KEY (main_obs_id) REFERENCES observations(observation_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2965 (class 2606 OID 1472773)
-- Dependencies: 2833 2435 2492
-- Name: obs_to_obs_sec_obs_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.obs_to_obs
    ADD CONSTRAINT obs_to_obs_sec_obs_id_fkey FOREIGN KEY (sec_obs_id) REFERENCES observations(observation_id) ON UPDATE CASCADE ON DELETE CASCADE;



--
-- TOC entry 2931 (class 2606 OID 20029)
-- Dependencies: 2452 2435 2871
-- Name: observation_to_gid; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.observations
    ADD CONSTRAINT observation_to_gid FOREIGN KEY (gid) REFERENCES units_positions(gid);


--
-- TOC entry 2932 (class 2606 OID 20034)
-- Dependencies: 2440 2850 2435
-- Name: observation_to_sensor_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.observations
    ADD CONSTRAINT observation_to_sensor_fk FOREIGN KEY (sensor_id) REFERENCES sensors(sensor_id);


--
-- TOC entry 2933 (class 2606 OID 20039)
-- Dependencies: 2863 2446 2435
-- Name: observation_to_unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.observations
    ADD CONSTRAINT observation_to_unit_id_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2926 (class 2606 OID 20044)
-- Dependencies: 2429 2824 2429
-- Name: parent_group_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.groups
    ADD CONSTRAINT parent_group_fk FOREIGN KEY (parent_group_id) REFERENCES groups(id);


--
-- TOC entry 2956 (class 2606 OID 1411931)
-- Dependencies: 2898 2479 2478
-- Name: query_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.alert_queries_to_units
    ADD CONSTRAINT query_id_fk FOREIGN KEY (query_id) REFERENCES alert_queries(query_id);


--
-- TOC entry 2947 (class 2606 OID 20054)
-- Dependencies: 2456 2850 2440
-- Name: sensor_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_to_sensors
    ADD CONSTRAINT sensor_id_fk FOREIGN KEY (sensor_id) REFERENCES sensors(sensor_id);


--
-- TOC entry 2938 (class 2606 OID 20059)
-- Dependencies: 2845 2440 2438
-- Name: sensor_to_phenomenon_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.sensors
    ADD CONSTRAINT sensor_to_phenomenon_fk FOREIGN KEY (phenomenon_id) REFERENCES phenomenons(phenomenon_id);


--
-- TOC entry 2951 (class 2606 OID 113873)
-- Dependencies: 2857 2443 2468
-- Name: sessions_system_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.sessions
    ADD CONSTRAINT sessions_system_user_id_fkey FOREIGN KEY (system_user_id) REFERENCES system_users(user_id);


--
-- TOC entry 2960 (class 2606 OID 1412034)
-- Dependencies: 2452 2481 2871
-- Name: status_to_gid; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.ignition_status
    ADD CONSTRAINT status_to_gid FOREIGN KEY (gid) REFERENCES units_positions(gid);


--
-- TOC entry 2961 (class 2606 OID 1412039)
-- Dependencies: 2863 2481 2446
-- Name: status_to_unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.ignition_status
    ADD CONSTRAINT status_to_unit_id_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2939 (class 2606 OID 20064)
-- Dependencies: 2429 2443 2824
-- Name: system_users_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.system_users
    ADD CONSTRAINT system_users_group_id_fkey FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- TOC entry 2941 (class 2606 OID 20069)
-- Dependencies: 2863 2446 2447
-- Name: unit_conf_to_unit_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_conf
    ADD CONSTRAINT unit_conf_to_unit_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2948 (class 2606 OID 20074)
-- Dependencies: 2446 2456 2863
-- Name: unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_to_sensors
    ADD CONSTRAINT unit_id_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2957 (class 2606 OID 1411936)
-- Dependencies: 2479 2446 2863
-- Name: unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.alert_queries_to_units
    ADD CONSTRAINT unit_id_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2946 (class 2606 OID 20079)
-- Dependencies: 2454 2446 2863
-- Name: unit_id_pk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_to_groups
    ADD CONSTRAINT unit_id_pk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2944 (class 2606 OID 20084)
-- Dependencies: 2452 2446 2863
-- Name: unit_position_to_unit_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_positions
    ADD CONSTRAINT unit_position_to_unit_name_fkey FOREIGN KEY (unit_id) REFERENCES units(unit_id);



--
-- TOC entry 2940 (class 2606 OID 20094)
-- Dependencies: 2445 2446 2861
-- Name: unit_to_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units
    ADD CONSTRAINT unit_to_user_fk FOREIGN KEY (holder_id) REFERENCES unit_holders(holder_id);


--
-- TOC entry 2942 (class 2606 OID 20099)
-- Dependencies: 2446 2863 2448
-- Name: units_history_points_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_positions_recent
    ADD CONSTRAINT units_history_points_fkey FOREIGN KEY (unit_id) REFERENCES units(unit_id);

--
-- TOC entry 2950 (class 2606 OID 20109)
-- Dependencies: 2863 2446 2459
-- Name: units_tracks_conf_to_units_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_tracks_conf
    ADD CONSTRAINT units_tracks_conf_to_units_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


--
-- TOC entry 2949 (class 2606 OID 20114)
-- Dependencies: 2863 2446 2458
-- Name: units_tracks_to_units_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY maplog.units_tracks
    ADD CONSTRAINT units_tracks_to_units_fk FOREIGN KEY (unit_id) REFERENCES units(unit_id);


-- Completed on 2011-07-27 13:21:03 CEST

--
-- PostgreSQL database dump complete
--

