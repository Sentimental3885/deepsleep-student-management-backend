package com.deepsleep.infrastructure.sms;

import com.aliyun.credentials.models.Config;
import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.deepsleep.config.AliyunSmsProperties;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunSmsSender implements SmsSender {

    private Client smsClient;
    private final AliyunSmsProperties aliyunSmsProperties;

    private static final String TEMPLATE_PARAM_FORMAT = """
        {
            "code": "%s",
            "min": "5"
        }
    """;

    @PostConstruct
    private void clientInit() throws Exception {
        Config credentialConfig = new Config();
        credentialConfig.setType("access_key");
        credentialConfig.setAccessKeyId(aliyunSmsProperties.accessKeyId());
        credentialConfig.setAccessKeySecret(aliyunSmsProperties.accessKeySecret());
        com.aliyun.credentials.Client credentialClient =
                new com.aliyun.credentials.Client(credentialConfig);

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credentialClient);

        config.endpoint = aliyunSmsProperties.endpoint();
        smsClient = new Client(config);
    }

    @Override
    public void send(String phone, String smsCode) {
        SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = new SendSmsVerifyCodeRequest()
                .setPhoneNumber(phone)
                .setSignName(aliyunSmsProperties.signName())
                .setTemplateCode(aliyunSmsProperties.templateCode())
                .setTemplateParam(String.format(TEMPLATE_PARAM_FORMAT, smsCode));
        RuntimeOptions runtime = new RuntimeOptions();

        try {
            SendSmsVerifyCodeResponse response = smsClient.sendSmsVerifyCodeWithOptions(sendSmsVerifyCodeRequest, runtime);
            if (response == null || response.body == null || !response.body.success) {
                log.error(new com.google.gson.Gson().toJson(response));
                throw new BusinessException(ResultCode.SMS_SEND_FAILED, "阿里云短信验证码发送失败！");
            }
        } catch (TeaException te) {
            log.error("验证码发送失败，错误信息：{}，诊断地址：{}", te.getMessage(), te.getData().get("Recommend"));
            throw new BusinessException(ResultCode.SMS_SEND_FAILED, "阿里云短信验证码发送失败！");
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error("短信验证码发送失败", e);
            throw new BusinessException(ResultCode.SMS_SEND_FAILED, "阿里云短信验证码发送失败！");
        }
    }

}
