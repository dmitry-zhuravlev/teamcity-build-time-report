docker build -t dmitry/build-time-report-ui-app:1.0 .
docker run -d -p <host.port>:8080 --network=<network_name> --name build-time-report-ui-app dmitry/build-time-report-ui-app:1.0 \
--cassandra.host=<cassandra.host> \
--cassandra.port=<cassandra.port> \
--cassandra.keyspace=<cassandra.keyspace>