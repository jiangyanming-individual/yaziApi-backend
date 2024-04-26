package com.jiang.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class TopticConsumer {
    private static final String TOPIC_EXCHANGE_NAME = "topic-exchange";

    public static void main(String[] argv) throws Exception {
        //创建工厂：
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();
        Channel channel3 = connection.createChannel();
        //声明交换机1
        channel1.exchangeDeclare(TOPIC_EXCHANGE_NAME, "topic");
        //声明队列1
        String queueName1 = "qianduan_queue";
        channel1.queueDeclare(queueName1,true,false,false,null);
        //绑定交换机1：
        channel1.queueBind(queueName1, TOPIC_EXCHANGE_NAME, "#.qianduan.#");

        //声明交换机2
        channel2.exchangeDeclare(TOPIC_EXCHANGE_NAME, "topic");
        //声明队列2
        String queueName2 = "houduan_queue";
        channel2.queueDeclare(queueName2,true,false,false,null);
        //绑定交换机2：
        channel2.queueBind(queueName2, TOPIC_EXCHANGE_NAME, "#.houduan.#");


        //声明交换机2
        channel3.exchangeDeclare(TOPIC_EXCHANGE_NAME, "topic");
        //声明队列2
        String queueName3 = "chanpin_queue";
        channel3.queueDeclare(queueName3,true,false,false,null);
        //绑定交换机2：
        channel3.queueBind(queueName3, TOPIC_EXCHANGE_NAME, "#.chanpin.#");

        System.out.println(" [topicConsumer] Waiting for messages. To exit press CTRL+C");
        //队列1消费消息：
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [前端] Received '"+ delivery.getEnvelope().getRoutingKey()+" " + message + "'");
        };
        channel1.basicConsume(queueName1, true, deliverCallback1, consumerTag -> { });

        //队列2消费消息：
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [后端] Received '" + delivery.getEnvelope().getRoutingKey()+" " + message + "'");
        };
        channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });

        //队列3消费消息：
        DeliverCallback deliverCallback3 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [产品] Received '" + delivery.getEnvelope().getRoutingKey()+" " + message + "'");
        };
        channel3.basicConsume(queueName3, true, deliverCallback3, consumerTag -> { });
    }
}