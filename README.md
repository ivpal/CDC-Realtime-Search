# Change Data Capture Realtime Search

[![Kotlin](https://img.shields.io/badge/kotlin-1.7.22-blue.svg?logo=kotlin)](http://kotlinlang.org)

![scheme](./scheme.png)

Users - service designed to store information about users. Changes in users database are captured via Debezium connector
and emit to Redpanda topic. Search service receives events from Redpanda and update index in Elasticsearch. Search service
also provides API for query user information from Elasticsearch.

## Setup
```shell
git clone https://github.com/ivpal/CDC-Realtime-Search.git
cd CDC-Realtime-Search/
```
For build Docker images you need Java 17 or higher:
```shell
./gradlew users:jibDockerBuild
./gradlew search:jibDockerBuild
```

# Run
```shell
docker-compose up -d
```

### Start PostgreSQL connector

```shell
curl --location --request POST 'localhost:8083/connectors/' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "users-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "database.hostname": "users-db",
    "database.port": "5432",
    "database.user": "users",
    "database.password": "users",
    "database.dbname" : "postgres",
    "database.server.id": "184054",
    "topic.prefix": "users",
    "database.history.kafka.bootstrap.servers": "redpanda:9092",
    "database.history.kafka.topic": "schema-changes.users"
  }
}'
```

## Usage
### Create user
```shell
curl --location --request POST 'localhost:8080/api/users' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "ivpal",
    "firstname": "Pavel",
    "lastname": "Ivanov"
}'
```
Search request:
```shell
curl --location --request GET 'localhost:8081/api/search?q=pa'
```
Response:
```json
[
  {
    "id": 1,
    "username": "ivpal",
    "firstname": "Pavel",
    "lastname": "Ivanov"
  }
]
```
### Update user
```shell
curl --location --request PUT 'localhost:8080/api/users/1' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "newusername",
    "firstname": "Pavel",
    "lastname": "Ivanov"
}'
```
Search request:
```shell
curl --location --request GET 'localhost:8081/api/search?q=pa'
```
Response:
```json
[
    {
        "id": 1,
        "username": "newusername",
        "firstname": "Pavel",
        "lastname": "Ivanov"
    }
]
```
### Delete user
```shell
curl --location --request DELETE 'localhost:8080/api/users/1'
```
Search request:
```shell
curl --location --request GET 'localhost:8081/api/search?q=pa'
```
Response:
```json
[]
```