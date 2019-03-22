package io.vertx.microservice.provider;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;

import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.microservice.common.BaseMicroserviceVerticle;
import io.vertx.microservice.service.MyService;
import io.vertx.microservice.service.SecondService;
import io.vertx.serviceproxy.ServiceBinder;

public class ZookeeperProviderVerticle extends BaseMicroserviceVerticle{

    /**
     * 如果provider和consumser没有部署在同一台服务器上，host必须是provider所在服务器的真实ip，不然consumser调用httpEndPoint的时候报404
     * 如果provider和consumser部署在同一台服务器上，host可以是127.0.0.1
     */

    private static final Logger logger = LoggerFactory.getLogger(io.vertx.microservice.provider.ZookeeperProviderVerticle.class);
    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();

        MyService myService = MyService.create();
        SecondService secondService = SecondService.create();

        // Register the handler
        new ServiceBinder(vertx)
                .setAddress(MyService.SERVICE_ADDRESS)
                .register(MyService.class, myService);

        new ServiceBinder(vertx)
                .setAddress(SecondService.SERVICE_ADDRESS)
                .register(SecondService.class, secondService);

        Router router = Router.router(vertx);

        BridgeOptions opts = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress(MyService.SERVICE_ADDRESS))
                .addOutboundPermitted(new PermittedOptions().setAddress(MyService.SERVICE_ADDRESS))

                .addInboundPermitted(new PermittedOptions().setAddress(SecondService.SERVICE_ADDRESS))
                .addOutboundPermitted(new PermittedOptions().setAddress(SecondService.SERVICE_ADDRESS));

        SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);

        router.route("/eventbus/*").handler(ebHandler);

        router.route().handler(BodyHandler.create());

        router.route().handler(StaticHandler.create());

        router.get("/sayHi/:username").handler(this::sayHi);
        router.route().handler(StaticHandler.create());

        publishEventBusService(MyService.SERVICE_NAME, MyService.SERVICE_ADDRESS, MyService.class)
                .compose(v -> publishEventBusService(SecondService.SERVICE_NAME, SecondService.SERVICE_ADDRESS, SecondService.class)
                        , null);

        int httpServerPort = config().getInteger("http.port");

        String host = config().getString("http.host");

        String REST_API_NAME = config().getString("rest.api.name");

        createHttpServer(router, host, httpServerPort)
                .compose(serverCreated -> publishHttpEndpoint(REST_API_NAME, host, httpServerPort))
                .setHandler((future.completer()));

    }

    private void sayHi(RoutingContext context){
        String username = context.request().getParam("username");
        logger.info("call provider httpEndPoint.username:"+username);
        context.response().end("Hi " + username + ".");
    }

    private Future<Void> createHttpServer(Router router, String host, int port) {
        Future<HttpServer> httpServerFuture = Future.future();
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, httpServerFuture.completer());
        return httpServerFuture.map(r -> null);
    }
}
