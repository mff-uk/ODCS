-- Update version.
UPDATE "properties" SET "value" = '002.001.000' WHERE "key" = 'UV.Core.version';
UPDATE "properties" SET "value" = '002.000.000' WHERE "key" = 'UV.Plugin-DevEnv.version';

-- Add new columns
ALTER TABLE "dpu_instance"
ADD COLUMN "menu_name" VARCHAR(255);
ALTER TABLE "dpu_template"
ADD COLUMN "menu_name" VARCHAR(255);

-- Permission changes
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.definePipelineDependencies', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
UPDATE permission SET rwonly = true WHERE name = 'pipeline.schedule';
UPDATE permission SET rwonly = true WHERE name = 'pipeline.runDebug';
UPDATE permission SET rwonly = true WHERE name = 'pipeline.run';
DELETE FROM permission WHERE name = 'pipelineExecution.downloadAllLogs';
DELETE FROM permission WHERE name = 'pipelineExecution.readDpuInputOutputData';
DELETE FROM permission WHERE name = 'pipelineExecution.readEvent';
DELETE FROM permission WHERE name = 'pipelineExecution.readLog';
DELETE FROM permission WHERE name = 'pipelineExecution.sparqlDpuInputOutputData';
DELETE FROM permission WHERE name = 'scheduleRule.disable';
DELETE FROM permission WHERE name = 'scheduleRule.enable';
UPDATE permission SET rwonly = true WHERE name = 'scheduleRule.execute';
DELETE FROM permission WHERE name = 'deleteDebugResources';
DELETE FROM permission WHERE name = 'dpuTemplate.save';
DELETE FROM permission WHERE name = 'dpuTemplate.import';
INSERT INTO permission VALUES (nextval('seq_permission'), 'dpuTemplate.createFromInstance', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
-- Map existing permissions to roles
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name = 'pipeline.exportScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.exportScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name = 'pipeline.importScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.importScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name = 'pipeline.importUserData'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.importUserData'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name = 'dpuTemplate.setVisibilityAtCreate'));
