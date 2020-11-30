在springboot框架下，微服务解决方法

* Saga-server 负责记录回滚sql语句（以任务形式），并在需要的时候回滚。也可以定时或者UI界面触发

* database 负责保存事务数据以及回滚任务

* saga server job，可以自动回滚那些回滚失败的任务

* client1，任何微服务客户端，通过kafka 发送事务数据。客户端在调用start时候，标记自己的事务，在commit或者rollback的时候发送回滚sql到saga server

  

![](../../doc/saga-microservice.png)

start，发送全局事务gid+纳秒时间戳 到事务管理服务器，其负责检测是否有同样的事务id，如果有，则加入，如果没有，则创建

commit，保存所有rollback语句到服务器

rollback， 保存所有rollback语句到服务，并设置rollback标记给服务器

服务器接收到rollback标记，标记事务失败。但并不立即回滚。直到最外层的事务rollback标记发回才开始开始回滚，取出所同一个gid的rollback语句，然后再发回到各个客户端，客户端依次执行，并反馈结果到saga-server。服务器会判断如果所有成功执行rollback，则标记事务成功回滚 则发回失败。 服务器接收到失败后，，标记下次回滚时间。成功的则不需要标记

服务器定时清理成功回滚的全局事务，把提交成功的数据删除
服务器定时执行未回滚成功的事务，也可以通过界面执行失败事务

关键设计：

* 服务器和客户端通过消息队列，同样的gid，将顺序发送给同样的事务管理器·，以保证顺序

* 回滚任务由各个客户端生成，beetlsql的saga mapper会自动生成操作的逆向操作（不需要解析sql生成逆向sql）

  



