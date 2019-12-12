package rabbitmq.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/12/11:53
 */
public class ProducerDirectExchange {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建一个ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("114.115.211.121");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        //2.通过ConnectionFactory创建连接
        Connection connection = connectionFactory.newConnection();

        //3.通过connection创建channel
        Channel channel = connection.createChannel();

        //声明
        String exchangeName = "test_direct_exchange";
        String routingKey = "test.direct";

        String msg = "hello direct exchange";
        channel.basicPublish(exchangeName,routingKey,null,msg.getBytes());

    }
}
