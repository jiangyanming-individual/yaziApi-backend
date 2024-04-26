package com.jiang.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class DirectProductor {

    private static final String DiRECT_EXCHANGE_NAME = "direct-exchange";

    public static void main(String[] argv) throws Exception {
        //创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //声明fanout交换机
            channel.exchangeDeclare(DiRECT_EXCHANGE_NAME, "direct");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String message = scanner.nextLine();

                String[] splits= message.split(" ");
                message = splits[0];
                String routingKey = splits[1];

                System.out.println("message: "+ message + " " + "routingKey: "+ routingKey);
                //发布订阅：
                channel.basicPublish(DiRECT_EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [directProductor] Sent '" + message + "'" + "with routingKey: " + routingKey);
            }
        }
    }
}
