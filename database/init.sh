#!/usr/bin/env bash

cd cassandra-cluster
./create_network.sh
./start_cluster.sh
cd ..
./configure_schema.sh
