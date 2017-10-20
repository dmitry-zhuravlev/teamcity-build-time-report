CREATE KEYSPACE IF NOT EXISTS report WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 2 };

CREATE TABLE IF NOT EXISTS report.teamcity_build(
      id bigint,
      buildTypeId text,
      projectId text,
      serverName text,
      buildDuration double,
      PRIMARY KEY (id, buildTypeId, projectId, serverName)
 );

 CREATE TABLE IF NOT EXISTS report.teamcity_project(
      id text,
      parentProjectId text,
      serverName text,
      PRIMARY KEY (id, parentProjectId, serverName)
 );