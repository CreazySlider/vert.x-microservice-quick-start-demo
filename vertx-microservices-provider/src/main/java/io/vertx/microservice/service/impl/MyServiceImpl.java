package io.vertx.microservice.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.microservice.service.MyService;

public class MyServiceImpl implements MyService {

	@Override
	public void sayHi(String username, Handler<AsyncResult<String>> resultHandler) {
		System.out.println("call provider eventbus service proxy.MyService username:"+username);
		resultHandler.handle(Future.succeededFuture("Hi " + username + "."));
	}

}
