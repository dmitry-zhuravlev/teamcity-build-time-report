CREATE KEYSPACE IF NOT EXISTS report WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 2 };

CREATE TABLE IF NOT EXISTS report.teamcity_build(
      id bigint,
      buildTypeId text,
      projectId text,
      serverName text,
      buildDuration bigint,
      finishDate timestamp,
      PRIMARY KEY (buildTypeId, serverName, finishDate, id)
 );

 CREATE TABLE IF NOT EXISTS report.teamcity_build_type(
       buildTypeId text,
       buildTypeName text,
       projectId text,
       serverName text,
       PRIMARY KEY (serverName, buildTypeId)
  );

 CREATE TABLE IF NOT EXISTS report.teamcity_project(
      id text,
      name text,
      parentProjectId text,
      serverName text,
      PRIMARY KEY (serverName, id)
 );

 CREATE TABLE IF NOT EXISTS report.teamcity_server(
      serverName text,
      PRIMARY KEY (serverName)
 );

 CREATE MATERIALIZED VIEW report.teamcity_build_type_view AS
        SELECT * FROM report.teamcity_build_type
        WHERE buildTypeId IS NOT NULL AND buildTypeName IS NOT NULL AND projectId IS NOT NULL AND serverName IS NOT NULL
        PRIMARY KEY (serverName, projectId, buildTypeId);

   CREATE MATERIALIZED VIEW report.teamcity_build_view AS
       SELECT * FROM report.teamcity_build
       WHERE id IS NOT NULL AND buildTypeId IS NOT NULL AND projectId IS NOT NULL AND serverName IS NOT NULL AND buildDuration IS NOT NULL AND finishDate IS NOT NULL
       PRIMARY KEY (buildTypeId, projectId, serverName, finishDate, id);




 CREATE KEYSPACE IF NOT EXISTS report_test WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 2 };

 CREATE TABLE IF NOT EXISTS report_test.teamcity_build(
       id bigint,
       buildTypeId text,
       projectId text,
       serverName text,
       buildDuration bigint,
       finishDate timestamp,
       PRIMARY KEY (buildTypeId, serverName, finishDate, id)
  );

  CREATE TABLE IF NOT EXISTS report_test.teamcity_build_type(
        buildTypeId text,
        buildTypeName text,
        projectId text,
        serverName text,
        PRIMARY KEY (serverName, buildTypeId)
   );

  CREATE TABLE IF NOT EXISTS report_test.teamcity_project(
       id text,
       name text,
       parentProjectId text,
       serverName text,
       PRIMARY KEY (serverName, id)
  );

  CREATE TABLE IF NOT EXISTS report_test.teamcity_server(
       serverName text,
       PRIMARY KEY (serverName)
  );

  CREATE MATERIALIZED VIEW report_test.teamcity_build_type_view AS
          SELECT * FROM report_test.teamcity_build_type
          WHERE buildTypeId IS NOT NULL AND buildTypeName IS NOT NULL AND projectId IS NOT NULL AND serverName IS NOT NULL
          PRIMARY KEY (serverName, projectId, buildTypeId);

  CREATE MATERIALIZED VIEW report_test.teamcity_build_view AS
         SELECT * FROM report_test.teamcity_build
         WHERE id IS NOT NULL AND buildTypeId IS NOT NULL AND projectId IS NOT NULL AND serverName IS NOT NULL AND buildDuration IS NOT NULL AND finishDate IS NOT NULL
         PRIMARY KEY (buildTypeId, projectId, serverName, finishDate, id);