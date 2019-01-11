#!/usr/bin/env bash




mvn clean package
java -jar target/vertx-microservices-consumer-fat.jar

