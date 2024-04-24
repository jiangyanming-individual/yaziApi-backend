package com.jiang.springbootinit.controller;
import cn.hutool.core.io.FileUtil;
import com.alibaba.druid.sql.visitor.functions.Char;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.jiang.springbootinit.annotation.AuthCheck;
import com.jiang.springbootinit.common.*;
import com.jiang.springbootinit.constant.CommonConstant;
import com.jiang.springbootinit.constant.FileConstant;
import com.jiang.springbootinit.constant.UserConstant;
import com.jiang.springbootinit.exception.BusinessException;
import com.jiang.springbootinit.exception.ThrowUtils;
import com.jiang.springbootinit.manager.AiManager;
import com.jiang.springbootinit.manager.RedissonLimitRateManager;
import com.jiang.springbootinit.model.dto.chart.*;
import com.jiang.springbootinit.model.dto.file.UploadFileRequest;
import com.jiang.springbootinit.model.dto.post.PostQueryRequest;
import com.jiang.springbootinit.model.entity.Chart;
import com.jiang.springbootinit.model.entity.Post;
import com.jiang.springbootinit.model.entity.User;
import com.jiang.springbootinit.model.enums.FileUploadBizEnum;
import com.jiang.springbootinit.model.vo.BiGenResponse;
import com.jiang.springbootinit.service.ChartService;
import com.jiang.springbootinit.service.UserService;
import com.jiang.springbootinit.utils.ExcelToCsvUtils;
import com.jiang.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import nonapi.io.github.classgraph.utils.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 帖子接口
 *
 */
@RestController
@RequestMapping("/Chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService ChartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedissonLimitRateManager redissonLimitRateManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    private final static Gson GSON = new Gson();
    // region 增删改查
    /**
     * 创建
     *
     * @param ChartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest ChartAddRequest, HttpServletRequest request) {
        if (ChartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart Chart = new Chart();
        BeanUtils.copyProperties(ChartAddRequest, Chart);
        String goal = ChartAddRequest.getGoal();
        String chartData = ChartAddRequest.getChartData();
        String chartType = ChartAddRequest.getChartType();
        //获取当前登录的用户id：
        User loginUser = userService.getLoginUser(request);
        Chart.setUserId(loginUser.getId());
        Chart.setGoal(goal);
        Chart.setChartData(chartData);
        Chart.setChartType(chartType);
        boolean result = ChartService.save(Chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = Chart.getId();

        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = ChartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = ChartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param ChartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest ChartUpdateRequest) {
        if (ChartUpdateRequest == null || ChartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart Chart = new Chart();
        BeanUtils.copyProperties(ChartUpdateRequest, Chart);
        // 参数校验
        long id = ChartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = ChartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = ChartService.updateById(Chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart newChart = ChartService.getById(id);
        if (newChart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(newChart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param ChartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartVOByPage(@RequestBody ChartQueryRequest ChartQueryRequest,
            HttpServletRequest request) {
        long current = ChartQueryRequest.getCurrent();
        long size = ChartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> ChartPage = ChartService.page(new Page<>(current, size),
                getQueryWrapper(ChartQueryRequest));
        return ResultUtils.success(ChartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param ChartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartVOByPage(@RequestBody ChartQueryRequest ChartQueryRequest,
            HttpServletRequest request) {
        if (ChartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        ChartQueryRequest.setUserId(loginUser.getId());
        long current = ChartQueryRequest.getCurrent();
        long size = ChartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> ChartPage = ChartService.page(new Page<>(current, size),
                getQueryWrapper(ChartQueryRequest));
        return ResultUtils.success(ChartPage);
    }

    // endregion
    /**
     * 编辑（用户）
     *
     * @param ChartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest ChartEditRequest, HttpServletRequest request) {
        if (ChartEditRequest == null || ChartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart Chart = new Chart();
        BeanUtils.copyProperties(ChartEditRequest, Chart);
        User loginUser = userService.getLoginUser(request);
        long id = ChartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = ChartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = ChartService.updateById(Chart);
        return ResultUtils.success(result);
    }


    @PostMapping("/generate")
    public BaseResponse<BiGenResponse> geneChart(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) throws IOException {
        String goal = genChartByAiRequest.getGoal();
        String chartName = genChartByAiRequest.getChartName();
        String chartType = genChartByAiRequest.getChartType();
        //获取当前系统登录的用户：
        User loginUser = userService.getLoginUser(request);
        //校验参数：
        ThrowUtils.throwIf(StringUtils.isBlank(goal),ErrorCode.PARAMS_ERROR,"分析目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(chartName)  && chartName.length()>100,
                ErrorCode.PARAMS_ERROR,"图表名称过长");

        // 1.系统预设不用prompt;直接调用现有模型id
        long biModelId = 1659171950288818178L;

        //文件安全校验
        /**
         * 文件大小 1M
         */
        long FILE_MAX_SIZE = 1 * 1024 * 1024L;
        List<String> VALID_FILE_SUFFIX= Arrays.asList("xlsx","xls","csv","json");
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size>FILE_MAX_SIZE,ErrorCode.PARAMS_ERROR,"文件大小超过1M");
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);//获取后缀
        ThrowUtils.throwIf(!VALID_FILE_SUFFIX.contains(suffix),ErrorCode.PARAMS_ERROR,"文件格式不支持");

        // (1)分析需求：
        // 分析网站用户的增长情况

        // (2)原始数据：
        // 日期，用户数
        // 1号，10
        // 2号，20
        // 3号，30

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
        //2.1 压缩内容，转为csv格式
        String csvData = ExcelToCsvUtils.excelToCsv(multipartFile);
        //2.2 拼接内容：
        userInput.append(csvData).append("\n");

        /**
         * 使用调用接口限流：
         */
        redissonLimitRateManager.doLimitRate("genChartByAi_"+loginUser.getId());
        //3.调用ai：
        String res = aiManager.doChat(biModelId, userInput.toString());
        //4.处理AI生成结论
        String[] splits = res.split("【【【【【");
        if (splits.length<3){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"AI生成错误");
        }
        //去掉换行和空格；得到生成分析数据和结论
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        //5. 生成的数据，插入到数据库
        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setChartName(chartName);
        chart.setGenChart(genChart);
        chart.setGetResult(genResult);
        chart.setUserId(loginUser.getId());
        chart.setChartStatus(ChartStatusEnum.SUCCEED_STATUS.getValue());

        boolean save = ChartService.save(chart);
        if (!save){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"插入数据错误");
        }
        BiGenResponse biGenResponse = new BiGenResponse();
        biGenResponse.setChartId(chart.getId());
        biGenResponse.setGenChart(genChart);
        biGenResponse.setGenResult(genResult);

        return ResultUtils.success(biGenResponse);
    }
    /**
     * AI生成异步接口
     * @param multipartFile
     * @param
     * @param request
     * @return
     */
    @PostMapping("/generate/async")
    public BaseResponse<BiGenResponse> geneChart_asyn(@RequestPart("file") MultipartFile multipartFile,
                                                      GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) throws IOException {
        String goal = genChartByAiRequest.getGoal();
        String chartName = genChartByAiRequest.getChartName();
        String chartType = genChartByAiRequest.getChartType();
        //获取当前系统登录的用户：
        User loginUser = userService.getLoginUser(request);
        //校验参数：
        ThrowUtils.throwIf(StringUtils.isBlank(goal),ErrorCode.PARAMS_ERROR,"分析目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(chartName)  && chartName.length()>100,
                ErrorCode.PARAMS_ERROR,"图表名称过长");

        // 1.系统预设不用prompt;直接调用现有模型id
        long biModelId = 1659171950288818178L;
        //文件安全校验
        /**
         * 文件大小 1M
         */
        long FILE_MAX_SIZE = 1 * 1024 * 1024L;
        List<String> VALID_FILE_SUFFIX= Arrays.asList("xlsx","xls","csv","json");
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size>FILE_MAX_SIZE,ErrorCode.PARAMS_ERROR,"文件大小超过1M");
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);//获取后缀
        ThrowUtils.throwIf(!VALID_FILE_SUFFIX.contains(suffix),ErrorCode.PARAMS_ERROR,"文件格式不支持");

        /**
         * 使用调用接口限流：
         */
        redissonLimitRateManager.doLimitRate("genChartByAi_"+loginUser.getId());

        // (1)分析需求：
        // 分析网站用户的增长情况

        // (2)原始数据：
        // 日期，用户数
        // 1号，10
        // 2号，20
        // 3号，30
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
        //2.1 压缩内容，转为csv格式
        String csvData = ExcelToCsvUtils.excelToCsv(multipartFile);
        //2.2 拼接内容：
        userInput.append(csvData).append("\n");

        //异步生成：先将用户的数据插入到数据库
        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setChartName(chartName);
        chart.setUserId(loginUser.getId());
        chart.setChartStatus(ChartStatusEnum.WAIT_STATUS.getValue()); //设置为wait状态
        boolean save = ChartService.save(chart);
        if (!save){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"插入数据错误");
        }

        CompletableFuture.runAsync(()->{
            // 等待-->执行中--> 成功/失败

            //调用AI接口前：
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setChartStatus(ChartStatusEnum.RUNNING_STATUS.getValue());
            boolean updateRes = this.ChartService.updateById(updateChart);
            if (!updateRes){
                handleUpdateChartException(chart.getId(),"更新图表状态-执行中-失败");
                return;
            }
            //调用ai：
            String res = aiManager.doChat(biModelId, userInput.toString());
            //处理AI生成结论
            String[] splits = res.split("【【【【【");
            if (splits.length<3){
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
                handleUpdateChartException(chart.getId(),"图表生成-成功状态-失败");
                return;
            }
        },threadPoolExecutor);

        //返回给前端数据
        BiGenResponse biGenResponse = new BiGenResponse();
        biGenResponse.setChartId(chart.getId()); //返回图表id
        return ResultUtils.success(biGenResponse);
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
    /**
     * queryWrapper封装类：
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String chartName = chartQueryRequest.getChartName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(  id!=null && id >0,"id", id);
        //模糊查询图表名称：
        queryWrapper.like(StringUtils.isNotBlank(chartName),"chartName", chartName);
        queryWrapper.eq(StringUtils.isNotBlank(goal),"goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(goal),"chartType", chartType);
        //isNotEmpty 要把控的更严格
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}
