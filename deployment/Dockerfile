FROM maven:3-openjdk-8 AS build

ARG NFV_SOL006_LIBS_REPO
ARG NFV_SOL006_LIBS_VERSION

ARG TMF_INFO_MODELS_REPO
ARG TMF_INFO_MODELS_VERSION

ARG SERVER_PORT

WORKDIR /home
RUN git clone ${NFV_SOL006_LIBS_REPO} nfv-sol-libs
WORKDIR /home/nfv-sol-libs
RUN git checkout ${NFV_SOL006_LIBS_VERSION}
WORKDIR /home/nfv-sol-libs/NFV_MANO_SOL006_LIBS_COMMON
RUN mvn clean install
WORKDIR /home/nfv-sol-libs/NFV_MANO_SOL006_LIBS_DESCRIPTORS
RUN mvn clean install

# Make ssh dir
RUN mkdir /root/.ssh/

# Copy over private key, and set permissions
ADD id_rsa /root/.ssh/id_rsa
RUN chmod 700 /root/.ssh/id_rsa
RUN chown -R root:root /root/.ssh

# Create known_hosts
RUN touch /root/.ssh/known_hosts

# Remove host checking
RUN echo "Host github.com\n\tStrictHostKeyChecking no\n" >> /root/.ssh/config

WORKDIR /home
RUN git clone ${TMF_INFO_MODELS_REPO} tmf-models
WORKDIR /home/tmf-models
RUN git checkout ${TMF_INFO_MODELS_VERSION}
WORKDIR /home/tmf-models/information_models
RUN mvn clean install

RUN mkdir /usr/src/translator
COPY . /usr/src/translator
WORKDIR /usr/src/translator
RUN mvn clean package

FROM openjdk:8-jre-alpine
RUN mkdir /translator
COPY --from=build /usr/src/translator/sol006_tmf_translator/target/sol006_tmf_translator-1.0-SNAPSHOT.jar /translator/
WORKDIR /translator

EXPOSE ${SERVER_PORT}

CMD java -jar sol006_tmf_translator-1.0-SNAPSHOT.jar