package xll.baitaner.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xll.baitaner.service.entity.WXUserInfo;
import xll.baitaner.service.service.WXUserService;
import xll.baitaner.service.utils.HttpRequest;
import xll.baitaner.service.utils.LogUtils;
import xll.baitaner.service.utils.ResponseResult;
import xll.baitaner.service.utils.WXPayConfigImpl;

/**
 * 微信登录
 */
@Api(value = "微信用户模块controller", description = "微信用户模块操作接口")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class WXUserController {

    private String TAG = "BAITAN-WXUserController ";

    private WXPayConfigImpl wxConfig;

    @Autowired
    private WXUserService wxUserService;

    /**
     * 微信小程序登录流程 todo 登录状态储存，每次后台请求做校验
     * @param code
     * @return
     */
    @ApiOperation(
            value = "微信登录"
    )
    @ApiImplicitParam(name = "code", value = "小程序登录返回的code", required = true, dataType = "String")
    @GetMapping("wxlogin")
    public ResponseResult wxlogin(String code){
        if(code == null || "".equals(code)){
            return ResponseResult.result(1, "code值为空",null);
        }

        try {
            wxConfig = WXPayConfigImpl.getInstance();
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + wxConfig.getAppID()
                    + "&secret=" + wxConfig.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
            LogUtils.info(TAG, "微信登录，请求url：" + url);

            String res = HttpRequest.sendRequest(url, "GET", null);
            if (res == null || "".equals(res)) {
                LogUtils.warn(TAG, "微信登录，请求接口返回值为空或null 请求微信失败");
                return ResponseResult.result(1, "请求微信失败：" + res + "--",null);
            }
            LogUtils.info(TAG, "微信登录，请求接口返回值：" + res);

            JSONObject obj = JSONObject.fromObject(res);
            if (obj.containsKey("errcode")) {
                String errcode = obj.get("errcode").toString();
                LogUtils.error(TAG, "微信登录，微信返回的错误码：" + errcode);
                return ResponseResult.result(1, "微信返回的错误码：" + errcode,null);
            } else if (obj.containsKey("session_key")) {
                String openId = obj.get("openid").toString();
                LogUtils.info(TAG, "微信登录成功，返回openId： " + openId);

                return ResponseResult.result(0, "success",openId);
            }
            return ResponseResult.result(0, "请求微信失败：" + res,null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.result(1, e.toString(),null);
        }
    }

    /**
     * 微信用户信息数据操作
     * @param userInfo
     * @return
     */
    @ApiOperation(
            value = "微信用户信息提交",
            notes = "微信用户登录后提交用户信息"
    )
    @ApiImplicitParam(name = "userInfo", value = "微信用户信息", required = true, dataType = "WXUserInfo")
    @PostMapping("wxuserinfo")
    public ResponseResult WXUserInfo(WXUserInfo userInfo){
        String res = wxUserService.wxuserinfo(userInfo);
        return ResponseResult.result(res == null ? 0 : 1, res == null ? "success" : res, null);
    }

    /**
     * 获取微信用户信息
     * @param userInfo
     * @return
     */
    @ApiOperation(
            value = "获取微信用户信息",
            notes = "获取微信用户信息"
    )
    @ApiImplicitParam(name = "openId", value = "微信用户openId", required = true, dataType = "String")
    @GetMapping("getwxuserinfo")
    public ResponseResult getWXUserInfo(String openId){
        WXUserInfo userInfo = wxUserService.getWXUserById(openId);
        return ResponseResult.result( 0 ,"success", userInfo != null ? userInfo : null);
    }
}
