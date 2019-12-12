package rabbitmq.returnListener;

import com.rabbitmq.client.*;

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
        String exchange = "test_return_exchange";
        String routingKey = "return.save";
        String routingKeyError = "error.save";

        String msg = "hello return  exchange";

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("handler return listener：");
                System.out.println("replyCode："+replyCode);
                System.out.println("replyText："+replyText);
                System.out.println("exchange："+exchange);
                System.out.println("routingKey："+routingKey);
                System.out.println("properties："+properties);
                System.out.println("body："+new String(body));

//                handler return listener：
//                replyCode：312
//                replyText：NO_ROUTE
//                exchange：test_return_exchange
//                routingKey：error.save
//                properties：#contentHeader<basic>(content-type=null, content-encoding=null, headers=null, delivery-mode=null, priority=null, correlation-id=null, reply-to=null, expiration=null, message-id=null, timestamp=null, type=null, user-id=null, app-id=null, cluster-id=null)
//                body：hello return  exchange
            }

        });

        channel.basicPublish(exchange,routingKeyError,true,null,msg.getBytes());
    }
}
