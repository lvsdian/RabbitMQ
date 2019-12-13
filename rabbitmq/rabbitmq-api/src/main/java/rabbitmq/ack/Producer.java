package rabbitmq.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
        String exchange = "test_ack_exchange";
        String routingKey = "ack.save";



        Map<String,Object> headers = new HashMap<>();

        for (int i = 0; i < 5; i++) {

            String msg = "hello ack  message"+i;

            headers.put("num",i);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("utf-8")
                    .headers(headers)
                    .build();
            channel.basicPublish(exchange,routingKey,true,properties,msg.getBytes());
        }



    }
}
