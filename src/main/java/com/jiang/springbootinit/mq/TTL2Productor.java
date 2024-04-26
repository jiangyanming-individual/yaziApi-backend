package com.jiang.springbootinit.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;


/**
 * 单一生产者
 */
public class TTL2Productor {


    //队列的名字
    private final static String QUEUE_NAME = "ttl2-queue";
    public static void main(String[] argv) throws Exception {
        //创建工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置rabbitmq的信息：
        factory.setHost("localhost");
//        factory.setUsername("xxxx");
//        factory.setPassword("xxxx");
        try (Connection connection = factory.newConnection();
             //创建链接
             Channel channel = connection.createChannel()) {
            //创建消息队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            //指定某条消息过期时间：
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .expiration("60000")
                    .build();
            //发布消息
            channel.basicPublish("", QUEUE_NAME, properties, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [productor] Sent '" + message + "'");
        }
    }
}