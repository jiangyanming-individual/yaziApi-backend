package com.jiang.springbootinit.bimq;
import com.jiang.springbootinit.common.ChartStatusEnum;
import com.jiang.springbootinit.common.ErrorCode;
import com.jiang.springbootinit.constant.BiMqConstant;
import com.jiang.springbootinit.constant.CommonConstant;
import com.jiang.springbootinit.exception.BusinessException;
import com.jiang.springbootinit.manager.AiManager;
import com.jiang.springbootinit.model.entity.Chart;
import com.jiang.springbootinit.service.ChartService;
import com.jiang.springbootinit.utils.ExcelToCsvUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class BiMqConsumer {

    @Resource
    private ChartService ChartService;


    @Resource
    private AiManager aiManager;

    /**
     * 指定程序监听消息队列和确认消息,固定的模板
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE},ackMode = "MANUAL") //手动确认
    private void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("receive message: {}", message);

        if (StringUtils.isBlank(message)) {
            // 消息为空，则拒绝掉消息
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接受到的消息为空");
        }

        long chartId= Long.parseLong(message);
        Chart chart = ChartService.getById(chartId);
        if (chart == null){
            // 消息为空，则拒绝掉消息
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图表为空");
        }
        // 等待-->执行中--> 成功/失败
        //调用AI接口前：
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setChartStatus(ChartStatusEnum.RUNNING_STATUS.getValue());
        boolean updateRes = this.ChartService.updateById(updateChart);
        if (!updateRes){
            channel.basicNack(deliveryTag, false, false);
            handleUpdateChartException(chart.getId(),"更新图表状态-执行中-失败");
            return;
        }
        //调用ai：
        String res = aiManager.doChat(BiMqConstant.BI_MODEL_ID, buildUserInPut(chart));
        //处理AI生成结论
        String[] splits = res.split("【【【【【");
        if (splits.length<3){
            channel.basicNack(deliveryTag, false, false);
            handleUpdateChartException(chart.getId(),"AI生成失败");
        }
        //去掉换行和空格；得到生成分析数据和结论
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        //生成后，报错AI的结果：
        Chart saveChart = new Chart();
        saveChart.setId(chart.getId());
        saveChart.setGenChart(genChart);
        saveChart.setGetResult(genResult);
        saveChart.setChartStatus(ChartStatusEnum.SUCCEED_STATUS.getValue());

        boolean saveRes = ChartService.updateById(saveChart);
        if (!saveRes){
            channel.basicNack(deliveryTag, false, false);
            handleUpdateChartException(chart.getId(),"图表生成-成功状态-失败");
            return;
        }
    }


    /**
     * 获取ai输入数据：
     * @param chart
     * @return
     */
    public String buildUserInPut(Chart chart){
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String chartData = chart.getChartData();
        //用户输入：
        StringBuilder userInput = new StringBuilder(); //线程不安全，但效率高
        //1.分析目标
        userInput.append("分析需求：").append("\n");
        String userGoal=goal;
        if (StringUtils.isNotBlank(chartType)){
            userGoal+= "请使用,"+chartType;
        }
        userInput.append(userGoal).append("\n");
        //2. 原始数据：：
        userInput.append("原始数据：").append("\n");
        //2.2 拼接内容：
        userInput.append(chartData).append("\n");
        return userInput.toString();
    }

    /**
     * 处理图表更新失败的方法
     * @param chartId
     * @param execMessage
     */
    public void handleUpdateChartException(long chartId, String execMessage){

        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setChartStatus(ChartStatusEnum.FAILED_STATUS.getValue());//设置为失败
        updateChart.setExecMessage(execMessage);
        boolean updateRes = ChartService.updateById(updateChart);
        if (!updateRes){
            //打印日志：
            log.info("更新图表失败状态失败："+chartId+","+execMessage);
        }
    }

}
