package rabbitmq.ratelimit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/11/21:06
 */
public class Producer {
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

        //4.通过channel发送数据
        String exchange = "test_qos_exchange";
        String routingKey = "qos.save";

        for (int i = 0; i < 5; i++) {
            String msg = "hello qos  message"+i;
            channel.basicPublish(exchange,routingKey,true,null,msg.getBytes());
        }



    }
}
