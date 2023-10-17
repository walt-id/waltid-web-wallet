#!/bin/bash

CONFIG_DIR="config"

# initiating witnesses
kli init --name wan --salt 0AB3YW5uLXRoZS13aXRuZXNz --nopasscode \
    --config-dir "${CONFIG_DIR}" \
    --config-file main/wan-witness

kli witness start --name wan --alias wan -T 5632 -H 5642 \
    --config-dir "${CONFIG_DIR}" \
    --config-file wan-witness &
WAN_WITNESS_PID=$!


kli init --name wil --salt 0AB3aWxsLXRoZS13aXRuZXNz --nopasscode \
    --config-dir "${CONFIG_DIR}" \
    --config-file main/wil-witness


kli witness start --name wil --alias wil -T 5633 -H 5643 \
    --config-dir "${CONFIG_DIR}" \
    --config-file wil-witness &
WIL_WITNESS_PID=$!


kli init --name wes --salt 0AB3ZXNzLXRoZS13aXRuZXNz --nopasscode \
    --config-dir "${CONFIG_DIR}" \
    --config-file main/wes-witness


kli witness start --name wes --alias wes -T 5634 -H 5644 \
    --config-dir "${CONFIG_DIR}" \
    --config-file wes-witness &
WES_WITNESS_PID=$!