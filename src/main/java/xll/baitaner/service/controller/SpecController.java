package xll.baitaner.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xll.baitaner.service.entity.Spec;
import xll.baitaner.service.service.SpecService;
import xll.baitaner.service.utils.ResponseResult;


@Api(value = "商品规格模块controller", description = "商品规格模块接口，包括规格新增、修改和删除")
@RestController
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 新增商品规格接口
     * @param spec
     * @return
     */
    @ApiOperation(
            value = "新增商品规格接口",
            httpMethod = "POST",
            notes = "新增商品规格接口，新增商品时无商品id，则不填commodityId，返回规格id，新增商品时再一并提交")
    @ApiImplicitParam(name = "spec", value = "商品规格实体类", required = true, dataType = "Spec")
    @RequestMapping("addspec")
    public ResponseResult addSpec(Spec spec){
        int res = specService.addSpec(spec);
        if (res != -1)
            return ResponseResult.result(0, "success" , res);
        else
            return ResponseResult.result(1, "fail" , null);
    }

    /**
     * 更新规格接口
     * @param spec
     * @return
     */
    @ApiOperation(
            value = "更新规格",
            httpMethod = "POST",
            notes = "更新规格接口"
    )
    @ApiImplicitParam(name = "spec", value = "商品规格实体类", required = true, dataType = "Spec")
    @RequestMapping("updatspec")
    public ResponseResult updateSpec(Spec spec){
        String res = specService.updateSpec(spec);
        return ResponseResult.result(res == null ? 0 : 1, res == null ? "success" : "fail", res);
    }

    /**
     * 删除规格接口
     * @param specId
     * @return
     */
    @ApiOperation(
            value = "删除规格",
            httpMethod = "GET",
            notes = "删除规格接口"
    )
    @ApiImplicitParam(name = "specId", value = "商品规格id", required = true, dataType = "int")
    @RequestMapping("deletespec")
    public ResponseResult deleteSpec(int specId){
        String res = specService.deleteSpec(specId);
        return ResponseResult.result(res == null ? 0 : 1, res == null ? "success" : "fail", res);
    }
}
