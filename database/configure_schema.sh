#!/usr/bin/env bash

run_commands () {
  echo "Configuring Cassandra cluster..."
  query=$(<$(dirname $0)/schema/schema.cql)
  docker run --net=default_network --link cassandra-seed1:cassandra --rm cassandra cqlsh -e "$query" cassandra;
}

until run_commands; do
 echo "Configuration of Cassandra failed. Waiting for Cassandra cluster retrying in 5 seconds..."
 sleep 5
done


