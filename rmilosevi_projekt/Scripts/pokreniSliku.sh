#!/bin/bash

NETWORK=rmilosevi_mreza_1

docker run -d \
  -p 9005:9005 \
  --network=$NETWORK \
  --ip 200.20.0.4 \
  --name=nwtishsqldb_bp \
  --hostname=nwtishsqldb_bp \
  --mount type=bind,source=/opt/hsqldb-2.7.1/hsqldb/data,target=/opt/data \
  nwtishsqldb_bp:1.0.0 &

wait
