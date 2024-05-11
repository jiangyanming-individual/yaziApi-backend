package com.jiang.springbootinit.manager;

import com.jiang.springbootinit.common.ErrorCode;
import com.jiang.springbootinit.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Lenovo
 * @date 2024/4/19
 * @time 10:53
 * @project yaziApi-backend
 **/
@Component
public class AiManager {

    @Resource
    private YuCongMingClient yuCongMingClient;

    public String doChat(Long modelId,String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);//鱼聪明的id
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"返回为空");
        }
        //返回数据内容：
        return response.getData().getContent();

    }
}
