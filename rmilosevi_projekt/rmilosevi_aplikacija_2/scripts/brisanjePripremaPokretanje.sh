#!/bin/bash

echo "GASI DOCKER"
docker stop rmilosevi_payara_micro
echo "BRISI CONTAINER"
docker rm rmilosevi_payara_micro
echo "PRIPREMI SLIKU"
./scripts/pripremiSliku.sh
echo "POKRENI SLIKU"
./scripts/pokreniSliku.sh