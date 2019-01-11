package io.vertx.microservice.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen
@ProxyGen
public interface MyService {

    public static final String SERVICE_NAME = "eb-myService";

    public static final String SERVICE_ADDRESS = "eb-myService-address";

    void sayHi(String username, Handler<AsyncResult<String>> resultHandler);
}
