package com.xiaoxinyun.commonutil.utils.entity;

import com.alibaba.fastjson.JSONObject;
import com.xiaoxinyun.commonutil.utils.CommUtil;
import com.xiaoxinyun.commonutil.utils.exception.ParamsException;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-24 16:16:26
 */
@Data
public class BaseMessage {

    private Integer state;

    private String message;

    private Object data;

    private Object extra;

    private Object messageDetail;

    public BaseMessage() {
        this.state = 0;
        this.message = "成功";
    }

    public BaseMessage(Integer state, String message) {
        this.state = state;
        this.message = message;
    }


    public BaseMessage(Exception ex) {
        initErrorMessage(ex);
    }


    public BaseMessage initStateAndMessage(Integer state, String message) {
        this.setState(state);
        this.setMessage(message);
        return this;
    }

    //初始化错误消息
    public BaseMessage initErrorMessage(Exception ex) {
        initMessage(this, ex, null, null);
        return this;
    }

    //初始化错误消息
    public BaseMessage initErrorMessage(Exception ex, String message) {
        initMessage(this, ex, null, message);
        return this;
    }

    public BaseMessage initData(Object data) {
        this.setData(data);
        return this;
    }

    public static BaseMessage success(Object data) {
        BaseMessage baseMessage = new BaseMessage();
        baseMessage.setData(data);
        return baseMessage;
    }

    public static BaseMessage success() {
        return new BaseMessage();
    }

    public static BaseMessage error(String message) {
        return error(400, message);
    }

    public static BaseMessage error(int state, String message) {
        BaseMessage baseMessage = new BaseMessage();
        baseMessage.setState(state);
        baseMessage.setMessage(message);
        return baseMessage;
    }

    public static BaseMessage error(Exception ex) {
        BaseMessage baseMessage = new BaseMessage();
        initMessage(baseMessage, ex, 400, ex.getMessage());
        return baseMessage;
    }


    private static void initMessage(BaseMessage baseMessage, Exception ex, Integer state, String message) {

        state = state == null ? 9999 : state;
        message = StringUtils.isEmpty(message) ? "未知异常：内部服务器错误" : message;
        baseMessage.setState(state);
        baseMessage.setMessage(message);

        //ParamsException 表示参数异常
        if (ex != null && !(ex instanceof ParamsException)) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String msg = sw.toString();

                JSONObject jsonObject = new JSONObject();
                if (CommUtil.isNotEmpty(msg)) {
                    String[] list;
                    if (msg.contains("\\r\\n")) {
                        list = msg.split("\\r\\n");
                    } else {
                        list = msg.split("\\n");
                    }
                    for (int i = 0; i < list.length; i++) {
                        if (i > 35) {
                            jsonObject.put(i + "", " ... 35 more");
                            break;
                        }
                        String str = list[i];
                        if (CommUtil.isNotEmpty(str)) {
                            jsonObject.put(i + "", str.replace("\t", " "));
                        }
                    }
                }
                baseMessage.setMessageDetail(jsonObject);
            } catch (Exception e) {
                baseMessage.setMessageDetail("设置详细信息异常");
            }
        }
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        return this.state != null && this.state.intValue() == 0;
    }
}
