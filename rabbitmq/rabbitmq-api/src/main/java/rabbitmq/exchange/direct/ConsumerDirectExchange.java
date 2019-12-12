package rabbitmq.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/12/11:52
 */
public class ConsumerDirectExchange {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //1.创建一个ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("114.115.211.121");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);

        //2.通过ConnectionFactory创建连接
        Connection connection = connectionFactory.newConnection();

        //3.通过connection创建channel
        Channel channel = connection.createChannel();

        //声明
        String exchangeName = "test_direct_exchange";
        String exchangeType = "direct";
        String queueName = "test_direct_queue";
        String routingKey = "test.direct";

        //声明交换机
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,false,null);
        //声明队列
        channel.queueDeclare(queueName,false,false,false,null);
        //绑定
        channel.queueBind(queueName,exchangeName,routingKey);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName,true,consumer);
        //循环获取消息
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String msg = new String (delivery.getBody());
            System.out.println("收到消息："+msg);

        }
    }
}
