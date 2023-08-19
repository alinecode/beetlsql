一些对字段自动加密注解

```java


public String name;

@MD5(saltProperty="name") 
public String password;

```

对于AES，DES，需要一个key，这个Key来自于EncryptConfig

```java
//系统初始化的时候从配置获取到key
String key = ....
EncryptConfig.config(EncryptType.AES,xxxKey)

```

Bean定义

```java

@AES
public String password;

```



