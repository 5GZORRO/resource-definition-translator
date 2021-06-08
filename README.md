# Resource Definition Translator
The Resource Definition Translator is a component of the Virtual Resource Manager (VRM) architecture.
Application that allows the translation of SOL006 descriptors into resources and services using the TMF Forum models.
The translated descriptors are posted on the Resource and Service Offer Catalog. The descriptors to be translated can be
specified either by the northbound interface or by specifying the descriptor id present on a descriptor repository.
If you want to install the Resource Definition Translator using docker-compose or running only the translator by itself 
skip the Requirements, and the first installation sections.
## Requirements 
- Java 8
  ```bash
  sudo apt update
  sudo apt install openjdk-8-jdk
  ```
- Maven
  ```bash
  sudo apt update
  sudo apt install maven
  ```
- PostgreSQL
  ```bash
  docker run --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -d postgres
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
## Installation
Customize your translator properties in 
```resource-definition-translator/sol006_tmf_translator/src/main/resources/application.properties``` 
then from ```resource-definition-translator/``` run the following commands.
```bash
mvn clean install
java -jar sol006_tmf_translator/target/sol006_tmf_translator-1.0-SNAPSHOT.jar
```

## Installation [docker-compose]
Customize your Resource Definition Translator properties in ```resource-definition-translator/deployment/.env```. </br>
In ```resource-definition-translator/``` add a file containing a private key that can be used to clone repositories
from the 5GZORRO GitHub (N.B the file must be called ```id_rsa```). </br>
From ```resource-definition-translator/``` run the following command.
```bash
docker-compose -f deployment/docker-compose.yaml up -d
```

## Installation [Dockerfile]
Customize your Resource Definition Translator properties in 
```resource-definition-translator/deployment/translator_dockerfile/env_file```. </br>
In ```resource-definition-translator/``` add a file containing a private key that can be used to clone repositories
from the 5GZORRO GitHub (N.B the file must be called ```id_rsa```). </br>
From ```resource-definition-translator/``` run the following command.
```bash
docker build -t translator -f deployment/translator_dockerfile/Dockerfile . \
--build-arg NFV_SOL006_LIBS_REPO=https://github.com/nextworks-it/nfv-sol-libs.git \
--build-arg NFV_SOL006_LIBS_VERSION=master \
--build-arg TMF_INFO_MODELS_REPO=git@github.com:5GZORRO/resource-and-service-offer-catalog.git \
--build-arg TMF_INFO_MODELS_VERSION=main
```
Run the application from ```resource-definition-translator/deployment/translator_dockerfile/```.
```bash
docker run --env-file=env-file translator:latest
```

## Usage
Browsing `http://localhost:TRANSLATOR_PORT/sol006-tmf/` you can access the swagger documentation and test the REST APIs.


