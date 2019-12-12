### AMQP

##### AMQP vs JMS

- jms是java的消息服务，属API规范，有点对点、发布订阅两种模式，支持TextMessage、MapMessage 等复杂的消息正文格式(5种)。

- ;AMQP是高级消息队列协议，提供5种消息模型(direct/fanout/topic/headers/system)，仅支持byte[]类型信息，几种消息队列都是基于AMQP来实现的。

##### 协议模型

![](img\AMQP协议模型.jpg)

##### 核心概念

1. server：又称broker，接受客户端的连接，实现AMQP实体服务。
2. connection：连接，应用程序与broker的连接。
3. Channel：网络信道，几乎所有的操作都在channel中进行，channel是进行消息读写的通道。客户端可建立多个channel，每个channel代表一个会话任务。
4. message：服务器与应用程序之间传送的数据，由properties和body组成，properties可对消息进行修饰，比如消息的优先级，延迟等特性；body就是消息体内容。
5. virtual host：虚拟主机，用于进行逻辑隔离，最上层的消息路由。一个virtual host里面可以有若干个exchange 和 queue，同一个virtual host里面不能有相同名称的exchange和queue。
6. exchange：交换机，接收消息，根据路由键转发消息到绑定的队列。
7. banding：exchange和queue之间的虚拟连接，banding中可以包含routing key。
8. routing key：虚拟机可用它来确定如何路由一个特定消息。
9. queue：也称为message queue，保存消息并将它们转发。

##### activemq vs rabbitmq vs rocketmq vs kafka

- 吞吐量：activeMQ、rabbitMQ比rocketMQ、kafka低  
- 时效性：RabbitMQ基于erlang开发，并发能力强，延时很低，达到微秒级，其他三个都是 ms 级。
- 可用性：activeMQ、rabbitMQ基于主从架构实现高可用，rocketMQ、kafka基于分布式架构实现高可用
- 性能：rabbitmq采用erlang语言开发，使得rabbitmq在broker之间进行数据交互性能非常优秀。erlang有着和原生socket一样的延迟

RabbitMQ基于信道channel传输，没有用tcp连接来进行数据传输，tcp链接创建和销毁对于系统性能的开销比较大消费者链接RabbitMQ其实就是一个TCP链接，一旦链接创建成功之后，    就会基于链接创建Channel，每个线程把持一个Channel,Channel复用TCP链接，减少了系统创建和销毁链接的消耗，提高了性能 

### rabbitmq

#### 安装与使用

##### 安装

```shell
# 准备
yum install build-essential openssl openssl-devel unixODBC unixODBC-devel make gcc gcc-c++ kernel-devel m4 ncurses-devel tk tc xz

# 下载安装包
wget www.rabbitmq.com/releases/erlang/erlang-18.3-1.el7.centos.x86_64.rpm
wget http://repo.iotti.biz/CentOS/7/x86_64/socat-1.7.3.2-5.el7.lux.x86_64.rpm
wget www.rabbitmq.com/releases/rabbitmq-server/v3.6.5/rabbitmq-server-3.6.5-1.noarch.rpm

#安装
rpm -ivh erlang-18.3-1.el7.centos.x86_64.rpm
rpm -ivh socat-1.7.3.2-5.el7.lux.x86_64.rpm
rpm -ivh rabbitmq-server-3.6.5-1.noarch.rpm ```
```

##### 启动

```shell
# 设置开启启动
chkconfig rabbitmq-server on
 
# 启动服务
service rabbitmq-server start
# 或
rabbitmq-server start & 
# rabbitmq-server start & 如果显示正在运行可以用ps -ef|grep rabbit查出正在运行的端口号并 kill 端口号
# rabbitmq-server start & 如果运行成功，会提示log文件位置：/var/log/rabbitmq/rabbit@【主机名】-c49b.log(主机名配置文件：/etc/hostname)
# 验证是否启动： lsof -i:5672 

# 停止服务
service rabbitmq-server stop
# 或
rabbitmqctl stop_app
```

##### 配置

```shell
# 开启插件
rabbitmq-plugins enable rabbitmq_management

# 开放端口
firewall-cmd --add-port=5672/tcp --permanent
firewall-cmd --add-port=15672/tcp --permanent
firewall-cmd --reload

# 启动rabbitmq后，在/var/log/rabbitmq目录下会有日志文件，在rabbit@ecs-c49b.log文件的最上方，可以看# 到一个 config file(s) 指定了配置文件位置，
# /usr/share/doc/rabbitmq-server-3.7.7/rabbitmq.config.example 是一个配置文件的模板。

#/usr/lib/rabbitmq/lib/rabbitmq_server-3.6.5/ebin/rabbit.app 是核心配置文件
# 修改rabbit.app 42行，改为  {loopback_users, [guest]},  即可。
```

##### 命令行使用

![](img\rabbitmq_option_1.jpg)

![](img\rabbitmq_option_2.jpg)



![](img\rabbitmq_option_3.jpg)

![](img\rabbitmq_option_4.jpg)

![](img\rabbitmq_option_5.jpg)

![](img\rabbitmq_option_6.jpg)

![](img\rabbitmq_option_7.jpg)

##### 架构图



<img src="img\rabbitmq整体架构图.jpg" style="zoom: 67%;" />

#### exchange

##### 架构图

接收消息，并根据路由键转发消息到队列。

![](img\exchange.jpg)

##### 属性

	1. name
 	2. type：交换机类型，direct、topic、fanout、headers。
 	3. durability：是否需要持久化。
 	4. auto delete：当最后一个绑定到exchange上的队列删除后，自动删除该exchange。
 	5. internal：当前exchange是否用于rabbitmq内部使用，默认false
 	6. arguments：扩展参数，用于扩展amqp协议自定制化使用。

##### Direct Exchange

​		所有发送到Direct Exchange的消息被转发到RouteKey中指定的Queue。即RouteKey一样才能正常接收。  

​		注意：Direct模式可以使用rabbitmq自带的exchange：default exchange，所以不需要将exchange进行任何绑定操作，消息传递时，RouteKey必须完全匹配才会被队列接收，否则该消息会被抛弃。

![](img\direct_exchange结构图.jpg)

##### Topic Exchange

​		所有发送到Topic Exchange的消息被转发到所有关心RouteKey中指定Topic的Queue上。  

​		exchange将RouteKey和某个Topic进行模糊匹配，此时队列需要绑定一个Topic。

​		模糊匹配：`#`匹配一个或多个词，`*`匹配不多不少一个词。

![](C:\Users\LSD\Desktop\rabbitMQ\img\topic_exchange结构图.jpg)