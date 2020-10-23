package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName AlipayConfig
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/22
 * @Version V1.0
 **/
public class AlipayConfig {

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600766903";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC+GddnFecJr5Qi33YwvmGtSTT3Q4DJd6SHOdDKVLfkYpyHzQdEqoBJE0nuGdvf2KbOfWc6/lfDB8a5sntXAisWcVV8leMeJ+sEvgC/Xyv9jXBfW8Ft9n7bt+J9UmY7DwhtbUzh+mE/PZAz0jyXu2riw36L9nPgpzMND1NNC8qmWIQfciSmvzMKGtTR0XthgFPGENn6BeV5FKDmxHAJK8bZdjzyAM4DOa5jhlTJNclRAtxHSz6eLnQ9uT4CF728bP77w0EbkxGaY0UKZJhpg9yW0uKbcSb1/eTKXW6GXWwyscDbdiSXHZXh95lVSJqLyAjm69EjAL98QXR+Rpmgdf/PAgMBAAECggEAXCU8gwxb1BcWcBzvNktiH1Pn558B0yY6Sw3cRebwyDSAeVcJmeAnlMRTxqUkLgnfOSISIsdclP209xgrrw4vAo7nCB/DWj+xLLhItnqUYS/o82APj5S3AxvMHkENqrzrsdcUOElcUAIwXt4eXtIKymZGdeN9YbzKAx+VmjgH/777kR/9HZ1UJdd7xazGvs1n1rsxQBwQg2XBRBIKCIJLhHZMDbfvw7vZaNkpe9ibdFnam8SfBNSLgv6+b7cmq92BwPbssuABOOmhWPbS9zlYuOsx44ZfPOn7RtvfpEqc418b2YwkOeP9x4Nl96BB0kK9HBQhTO2yZpklbwUMdiaieQKBgQDwKljjhAiw2P/0kpGvpERKxFMc0jVnyfIPTEQpAQhfcF62Nfnb4GOJ95yGYZblLYGSFqLxRNznHnR6iValFcjn31GtTEqDPIF5pDhn9S2KwPiWwlDMZGdShulYU8FT9BwbTekG0TBub2t6JY5ioOCWPuiWF32JAj2SdNe24UeQowKBgQDKonoDVjhspkwX6kYquN+hFLw3SEfBHYt4pa8fKT2eXVX19dDkdIGY9/PP/gTfDdMgQXlaoD8aKSeQQU5MHQ/SXeYZuvxqvW9X1ThFEXJXEkTwNQaC8sxYaVoqfPUBMFAKx984TVOXMWx6xepI6s+ZaXWQaPoj3ePXZecPl3vK5QKBgQCqu5AhffWnszySo7fKA59A/7yOGqo4tInOcktqUv+eyYWrR0fvFd/tbwfpSGs7VVRthIYluuzHqRDpI8Vi6s++dzXbWwcFsPfif+pRjSzpHTIcCtvh0ebZ9mrgjfo8MqfgiUyFuiUR/UeMuDI2z2OZq/Mw8FYgFmWrPkE0E4cKvQKBgEfaW1epkDJ+uYbVbPIjQjE1UBRuTftdhaJPZgyvrAjm4d9ovhz0bywV6F+9nG6X0WCjtut63UViiIPvlFsLBxw6bah8FJuo9r13fw+Gwzu637UpqFhwziMfoHhmfN5iCk/4Vd/6E4UOYoAahBw9H0YwDh8aCnEDcpJjsQ5nUd89AoGBAMWb6poiF9JGZKKa0pFe0ks9P9QpoXO7deVk291bsLi4Xi3acg6085Ep0WN0hOa3Lj898T4/+oCJN1lVrqQs8aXeXI5ISWE/CRx0A1Edo4S02Hjfzbdxbf5tF5C2KvoGFl33pU1hobt4Cl4XXtGTtXPuO2b8Em9N6SsL34mOHB98";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnWFvTA+ru50jKnk9PnRHFDpYn9zIADJM48ij78LSxiBt7elYABj7LSzgHcijYXOKHDHWUsOnadqXgyNxJDB9Iop1iPg+qhXPRwHOSLPZoGSatK0feNKxbIkjKeyFQP5+mKDdIpowp7CATECrNwV1UXhDGH68DiV0+TLor5w1Q2EG36+TRYFXkOQQ1WRfXFjA+tJLYDFt7lrpN2I4Vi0zYhO7I0uEpzf/SpT0V122qPpLpCGF/tUDTuF/aUAtk0bEkHjeqc+2FeIOG35f+ZdT+z/LSfC+n0xKvUo2dl6eaLzKgRUmu/vQtcHzou7d9bVpj2NxsDI1Hvn9UaTgcMr54QIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/pay/returnNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，内网也可以正常访问
    public static String return_url = "http://localhost:8900/pay/returnURL";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
