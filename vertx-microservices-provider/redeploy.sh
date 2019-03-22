#!/usr/bin/env bash


mvn clean package
java -jar target/vertx-microservices-provider-fat.jar -cluster -conf src/config/local.json

