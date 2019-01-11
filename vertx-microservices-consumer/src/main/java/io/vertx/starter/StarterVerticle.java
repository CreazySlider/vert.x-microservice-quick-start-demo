package io.vertx.starter;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.microservice.common.BaseMicroserviceVerticle;
import io.vertx.microservice.service.MyService;
import io.vertx.microservice.service.SecondService;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.EventBusService;

public class StarterVerticle extends BaseMicroserviceVerticle {

	private static final Logger logger = LoggerFactory.getLogger(StarterVerticle.class);

	@Override
	public void start() throws Exception {
		super.start();
		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());

		router.get("/httpEndPoint/sayHi/:username").handler(context -> {
			long startTime = System.currentTimeMillis();
			String username = context.request().getParam("username");
			zookeeperDiscovery.getRecord(r -> r.getName().equals("vert.x-microservice-httpEndpoint"), ar -> {
				if (ar.succeeded()) {
					if (ar.result() != null) {
						// Retrieve the service reference
						ServiceReference reference = zookeeperDiscovery.getReference(ar.result());
						// Retrieve the service object
						WebClient client = reference.getAs(WebClient.class);
						/**
						 * 如果provider和consumer部署在同一台服务器上面，可以不写host
						 * 否则必须写host
						 * 如果想实现负载均衡，此host可以写成nginx的host，consumser的host配置在nginx上
						 */
						client.get("192.168.3.10","/sayHi/" + username).send(response -> {
							context.response().end(response.result().bodyAsString());
							reference.release();
							long endTime = System.currentTimeMillis();
							logger.info("程序运行时间：" + (endTime - startTime) + "ms");
						});
					}
				}
			});
		});

		router.get("/ebServiceProxy/sayHi/:username").handler(context -> {
			long startTime = System.currentTimeMillis();
			String username = context.request().getParam("username");

			zookeeperDiscovery.getRecord(new JsonObject().put("name", MyService.SERVICE_NAME), ar -> {
				if (ar.succeeded() && ar.result() != null) {
					ServiceReference reference = zookeeperDiscovery.getReference(ar.result());
					MyService service = reference.getAs(MyService.class);

					service.sayHi(username, r ->{
						context.response().end(r.result());
						long endTime = System.currentTimeMillis();
						logger.info("程序运行时间：" + (endTime - startTime) + "ms");
					});
					// Dont' forget to release the service
					reference.release();
				}
			});
		});

		router.get("/ebServiceProxy/getLength/:param").handler(context -> {
			long startTime = System.currentTimeMillis();
			String param = context.request().getParam("param");

			EventBusService.getProxy(zookeeperDiscovery, SecondService.class, ar -> {
				if (ar.succeeded()) {
					SecondService service = ar.result();
					service.getLength(param, r -> {
						context.response().end(String.valueOf(r.result()));
						long endTime = System.currentTimeMillis();
						logger.info("程序运行时间：" + (endTime - startTime) + "ms");
						// Dont' forget to release the service
						ServiceDiscovery.releaseServiceObject(zookeeperDiscovery, service);
					});
				}
			});
		});
        vertx.createHttpServer().requestHandler(router).listen(8005);

	}

	@Override
	public void stop() throws Exception {
		zookeeperDiscovery.close();
	}
}
