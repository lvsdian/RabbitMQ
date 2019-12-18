package cn.andios.rabbitmqspring;

import jdk.nashorn.internal.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rabbitmq.Application;
import rabbitmq.entity.Order;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testAdmin() throws Exception{
        rabbitAdmin.declareExchange(new DirectExchange("test.direct",false,false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic",false,false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout",false,false));

        rabbitAdmin.declareQueue(new Queue("test.direct.queue",false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue",false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue",false));

        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
                Binding.DestinationType.QUEUE,
                "test.direct",
                "direct",
                new HashMap<>()));

        rabbitAdmin.declareBinding(
                BindingBuilder
                    .bind(new Queue("test.topic.queue",false))
                    .to(new TopicExchange("test.topic",false,false))
                    .with("user.#"));

        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.fanout.queue",false))
                        .to(new FanoutExchange("test.fanout",false,false)));
        rabbitAdmin.purgeQueue("test.topic.queue",false);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMsg() throws Exception{

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc","信息描述");
        messageProperties.getHeaders().put("type","自定义消息类型");

        Message message = new Message("hello message".getBytes(),messageProperties);

        rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                System.out.println("额外添加的设置...");
                message.getMessageProperties().getHeaders().put("desc","额外添加的信息描述");
                message.getMessageProperties().getHeaders().put("attr","额外添加的信息属性");

                return message;
            }
        });
    }

    @Test
    public void testSendMsg2() throws Exception{

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("hello 123".getBytes(),messageProperties);

        rabbitTemplate.send("topic001","spring.abc",message);

        rabbitTemplate.convertAndSend("topic001", "spring.amqp","hello Object send");
        rabbitTemplate.convertAndSend("topic002", "rabbitmq.amqp","hello Object send111");
    }

    @Test
    public void testSendMsg4Text() throws Exception{

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("hello 123".getBytes(),messageProperties);

        rabbitTemplate.send("topic001","spring.abc",message);
        rabbitTemplate.send("topic002","rabbit.abc",message);
    }

    public void testSendJsonMessage(){
        Order order = new Order("001", "消息订单", "订单内容");

    }

}
