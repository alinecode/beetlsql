一个模拟Spring应用，Saga事务的管理,假设用户有4元余额，想购买一个3元产品

* 启动saga-server,运行SagaServerApplication（需要安装Kafka）
* 启动DemoApplication,OrderApplication,UserApplication
* 可以访问各个系统的swagger，比如http://127.0.0.1:8080(8081,8082)/swagger-ui/index.html
* 可以访问saga-server 的swagger   http://127.0.0.1:18081/swagger-ui/index.html

如下是购买流程

* 浏览器访问DemoApplication的swagger,调用buy/{gid},这里gid为任意订单号。执行操作，系统应该分别调用用户和订单系统，获取余额，增加订单和扣费
在调用成功后，模拟一个失败验证能否回滚。
* 浏览器访问Saga-server 的swagger，查询所有事务API，http://127.0.0.1:18081/api/v1/allRollbackTask ，返回内如下
可以看到事务在三个系统中都回滚成功
```
{
  "success": true,
  "msg": "成功",
  "data": [
    {
      "id": "a717d181-608b-4f99-ab8a-9c60e83cce12",
      "gid": "123",
      "appName": "userSystem",
      "status": "Success",
      "rollbackStatus": "Success",
      "time": 1138055785025836,
      "taskInfo": "{\"tasks\":[{\"@Clazz\":\"org.beetl.sql.saga.ms.client.SagaLevel3Transaction$KafkaSagaTaskTrace\",\"rollbackTask\":{\"@Clazz\":\"org.beetl.sql.saga.common.ami.SagaUpdateByIdAMI$UpdateSagaRollbackTask\",\"sqlManagerName\":\"mySqlManager\",\"obj\":{\"@Clazz\":\"org.beetl.sql.saga.demo.entity.UserEntity\",\"id\":\"xiandafu\",\"name\":\"闲大赋\",\"balance\":4}},\"success\":false}],\"success\":true}",
      "createTime": 1607864163823,
      "updateTime": 1607864164067
    },
    {
      "id": "c4765a46-cf2f-4d7a-a714-dc35bf723df2",
      "gid": "123",
      "appName": "orderSystem",
      "status": "Success",
      "rollbackStatus": "Success",
      "time": 1138055465293352,
      "taskInfo": "{\"tasks\":[{\"@Clazz\":\"org.beetl.sql.saga.ms.client.SagaLevel3Transaction$KafkaSagaTaskTrace\",\"rollbackTask\":{\"@Clazz\":\"org.beetl.sql.saga.common.ami.SagaInsertAMI$InsertSagaRollbackTask\",\"sqlManagerName\":\"mySqlManager\",\"entityClass\":\"org.beetl.sql.saga.demo.entity.OrderEntity\",\"pkId\":\"4a40f48b-4b29-4b62-8d47-5f3867b03afd\"},\"success\":false}],\"success\":true}",
      "createTime": 1607864163570,
      "updateTime": 1607864164058
    },
    {
      "id": "48eb2fbe-9ff8-4913-bfd2-63176b3646b4",
      "gid": "123",
      "appName": "demoSystem",
      "status": "Error",
      "rollbackStatus": "Success",
      "time": 1138055258883126,
      "taskInfo": "{\"tasks\":[],\"success\":true}",
      "createTime": 1607864163543,
      "updateTime": 1607864164052
    }
  ],
  "errorCode": 0
}
```
> status字段标识业务执行是否成功，rollbackStatus标识回滚是否执行成功，如上可以看到userSystem
>系统和orderSystem都执行成功，但demoSystem执行失败抛出异常，导致所有三个系统都回滚

* 因为回滚成功，应该能继续调用/buy/{gid}，并不会抛出余额不足
* 查看各个系统的beetlsql 日志，会发现反向操作成功执行
* 查看Saga-server 的Swagger ，可以看到回滚任务执行结果
* 可以订阅各个TOPIC，了解回滚任务是如何发送到Saga-Server，以及如何从Saga—Server获取回滚任务
* 可以在SagaLevel3ClientConfig.retry方法打断点和


如下为是使用的topic和数据库
```

./kafka-topics.sh --delete --zookeeper 127.0.0.1:2181 --topic saga-client-demoSystem
./kafka-topics.sh --delete --zookeeper 127.0.0.1:2181 --topic saga-client-userSystem
./kafka-topics.sh --delete --zookeeper 127.0.0.1:2181 --topic saga-client-orderSystem
./kafka-topics.sh --delete --zookeeper 127.0.0.1:2181 --topic saga-server-topic
rm -rf ~/.h2
ls
```
