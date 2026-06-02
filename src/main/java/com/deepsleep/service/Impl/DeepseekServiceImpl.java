package com.deepsleep.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepsleep.config.DeepseekProperties;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.service.DeepseekService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeepseekServiceImpl implements DeepseekService {

    private final DeepseekProperties properties;
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(120)).build();


    @Override
    public String chat(String systemPrompt, String userMessage) {
        JSONArray messages = new JSONArray();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", properties.getModel());
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", properties.getMaxTokens());
        requestBody.put("stream", false);//选择不使用SSE流式输出

        Request request = new Request.Builder()
                .url(properties.getUrl())
                .header("Authorization", "Bearer " + properties.getKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(
                        requestBody.toJSONString(),
                        MediaType.get("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("DeepSeek API 请求失败: {}", response.code());
                throw new BusinessException(ResultCode.AI_SERVICE_ERROR);
            }

            String responseBody = response.body().string();
            JSONObject result = JSON.parseObject(responseBody);
            return result.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (IOException e) {
            log.error("DeepSeek API 调用异常", e);
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR);
        }
    }
}
