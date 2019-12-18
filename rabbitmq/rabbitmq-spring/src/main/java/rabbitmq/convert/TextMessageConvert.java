package rabbitmq.convert;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/17/18:26
 */
public class TextMessageConvert implements MessageConverter {

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(o.toString().getBytes(),messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        String  contentType = message.getMessageProperties().getContentType();
        if(contentType!= null && contentType.contains("text")){
            return new String(message.getBody());
        }
        return message.getBody();
    }
}
