package xll.baitaner.service.utils;

import com.github.wxpay.sdk.WXPayConfig;

import java.io.InputStream;

/**
 * 描述：微信支付配置
 * 创建者：xie
 * 日期：2017/11/14
 **/
public class WXPayConfigImpl implements WXPayConfig {

    private static WXPayConfigImpl INSTANCE;


    public static WXPayConfigImpl getInstance() throws Exception{
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
     * 小程序AppID
     * @return
     */
    public String getAppID() {
        return "wxa2093db3cd2a5828";
    }

    /**
     * 商户号
     * @return
     */
    public String getMchID() {
        return "1491923852";
    }

    /**
     * API 秘钥
     * @return
     */
    public String getKey() {
        return "49D0jW58OnLnwKPovzyjg6wcXa5MFrSD";
    }


    /**
     * 证书内容
     * @return
     */
    @Override
    public InputStream getCertStream() {
        return null;
    }


    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}