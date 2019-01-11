package io.vertx.microservice.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen
@ProxyGen
public interface SecondService {

    public static final String SERVICE_NAME = "eb-secondService";

    public static final String SERVICE_ADDRESS = "eb-secondService-address";

    void getLength(String param, Handler<AsyncResult<Integer>> resultHandler);
}
