ALTER TABLE dpu_instance DISABLE KEYS;
ALTER TABLE dpu_template DISABLE KEYS;
ALTER TABLE exec_context_dpu DISABLE KEYS;
ALTER TABLE exec_context_pipeline DISABLE KEYS;
ALTER TABLE exec_dataunit_info DISABLE KEYS;
ALTER TABLE exec_pipeline DISABLE KEYS;
ALTER TABLE exec_record DISABLE KEYS;
ALTER TABLE exec_schedule DISABLE KEYS;
ALTER TABLE exec_schedule_after DISABLE KEYS;
ALTER TABLE logging DISABLE KEYS;
ALTER TABLE ppl_edge DISABLE KEYS;
ALTER TABLE ppl_graph DISABLE KEYS;
ALTER TABLE ppl_model DISABLE KEYS;
ALTER TABLE ppl_node DISABLE KEYS;
ALTER TABLE ppl_open_event DISABLE KEYS;
ALTER TABLE ppl_position DISABLE KEYS;
ALTER TABLE ppl_ppl_conflicts DISABLE KEYS;
ALTER TABLE rdf_ns_prefix DISABLE KEYS;
ALTER TABLE sch_email DISABLE KEYS;
ALTER TABLE sch_sch_notification DISABLE KEYS;
ALTER TABLE sch_sch_notification_email DISABLE KEYS;
ALTER TABLE sch_usr_notification DISABLE KEYS;
ALTER TABLE sch_usr_notification_email DISABLE KEYS;
ALTER TABLE usr_user DISABLE KEYS;
ALTER TABLE usr_user_role DISABLE KEYS;
ET FOREIGN_KEY_CHECKS=0;