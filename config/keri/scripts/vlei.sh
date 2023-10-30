#! /bin/bash

# A vLEI server makes schemas, credentials and data OOBIs (added through durls field) discoverable by other entities

CONFIG_DIR="config/keri/acdc"

vLEI-server -p 7723 --schema-dir "${CONFIG_DIR}/schemas" --cred-dir "${CONFIG_DIR}/credentials" --oobi-dir "${CONFIG_DIR}/oobis"