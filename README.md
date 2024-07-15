# Arquitetura de Microsserviços com SAGA

Projeto conceitual utilizando arquitetura de microserviços com spring boot e apache kafka

### Sumário:

* [Tecnologias](#tecnologias)
* [Ferramentas utilizadas](#ferramentas-utilizadas)
* [Executando o projeto](#executando-o-projeto)
  * [01 - Executando via docker-compose](#01---executando-via-docker-compose)
  * [02 - Executando via automação com script em Python](#02---executando-via-automa%C3%A7%C3%A3o-com-script-em-python)
  * [03 - Executando os serviços de bancos de dados e Message Broker](#03---executando-os-servi%C3%A7os-de-bancos-de-dados-e-message-broker)
  * [04 - Executando manualmente via CLI](#04---executando-manualmente-via-cli)
* [Acessando a aplicação](#acessando-a-aplica%C3%A7%C3%A3o)
* [Acessando tópicos com Redpanda Console](#acessando-t%C3%B3picos-com-redpanda-console)
* [Dados da API](#dados-da-api)
  * [Acesso ao MongoDB](#acesso-ao-mongodb)

## Tecnologias

[Voltar ao início](#sum%C3%A1rio)

* **Java 17**
* **Spring Boot 3**
* **Apache Kafka**
* **API REST**
* **PostgreSQL**
* **MongoDB**
* **Docker**
* **docker-compose**
* **Redpanda Console**

# Ferramentas utilizadas

[Voltar ao início](#sum%C3%A1rio)

* **IntelliJ IDEA Community Edition**
* **Docker**
* **Gradle**


## Executando o projeto

[Voltar ao início](#sum%C3%A1rio)

Há várias maneiras de executar os projetos:

1. Executando tudo via `docker-compose`

[Voltar ao nível anterior](#executando-o-projeto)

Basta executar o comando no diretório raiz do repositório:

`docker-compose up --build -d`

### 01 - Executando via docker-compose

[Voltar ao nível anterior](#executando-o-projeto)

Basta executar o comando no diretório raiz do repositório:

`docker-compose up --build -d`

**Obs.: para rodar tudo desta maneira, é necessário realizar o build das 5 aplicações, veja nos passos abaixo sobre como fazer isto.**

### 02 - Executando via automação com script em Python

[Voltar ao nível anterior](#executando-o-projeto)

Basta executar o arquivo `build.py`. Para isto, **é necessário ter o Python 3 instalado**.

Para executar, basta apenas executar o seguinte comando no diretório raiz do repositório:

`python build.py`

Será realizado o `build` de todas as aplicações, removidos todos os containers e em sequência, será rodado o `docker-compose`.

### 03 - Executando os serviços de bancos de dados e Message Broker

[Voltar ao nível anterior](#executando-o-projeto)

Para que seja possível executar os serviços de bancos de dados e Message Broker, como MongoDB, PostgreSQL e Apache Kafka, basta ir no diretório raiz do repositório, onde encontra-se o arquivo `docker-compose.yml` e executar o comando:

`docker-compose up --build -d order-db kafka product-db payment-db inventory-db`

Como queremos rodar apenas os serviços de bancos de dados e Message Broker, é necessário informá-los no comando do `docker-compose`, caso contrário, as aplicações irão subir também.

Para parar todos os containers, basta rodar:

`docker-compose down`

Ou então:

`docker stop ($docker ps -aq)`
`docker container prune -f`

### 04 - Executando manualmente via CLI

[Voltar ao nível anterior](#executando-o-projeto)

Antes da execução do projeto, realize o `build` da aplicação indo no diretório raiz e executando o comando:

`gradle build -x test`

Para executar os projetos com Gradle, basta entrar no diretório raiz de cada projeto, e executar o comando:

`gradle bootRun`

Ou então, entrar no diretório: `build/libs` e executar o comando:

`java -jar nome_do_jar.jar`

## Acessando a aplicação

[Voltar ao início](#sum%C3%A1rio)

Para acessar as aplicações e realizar um pedido, basta acessar a URL:

http://localhost:3000/swagger-ui.html

As aplicações executarão nas seguintes portas:

* Order-Service: 3000
* Orchestrator-Service: 8080
* Product-Validation-Service: 8090
* Payment-Service: 8091
* Inventory-Service: 8092
* Apache Kafka: 9092
* Redpanda Console: 8081
* PostgreSQL (Product-DB): 5432
* PostgreSQL (Payment-DB): 5433
* PostgreSQL (Inventory-DB): 5434
* MongoDB (Order-DB): 27017

## Acessando tópicos com Redpanda Console

[Voltar ao início](#sum%C3%A1rio)

Para acessar o Redpanda Console e visualizar tópicos e publicar eventos, basta acessar:

http://localhost:8081


### Acesso ao MongoDB

[Voltar ao início](#sum%C3%A1rio)

Para conectar-se ao MongoDB via linha de comando (cli) diretamente do docker-compose, basta executar o comando abaixo:

**docker exec -it order-db mongosh "mongodb://admin:123456@localhost:27017"**

Para listar os bancos de dados existentes:

**show dbs**

Para selecionar um banco de dados:

**use admin**

Para visualizar as collections do banco:

**show collections**

Para realizar queries e validar se os dados existem:

**db.order.find()**

**db.event.find()**

**db.order.find(id=ObjectId("65006786d715e21bd38d1634"))**

**db.order.find({ "products.product.code": "COMIC_BOOKS"})**