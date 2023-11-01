FROM docker.io/gradle:jdk17 as buildstage

COPY src/ /work/src
COPY gradle/ /work/gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew /work/

WORKDIR /work
RUN gradle clean installDist

FROM docker.io/eclipse-temurin:17

SHELL ["/bin/bash", "-c"]

RUN apt update && \
    apt upgrade -y

# installing crypto lib dependencies for KERI
RUN apt install -y libsodium23 && \
    apt install -y libsodium-dev && \
    apt install -y libffi-dev

# setup Rust for blake3 dependency build
RUN curl https://sh.rustup.rs -sSf | sh -s -- -y && \
    source "$HOME/.cargo/env"

# installing keripy directly from source (published 1.0.0 is bugged)
WORKDIR /keripy
RUN apt install git -y && \
    git clone -b development https://github.com/WebOfTrust/keripy.git . && \
    git checkout 4185296affb2348d19af6009be04f682a3e19360

RUN apt install pip -y && \
    source "$HOME/.cargo/env" &&pip install -r requirements.txt && \
    mkdir -p /usr/local/var/keri && \
    pip install -e .

# vLEI spec leverage `did:keri` to make acdc schemas, credentials and OOBIs (through durls field) discoverable
WORKDIR /vLEI
RUN git clone -b dev https://github.com/WebOfTrust/vLEI.git . && \
    git checkout ed982313dab86bfada3825857601a10d71ce9631 && \
    pip install -e ./

COPY --from=buildstage /work/build/install/ /
WORKDIR /waltid-web-wallet

EXPOSE 4545
ENTRYPOINT ["/waltid-web-wallet/bin/waltid-web-wallet"]
