# 开发工具清单
1. Visual Studio Code（VS Code）
2. JDK（Oracle JDK） 17最低
3. apache Maven
4. TortoiseGit
5. Redis
6. TRAE（TraeCode，字节 AI IDE，国内版）

application.properties 修要改以下两点：
1需要改的是数据库连接信息
spring.datasource  
2 redis 连接信息密码默认没，如果有密码，需要配置
spring.data.redis.password=test123456