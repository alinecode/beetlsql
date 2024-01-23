* 参考文档，启动docker https://zhuanlan.zhihu.com/p/567151891

```yaml
version: '3'
services:
  docker-fe:
    image: "freeoneplus/apache-doris:fe-1.1.2-lts-20220920-x86"
    container_name: "doris-fe"
    tty: true
    hostname: "fe"
    restart: always
    ports:
      - 8030:8030
      - 9030:9030
    networks:
      doris_net:
        ipv4_address: 172.20.80.2
  docker-be:
    image: "freeoneplus/apache-doris:be-1.1.2-lts-20220920-x86"
    container_name: "doris-be"
    tty: true
    hostname: "fe"
    restart: always
    ports:
      - 8040:8040
      - 9000:9000
      - 9050:9050
    networks:
      doris_net:
        ipv4_address: 172.20.80.3
  register:
    image: "freeoneplus/apache-doris:register"
    container_name: "doris-register"
    hostname: "register"
    privileged: true
    command: ["sh","-c","/root/register.sh"]
    depends_on:
      - docker-fe
      - docker-be
    volumes: 
      - /etc/localtime:/etc/localtime:ro
      - /var/run/docker.sock:/var/run/docker.sock
    networks:
      doris_net:
        ipv4_address: 172.20.80.4
networks:
  doris_net:
    ipam:
      config:
      - subnet: 172.20.80.0/16
```
用mysql工具连上

```sql
CREATE TABLE IF NOT EXISTS test.example_tbl_agg1
(
    `user_id` LARGEINT NOT NULL COMMENT "用户id",
    `date` DATE NOT NULL COMMENT "数据灌入日期时间",
    `city` VARCHAR(20) COMMENT "用户所在城市",
    `age` SMALLINT COMMENT "用户年龄",
    `sex` TINYINT COMMENT "用户性别",
    `last_visit_date` DATETIME REPLACE DEFAULT "1970-01-01 00:00:00" COMMENT "用户最后一次访问时间",
    `cost` BIGINT SUM DEFAULT "0" COMMENT "用户总消费",
    `max_dwell_time` INT MAX DEFAULT "0" COMMENT "用户最大停留时间",
    `min_dwell_time` INT MIN DEFAULT "99999" COMMENT "用户最小停留时间"
)
AGGREGATE KEY(`user_id`, `date`, `city`, `age`, `sex`)
DISTRIBUTED BY HASH(`user_id`) BUCKETS 1
PROPERTIES (
"replication_allocation" = "tag.location.default: 1"
);

```