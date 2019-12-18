package rabbitmq.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import rabbitmq.adapter.MessageDelegate;
import rabbitmq.convert.TextMessageConvert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/14/13:40
 */
@Configuration
@ComponentScan({"rabbitmq.*"})
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("114.115.211.121:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");

        return connectionFactory;
    }

    /**
     * rabbitAdmin
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    /**
     * queue exchange bind
     */
    @Bean
    public TopicExchange exchange001(){
        return new TopicExchange("topic001",true,false);
    }
    @Bean
    public Queue queue001(){
        return new Queue("queue001",true);
    }
    @Bean
    public Binding binding001(){
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002(){
        return new TopicExchange("topic002",true,false);
    }
    @Bean
    public Queue queue002(){
        return new Queue("queue002",true);
    }
    @Bean
    public Binding binding002(){
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    /**
     * rabbitTemplate
     */
    @Bean
    public RabbitTemplate  rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return  rabbitTemplate;
    }


    /**
     * simpleMessageListenerContainer，对消费者有很多的配置项
     */
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        simpleMessageListenerContainer.setQueues(queue001(),queue002());
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        //是否重回队列
        simpleMessageListenerContainer.setDefaultRequeueRejected(false);
        //签收模式--自动签收
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //tag策略
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue+"_"+UUID.randomUUID().toString();
            }
        });

        //监听消息，进入onMessage方法，可以使用ChannelAwareMessageListener来监听，也可以使用MessageListenerAdapter
//        simpleMessageListenerContainer.setMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                String msg = new String(message.getBody());
//                System.err.println("-----------------------------消费者："+msg);
//            }
//        });





        //1.自定义的消息适配器
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
//        //适配器默认是有自己的方法的名字，即在MessageListenerAdapter类中指定的ORIGINAL_DEFAULT_LISTENER_METHOD为handleMessage，
//        // 就会调用MessageDelegate中的handleMessage方法，这里可以改变这个默认值，改为consumerMessage
//        messageListenerAdapter.setDefaultListenerMethod("consumerMessage");
//
//        //适配器设置转换器
//        messageListenerAdapter.setMessageConverter(new TextMessageConvert());

//        //Container设置适配器
//        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);



        //2.适配器：我们的队列名称和方法名称进行一一匹配
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
//
//        //messageListenerAdapter设置转换器
//        messageListenerAdapter.setMessageConverter(new TextMessageConvert());
//        //messageListenerAdapter设置TagToMethodName
//        Map<String,String> queueOrTagToMethodName = new HashMap<>();
//        queueOrTagToMethodName.put("queue001","method1");
//        queueOrTagToMethodName.put("queue002","method2");
//        messageListenerAdapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
//        //Container设置适配器
//        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);

        //支持json格式的转换器
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
        messageListenerAdapter.setDefaultListenerMethod("consumeMessage");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        //将转换器设置到adapter中
        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
        //将adapter添加到容器中
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);
        return simpleMessageListenerContainer;
    }
}
