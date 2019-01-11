package io.vertx.microservice.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.microservice.service.SecondService;

public class SecondServiceImpl implements SecondService {
    @Override
    public void getLength(String param, Handler<AsyncResult<Integer>> resultHandler) {
        System.out.println("call provider eventbus service proxy.SecondService param:"+param);
        resultHandler.handle(Future.succeededFuture(param.length()));
    }
}
