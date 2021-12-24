# base_tools
## 封装了数据库工具（Jdbc可用多数据源）
 包含jdbc以及hibernate的封装
 jdbc 支持多数据源
 hibernate 自动取配置中的第一个数据源
多数据源配置示例
```
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    sources:
      - key: default
        url: jdbc:mysql://1.1.1.1:3306/schema?autoReconnect=true&useSSL=false&characterEncoding=utf8
        username: root
        password: password
      - key: default1
        url: jdbc:mysql://2.2.2.2:3306/schema2?autoReconnect=true&useSSL=false&characterEncoding=utf8
        username: root
        password: password
    jpa:
      database: MYSQL
      hibernate:
        ddl-auto: none
        dialect: com.base.dao.MySQLDialect
      showSql: false
    properties:
      hibernate:
        dialect: com.base.dao.MySQLDialect
    # 下面为连接池的补充设置，应用到上面所有数据源中
    # 初始化大小，最小，最大
    initialSize: 2
    minIdle: 2
    maxActive: 2
    # 配置获取连接等待超时的时间
    maxWait: 60000
    # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    timeBetweenEvictionRunsMillis: 60000
    # 配置一个连接在池中最小生存的时间，单位是毫秒
    minEvictableIdleTimeMillis: 300000
    validationQuery: SELECT 1 FROM DUAL
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    # 打开PSCache，并且指定每个连接上PSCache的大小
    poolPreparedStatements: true
    maxPoolPreparedStatementPerConnectionSize: 20
    # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
    filters: stat,wall
    # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
    useGlobalDataSourceStat: true # 合并多个DruidDataSource的监控数据
```
单数据源配置示例

```
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://1.1.1.1:3306/schema?autoReconnect=true&useSSL=false&characterEncoding=utf8
    username: root
    password: password
    jpa:
      database: MYSQL
      hibernate:
        ddl-auto: none
        dialect: com.base.dao.MySQLDialect
      showSql: false
    properties:
      hibernate:
        dialect: com.base.dao.MySQLDialect
    # 下面为连接池的补充设置，应用到上面所有数据源中
    # 初始化大小，最小，最大
    initialSize: 2
    minIdle: 2
    maxActive: 2
    # 配置获取连接等待超时的时间
    maxWait: 60000
    # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    timeBetweenEvictionRunsMillis: 60000
    # 配置一个连接在池中最小生存的时间，单位是毫秒
    minEvictableIdleTimeMillis: 300000
    validationQuery: SELECT 1 FROM DUAL
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    # 打开PSCache，并且指定每个连接上PSCache的大小
    poolPreparedStatements: true
    maxPoolPreparedStatementPerConnectionSize: 20
    # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
    filters: stat,wall
    # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
    useGlobalDataSourceStat: true # 合并多个DruidDataSource的监控数据
```

## redis工具

## Gis相关工具
  * 包含各种坐标转换
  * 拆分单元格
  * 平面坐标计算。。。。
  
## 常用工具类
  * 数据格式转换
  * 二维码
  * 反射
  * 加密、解密
  * bean操作
  * 时间操作
  * 二维码
  * md5等
  * 等等等
