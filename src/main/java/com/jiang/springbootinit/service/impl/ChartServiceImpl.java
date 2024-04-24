package com.jiang.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.springbootinit.mapper.ChartMapper;
import com.jiang.springbootinit.model.entity.Chart;
import com.jiang.springbootinit.service.ChartService;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-04-16 20:09:36
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




