#! /bin/bash

# A vLEI server makes schemas, credentials and data OOBIs (added through durls field) discoverable by other entities
# Caveat: This script should be run from the root directory

CONFIG_DIR="config/keri/acdc"

vLEI-server -p 7723 --schema-dir "${CONFIG_DIR}/schemas/saidified" --cred-dir "${CONFIG_DIR}/cache/credentials" --oobi-dir "${CONFIG_DIR}/cache/oobis"