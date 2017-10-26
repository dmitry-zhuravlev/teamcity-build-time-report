CREATE KEYSPACE IF NOT EXISTS report WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 2 };

CREATE TABLE IF NOT EXISTS report.teamcity_build(
      id bigint,
      buildTypeId text,
      projectId text,
      serverName text,
      buildDuration bigint,
      finishDate timestamp,
      PRIMARY KEY (buildTypeId, projectId, serverName, finishDate, id)
 );

 CREATE TABLE IF NOT EXISTS report.teamcity_build_type(
       buildTypeId text,
       buildTypeName text,
       projectId text,
       serverName text,
       PRIMARY KEY (projectId, serverName, buildTypeId)
  );

 CREATE TABLE IF NOT EXISTS report.teamcity_project(
      id text,
      name text,
      parentProjectId text,
      serverName text,
      PRIMARY KEY (id, parentProjectId, serverName)
 );