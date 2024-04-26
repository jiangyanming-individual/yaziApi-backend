package com.jiang.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;


/**
 *单一消费者
 */
public class SingleConsumer {

    //队列的名字
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] argv) throws Exception {
        //创建工厂：
        ConnectionFactory factory = new ConnectionFactory();
        // 设置 rabbitmq 对应的信息
        factory.setHost("localhost");
//        factory.setUsername("xxx");
//        factory.setPassword("xxx");

        //创建通道链接：
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [consumer] Waiting for messages. To exit press CTRL+C");
        //消费者定义如何处理消息：
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [consumer] Received '" + message + "'");
        };
        //消费消息：autoAck：自动确认
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}