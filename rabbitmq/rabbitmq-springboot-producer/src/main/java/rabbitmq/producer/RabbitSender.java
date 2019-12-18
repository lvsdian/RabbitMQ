package rabbitmq.producer;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import rabbitmq.entity.Order;

import java.util.Map;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/17/21:24
 */
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
        System.err.println("correlationData："+correlationData);
        System.err.println("ack："+ack);
        if(!ack){
            System.err.println("异常处理...");
        }
    };

    private final RabbitTemplate.ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routingKey) -> {
        System.err.println("return message："+message);
        System.err.println("return exchange："+exchange);
        System.err.println("return replyText："+replyText);
        System.err.println("return routingKey："+routingKey);
    };

    public void send(Object message, Map<String,Object> properties){
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message,messageHeaders);

        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);

        CorrelationData correlationData = new CorrelationData();
        // 设置id：唯一id+时间，保证全局唯一
        correlationData.setId("123");
        //如果这里路由匹配失败，就会进入returnCallback监听
        rabbitTemplate.convertAndSend("exchange-1","spring.hello",msg,correlationData);
    }

    public void sendOrder(Order order){
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);

        CorrelationData correlationData = new CorrelationData();
        // 设置id：唯一id+时间，保证全局唯一
        correlationData.setId("123");
        //如果这里路由匹配失败，就会进入returnCallback监听
        rabbitTemplate.convertAndSend("exchange-3","spring.123",order,correlationData);
    }
}
