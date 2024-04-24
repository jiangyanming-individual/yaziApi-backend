package com.jiang.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
//import javafx.scene.transform.Shear;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @date 2024/4/18
 * @time 20:50
 * @project springboot-init
 **/
@Slf4j
public class ExcelToCsvUtils {

    public static String excelToCsv(MultipartFile multipartFile) throws IOException {

        List<Map<Integer, String>> list = null;
        try {
            //取出数据
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (CollUtil.isEmpty(list)){
            return "";
        }
        //转为csv：
        StringBuilder stringBuilder = new StringBuilder();
        // 1.取出表头：
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap)list.get(0);
        List<String> headerList = headerMap.values().
                stream().filter(ObjectUtil::isNotEmpty).
                collect(Collectors.toList());
        //2.拼接表头：
        stringBuilder.append(StringUtils.join(headerList,',')).append("\n");
        //3.取出表的内容：
        for (int i=1;i<list.size();i++){
            LinkedHashMap<Integer, String> eachRowMap = (LinkedHashMap)list.get(i);
            List<String> dataList = eachRowMap.values().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList,",")).append("\n");
        }

        System.out.println(stringBuilder.toString());
        return stringBuilder.toString(); //返回数据
    }


    public static String excelToCsv() throws IOException {
        File file=null;
        List<Map<Integer, String>> list = null;
        try {
            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
            //取出数据
            list = EasyExcel.read(file)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (CollUtil.isEmpty(list)){
            return "";
        }
        //转为csv：
        StringBuilder stringBuilder = new StringBuilder();
        // 1.取出表头：
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap)list.get(0);
        List<String> headerList = headerMap.values().
                stream().filter(ObjectUtil::isNotEmpty).
                collect(Collectors.toList());
        //2.拼接表头：
        stringBuilder.append(StringUtils.join(headerList,',')).append("\n");
        //3.取出表的内容：
        for (int i=1;i<list.size();i++){
            LinkedHashMap<Integer, String> eachRowMap = (LinkedHashMap)list.get(i);
            List<String> dataList = eachRowMap.values().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList,",")).append("\n");
        }
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString(); //返回数据
    }
    public static void main(String[] args) throws IOException {
        excelToCsv();
    }
}
