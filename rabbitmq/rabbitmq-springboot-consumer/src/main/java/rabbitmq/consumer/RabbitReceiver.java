package rabbitmq.consumer;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rabbitmq.entity.Order;

import java.io.IOException;
import java.util.Map;


/**
 * @description:
 * @author:LSD
 * @when:2019/12/18/11:46
 */
@Component
public class RabbitReceiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value ="${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
            key = "${spring.rabbitmq.listener.order.key}"
    ))

    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws IOException {
        System.err.println("------------------------");
        System.err.println("消费端："+message.getPayload());
        Long deliveryTag  = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_MODE);
        //手工ACK
        channel.basicAck(deliveryTag,false);
    }

    @RabbitHandler
    public void onOrderMessage(@Payload rabbitmq.entity.Order order,
                               Channel channel,
                               @Headers Map<String,Object> headers) throws IOException {
        System.err.println("------------------------");
        System.err.println("消费Order："+order.getId());
        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_MODE);
        //手工ACK
        channel.basicAck(deliveryTag,false);
    }
}
