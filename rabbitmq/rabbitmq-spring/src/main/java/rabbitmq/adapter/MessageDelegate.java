package rabbitmq.adapter;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/17/13:20
 */
public class MessageDelegate {

    public void handleMessage(byte [] messageBody){
        System.err.println("默认方法，消息内容："+ new String(messageBody));
    }
    public void consumerMessage(byte [] messageBody){
        System.err.println("consumerMessage--字节数组，消息内容："+ new String(messageBody));
    }

    public void consumerMessage(String messageBody){
        System.err.println("consumerMessage--字符串，消息内容："+ messageBody);
    }

    public void method1(String messageBody){
        System.err.println("method1收到消息："+messageBody);
    }

    public void method2(String messageBody){
        System.err.println("method2收到消息："+messageBody);
    }

}
