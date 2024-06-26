# GG-ws
WebSocket

## 整体工作流程介绍

![图1](https://github.com/zhong15/GG-ws/blob/main/arch.png?raw=true)

- 连接建立：
  - 1 客户端发送请求到连接服务器 gg-ws-connect
  - b.2 连接服务器 gg-ws-connect 存储连接信息到缓存服务器
- 发送消息：
  - 1 客户端发送消息到连接服务器 gg-ws-connect
  - a.2 转发到发送服务器 gg-ws-send
  - a.3 发送服务器 gg-ws-send 再发送到业务服务器 gg-ws-biz
- 推送消息：
  - c.1 业务服务器 gg-ws-biz 推送消息到推送服务器 gg-ws-push
  - c.2 推送服务器 gg-ws-push 到缓存服务器查找连接信息
  - c.3 如果不在线，推送服务器 gg-ws-push 存储消息到数据库
  - c.4 如果在线，推送服务器 gg-ws-push 根据连接信息转发消息到连接服务器 gg-ws-connect
  - 1 连接服务器 gg-ws-connect 发送消息到客户端
- 离线后登陆接收消息：
  - d.2 连接服务器 gg-ws-connect 查询接收服务器 gg-ws-receive
  - d.3 接收服务器 gg-ws-receive 查询数据库返回未推送消息
  - 1 连接服务器 gg-ws-connect 发送消息到客户端

## 版本特性介绍

### 0.0.1

- 支持连接服务器 gg-ws-connect 多节点部署，通过缓存服务器（Redis）感知前端连接
- 支持连接服务器 gg-ws-connect 定时检测持有的前端连接的活性
- 支持前端连接发送消息到业务服务器 gg-ws-biz，业务服务器 gg-ws-biz 可选原路返回消息到前端连接
- 支持业务服务器 gg-ws-biz 推送消息到前端连接，如果前端连接离线则按顺序持久化到数据库（Cassandra）
- 支持前端连接上线时接收离线推送的消息
- 支持通过缓存服务器（Redis）生成分布式 ID
- 多节点同一个用户同一时间只能建立一个连接，用户上线后会剔除之前建立的连接
- 支持缓存重启后刷入连接信息（提供接口：/refresh）

## 工程代码实现介绍

采用 Spring Boot 框架和 spring-boot-starter-websocket 实现消息收发，采用 Redis 存储连接信息、生成分布式 ID，离线推送消息采用 Cassandra 存储，各个系统间采用 http 协议连接访问

### gg-ws-biz
业务服务

- POST /receive?userId=&message=
  - String
- GET /push?userId=&message=
  - Integer

### gg-ws-connect
连接服务

- POST /push?userId=&message=
  - Integer
- POST /refresh
  - Integer

### gg-ws-push
推送服务

- POST /push?userId=&message=
  - Integer

### gg-ws-receive
接收服务

- GET /receive/{userId}
  - List<Message>
- POST /acknowledge?userId=&messageIds=
  - Integer

### gg-ws-send
发送服务

- POST /send?userId=&message=
  - String

### gg-ws-send
存放测试 websocket 的 html

### scripts
相关脚本，如：数据库脚本