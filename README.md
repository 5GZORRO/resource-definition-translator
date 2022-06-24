# Resource Definition Translator

## Introduction
The Resource Definition Translator is a component of the Any Resource Manager (xRM) architecture.
Application that allows the translation of Virtual Network Function, Physical Network Function, Network Service, 
Radio, Spectrum, Edge, Cloud and Slice resources and services into resources and services specified with the
TMF Forum models. The translated descriptors are posted on the Resource and Service Offer Catalog.

## Prerequisites

### System Requirements
- 1 vCPU
- 2GB RAM

### Software dependencies
- PostgreSQL </br>
  ```bash
  docker run --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres
  ```

If you want to deploy the Resource Definition Translator in a not-virtualized environment you'll need also:

- Java 8 </br>
  ```bash
  sudo apt update
  sudo apt install openjdk-8-jdk
  ```
- Maven </br>
  ```bash
    sudo apt update
    sudo apt install maven
    ```
- SOL006 Library
  ```bash
  git clone https://github.com/nextworks-it/nfv-sol-libs.git
  cd nfv-sol-libs
  ./install_nfv_sol_libs.sh
  ```
- TMF Information Models Library
  ```bash
  git clone https://github.com/5GZORRO/resource-and-service-offer-catalog.git
  cd resource-and-service-offer-catalog/information_models
  mvn clean install
  ```

### 5GZORRO Module dependencies
- [Resource and Service Offer Catalogue](https://github.com/5GZORRO/resource-and-service-offer-catalog)

## Installation
The following procedures consider the previously listed dependencies already up and running.

### Local Machine
Customize your translator properties in 
```resource-definition-translator/sol006_tmf_translator/src/main/resources/application.properties``` 
then from ```resource-definition-translator/``` run the following commands.
```bash
mvn clean install
java -jar sol006_tmf_translator/target/sol006_tmf_translator-1.0-SNAPSHOT.jar
```

### Docker  Compose
Customize your Resource Definition Translator properties in ```resource-definition-translator/deployment/.env```. </br>
From ```resource-definition-translator/``` run the following command.
```bash
docker-compose -f deployment/docker-compose.yaml up -d
```

### Dockerfile
Customize your Resource Definition Translator properties in 
```resource-definition-translator/deployment/translator_dockerfile/env_file```. </br>
From ```resource-definition-translator/``` run the following command.
```bash
docker build -t translator -f deployment/translator_dockerfile/Dockerfile . \
--build-arg NFV_SOL006_LIBS_REPO=https://github.com/nextworks-it/nfv-sol-libs.git \
--build-arg NFV_SOL006_LIBS_VERSION=master \
--build-arg TMF_INFO_MODELS_REPO=https://github.com/5GZORRO/resource-and-service-offer-catalog.git \
--build-arg TMF_INFO_MODELS_VERSION=main
```
Run the application from ```resource-definition-translator/deployment/translator_dockerfile/```.
```bash
docker run --env-file=env_file translator:latest
```

## Configuration
No particular configurations are needed.

## Maintainers
**Pietro Giuseppe Giardina** - *Design* - p.giardina@nextworks.it </br>
**Michael De Angelis** - *Develop and Design* - m.deangelis@nextworks.it </br>

## License
This module is distributed under [Apache 2.0 License](LICENSE) terms.