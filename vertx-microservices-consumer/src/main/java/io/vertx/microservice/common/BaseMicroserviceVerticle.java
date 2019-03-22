package io.vertx.microservice.common;

import io.vertx.microservice.common.BaseMicroserviceVerticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.backend.zookeeper.ZookeeperBackendService;
import io.vertx.servicediscovery.types.EventBusService;

public class BaseMicroserviceVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(io.vertx.microservice.common.BaseMicroserviceVerticle.class);

    protected ServiceDiscovery zookeeperDiscovery;

    protected Set<Record> registeredRecords = new ConcurrentHashSet<>();

    @Override
    public void start() throws Exception {
        ServiceDiscoveryOptions options = new ServiceDiscoveryOptions();
        options
                .setName(config().getString("zookeeper.discovery.name"))
                .setBackendConfiguration(new JsonObject()
                        .put("backend-name", ZookeeperBackendService.class.getName())//要使用自定义注册中心，这个是固定写法
                        .put("connection", config().getString("zookeeper.ip") + ":" + config().getString("zookeeper.port"))
                        .put("maxRetries", 4)//最大初始化服务发现次数，默认3次
                        .put("baseSleepTimeBetweenRetries", 1000)//重试创建服务发现间隔时间，单位ms，默认1000ms
                        .put("canBeReadOnly", false)//是否只读，默认false
                        .put("connectionTimeoutMs", 1000)//连接zookeeper超时时间，默认1000ms
                        .put("basePath", config().getString("basePath"))//存储服务记录的Zookeeper路径，默认/services
                        .put("guaranteed", true)//是否在发生故障时保证节点删除,默认false
                );

        zookeeperDiscovery = ServiceDiscovery.create(vertx, options);
    }


    protected Future<Void> publishEventBusService(String name, String address, Class serviceClass) {
        Record record = EventBusService.createRecord(name, address, serviceClass);
        return publish(record);
    }

    /**
     * Publish a service with record.
     *
     * @param record service record
     * @return async result
     */
    private Future<Void> publish(Record record) {
        if (zookeeperDiscovery == null) {
            try {
                start();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot create discovery service");
            }
        }

        Future<Void> future = Future.future();
        // publish the service
        zookeeperDiscovery.publish(record, ar -> {
            if (ar.succeeded()) {
                logger.info("Service <" + ar.result().getName() + "> published");
                future.complete();
            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    @Override
    public void stop(Future<Void> future) throws Exception {
        logger.info("start unpublish microservices.");
        // In current design, the publisher is responsible for removing the service
        List<Future> futures = new ArrayList<>();
        registeredRecords.forEach(record -> {
            Future<Void> cleanupFuture = Future.future();
            futures.add(cleanupFuture);
            zookeeperDiscovery.unpublish(record.getRegistration(), cleanupFuture.completer());
        });

        if (futures.isEmpty()) {
            zookeeperDiscovery.close();
            future.complete();
        } else {
            CompositeFuture.all(futures).setHandler(ar -> {
                zookeeperDiscovery.close();
                if (ar.failed()) {
                    future.fail(ar.cause());
                } else {
                    future.complete();
                }
            });
        }
    }

}
