package rabbitmq.ratelimit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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


        String exchangeName = "test_qos_exchange";
        String routingKey = "qos.#";
        String queueName = "test_qos_queue";

       channel.exchangeDeclare(exchangeName,"topic",true,false,null);
       channel.queueDeclare(queueName,true,false,false,null);
       channel.queueBind(queueName,exchangeName,routingKey);

       //实现限流：
        //  1.autoAck设为false
        //  2.设置 basicQos
        //      prefetchCount：告诉rabbitMQ不要一次推送多余N个消息，如果有N个消息没有ack，consumer将block掉，直到有消息ack
        //      global：是否将上面的设置应用于channel，即这个限制是channel级别还是consumer级别
        channel.basicQos(0,1,false);

        channel.basicConsume(queueName,false,new MyConsumer(channel));

    }
}
