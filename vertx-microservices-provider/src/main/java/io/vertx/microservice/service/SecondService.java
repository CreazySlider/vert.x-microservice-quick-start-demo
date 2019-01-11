package io.vertx.microservice.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.microservice.service.impl.SecondServiceImpl;
import io.vertx.serviceproxy.ServiceProxyBuilder;

@VertxGen
@ProxyGen
public interface SecondService {

    public static final String SERVICE_NAME = "eb-secondService";

    public static final String SERVICE_ADDRESS = "eb-secondService-address";

    static SecondService create(){
        return new SecondServiceImpl();
    }

    static SecondService createProxy(Vertx vertx){
        return new ServiceProxyBuilder(vertx)
                .setAddress(SERVICE_ADDRESS)
                .build(SecondService.class);
    }

    void getLength(String param, Handler<AsyncResult<Integer>> resultHandler);
}
