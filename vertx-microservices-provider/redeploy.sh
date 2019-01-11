#!/usr/bin/env bash




mvn clean package
java -jar target/vertx-microservices-provider-fat.jar

