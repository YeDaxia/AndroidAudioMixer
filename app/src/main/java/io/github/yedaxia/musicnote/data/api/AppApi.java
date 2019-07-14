package io.github.yedaxia.musicnote.data.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.github.yedaxia.musicnote.data.entity.ApiResult;
import io.github.yedaxia.musicnote.util.http.DHttpRequest;
import io.github.yedaxia.musicnote.util.http.DHttpResponse;
import io.github.yedaxia.musicnote.util.http.DHttpUtils;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/12.
 */

public class AppApi {

    /**
     * 检查改设备是否已经被激活
     * @return
     */
    public boolean checkActivate(String deviceId) throws IOException{
        String apiUrl = Urls.getCheckActivateUrl() + "?deviceId="+deviceId;
        DHttpResponse response = DHttpUtils.httpGet(apiUrl);
        JSONObject jsonObject = JSON.parseObject(response.streamAsString());
        return jsonObject.getBoolean("data");
    }

    /**
     * 验票
     * @param code
     * @param activityId
     * @param validCode
     * @return
     */
    public ApiResult validTicket(String code, String activityId, String validCode) throws IOException{
        DHttpRequest request = new DHttpRequest();
        request.setUrl(Urls.getValidTicketUrl());
        Map<String, String> params = new HashMap<>();
        params.put("activityId", activityId);
        params.put("code", code);
        params.put("validCode", validCode);
        request.setParams(params);
        DHttpResponse response = DHttpUtils.httpPost(request);
        return JSON.parseObject(response.streamAsString(), ApiResult.class);
    }
}
