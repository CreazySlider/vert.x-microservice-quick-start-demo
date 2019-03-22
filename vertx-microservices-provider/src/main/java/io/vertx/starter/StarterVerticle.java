package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.microservice.provider.ZookeeperProviderVerticle;
import io.vertx.microservice.service.MyService;

public class StarterVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {

		vertx.deployVerticle(ZookeeperProviderVerticle.class.getName(), new DeploymentOptions()
				.setConfig(config()));

	}


}
