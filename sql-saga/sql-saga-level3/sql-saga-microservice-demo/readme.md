一个模拟Spring应用，Saga事务的管理,假设用户有4元余额，想购买一个3元产品

* 启动saga-server,运行SagaServerApplication（需要安装Kafka）
* 启动DemoApplication,OrderApplication,UserApplication
* 可以访问各个系统的swagger，比如http://127.0.0.1:8080(8081,8082)/swagger-ui/index.html
* 浏览器访问 http://127.0.0.1:8080/buy/123,这里123为任意订单号。执行操作，系统应该分别调用用户和订单系统，
在调用成功后，模拟一个失败。
* 浏览器访问http://127.0.0.1:18081/swagger-ui/index.html，查询所有事务，可以看到一条事务
且gid为123，标记的用户和订单俩个回滚任务都执行成功
* 因为回滚成功，应该能继续调用/buy/123，并不会抛出余额不足
