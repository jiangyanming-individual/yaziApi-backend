package com.jiang.springbootinit.bimq;

import com.jiang.springbootinit.constant.BiMqConstant;
import com.jiang.springbootinit.constant.CommonConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Bi项目： 只执行一次，创建交换机和队列
 */
public class BiMqInitMain {
    public static void main(String[] argv) throws Exception {
        try {
            //创建链接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String DiRECT_EXCHANGE_NAME= BiMqConstant.BI_EXCHANGE;
            //声明fanout交换机
            channel.exchangeDeclare(DiRECT_EXCHANGE_NAME, "direct");
            //声明一个交换机
            String queueName=BiMqConstant.BI_QUEUE;
            channel.queueDeclare(queueName,true,false,false,null);
            //绑定交换机
            channel.queueBind(queueName, DiRECT_EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY);
        }catch (Exception e){
            e.getStackTrace();
        }
    }
}
