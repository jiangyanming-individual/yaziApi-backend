package com.jiang.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class FanoutConsumer {
    private static final String FANOUT_EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        //创建工厂：
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();

        //声明交换机1
        channel1.exchangeDeclare(FANOUT_EXCHANGE_NAME, "fanout");
        //声明队列1
        String queueName1 = "xiaolan_queue";
        channel1.queueDeclare(queueName1,true,false,false,null);
        //绑定交换机1：
        channel1.queueBind(queueName1, FANOUT_EXCHANGE_NAME, "");

        //声明交换机2
        channel2.exchangeDeclare(FANOUT_EXCHANGE_NAME, "fanout");
        //声明队列2
        String queueName2 = "xiaohong_queue";
        channel2.queueDeclare(queueName2,true,false,false,null);
        //绑定交换机2：
        channel2.queueBind(queueName2, FANOUT_EXCHANGE_NAME, "");

        System.out.println(" [fanoutConsumer] Waiting for messages. To exit press CTRL+C");
        //队列1消费消息：
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaolan] Received '" + message + "'");
        };
        channel1.basicConsume(queueName1, true, deliverCallback1, consumerTag -> { });

        //队列2消费消息：
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaohong] Received '" + message + "'");
        };
        channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
    }
}