drop table IF  EXISTS test.user;
CREATE TABLE test.user
(
    `id` String,
    `name` String,
    `create_ts` UInt64,
    `day` Date

)ENGINE = MergeTree()
PARTITION BY (toYYYYMM(day))
PRIMARY KEY id
ORDER BY id;