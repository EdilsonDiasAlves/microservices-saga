# Arquitetura de Microsserviços com SAGA

Projeto conceitual utilizando arquitetura de microserviços com spring boot e apache kafka  
![image](https://github.com/user-attachments/assets/28082dd6-da1d-4ea8-a2e5-e0e93dfebb84)  
  

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
  * [Produtos registrados e seu estoque](#produtos-registrados-e-seu-estoque)
  * [Endpoint para iniciar a saga](#endpoint-para-iniciar-a-saga)
  * [Endpoint para visualizar a saga](#endpoint-para-visualizar-a-saga)
  * [Acesso ao MongoDB](#acesso-ao-mongodb)

## Tecnologias

* **Java 17**
* **Spring Boot 3**
* **Apache Kafka**
* **API REST**
* **PostgreSQL**
* **MongoDB**
* **Docker**
* **docker-compose**
* **Redpanda Console**
    
[Voltar ao início](#sum%C3%A1rio)  
  
# Ferramentas utilizadas

* **IntelliJ IDEA Community Edition**
* **Docker**
* **Gradle**  
[Voltar ao início](#sum%C3%A1rio)
  
## Executando o projeto

Há várias maneiras de executar os projetos:
  
### 01 - Executando via docker-compose
  
Basta executar o comando no diretório raiz do repositório:

`docker-compose up --build -d`

**Obs.: para rodar tudo desta maneira, é necessário realizar o build das 5 aplicações, veja nos passos abaixo sobre como fazer isto.**

[Voltar ao nível anterior](#executando-o-projeto)  
[Voltar ao início](#sum%C3%A1rio)  
  
### 02 - Executando via automação com script em Python

Basta executar o arquivo `build.py`. Para isto, **é necessário ter o Python 3 instalado**.

Para executar, basta apenas executar o seguinte comando no diretório raiz do repositório:

`python build.py`

Será realizado o `build` de todas as aplicações, removidos todos os containers e em sequência, será rodado o `docker-compose`.

[Voltar ao nível anterior](#executando-o-projeto)  
[Voltar ao início](#sum%C3%A1rio)  
  
### 03 - Executando os serviços de bancos de dados e Message Broker

Para que seja possível executar os serviços de bancos de dados e Message Broker, como MongoDB, PostgreSQL e Apache Kafka, basta ir no diretório raiz do repositório, onde encontra-se o arquivo `docker-compose.yml` e executar o comando:

`docker-compose up --build -d order-db kafka product-db payment-db inventory-db`

Como queremos rodar apenas os serviços de bancos de dados e Message Broker, é necessário informá-los no comando do `docker-compose`, caso contrário, as aplicações irão subir também.

Para parar todos os containers, basta rodar:

`docker-compose down`

Ou então:

`docker stop ($docker ps -aq)`
`docker container prune -f`

[Voltar ao nível anterior](#executando-o-projeto)  
[Voltar ao início](#sum%C3%A1rio)  
  
### 04 - Executando manualmente via CLI

Antes da execução do projeto, realize o `build` da aplicação indo no diretório raiz e executando o comando:

`gradle build -x test`

Para executar os projetos com Gradle, basta entrar no diretório raiz de cada projeto, e executar o comando:

`gradle bootRun`

Ou então, entrar no diretório: `build/libs` e executar o comando:

`java -jar nome_do_jar.jar`  

[Voltar ao nível anterior](#executando-o-projeto)  
[Voltar ao início](#sum%C3%A1rio)  
  
## Acessando a aplicação

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

[Voltar ao início](#sum%C3%A1rio)  
  
## Acessando tópicos com Redpanda Console

Para acessar o Redpanda Console e visualizar tópicos e publicar eventos, basta acessar:

http://localhost:8081  
***Obs:*** Para publicar no redpanda, deve ser necessário acessar o menu Topics -> Escolher um dos tópicos.  
No combobox "Actions", deverá selecionar "Produce record", deixar os campos do key como nulos e enviar apenas os campos do value com o "TYPE" sendo Json.  
Na caixa de texto "DATA", enviar um json de exemplo: [Endpoint para visualizar a saga](#endpoint-para-visualizar-a-saga).  
***IMPORTANTE:***  Selecionar a opção UNCOMPRESSED no campo "COMPRESSION TYPE".
  
[Voltar ao início](#sum%C3%A1rio)  

### Produtos registrados e seu estoque

[Voltar ao nível anterior](#dados-da-api)

Existem 4 produtos iniciais cadastrados no serviço `product-validation-service` e suas quantidades disponíveis em `inventory-service`:

* **COMIC_BOOKS** (4 em estoque)
* **BOOKS** (2 em estoque)
* **MOVIES** (5 em estoque)
* **MUSIC** (9 em estoque)

### Endpoint para iniciar a saga:

[Voltar ao nível anterior](#dados-da-api)

**POST** http://localhost:3000/api/order

Payload:

```json
{
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.50
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.90
      },
      "quantity": 1
    }
  ]
}
```

Resposta:

```json
{
  "id": "64429e987a8b646915b3735f",
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.5
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.9
      },
      "quantity": 1
    }
  ],
  "createdAt": "2023-04-21T14:32:56.335943085",
  "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
}
```

### Endpoint para visualizar a saga:

[Voltar ao nível anterior](#dados-da-api)

É possível recuperar os dados da saga pelo **orderId** ou pelo **transactionId**, o resultado será o mesmo:

**GET** http://localhost:3000/api/event?orderId=64429e987a8b646915b3735f

**GET** http://localhost:3000/api/event?transactionId=1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519

Resposta:

```json
{
  "id": "64429e9a7a8b646915b37360",
  "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519",
  "orderId": "64429e987a8b646915b3735f",
  "payload": {
    "id": "64429e987a8b646915b3735f",
    "products": [
      {
        "product": {
          "code": "COMIC_BOOKS",
          "unitValue": 15.5
        },
        "quantity": 3
      },
      {
        "product": {
          "code": "BOOKS",
          "unitValue": 9.9
        },
        "quantity": 1
      }
    ],
    "totalAmount": 56.40,
    "totalItems": 4,
    "createdAt": "2023-04-21T14:32:56.335943085",
    "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
  },
  "source": "ORCHESTRATOR",
  "status": "SUCCESS",
  "eventHistory": [
    {
      "source": "ORCHESTRATOR",
      "status": "SUCCESS",
      "message": "Saga started!",
      "createdAt": "2023-04-21T14:32:56.78770516"
    },
    {
      "source": "PRODUCT_VALIDATION_SERVICE",
      "status": "SUCCESS",
      "message": "Products are validated successfully!",
      "createdAt": "2023-04-21T14:32:57.169378616"
    },
    {
      "source": "PAYMENT_SERVICE",
      "status": "SUCCESS",
      "message": "Payment realized successfully!",
      "createdAt": "2023-04-21T14:32:57.617624655"
    },
    {
      "source": "INVENTORY_SERVICE",
      "status": "SUCCESS",
      "message": "Inventory updated successfully!",
      "createdAt": "2023-04-21T14:32:58.139176809"
    },
    {
      "source": "ORCHESTRATOR",
      "status": "SUCCESS",
      "message": "Saga finished successfully!",
      "createdAt": "2023-04-21T14:32:58.248630293"
    }
  ],
  "createdAt": "2023-04-21T14:32:58.28"
}
```
  
### Acesso ao MongoDB

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
  
[Voltar ao início](#sum%C3%A1rio)
