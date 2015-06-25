-- ##################################################################################
-- ##    UV permissions initialization script specific for eDemo installation       #
-- ##    REQUIRES database initialized by data-edem.sql script !!                   #
-- ##################################################################################

-- clear permissions tables
DELETE FROM permission;
ALTER SEQUENCE "seq_permission" RESTART WITH 1;

-- Insert permissions
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'administrator', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.delete', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.edit', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.definePipelineDependencies', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.export', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.exportScheduleRules', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.import', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.importScheduleRules', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.importUserData', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.schedule', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.runDebug', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.exportDpuData', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.exportDpuJars', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.setVisibility', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.setVisibilityPublicRw', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.delete', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.stop', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.run', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.delete', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.edit', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.setPriority', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.createFromInstance', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.setVisibility', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.delete', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.edit', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.export', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.copy', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.showScreen', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'user.management', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.copy', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'runtimeProperties.edit', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'userNotificationSettings.editEmailGlobal', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'userNotificationSettings.editNotificationFrequency', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'userNotificationSettings.createPipelineExecutionSettings', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));