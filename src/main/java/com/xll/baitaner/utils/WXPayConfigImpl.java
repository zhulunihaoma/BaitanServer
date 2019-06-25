package com.xll.baitaner.utils;

import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.util.ClassUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 描述：微信支付配置
 * 创建者：xie
 * 日期：2017/11/14
 **/
public class WXPayConfigImpl implements WXPayConfig {

    private static WXPayConfigImpl INSTANCE;


    public static WXPayConfigImpl getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized (WXPayConfigImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WXPayConfigImpl();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 小程序AppID --小仙女
     * @return
     */
//    public String getAppID() {
//        return "wxa2093db3cd2a5828";
//    }
//
//    public String getAppSecret(){return "fc19d202cf86fb5ea0150c0a4847a6db";}


    /**
     * 小程序AppID --新摆摊小程序
     *
     * @return
     */
    public String getAppID() {
        return "wxdea72e3ef1d40e45";
    }

    public String getAppSecret() {
        return "381b235396907b0519127e13370918ad";
    }

    /**
     * 商户号
     *
     * @return
     */
    public String getMchID() {
        return "1491923852";
    }

    /**
     * API 秘钥
     *
     * @return
     */
    public String getKey() {
        return "49D0jW58OnLnwKPovzyjg6wcXa5MFrSD";
    }


    private byte[] certData;

    public WXPayConfigImpl() throws Exception {
        String certPath = ClassUtils.getDefaultClassLoader().getResource("").getPath()
                + "../../../../webapps/cert/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    /**
     * 证书内容
     *
     * @return
     */
    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }


    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}