# Resource Definition Translator
Application that allows the translation of SOL006 descriptors into resources and services using the TMF Forum models.
The translated descriptors are posted on the Resource and Service Offer Catalog. The descriptors to be translated can be
specified either by the northbound interface or by specifying the descriptor id present on a descriptor repository.
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
  git clone --branch sol006 https://github.com/nextworks-it/nfv-sol-libs.git
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
Customize your translator properties in ```resource-definition-translator/sol006_tmf_translator/src/
main/resources/application.properties``` then from ```resource-definition-translator/sol006_tmf_translator```
run the following commands.
```bash
mvn clean install
java -jar resource-definition-translator/sol006_tmf_translator/target/sol006_tmf_translator-1.0-SNAPSHOT.jar
```

## Usage
Browsing `http://localhost:9090/sol006-tmf/` you can access the swagger documentation and test the REST APIs.


