package rabbitmq.quickstart;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/11/20:55
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //1.创建一个ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("114.115.211.121");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        //2.通过ConnectionFactory创建连接
        Connection connection = connectionFactory.newConnection();

        //3.通过connection创建channel
        Channel channel = connection.createChannel();

        //4.声明一个队列
        String queueName = "test001";
        channel.queueDeclare("test001",true,false,false,null);

        //5.创建消费者
        QueueingConsumer queueConsumer = new QueueingConsumer(channel);

        //6.设置channel
        channel.basicConsume(queueName,true,queueConsumer);

        //7.获取消息
        while(true){
            QueueingConsumer.Delivery delivery = queueConsumer.nextDelivery();
            String msg = new String (delivery.getBody());
            System.out.println("消费端："+msg);
        }
    }
}
