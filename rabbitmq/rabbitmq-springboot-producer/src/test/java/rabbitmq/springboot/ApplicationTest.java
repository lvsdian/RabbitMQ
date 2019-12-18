package rabbitmq.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rabbitmq.Application;
import rabbitmq.entity.Order;
import rabbitmq.producer.RabbitSender;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

    @Test
    public void contextLoads(){
    }

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSender(){
        Map<String,Object> properties = new HashMap<>();
        properties.put("number","123");
        properties.put("send_time",LocalDate.now());

        rabbitSender.send("hello rabbitmq for spring boot",properties);
    }

    @Test
    public void testSenderOrder(){
        Order order = new Order("orderId", "订单");
        rabbitSender.sendOrder(order);
    }

}
