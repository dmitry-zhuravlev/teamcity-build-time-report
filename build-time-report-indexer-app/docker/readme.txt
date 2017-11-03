docker build -t dmitry/build-time-report-indexer-app:1.0 .
docker run -d -p <host.port>:8080 --network=<network_name> --name build-time-report-indexer-app dmitry/build-time-report-indexer-app:1.0 \
--cassandra.host=<cassandra.host> \
--cassandra.port=<cassandra.port> \
--cassandra.keyspace=<cassandra.keyspace> \
--teamcity.server.username=<teamcity.server.username> \
--teamcity.server.password=<teamcity.server.password> \
--teamcity.server.url=<teamcity.server.url> \
--teamcity.server.id=<teamcity.server.id> \
--teamcity.server.name=<teamcity.server.name>