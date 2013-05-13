INSERT INTO "DB"."INTLIB"."DPU_MODEL" (id, name, description, type, jar_path) VALUES
(1, 'RDF Extractor', 'Extracts RDF data.', 'EXTRACTOR', 'RDF_extractor-0.0.1.jar');
INSERT INTO "DB"."INTLIB"."DPU_MODEL" (id, name, description, type, jar_path) VALUES
(2, 'File Extractor', 'Extracts RDF data from file.', 'EXTRACTOR', 'File_extractor-0.0.1.jar');
INSERT INTO "DB"."INTLIB"."DPU_MODEL" (id, name, description, type, jar_path) VALUES
(3, 'SPARQL Transformer', 'SPARQL Transformer.', 'TRANSFORMER', 'SPARQL_transformer-0.0.1.jar');
INSERT INTO "DB"."INTLIB"."DPU_MODEL" (id, name, description, type, jar_path) VALUES
(4, 'RDF Loader', 'Loads RDF data.', 'LOADER', 'RDF_loader-0.0.1.jar');
INSERT INTO "DB"."INTLIB"."DPU_MODEL" (id, name, description, type, jar_path) VALUES
(5, 'File Loader', 'Lods RDF data into file.', 'LOADER', 'File_loader-0.0.1.jar');

INSERT INTO "DB"."INTLIB"."DPU_TEMPLATE_CONFIG" (id, dpu_id) VALUES
(1, 1);
INSERT INTO "DB"."INTLIB"."DPU_TEMPLATE_CONFIG" (id, dpu_id) VALUES
(2, 2);
INSERT INTO "DB"."INTLIB"."DPU_TEMPLATE_CONFIG" (id, dpu_id) VALUES
(3, 3);
INSERT INTO "DB"."INTLIB"."DPU_TEMPLATE_CONFIG" (id, dpu_id) VALUES
(4, 4);
INSERT INTO "DB"."INTLIB"."DPU_TEMPLATE_CONFIG" (id, dpu_id) VALUES
(5, 5);
