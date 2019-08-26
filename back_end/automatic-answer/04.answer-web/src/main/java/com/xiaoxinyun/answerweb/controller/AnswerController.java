package com.xiaoxinyun.answerweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxinyun.answerserviceinterface.entity.Answer;
import com.xiaoxinyun.answerserviceinterface.service.AnswerService;
import com.xiaoxinyun.answerweb.constants.ExcelConstant;
import com.xiaoxinyun.answerweb.controller.base.BaseController;
import com.xiaoxinyun.commonutil.entity.BaseMessage;
import com.xiaoxinyun.commonutil.utils.CommUtil;
import com.xiaoxinyun.commonutil.utils.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-24 16:35:39
 */

@RestController
@RequestMapping("/answer")
@Slf4j
public class AnswerController extends BaseController {

    @Reference(version = "${dubbo.version}",group = "answer",filter = "com.xiaoxinyun.answerweb.config.dubbo.filter.AddTokenFilter")
    AnswerService answerService;


    @ApiOperation(value = "下载药品信息模板", httpMethod = "POST")
    @RequestMapping(value = "/downTemplate", method = RequestMethod.POST)
    public void downTemplate() {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("HHmmss");
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            String filename = URLEncoder.encode("药品导入模板", "UTF-8") + fmt.format(new Date()) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
            JSONArray jsonArray = JSON.parseArray(ExcelConstant.medicalConfig);
            OutputStream os = response.getOutputStream();
            ExcelUtil.downExcel(os, null, jsonArray);

        } catch (FileNotFoundException e) {
            log.error("下载药品导入模板异常", e);
            e.printStackTrace();
        } catch (IOException e) {
            log.error("下载药品导入模板异常", e);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("下载药品导入模板异常", e);
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "上传药品信息", httpMethod = "POST")
    @RequestMapping(value = "/uploadAnswer", method = RequestMethod.POST)
    public BaseMessage uploadAnswer(
            @ApiParam(value = "file detail") @RequestPart("file") MultipartFile file) {
        BaseMessage baseMessage = new BaseMessage();

        //机构药库导入进度
        Object progress = request.getSession().getAttribute("orgInfoImportProgress");
        int orgInfoImportProgress = 1;// 导入标准药库进度
        if (progress != null) {
            orgInfoImportProgress = (int) progress;
            if (orgInfoImportProgress > 0) {
                baseMessage.initStateAndMessage(200, "当前导入进度：" + orgInfoImportProgress + "%");
                return baseMessage;
            }
        }

        JSONArray jsonArray = JSON.parseArray(ExcelConstant.medicalConfig);
        String Items = request.getParameter("Items");
        JSONArray re = null;

        Integer repeatNum = 0;//重复数量
        Integer successNum = 0;//成功数量
        String msg = "导入失败";


        try {
//          ;
            BaseMessage baseMessage1 = ExcelUtil.excelToJson(file.getInputStream(), jsonArray, 5000);
            if (baseMessage1.getState() == 0) {
                if (baseMessage1.getData() != null) {
                    re = (JSONArray) baseMessage1.getData();
                } else {
                    baseMessage.initStateAndMessage(1010, "数据为空");
                    BaseMessage.error("数据为空");
                }
            } else {
                baseMessage.initStateAndMessage(baseMessage1.getState(), baseMessage1.getMessage());
                return baseMessage;
            }

        } catch (Exception ex) {
            log.error("解析excel异常", ex);
            baseMessage.initErrorMessage(ex);
            return baseMessage;
        } finally {
            request.getSession().removeAttribute("orgInfoImportProgress");
        }
        if (re == null || re.size() == 0) {
            request.getSession().removeAttribute("orgInfoImportProgress");
            baseMessage.initStateAndMessage(1002, "传入数据为空");
            return baseMessage;
        }
        JSONObject object = new JSONObject();
        try {
            List<Answer> savelist = new ArrayList<>();
            List<Answer> uploadMedicalList = JSON.parseArray(re.toJSONString(), Answer.class);

            //去重
            List<String> strList = new ArrayList<>();
            for (Answer answer : uploadMedicalList) {
                String str = JSONObject.toJSONString(answer);
                boolean isExist = false;
                for (String s : strList) {
                    if (CommUtil.isEquals(str, s)) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) savelist.add(answer);
            }

            Integer count = answerService.saveList(savelist);


            baseMessage.initStateAndMessage(0, msg);
            baseMessage.setData("数据总数：" + savelist.size() + " 导入成功数量：" + count);

            request.getSession().setAttribute("orgInfoImportProgress", 100);

        } catch (Exception ex) {
            log.error("导入药品异常", ex);
            baseMessage.initErrorMessage(ex);
        } finally {
            request.getSession().removeAttribute("orgInfoImportProgress");
        }
        return baseMessage;

    }

}
