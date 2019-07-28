package com.xll.baitaner.controller;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.xll.baitaner.entity.WXUserInfo;
import com.xll.baitaner.service.WXUserService;
import com.xll.baitaner.utils.HttpRequest;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.ResponseResult;
import com.xll.baitaner.utils.WXPayConfigImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;

import java.util.Arrays;

/**
 * 微信登录
 */
@Api(value = "微信用户模块controller", description = "微信用户模块操作接口")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class WXUserController {

    private final String TAG = "BAITAN-WXUserController ";

    private WXPayConfigImpl wxConfig;

    private static String session_key;

    @Resource
    private WXUserService wxUserService;

    /**
     * 微信小程序登录流程 todo 登录状态储存，每次后台请求做校验
     *
     * @param code
     * @return
     */
    @ApiOperation(
            value = "微信登录"
    )
    @ApiImplicitParam(name = "code", value = "小程序登录返回的code", required = true, dataType = "String")
    @GetMapping("wxlogin")
    public ResponseResult wxlogin(String code) {
        if (code == null || "".equals(code)) {
            return ResponseResult.result(1, "code值为空", null);
        }

        try {
            wxConfig = WXPayConfigImpl.getInstance();
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + wxConfig.getAppID()
                    + "&secret=" + wxConfig.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
            LogUtils.info(TAG, "微信登录，请求url：" + url);

            String res = HttpRequest.sendRequest(url, "GET", null);
            if (res == null || "".equals(res)) {
                LogUtils.warn(TAG, "微信登录，请求接口返回值为空或null 请求微信失败");
                return ResponseResult.result(1, "请求微信失败：" + res + "--", null);
            }
            LogUtils.info(TAG, "微信登录，请求接口返回值：" + res);

            JSONObject obj = JSONObject.fromObject(res);
            if (obj.containsKey("errcode")) {
                String errcode = obj.get("errcode").toString();
                LogUtils.error(TAG, "微信登录，微信返回的错误码：" + errcode);
                return ResponseResult.result(1, "微信返回的错误码：" + errcode, null);
            } else if (obj.containsKey("session_key")) {
                session_key = obj.get("session_key").toString();
                String openId = obj.get("openid").toString();
                LogUtils.info(TAG, "微信登录成功，返回openId： " + openId);

                /**设置微信用户的unionid**/
                if(obj.containsKey("unionid")){
                    if(wxUserService.isWXUser(openId)){
                        if(wxUserService.getWXUserById(openId).getUnionId() == null){
                            wxUserService.UpdateWXUserUnionid(openId, obj.get("unionid").toString());
                        }
                    }
                }

                return ResponseResult.result(0, "success", openId);
            }
            return ResponseResult.result(0, "请求微信失败：" + res, null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, e.toString(), null);
        }
    }

    /**
     * 微信用户信息数据操作
     *
     * @param userInfo
     * @return
     */
    @ApiOperation(
            value = "微信用户信息提交",
            notes = "微信用户登录后提交用户信息"
    )
    @ApiImplicitParam(name = "userInfo", value = "微信用户信息", required = true, dataType = "WXUserInfo")
    @PostMapping("wxuserinfo")
    public ResponseResult WXUserInfo(WXUserInfo userInfo) {
        String res = wxUserService.wxuserinfo(userInfo);
        return ResponseResult.result(res == null ? 0 : 1, res == null ? "success" : res, null);
    }

    /**
     * 获取微信用户信息
     *
     * @param openId
     * @return
     */
    @ApiOperation(
            value = "获取微信用户信息",
            notes = "获取微信用户信息"
    )
    @ApiImplicitParam(name = "openId", value = "微信用户openId", required = true, dataType = "String")
    @GetMapping("getwxuserinfo")
    public ResponseResult getWXUserInfo(String openId) {
        WXUserInfo userInfo = wxUserService.getWXUserById(openId);
        return ResponseResult.result(0, "success", userInfo != null ? userInfo : null);
    }

    /**
     * 获取微信用户号码
     *
     * @param openId
     * @return
     */
    @ApiOperation(value = "获取微信用户号码", httpMethod = "GET", notes = "获取微信用户号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "encryptedData", value = "微信返回加密数据", required = true, dataType = "string"),
            @ApiImplicitParam(name = "iv", value = "微信返回加密数据", required = true, dataType = "string"),
    })
    @GetMapping("getwxuserphone")
    public ResponseResult getWXUserPhone(String encryptedData, String iv){

        // 加密秘钥
        if (StringUtils.isBlank(session_key)){
            return ResponseResult.result(1, "fail", "session_key 无效");
        }
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        byte[] keyByte = Base64.decode(session_key);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return ResponseResult.result(0, "success", JSONObject.fromObject(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseResult.result(1, "fail", null);
    }
}
