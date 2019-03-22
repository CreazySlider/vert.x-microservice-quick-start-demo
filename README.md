            vert.x-microservice-quick-start-demo
	
Remote Procedure Call(RPC)

Backend is zookeeper.

Deployed two microservices:

	1.EventBusService(Must be started in cluster mode)
	
	2.HttpEndpoint
	
	
Start:

1.Modify the configuration file(src/config/local.json) according to your situation;

2.Start your zookeeper;

3.cd vertx-microservices-provider;./redeploy.sh

4.cd vertx-microservices-consumer;./redeploy.sh

5.http://consumer.http.host:8080/httpEndPoint/sayHi/Jack


  http://consumer.http.host:8080/ebServiceProxy/sayHi/Tom
  
  
  http://consumer.http.host:8080/ebServiceProxy/getLength/vertx
  