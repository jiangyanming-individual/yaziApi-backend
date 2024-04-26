package com.jiang.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "task_queue";
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        //创建了两个消费者
        for (int i = 0; i < 2; i++) {
            //创建两个消费者
            final Channel channel = connection.createChannel();
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            //当前消费者的消费能力：
            channel.basicQos(1);
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                try {
                    //处理消息：
                    System.out.println(" [consumer] Received 消费者" + +finalI+ "：消费了"+ message + " ");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                    Thread.sleep(10000); //模拟处理消息需要的时间；
                }catch (Exception e){
                    e.getStackTrace();
                    //拒绝掉消息：
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
                } finally {
                    System.out.println(" [x] Done");
                    //确认当前消息被处理过了
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            //真正消费消息：
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        }
    }

}