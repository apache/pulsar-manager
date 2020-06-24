## FAQ

* Q: Couldn't do any operation for pulsar?
* Q: Manager doesn't show the Standalone cluster option for adding to tenant?
* Q: 
```
java.lang.NullPointerException: null
	at org.apache.pulsar.manager.service.impl.BrokersServiceImpl.getBrokersList(BrokersServiceImpl.java:54) ~[pulsar-manager.jar:na]
	at org.apache.pulsar.manager.service.impl.ClustersServiceImpl.getClustersList(ClustersServiceImpl.java:70) ~[pulsar-manager.jar:na]
	at org.apache.pulsar.manager.service.impl.BrokerStatsServiceImpl.scheduleCollectStats(BrokerStatsServiceImpl.java:129) ~[pulsar-manager.jar:na]
	at sun.reflect.GeneratedMethodAccessor118.invoke(Unknown Source) ~[na:na]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_212]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_212]
	at org.springframework.scheduling.support.ScheduledMethodRunnable.run(ScheduledMethodRunnable.java:65) ~[spring-context-5.0.6.RELEASE.jar:5.0.6.RELEASE]
	at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54) ~[spring-context-5.0.6.RELEASE.jar:5.0.6.RELEASE]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) [na:1.8.0_212]
	at java.util.concurrent.FutureTask.runAndReset(FutureTask.java:308) [na:1.8.0_212]
	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180) [na:1.8.0_212]
	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294) [na:1.8.0_212]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [na:1.8.0_212]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [na:1.8.0_212]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_212]
```

Reason: The webservice URL configured by pulsar cluster when initializing metadata is inconsistent with the actual webservice URL.

A: 
```
./bin/pulsar-admin clusters list
./bin/pulsar-admin clusters get cluster-name (eg, standalone)
```

The following results are correct:
```
./bin/pulsar-admin clusters get standalone
{
  "serviceUrl" : "http://127.0.0.1:8080",
  "brokerServiceUrl" : "pulsar://127.0.0.1:6650"
}
```
Your network should be connected to serviceUrl's network.

If these two domain names are set incorrectly, please use the `./bin/pulsar-admin clusters update` command to update them to be correct.

```
./bin/pulsar-admin clusters update --broker-url http://broker-ip:6605 --url http://broker-ip:8080 cluster-name (eg, standalone)
```
Releated issue: https://github.com/apache/pulsar-manager/issues/292


Q: Pulsar Manager keeps saying "This environment is error. Please check it"

Reason: Your pulsar manager service cannot connect with your pulsar cluster network.

A: it's because _pulsar_  and _pulsar-manger_ runs in different containers with theirself localhosts (each container just know itself `localhost`).
you should either run these two containers in one network using something like docker composer, or set `--network host` in your command, something like this:
```bash
docker run --rm --network host -it  -e REDIRECT_HOST=http://localhost -e REDIRECT_PORT=9527 -e DRIVER_CLASS_NAME=org.postgresql.Driver -e URL='jdbc:postgresql://127.0.0.1:5432/pulsar_manager' -e USERNAME=pulsar -e PASSWORD=pulsar -e LOG_LEVEL=DEBUG -v $PWD:/data apachepulsar/pulsar-manager:v0.1.0 /bin/sh
```

Related issue: https://github.com/apache/pulsar-manager/issues/283
https://github.com/apache/pulsar-manager/issues/214


### Troubleshooting steps for Pulsar Manager

1. check whether pulsar cluster is normal

```
curl -v http://webservice-url:port/metrics/

For example:
curl -v http://localhost:8080/metrics/
```

2. Check the cluster configuration. A should be your real service address and cannot be filled in casually

```
./bin/pulsar-admin clusters list
./bin/pulsar-admin clusters get cluster-name (eg, standalone)
```

The following results are correct:
```
./bin/pulsar-admin clusters get standalone
{
  "serviceUrl" : "http://127.0.0.1:8080",
  "brokerServiceUrl" : "pulsar://127.0.0.1:6650"
}
```
Your network should be connected to serviceUrl's network.

If these two domain names are set incorrectly, please use the `./bin/pulsar-admin clusters update` command to update them to be correct.

```
./bin/pulsar-admin clusters update --broker-url http://broker-ip:6605 --url http://broker-ip:8080 cluster-name (eg, standalone)

3. Check the log of pulsar manager

```
docker logs -f pulsar-manager-container-name
```

```
docker exec -it /bin/sh pulsar-mananger-container-name /bin/sh
tail -f /pulsar-manager/puslar-manager.log
```