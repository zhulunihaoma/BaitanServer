package xll.baitaner.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xll.baitaner.service.entity.Sort;
import xll.baitaner.service.service.SortService;
import xll.baitaner.service.utils.ResponseResult;

import java.util.List;

/**
 * 描述：分类模块controller
 * 创建者：XLL
 * 日期：2018/11/26.
 **/
@Api(value = "分类模块controller",
     description = "店铺商品分类模块接口，包括分类创建更新以及顺序变化,注意所有接口的返回数据中都会带有改动后的分类列表数据")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class SortController {

    @Autowired
    private SortService sortService;

    /**
     * 新增分类
     * @param shopId
     * @param sortName
     * @return
     */
    @ApiOperation(
            value = "新增分类接口",
            httpMethod = "POST",
            notes = "新增分类接口,无论成功与否，都会返回实际的分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "sortName", value = "分类名称", required = true, dataType = "String")
    })
    @RequestMapping("addsort")
    public ResponseResult addSort(int shopId, String sortName){
        String result = sortService.addSort(shopId, sortName);
        List<Sort> sortList = sortService.getSortList(shopId);
        if(result == null){
            return ResponseResult.result(0, "success" , sortList);
        }
        else {
            return ResponseResult.result(1, result , sortList);
        }
    }

    /**
     * 更新分类名称
     * @param sort
     * @return
     */
    @ApiOperation(
            value = "更新分类名称接口",
            httpMethod = "POST",
            notes = "更新分类名称接口,无论成功与否，都会返回实际的分类列表")
    @ApiImplicitParam(name = "sort", value = "修改的分类实体类", required = true, dataType = "Sort")
    @RequestMapping("editsortname")
    public ResponseResult updateSortName(Sort sort){
        String result = sortService.updateSortName(sort);
        List<Sort> sortList = sortService.getSortList(sort.getShopId());
        if(result == null){
            return ResponseResult.result(0, "success" , sortList);
        }
        else {
            return ResponseResult.result(1, result , sortList);
        }
    }

    /**
     * 更新分类位置
     * @param sort
     * @param sortOrder
     * @return
     */
    @ApiOperation(
            value = "更新分类位置接口",
            httpMethod = "POST",
            notes = "更新分类位置接口,无论成功与否，都会返回实际的分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sort", value = "修改的分类实体类", required = true, dataType = "Sort"),
            @ApiImplicitParam(name = "order", value = "要移动到的位置，从0开始计数", required = true, dataType = "int")
    })
    @RequestMapping("editsortorder")
    public ResponseResult updateSortOrder(Sort sort, int sortOrder){
        boolean result = sortService.updateSortOrder(sort, sortOrder);
        List<Sort> sortList = sortService.getSortList(sort.getShopId());
        if(result){
            return ResponseResult.result(0, "success" , sortList);
        }
        else {
            return ResponseResult.result(1, "fail" , sortList);
        }
    }

    /**
     * 删除分类
     * @param sort
     * @return
     */
    @ApiOperation(
            value = "删除分类接口",
            httpMethod = "POST",
            notes = "删除分类接口,无论成功与否，都会返回实际的分类列表")
    @ApiImplicitParam(name = "sort", value = "删除的分类实体类", required = true, dataType = "Sort")
    @RequestMapping("deletesort")
    public ResponseResult deleteSort(Sort sort){
        boolean result = sortService.deleteSort(sort);
        List<Sort> sortList = sortService.getSortList(sort.getShopId());
        if(result){
            return ResponseResult.result(0, "success" , sortList);
        }
        else {
            return ResponseResult.result(1, "fail" , sortList);
        }
    }

    /**
     * 按顺序获取分类列表
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取分类列表",
            notes = "获取分类列表, 按order从0递增排列")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getsortlist")
    public ResponseResult getSortList(int shopId){
        return ResponseResult.result(0, "success" , sortService.getSortList(shopId));
    }
}
