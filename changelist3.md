#3.23.1

* 批量操作，可选逐条打印

#3.23.3

* Oracle 同位词数据获取

# 3.23.4

* Oracle 12翻页新的实现

# 3.23.9
* 增加sql-ext,包含了各种扩展，有xml支持，sql防火墙(firewall),性能优化

# 3.24.0
* 非javabean的pojo支持
* 加速扩展包重构
* 安全扩展包

# 3.25.0
* 扩展包sql重写，能支持多租户，数据权限等功能
* 
# 3.25.1
* DebugInterceptor 修复
* 
# 3.25.3
* BaseMapper增加batchUpdate实现 修复
* 优化SqlRewrite的RewriteMapper

# 3.27.0 （不兼容更新)
* 插入多列获取数据库计算值 ，API调整
* 修复Query的update和updateSeletive方法Bug

# 3.27.1 
* Debug Format SQL

# 3.27.2
* Debug Format SQL
* upsert bug fix for version update


# 3.27.5
* sql-tenant 改成sql-rewrite
* 增加clearProperty

# 3.28.0
* 存储过程调用，修复只支持一个输出参数
* SQLManager增加新的executeBatch方法

# 3.28.1
* sqlparser 升级到4.7，并修复兼容问题
* sql-dynamic-table bug修复，使用asm直接生成entity类
* query的逻辑删除功能，当执行update或者updateselective ，报错

# 3.29.0
* 支持apache doris
* 使用beetl最新版本，修复不支持char的BUG
* sql-dynamic-table 支持timestamp和byte[] 等更多类型操作

# 3.30.0
* 支持查询引擎trino

# 3.30.1..3.30.3
* 支持ms sqlserver从系统表获取列的注解 MSSqlServerMetadataManager
# 3.30.4
* 支持国产数据库oceanbase,ymatrix, openplant

# 3.30.10
* 修复Query Bug

# 3.30.11
* 依赖Beetl升级到最新版，并保持兼容
* 修复高并发改表情况下的同步问题
