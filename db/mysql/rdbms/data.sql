-- THIS FILE IS AUTOMATICALLY GENERATED BY A SCRIPT.
-- DO NOT EDIT IT MANUALLY, YOUR CHANGES WILL BE LOST!!!


SET FOREIGN_KEY_CHECKS = 0;
-- dbdump: dumping datasource "localhost:1111", username=dba
-- tablequalifier=NULL  tableowner=NULL  tablename=is given, one or more  tabletype=NULL
-- Connected to datasource "OpenLink Virtuoso", Driver v. 06.01.3127 OpenLink Virtuoso ODBC Driver.
-- get_all_tables: tablepattern="db.odcs.%",9
-- Definitions of 27 tables were read in.
-- SELECT * FROM `dpu_instance`
INSERT INTO `dpu_instance`(id,name,use_dpu_description,description,tool_tip,configuration,config_valid,dpu_id) VALUES(1,'SPARQL Extractor',1,'Extract from SPARQL: http://dbpedia.org/sparql','',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>
    <SPARQL__endpoint>http://dbpedia.org/sparql</SPARQL__endpoint>
    <Host__name></Host__name>
    <Password></Password>
    <GraphsUri class="linked-list">
      <string>http://dbpedia.org</string>
    </GraphsUri> 
    <SPARQL__query>CONSTRUCT {&lt;http://dbpedia.org/resource/Prague&gt; ?p ?o} where {&lt;http://dbpedia.org/resource/Prague&gt; ?p ?o } LIMIT 100</SPARQL__query>
    <ExtractFail>true</ExtractFail>
    <UseStatisticalHandler>false</UseStatisticalHandler>
  </cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>
</object-stream>',1,1);
INSERT INTO `dpu_instance`(id,name,use_dpu_description,description,tool_tip,configuration,config_valid,dpu_id) VALUES(2,'RDF File Loader',1,'Load to: /tmp/dbpedia.rdf','',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>
    <FilePath>/tmp/dbpedia.rdf</FilePath>
    <RDFFileFormat>RDFXML</RDFFileFormat>
    <DiffName>false</DiffName>
  </cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>
</object-stream>',1,5);

-- Table `dpu_instance` 2 rows output.
-- SELECT * FROM `dpu_template`
INSERT INTO `dpu_template`(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(1,'SPARQL Extractor',0,'Extracts RDF triples from SPARQL endpoint',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>
    <SPARQL__endpoint></SPARQL__endpoint>
    <Host__name></Host__name>
    <Password></Password>
    <SPARQL__query></SPARQL__query>
    <ExtractFail>true</ExtractFail>
    <UseStatisticalHandler>true</UseStatisticalHandler>
    <failWhenErrors>false</failWhenErrors>
    <retryTime>1000</retryTime>
    <retrySize>-1</retrySize>
    <endpointParams>
      <queryParam>query</queryParam>
      <defaultGraphParam>default-graph-uri</defaultGraphParam>
      <namedGraphParam>named-graph-uri</namedGraphParam>
      <defaultGraphURI class="linked-list"/>
      <namedGraphURI class="linked-list"/>
      <requestType>POST_URL_ENCODER</requestType>
    </endpointParams>
    <useSplitConstruct>false</useSplitConstruct>
    <splitConstructSize>50000</splitConstructSize>
  </cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>
</object-stream>',NULL,1,1,1,0,'SPARQL_Extractor','SPARQL_Extractor-1.0.0.jar','Extracts RDF data from SPARQL.');

INSERT INTO `dpu_template`(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(2,'RDF File Extractor',0,'Extracts RDF data from a file',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig>
    <Path></Path>
    <FileSuffix></FileSuffix>
    <RDFFormatValue>AUTO</RDFFormatValue>
    <fileExtractType>PATH_TO_FILE</fileExtractType>
    <OnlyThisSuffix>false</OnlyThisSuffix>
    <UseStatisticalHandler>true</UseStatisticalHandler>
  </cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig>
</object-stream>',NULL,1,1,1,0,'RDF_File_Extractor','RDF_File_Extractor-1.0.0.jar','Extracts RDF data from a file.');

INSERT INTO `dpu_template`(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(3,'SPARQL Transformer',0,'Transforms RDF data based on SPARQL (update) query',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.transformer.SPARQL.SPARQLTransformerConfig>
    <SPARQL__Update__Query></SPARQL__Update__Query>
    <isConstructType>false</isConstructType>
  </cz.cuni.mff.xrg.odcs.transformer.SPARQL.SPARQLTransformerConfig>
</object-stream>',NULL,1,1,1,1,'SPARQL_Transformer','SPARQL_Transformer-1.0.0.jar','SPARQL Transformer.');

INSERT INTO `dpu_template`(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(4,'SPARQL Loader',0,'Loads RDF data to SPARQL endpoint.',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.loader.rdf.RDFLoaderConfig>
    <SPARQL__endpoint></SPARQL__endpoint>
    <Host__name></Host__name>
    <Password></Password>
    <GraphsUri class="linked-list"/>
    <graphOption>OVERRIDE</graphOption>
    <insertOption>STOP_WHEN_BAD_PART</insertOption>
    <chunkSize>100</chunkSize>
    <validDataBefore>false</validDataBefore>
    <retryTime>1000</retryTime>
    <retrySize>-1</retrySize>
    <endpointParams>
      <queryParam>update</queryParam>
      <defaultGraphParam>using-graph-uri</defaultGraphParam>
      <postType>POST_URL_ENCODER</postType>
    </endpointParams>
  </cz.cuni.mff.xrg.odcs.loader.rdf.RDFLoaderConfig>
</object-stream>',NULL,1,1,1,2,'SPARQL_Loader','SPARQL_Loader-1.0.0.jar','Loads RDF data to SPARQL endpoint.');

INSERT INTO `dpu_template`(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(5,'RDF File Loader',0,'Loads RDF data into file.',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>
    <FilePath></FilePath>
    <RDFFileFormat>AUTO</RDFFileFormat>
    <DiffName>false</DiffName>
    <validDataBefore>false</validDataBefore>
  </cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>
</object-stream>',NULL,1,1,1,2,'RDF_File_Loader','RDF_File_Loader-1.0.0.jar','Loads RDF data into file.');

INSERT INTO `dpu_template`(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(6,'RDF Data Validator',0,'Validates RDF data and creates a validation report.',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.rdf.validator.RDFDataValidatorConfig>
    <stopExecution>false</stopExecution>
    <sometimesOutput>true</sometimesOutput>
  </cz.cuni.mff.xrg.odcs.rdf.validator.RDFDataValidatorConfig>
</object-stream>',NULL,1,1,1,1,'RDF_Data_Validator','RDF_Data_Validator-1.0.0.jar','Validate RDF data and create validation report.');

-- Table `dpu_template` 5 rows output.
-- SELECT * FROM `exec_context_dpu`
-- Table `exec_context_dpu` 0 rows output.
-- SELECT * FROM `exec_context_pipeline`
-- Table `exec_context_pipeline` 0 rows output.
-- SELECT * FROM `exec_dataunit_info`
-- Table `exec_dataunit_info` 0 rows output.
-- SELECT * FROM `exec_pipeline`
-- Table `exec_pipeline` 0 rows output.
-- Table `exec_record` has more than one blob column.
-- The column full_message of type LONG VARCHAR might not get properly inserted.
-- SELECT * FROM `exec_record`
-- Table `exec_record` has more than one blob column.
-- The column full_message of type LONG VARCHAR might not get properly inserted.
-- Table `exec_record` 0 rows output.
-- SELECT * FROM `exec_schedule`
-- Table `exec_schedule` 0 rows output.
-- SELECT * FROM `exec_schedule_after`
-- Table `exec_schedule_after` 0 rows output.
-- Table `logging` has more than one blob column.
-- The column stack_trace of type LONG VARCHAR might not get properly inserted.
-- SELECT * FROM `logging`
-- Table `logging` has more than one blob column.
-- The column stack_trace of type LONG VARCHAR might not get properly inserted.
-- Table `logging` 0 rows output.
-- SELECT * FROM `logging_event`
-- Table `logging_event` 0 rows output.
-- SELECT * FROM `logging_event_exception`
-- Table `logging_event_exception` 0 rows output.
-- SELECT * FROM `logging_event_property`
-- Table `logging_event_property` 0 rows output.
-- SELECT * FROM `ppl_edge`
INSERT INTO `ppl_edge`(id,graph_id,node_from_id,node_to_id,data_unit_name) VALUES(2,1,1,2,'output -> input;');
-- Table `ppl_edge` 1 rows output.
-- SELECT * FROM `ppl_graph`
INSERT INTO `ppl_graph`(id,pipeline_id) VALUES(1,1);
-- Table `ppl_graph` 1 rows output.
-- SELECT * FROM `ppl_model`
INSERT INTO `ppl_model`(id,name,description,user_id,visibility) VALUES(1,'DBpedia','Loads 100 triples from DBpedia.',2,2);
-- Table `ppl_model` 1 rows output.
-- SELECT * FROM `ppl_node`
INSERT INTO `ppl_node`(id,graph_id,instance_id,position_id) VALUES(1,1,1,1);
INSERT INTO `ppl_node`(id,graph_id,instance_id,position_id) VALUES(2,1,2,2);
-- Table `ppl_node` 2 rows output.
-- SELECT * FROM `ppl_position`
INSERT INTO `ppl_position`(id,pos_x,pos_y) VALUES(1,138,52);
INSERT INTO `ppl_position`(id,pos_x,pos_y) VALUES(2,487,132);
-- Table `ppl_position` 2 rows output.
-- SELECT * FROM `ppl_ppl_conflicts`
-- Table `ppl_ppl_conflicts` 0 rows output.
-- SELECT * FROM `rdf_ns_prefix`
-- Table `rdf_ns_prefix` 0 rows output.
-- SELECT * FROM `sch_email`
INSERT INTO `sch_email`(id,e_user,e_domain) VALUES(1,'admin','example.com');
INSERT INTO `sch_email`(id,e_user,e_domain) VALUES(2,'user','example.com');
-- Table `sch_email` 2 rows output.
-- SELECT * FROM `sch_sch_notification`
-- Table `sch_sch_notification` 0 rows output.
-- SELECT * FROM `sch_sch_notification_email`
-- Table `sch_sch_notification_email` 0 rows output.
-- SELECT * FROM `sch_usr_notification`
INSERT INTO `sch_usr_notification`(id,user_id,type_success,type_error) VALUES(1,1,1,1);
INSERT INTO `sch_usr_notification`(id,user_id,type_success,type_error) VALUES(2,2,1,1);
-- Table `sch_usr_notification` 2 rows output.
-- SELECT * FROM `sch_usr_notification_email`
INSERT INTO `sch_usr_notification_email`(notification_id,email_id) VALUES(1,1);
INSERT INTO `sch_usr_notification_email`(notification_id,email_id) VALUES(2,2);
-- Table `sch_usr_notification_email` 2 rows output.
-- SELECT * FROM `usr_user`
INSERT INTO `usr_user`(id,username,email_id,u_password,full_name,table_rows) VALUES(1,'admin',1,'10:34dbe217a123a1501be647832c77571bd0af1c8b584be30404157da1111499b9:f09771bb5a73b35d6d8cd8b5dfb0cf26bf58a71f6d3f4c1a8c92e33fb263aaff','John Admin',20);
INSERT INTO `usr_user`(id,username,email_id,u_password,full_name,table_rows) VALUES(2,'user',2,'10:34dbe217a123a1501be647832c77571bd0af1c8b584be30404157da1111499b9:f09771bb5a73b35d6d8cd8b5dfb0cf26bf58a71f6d3f4c1a8c92e33fb263aaff','John User',20);
-- Table `usr_user` 2 rows output.
-- SELECT * FROM `usr_user_role`
INSERT INTO `usr_user_role`(user_id,role_id) VALUES(1,0);
INSERT INTO `usr_user_role`(user_id,role_id) VALUES(1,1);
INSERT INTO `usr_user_role`(user_id,role_id) VALUES(2,0);
-- Table `usr_user_role` 3 rows output.


SET FOREIGN_KEY_CHECKS = 1;
