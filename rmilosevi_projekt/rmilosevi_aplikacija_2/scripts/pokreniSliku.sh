#!/bin/bash
NETWORK=rmilosevi_mreza_1

docker run -it -d \
  -p 8070:8080 \
  --network=$NETWORK \
  --ip 200.20.0.5 \
  --name=rmilosevi_payara_micro \
  --hostname=rmilosevi_payara_micro \
  rmilosevi_payara_micro:6.2023.4 \
  --deploy /opt/payara/deployments/rmilosevi_aplikacija_2-1.0.0.war \
  --contextroot rmilosevi_aplikacija_2 \
  --noCluster &

wait
