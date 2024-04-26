package com.jiang.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信消费者
 */
public class DlxDirectConsumer {
    //业务交换机：
    private static final String WORK_DiRECT_EXCHANGE_NAME = "direct2-exchange";
    //死信交换机
    private static final String DLX_EXCHANGE_NAME = "dlx-exchange";
    public static void main(String[] argv) throws Exception {
        //创建工厂：
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();
        //声明交换机1
        channel1.exchangeDeclare(WORK_DiRECT_EXCHANGE_NAME, "direct");

        Map<String, Object> args1 = new HashMap<String, Object>();
        //绑定死信交换机
        args1.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        args1.put("x-dead-letter-routing-key", "laoban");
        //声明队列1
        String queueName1 = "heihei_queue";
        channel1.queueDeclare(queueName1,true,false,false,args1); //绑定死信交换机
        //绑定交换机1：
        channel1.queueBind(queueName1, WORK_DiRECT_EXCHANGE_NAME, "heihei");

        //声明队列2
        channel2.exchangeDeclare(WORK_DiRECT_EXCHANGE_NAME, "direct");
        Map<String, Object> args2 = new HashMap<String, Object>();
        //绑定死信交换机
        args2.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        args2.put("x-dead-letter-routing-key", "waibao");
        //声明队列2
        String queueName2 = "haha_queue";
        channel2.queueDeclare(queueName2,true,false,false,args2);//绑定死信交换机
        //绑定交换机2：
        channel2.queueBind(queueName2, WORK_DiRECT_EXCHANGE_NAME, "haha");

        System.out.println(" [directConsumer] Waiting for messages. To exit press CTRL+C");
        //xiaozi消费消息：
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //拒绝消息；
            channel1.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            System.out.println(" [heihei] Received '"+ delivery.getEnvelope().getRoutingKey()+" " + message + "'");
        };
        channel1.basicConsume(queueName1, false, deliverCallback1, consumerTag -> { });

        //xiaohei消费消息：
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //拒绝消息；
            channel2.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            System.out.println(" [haha] Received '" + delivery.getEnvelope().getRoutingKey()+" " + message + "'");
        };
        channel2.basicConsume(queueName2, false, deliverCallback2, consumerTag -> { });
    }
}