package com.jiang.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.Scanner;

/**
 * 死信生产者
 */
public class DlxDirectProductor {

    //业务滴交换机
    private static final String WORK_DiRECT_EXCHANGE_NAME = "direct2-exchange";
    //死信交换机
    private static final String DLX_EXCHANGE_NAME = "dlx-exchange";
    public static void main(String[] argv) throws Exception {
        //创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(DLX_EXCHANGE_NAME, "direct"); //声明死信交换机
            //创建老板的死信队列，heihei队列完不成，就交给laoban
            String queueName1 = "laoban_queue";
            channel.queueDeclare(queueName1,true,false,false,null);
            //绑定死信交换机：
            channel.queueBind(queueName1, DLX_EXCHANGE_NAME, "laoban");

            //创建外包的死信队列,haha队列完不成，就交给laoban
            String queueName2 = "waibao_queue";
            channel.queueDeclare(queueName2,true,false,false,null);
            //绑定死信交换机
            channel.queueBind(queueName2, DLX_EXCHANGE_NAME, "waibao");

            //老板队列监听队列
            DeliverCallback laobandeliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [laoban] Received '"+ delivery.getEnvelope().getRoutingKey()+" " + message + "'");
            };
            //老板队列消费消息：
            channel.basicConsume(queueName1, false, laobandeliverCallback, consumerTag -> { });

            //外包队列监听队列
            DeliverCallback waibaodeliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [waibao] Received '"+ delivery.getEnvelope().getRoutingKey()+" " + message + "'");
            };
            //外包队列消费消息：
            channel.basicConsume(queueName2, false, waibaodeliverCallback, consumerTag -> { });

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String message = scanner.nextLine();

                String[] splits= message.split(" ");
                message = splits[0];
                String routingKey = splits[1];

                System.out.println("message: "+ message + " " + "routingKey: "+ routingKey);
                //发布订阅：
                channel.basicPublish(WORK_DiRECT_EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [directProductor] Sent '" + message + "'" + "with routingKey: " + routingKey);
            }
        }
    }
}
