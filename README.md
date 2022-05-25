# base_tools
## 封装了数据库工具（Jdbc可用多数据源）
 包含jdbc以及hibernate的封装
 jdbc 支持多数据源
 hibernate 自动取配置中的第一个数据源
* 多数据源配置示例
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
    pool:
      driver-class-name: ${spring.datasource.driverClassName}
      # 下面为连接池的补充设置，应用到上面所有数据源中
      # 初始化大小，最小，最大
      initial-size: 10
      min-idle: 10
      max-active: 20
      # 配置获取连接等待超时的时间
      max-wait: 60000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      time-between-eviction-runs-millis: 60000
      # 配置一个连接在池中最小生存的时间，单位是毫秒
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: true
      test-on-return: true
      # 打开PSCache，并且指定每个连接上PSCache的大小
      pool-prepared-statements: true
      #配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
      max-pool-prepared-statement-per-connection-size: 20
      filters: stat,wall
      use-global-data-source-stat: true
      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      # 配置监控服务器
      stat-view-servlet:
        login-username: 123456
        login-password: 123456
        reset-enable: false
        url-pattern: /druid/*
        enabled: true
      # 添加IP白名单
      #allow:
      # 添加IP黑名单，当白名单和黑名单重复时，黑名单优先级更高
      #deny:
      web-stat-filter:
        # 添加过滤规则
        url-pattern: /*
        # 忽略过滤格式
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
      db-type: mysql

  jpa:
    properties:
      hibernate:
        dialect: com.base.dao.MySQLDialect
        #new_generator_mappings: false
        format_sql: true
        ddl-auto: update
        show-sql: false
        database: MYSQL
      showSql: false

```
* 单数据源配置示例

```
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://1.1.1.1:3306/schema?autoReconnect=true&useSSL=false&characterEncoding=utf8
    username: root
    password: password
    pool:
      driver-class-name: ${spring.datasource.driverClassName}
      # 下面为连接池的补充设置，应用到上面所有数据源中
      # 初始化大小，最小，最大
      initial-size: 10
      min-idle: 10
      max-active: 20
      # 配置获取连接等待超时的时间
      max-wait: 60000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      time-between-eviction-runs-millis: 60000
      # 配置一个连接在池中最小生存的时间，单位是毫秒
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: true
      test-on-return: true
      # 打开PSCache，并且指定每个连接上PSCache的大小
      pool-prepared-statements: true
      #配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
      max-pool-prepared-statement-per-connection-size: 20
      filters: stat,wall
      use-global-data-source-stat: true
      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      # 配置监控服务器
      stat-view-servlet:
        login-username: 123456
        login-password: 123456
        reset-enable: false
        url-pattern: /druid/*
        enabled: true
      # 添加IP白名单
      #allow:
      # 添加IP黑名单，当白名单和黑名单重复时，黑名单优先级更高
      #deny:
      web-stat-filter:
        # 添加过滤规则
        url-pattern: /*
        # 忽略过滤格式
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
      db-type: mysql

  jpa:
    properties:
      hibernate:
        dialect: com.base.dao.MySQLDialect
        #new_generator_mappings: false
        format_sql: true
        ddl-auto: update
        show-sql: false
        database: MYSQL
      showSql: false

```

## redis工具
 * 配置示例
```
 redis:
  redis_ip: redis
  redis_port: 6379
  auth: password
  pool:
    maxIdle: 10
    maxTotal: 10
    testOnBorrow: true
    testOnReturn: true
```
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
