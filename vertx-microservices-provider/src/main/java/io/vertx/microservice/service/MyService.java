package io.vertx.microservice.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.microservice.service.impl.MyServiceImpl;
import io.vertx.serviceproxy.ServiceProxyBuilder;

@VertxGen
@ProxyGen
public interface MyService {


	public static final String SERVICE_NAME = "eb-myService";
	
	public static final String SERVICE_ADDRESS = "eb-myService-address";
	
	static MyService create() {
		return new MyServiceImpl();
	}
	
	static MyService createProxy(Vertx vertx) {
	    return new ServiceProxyBuilder(vertx)
	      .setAddress(SERVICE_ADDRESS)
	      .build(MyService.class);
	    // Alternatively, you can create the proxy directly using:
	    // return new ProcessorServiceVertxEBProxy(vertx, address);
	    // The name of the class to instantiate is the service interface + `VertxEBProxy`.
	    // This class is generated during the compilation
	}
	
	void sayHi(String username, Handler<AsyncResult<String>> resultHandler);
}
