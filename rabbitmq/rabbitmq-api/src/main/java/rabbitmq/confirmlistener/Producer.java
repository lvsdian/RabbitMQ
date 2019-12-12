package rabbitmq.confirmlistener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/12/20:01
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

        //4.指定消息投递模式：消息的确认模式
        channel.confirmSelect();

        String exchangeName = "test_confirm_exchange";
        String routingKey = "confirm.save";

        //5.添加一个确认监听
        String msg = "hello confirm msg";

        channel.addConfirmListener(new ConfirmListener() {
            //返回失败
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("-----ack");
            }

            //返回成功
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("+++++no ack");
            }
        });

        channel.basicPublish(exchangeName,routingKey,null,msg.getBytes());
    }
}


