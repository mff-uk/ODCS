
DROP TABLE "DB"."INTLIB"."DPU_ICONFIG_PAIRS";
DROP TABLE "DB"."INTLIB"."DPU_TCONFIG_PAIRS";
DROP TABLE "DB"."INTLIB"."DPU_INSTANCE_CONFIG";
DROP TABLE "DB"."INTLIB"."DPU_TEMPLATE_CONFIG";
DROP TABLE "DB"."INTLIB"."DPU_RECORD";
DROP TABLE "DB"."INTLIB"."PPL_EDGE";
DROP TABLE "DB"."INTLIB"."PPL_NODE";
DROP TABLE "DB"."INTLIB"."PPL_POSITION";
DROP TABLE "DB"."INTLIB"."PPL_GRAPH";
DROP TABLE "DB"."INTLIB"."PPL_EXECUTION";
DROP TABLE "DB"."INTLIB"."PPL_MODEL";
DROP TABLE "DB"."INTLIB"."DPU_INSTANCE";
DROP TABLE "DB"."INTLIB"."DPU_MODEL";


CREATE TABLE "DB"."INTLIB"."PPL_MODEL"
(
  "id" INTEGER IDENTITY,
  "name" VARCHAR(45),
  "description" VARCHAR(255),
  PRIMARY KEY ("id")
);

CREATE TABLE "DB"."INTLIB"."PPL_GRAPH"
(
  "id" INTEGER IDENTITY,
  "pipeline_id" INTEGER,
  PRIMARY KEY ("id"),
  UNIQUE (pipeline_id)
);

CREATE TABLE "DB"."INTLIB"."PPL_NODE"
(
  "id" INTEGER IDENTITY,
  "graph_id" INTEGER,
  "instance_id" INTEGER,
  "position_id" INTEGER,
  PRIMARY KEY ("id")
);

CREATE TABLE "DB"."INTLIB"."PPL_EDGE"
(
  "id" INTEGER IDENTITY,
  "graph_id" INTEGER,
  "node_from_id" INTEGER,
  "node_to_id" INTEGER,
  PRIMARY KEY ("id")
);

CREATE TABLE "DB"."INTLIB"."PPL_POSITION"
(
  "id" INTEGER IDENTITY,
  "pos_x" INTEGER,
  "pos_y" INTEGER,
  PRIMARY KEY ("id")
);

CREATE TABLE "DB"."INTLIB"."PPL_EXECUTION"
(
  "id" INTEGER IDENTITY,
  "status" INTEGER,
  "pipeline_id" INTEGER,
  "debug_mode" SMALLINT,
  "t_start" DATETIME,
  "t_end" DATETIME,
  "execution_directory" VARCHAR(255),
  PRIMARY KEY ("id")
);

CREATE TABLE "DB"."INTLIB"."DPU_RECORD"
(
  "id" INTEGER IDENTITY,
  "r_time" DATETIME,
  "r_type" SMALLINT,
  "dpu_id" INTEGER,
  "execution_id" INTEGER,
  "short_message" LONG VARCHAR,
  "full_message" LONG VARCHAR,
  PRIMARY KEY ("id")
);

CREATE TABLE "DB"."INTLIB"."DPU_INSTANCE"
(
  "id" INTEGER IDENTITY,
  "name" VARCHAR(45),
  "description" VARCHAR(255),
  "dpu_id" INTEGER,
  PRIMARY KEY ("id")
);

CREATE TABLE "DB"."INTLIB"."DPU_INSTANCE_CONFIG"
(
  "id" INTEGER IDENTITY,
  "instance_id" INTEGER,
  PRIMARY KEY ("id"),
  UNIQUE (instance_id)
);

CREATE TABLE "DB"."INTLIB"."DPU_TEMPLATE_CONFIG"
(
  "id" INTEGER IDENTITY,
  "dpu_id" INTEGER,
  PRIMARY KEY ("id"),
  UNIQUE (dpu_id)
);

CREATE TABLE "DB"."INTLIB"."DPU_ICONFIG_PAIRS"
(
  "conf_id" INTEGER,
  "c_property" VARCHAR(255),
  "c_value" LONG VARBINARY,
  PRIMARY KEY ("conf_id", "c_property")
);

CREATE TABLE "DB"."INTLIB"."DPU_TCONFIG_PAIRS"
(
  "conf_id" INTEGER,
  "c_property" VARCHAR(255),
  "c_value" LONG VARBINARY,
  PRIMARY KEY ("conf_id", "c_property")
);

CREATE TABLE "DB"."INTLIB"."DPU_MODEL"
(
  "id" INTEGER IDENTITY,
  "name" VARCHAR(45),
  "description" VARCHAR(255),
  "type" SMALLINT,
  "visibility" SMALLINT,
  "jar_path" VARCHAR(255),
  PRIMARY KEY ("id")
);

-- CONSTRAINTS

ALTER TABLE "DB"."intlib"."PPL_GRAPH"
  ADD CONSTRAINT "PPL_GRAPH_PPL_MODEL_id_id" FOREIGN KEY ("pipeline_id")
    REFERENCES "DB"."intlib"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE "DB"."intlib"."PPL_NODE"
  ADD CONSTRAINT "PPL_NODE_PPL_GRAPH_id_id" FOREIGN KEY ("graph_id")
    REFERENCES "DB"."intlib"."PPL_GRAPH" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."intlib"."PPL_NODE"
  ADD CONSTRAINT "PPL_NODE_DPU_INSTANCE_id_id" FOREIGN KEY ("instance_id")
    REFERENCES "DB"."intlib"."DPU_INSTANCE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."intlib"."PPL_NODE"
  ADD CONSTRAINT "PPL_NODE_PPL_POSITION_id_id" FOREIGN KEY ("position_id")
    REFERENCES "DB"."intlib"."PPL_POSITION" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE "DB"."intlib"."PPL_EDGE"
  ADD CONSTRAINT "PPL_EDGE_PPL_GRAPH_id_id" FOREIGN KEY ("graph_id")
    REFERENCES "DB"."intlib"."PPL_GRAPH" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."intlib"."PPL_EDGE"
  ADD CONSTRAINT "PPL_EDGE_PPL_NODE_FROM_id_id" FOREIGN KEY ("node_from_id")
    REFERENCES "DB"."intlib"."PPL_NODE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."intlib"."PPL_EDGE"
  ADD CONSTRAINT "PPL_EDGE_PPL_NODE_TO_id_id" FOREIGN KEY ("node_to_id")
    REFERENCES "DB"."intlib"."PPL_NODE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE "DB"."intlib"."PPL_EXECUTION"
  ADD CONSTRAINT "PPL_EXECUTION_PPL_MODEL_id_id" FOREIGN KEY ("pipeline_id")
    REFERENCES "DB"."intlib"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE "DB"."intlib"."DPU_RECORD"
  ADD CONSTRAINT "DPU_RECORD_DPU_INSTANCE_id_id" FOREIGN KEY ("dpu_id")
    REFERENCES "DB"."intlib"."DPU_INSTANCE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."intlib"."DPU_RECORD"
  ADD CONSTRAINT "DPU_RECORD_PPL_EXECUTION_id_id" FOREIGN KEY ("execution_id")
    REFERENCES "DB"."intlib"."PPL_EXECUTION" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE "DB"."intlib"."DPU_INSTANCE"
  ADD CONSTRAINT "DPU_INSTANCE_DPU_MODEL_id_id" FOREIGN KEY ("dpu_id")
    REFERENCES "DB"."intlib"."DPU_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE "DB"."intlib"."DPU_INSTANCE_CONFIG"
  ADD CONSTRAINT "DPU_INSTANCE_CONFIG_DPU_INSTANCE_id_id" FOREIGN KEY ("instance_id")
    REFERENCES "DB"."intlib"."DPU_INSTANCE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE "DB"."intlib"."DPU_TEMPLATE_CONFIG"
  ADD CONSTRAINT "DPU_TEMPLATE_CONFIG_DPU_MODEL_id_id" FOREIGN KEY ("dpu_id")
    REFERENCES "DB"."intlib"."DPU_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE "DB"."intlib"."DPU_ICONFIG_PAIRS"
  ADD CONSTRAINT "DPU_ICONFIG_PAIRS_DPU_INSTANCE_CONFIG_id_id" FOREIGN KEY ("conf_id")
    REFERENCES "DB"."intlib"."DPU_INSTANCE_CONFIG" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE "DB"."intlib"."DPU_TCONFIG_PAIRS"
  ADD CONSTRAINT "DPU_TCONFIG_PAIRS_DPU_TEMPLATE_CONFIG_id_id" FOREIGN KEY ("conf_id")
    REFERENCES "DB"."intlib"."DPU_TEMPLATE_CONFIG" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

-- workaround for bug in virtuoso's implementation of cascades on delete
-- see https://github.com/openlink/virtuoso-opensource/issues/56
CREATE TRIGGER delete_node_fix BEFORE DELETE ON ppl_node REFERENCING old AS n
{
	DELETE FROM ppl_edge
	 WHERE node_from_id = n.id
	  OR node_to_id = n.id;
};

