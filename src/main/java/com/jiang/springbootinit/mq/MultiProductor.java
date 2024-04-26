package com.jiang.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class MultiProductor {

    private static final String TASK_QUEUE_NAME = "task_queue";
    public static void main(String[] argv) throws Exception {
        //创建工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             //创建链接：
             Channel channel = connection.createChannel()) {
            //创建队列，开启队列持久：durable: true
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            Scanner scanner = new Scanner(System.in);
            System.out.println("请发送消息：");
            while (scanner.hasNext()){

                String message = scanner.nextLine();
                //开启消息持久化
                channel.basicPublish("", TASK_QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes("UTF-8"));
                System.out.println(" [productor] Sent '" + message + "'");
            }
        }
    }

}