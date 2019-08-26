package com.xiaoxinyun.answerweb.constants;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-24 16:24:36
 */
public class ExcelConstant {

    private String questionTitle;
    private String questionDesc;
    private String keyWord;
    private String answer;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;

    //药品模板配置（标准库和 机构药库统一 机构药库比标准药库多两个字段（价格和 配送方式））
    public static final String medicalConfig =
            "[{excel_field:'deptName',title:'科室名称'}," +
                    "{excel_field:'diseaseName',title:'疾病名称'}," +
                    "{excel_field:'questionTitle',title:'问题标题'}," +
                    "{excel_field:'questionDesc',title:'问题描述'}," +
                    "{excel_field:'keyWord',title:'关键字(多个用,隔开)'}," +
                    "{excel_field:'answer',title:'答案'}," +
                    "{excel_field:'answer1',title:'相似描述1'}," +
                    "{excel_field:'answer2',title:'相似描述2'}," +
                    "{excel_field:'answer3',title:'相似描述3'}," +
                    "{excel_field:'answer4',title:'相似描述4'}," +
                    "{excel_field:'answer5',title:'相似描述5'}" +


                    "]";
}
