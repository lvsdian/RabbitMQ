package rabbitmq.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/12/21:22
 */
public class MyConsumer extends DefaultConsumer {


    private Channel channel;

    public MyConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("consumerTag："+consumerTag);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //手工签收
        //  1.nack
        if((Integer) properties.getHeaders().get("num") == 0){
            //requeue参数为true，即如果no ack就会重回队列
            channel.basicNack(envelope.getDeliveryTag(),false,true);
        //  2.ack
        }else{
            channel.basicAck(envelope.getDeliveryTag(),false);
        }
    }
}
