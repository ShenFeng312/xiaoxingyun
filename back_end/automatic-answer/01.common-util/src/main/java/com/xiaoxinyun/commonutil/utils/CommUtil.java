package com.xiaoxinyun.commonutil.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mcs on 2018/7/31.
 */
public class CommUtil {

    public static String getGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 格式化字符串 支持常用的格式
     *
     * @param pattern 需要格式化的字符串
     * @param params  格式化参数
     * @return 返回字符串   如 format("hell {0}。", "张三") => "hello 张三。"
     */
    public static String format(String pattern, Object... params) {
        pattern = pattern == null ? "" : pattern;
        if (null == params || params.length == 0) {
            return pattern;
        }
        for (int i = 0; i < params.length; ++i) {
            Object object = params[i];
            String parm = "";
            if (object == null) {
                parm = "";
            } else if (object instanceof Date) {
                parm = formatLongDate(object);
            } else {
                try {
                    parm = String.valueOf(object);
                } catch (Exception ex) {
                }
            }
            if (parm == null || "null".equals(parm)) {
                parm = "";
            }
            pattern = pattern.replace(String.format("{%d}", i), parm);
        }
        return pattern;
    }

    /**
     * 判断字符串为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    /**
     * 判断字符串为非空
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }


    /**
     * isNullOrWhiteSpace
     *
     * @param
     * @return
     */
    public static boolean isNullOrWhiteSpace(String value) {
        return value == null || value.equals("null") || (value.length() >= 0 && value.trim().length() <= 0);
    }

    /**
     * 如果是 Null 返回 空字符串 否则执行String 的trim
     *
     * @param s
     * @return
     */
    public static String trimString(String s) {
        if (s == null) return "";
        return s.trim();
    }


    /**
     * @param
     * @return
     */
    public static <T> T toBean(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * @param
     * @return
     */
    public static <T> T toBean(String text, TypeReference<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * 判断电话号码
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        if (isEmpty(str)) return false;

        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        //String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";

        String regExp = "^1[3456789]\\d{9}$";
        p = Pattern.compile(regExp);
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 生成指定位数的 随机数
     *
     * @param length 随机数长度
     * @return
     */
    public static String getRandomNum(int length) {
        if (length < 1) {
            return "";
        }
        Random random = new Random();
        String randomNum = random.nextInt(10 * length) + "";
        //位数不够则需要补位
        int randLength = randomNum.length();
        if (randLength < length) {
            for (int i = 1; i <= length - randLength; i++)
                randomNum += random.nextInt(10);
        }
        return randomNum;
    }


    /**
     * 比较两个对象(String char 或者 值类型 （如：int Integer Long long）)
     *
     * @param t1
     * @param t2
     * @param <T>
     * @return
     */
    public static <T> boolean isEquals(T t1, T t2) {
        if (t1 == null && t2 == null) {
            return true;
        } else if (t1 != null && t2 != null) {
            return t1.equals(t2);
        } else {
            return false;
        }
    }


    ////////////////////////////////////////////////////格式化函数 begin//////////////////////////////////

    /**
     * 格式化money
     *
     * @param d
     * @return
     */
    public static String formatDoubleHoldTwo(double d) {
        return formatDouble(d, "0.00");
    }

    public static String formatDouble(double d, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        if (isNotEmpty(pattern)) {
            decimalFormat = new DecimalFormat(pattern);
        }
        return decimalFormat.format(d);
    }

    /**
     * 格式化 日期 长日期
     *
     * @param date
     * @return
     */
    public static String formatLongDate(Object date) {
        if (date == null) return "";
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    //短日期
    public static String formatShortDate(Object date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    //自定义格式
    public static String formatDate(Object date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (isNotEmpty(pattern)) {
            dateFormat = new SimpleDateFormat(pattern);
        }

        if (date instanceof Long) {
            Long ldate = (Long) date;
            return dateFormat.format(new Date(ldate));
        } else if (date instanceof Date) {
            return dateFormat.format((Date) date);
        }
        return "";

    }

    /**
     * 其他格式转换为date类型
     *
     * @param date
     * @return
     */
    public static Date toDate(Object date) {
        return toDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date toDate(Object date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (isNotEmpty(pattern)) {
            dateFormat = new SimpleDateFormat(pattern);
        }

        if (date instanceof Long) {
            Long ldate = (Long) date;
            return new Date(ldate);
        } else if (date instanceof Date) {
            return (Date) date;
        } else if (date instanceof String) {

            String dateStr = (String) date;
            if (dateStr.contains("T") && dateStr.length() == "yyyy-MM-ddTHH:mm:ss".length()) {
                dateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
            } else if (dateStr.contains("T") && dateStr.contains("Z")) {
                dateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.sssZ");
            } else if (dateStr.length() == "yyyy-MM-dd".length()) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            } else if (dateStr.length() == "yyyy-MM-dd HH:mm".length()) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            } else if (dateStr.length() == "yy-MM-dd HH:mm".length()) {
                dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
            }

            try {
                return dateFormat.parse((String) date);
            } catch (Exception ex) {
            }
        }
        return null;
    }


    ////////////////////////////////////////////////////格式化函数 end//////////////////////////////////


    /**
     * 读取文件最后指定行数
     *
     * @param fileName
     * @param lines
     * @return
     * @throws IOException
     */
    public static List<String> getLastLines(String fileName, int lines)
            throws IOException {
        List<String> list = new ArrayList<>();
        if (lines < 1 || isEmpty(fileName)) {
            return list;
        }
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(fileName, "r");
            long len = randomAccessFile.length();
            long start = randomAccessFile.getFilePointer();
            long nextend = start + len - 1;
            String line;
            randomAccessFile.seek(nextend);
            int c = -1;
            while (nextend > start) {
                c = randomAccessFile.read();
                if (c == '\n' || c == '\r') {
                    line = randomAccessFile.readLine();
                    if (isNotEmpty(line) && lines > 0) {
                        line = new String(line.getBytes("ISO-8859-1"), "utf-8");
                        list.add(line.replace("\t", " "));
                        lines--;
                    }
                    nextend--;
                }
                nextend--;
                if (lines == 0)
                    break;
                randomAccessFile.seek(nextend);
                if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行
                    line = randomAccessFile.readLine();
                    if (lines == 1)
                        list.add(line);
                }
            }
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
        //逆序
        Collections.reverse(list);
        return list;
    }




    /**
     * 目前只支持 ISO-8859-1  转 utf-8
     *
     * @param str
     * @return
     */
    public static String toUtf8(String str) {
        if (isNotEmpty(str)) {
            try {
                if (java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(str)) {
                    return new String(str.getBytes("ISO-8859-1"), "utf-8");
                }
            } catch (Exception ex) {
            }
        }
        return str;
    }





    public static void main(String[] args) throws IOException {
//        Double dd = 55.22;
//        String aa = CommonUtil.format("{0},{1},{2},{3},{4},{5},{6}", 22, 55L, new Date(), "4444", dd, 666.77f, false);
//        System.out.println(aa);
//
//        for (int i = 0; i < 10; i++) {
//            String random = getRandomNum(18);
//            System.out.println(random);
//        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        List<String> list = getLastLines("C:\\Users\\ucmed\\opt\\logs\\ysfwpt\\info.log.2018-10-22", 10);

        for (String str : list) {
            System.out.println(str);
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        String aa = "{\"ysptDtoStr\":\"{\"appId\":\"457275cdf3d0f301314a900e7e1ff9a0\",\"appValue\":\"o4YUkuM8ubaQFaMySv_wgmrWcuX0\",\"consultId\":\"20181027153930781\",\"consultInfo\":{\"address\":\"æ\u009C\u009Dé\u0098³è·¯36-101å®¤\",\"age\":\"64\",\"allergicHistory\":\"\",\"caProjectCode\":\"\",\"cfPayStatus\":\"\",\"clinicDate\":\"\",\"clinicTime\":\"2018-10-27 15:39:30\",\"conditionDesc\":\"\",\"deptId\":0,\"deptName\":\"é\u0097¨è¯\u008Aæ¶\u0088å\u008C\u0096å\u0086\u0085ç§\u0091\",\"doctorName\":\"å\u0090´å\u0086\u009Bé\u009C\u009E\",\"doctorNumber\":0,\"doctorPhone\":\"\",\"fee\":0,\"hospitalId\":\"ç»\u008Då\u0085´å¸\u0082äººæ°\u0091å\u008C»é\u0099¢\",\"hospitalName\":\"\",\"id\":0,\"idCard\":\"33060219540716001X\",\"patientId\":0,\"patientName\":\"ç\u008E\u008Bç»\u008Då\u0088\u009A\",\"phone\":\"18058418438\",\"regNo\":\"\",\"repulse\":\"\",\"scheduleId\":0,\"sex\":\"ç\u0094·\",\"timeType\":\"\",\"treatmentCard\":\"A03180660\",\"type\":\"\",\"userId\":\"UCMED18092915234627248680\"},\"diagnose\":{\"advice\":\"æ\u0097\u00A0\",\"allergicHistory\":\"æ\u0097\u00A0\",\"complain\":\"\",\"dispose\":\"\",\"firstDiagnose\":\"è¡\u0080ç³\u0096è¿\u0087å¤\u009A/\",\"pastHistory\":\"æ\u0097\u00A0\",\"presentHistory\":\"æ\u0097\u00A0\",\"reservationId\":0},\"hospitalId\":1118041111540620441,\"id\":0,\"oldConsultId\":\"\",\"patientList\":[{\"age\":\"64\",\"hospitalId\":\"1118041111540620441\",\"id\":0,\"idcard\":\"33060219540716001X\",\"name\":\"ç\u008E\u008Bç»\u008Då\u0088\u009A\",\"phone\":\"18058418438\",\"sex\":\"ç\u0094·\",\"treatcard\":\"A03180660\",\"userId\":\"\"}],\"pdf\":null,\"prescriptionNote\":\"\",\"prescriptionType\":2,\"recipes\":[{\"amount\":\"1\",\"amountUnit\":\"ç\u009B\u0092\",\"day\":\"30\",\"decoction\":\"\",\"decoctionMethod\":\"\",\"deptName\":\"é\u0097¨è¯\u008Aæ¶\u0088å\u008C\u0096å\u0086\u0085ç§\u0091\",\"doctorName\":\"\",\"doctorNumber\":\"\",\"dosage\":\"1\",\"dosageUnit\":\"ç\u0089\u0087\",\"emrDrugId\":\"10866\",\"herbals\":[],\"hospitalId\":1118041111540620441,\"id\":\"10866\",\"method\":\"é¤\u0090ä¸­æ\u009C\u008D\",\"name\":\"(ç\u0094²)â\u0096²(æ\u008B\u009Cå\u0094\u0090è\u008B¹)é\u0098¿å\u008D¡æ³¢ç³\u0096ç\u0089\u0087\",\"patientName\":\"ç\u008E\u008Bç»\u008Då\u0088\u009A\",\"phone\":\"18058418438\",\"prescriptionType\":\"1\",\"price\":94,\"reason\":\"\",\"reservationId\":0,\"status\":\"\",\"times\":\"tid\",\"timesState\":\"3æ¬¡/æ\u0097¥\",\"tmp1\":\"2018-10-27 15:39:30\",\"ysName\":\"\",\"ysPhone\":\"\"}],\"unionId\":0,\"userId\":\"\"}\"}";


        System.out.println(toUtf8(aa));

        aa = "哈哈哈";
        System.out.println(toUtf8(aa));


    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
