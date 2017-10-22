CREATE KEYSPACE IF NOT EXISTS report WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 2 };

CREATE TABLE IF NOT EXISTS report.teamcity_build(
      id bigint,
      buildTypeId text,
      projectId text,
      serverName text,
      buildDuration bigint,
      finishDate timestamp,
      PRIMARY KEY (buildTypeId, finishDate, id, projectId, serverName)
 );

 CREATE TABLE IF NOT EXISTS report.teamcity_project(
      id text,
      parentProjectId text,
      serverName text,
      PRIMARY KEY (id, parentProjectId, serverName)
 );