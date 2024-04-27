package com.jiang.springbootinit.bimq;
import com.jiang.springbootinit.common.ErrorCode;
import com.jiang.springbootinit.exception.BusinessException;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqConsumer {
    /**
     * 指定程序监听消息队列和确认消息,固定的模板
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @RabbitListener(queues = {"code_queue"},ackMode = "MANUAL") //手动确认
    private void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receive message: {}", message);
        System.out.println("receive message: " + message);
        try {
            //手动确认消息
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作失败");
        }
    }
}
