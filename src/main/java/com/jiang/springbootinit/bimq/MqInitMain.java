package com.jiang.springbootinit.bimq;

import com.jiang.springbootinit.mq.DirectProductor;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 只执行一次，创建交换机和队列
 */
public class MqInitMain {
    public static void main(String[] argv) throws Exception {
        try {
            //创建链接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String DiRECT_EXCHANGE_NAME= "code_exchange";
            //声明fanout交换机
            channel.exchangeDeclare(DiRECT_EXCHANGE_NAME, "direct");
            //声明一个交换机
            String queueName="code_queue";
            channel.queueDeclare(queueName,true,false,false,null);
            //绑定交换机
            channel.queueBind(queueName, DiRECT_EXCHANGE_NAME,"code_routingKey");
        }catch (Exception e){
            e.getStackTrace();
        }
    }
}
