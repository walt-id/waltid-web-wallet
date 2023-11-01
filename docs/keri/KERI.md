# Key Event Receipt Infrastructure (KERI) and Authentic Chain Data Container (ACDC)
Installation and demo workflow
## Installation
Follow installations instructors in these repositories
1. Install python KERI implementation from WebOfTrust [keripy](https://github.com/WebOfTrust/keripy).
2. Install [vLEI server](https://github.com/WebOfTrust/vLEI) from WebOfTrust (To promulgate ACDC OOBIs).

## Pre-requisites
In order to run the project you must 
1. Run demo witnesses provided by KERI in a separate terminal
```bash
kli witness demo
```
2. Run vLEI server. vLEI server repository provides sample schemas, credentials and OOBIs, we can use them or provide our own data. In the root directory of vLEI server run:

```bash
vLEI-server -s ./schema/acdc -c ./samples/acdc/ -o ./samples/oobis/
```
## Launch demo script
1. Demo script provides a full workflow that
   1. Incept controllers
   2. Resolve OOBIs
   3. Create a credential
   4. Issue and list ACDC credential
   5. Revocation script
2. Run command:
```bash
cd docs/keri/demo/;./single-issuer.sh
```


## Launch waltid-web-wallet

3. In the root directory of waltid-web-wallet
```bash
 ./gradlew build && ./gradlew run 
```