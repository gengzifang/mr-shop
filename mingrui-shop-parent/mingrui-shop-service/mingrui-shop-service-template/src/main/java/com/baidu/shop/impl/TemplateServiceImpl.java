package com.baidu.shop.impl;

import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.feign.BrandFeign;
import com.baidu.feign.CategoryFeign;
import com.baidu.feign.GoodsFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.shop.service.TemplateService;
import com.baidu.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/25
 * @Version V1.0
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;

    @Autowired
    private TemplateEngine templateEngine;


    @Override
    public Result<JsonObject> delHTMLBySpuId(Integer spuId) {

        //文件路径  文件名 后缀
        File file = new File(staticHTMLPath + File.separator + spuId + ".html");

        if(!file.delete()){
            return this.setResultError("文件删除失败");
        }

        return this.setResultSuccess();
    }


    @Override
    public Result<JsonObject> createStaticHTMLTemplate(Integer spuId) {

        //也就是说我们现在可以创建上下文了
        Map<String, Object> map = this.getPageInfoBySpuId(spuId);
        //创建模板引擎上下文
        Context context = new Context();
        //将所有准备的数据放到模板中
        context.setVariables(map);

        //main-->主线程
        //创建文件 param1:文件路径 param2:文件名称
        File file = new File(staticHTMLPath, spuId + ".html");
        //构建文件输出流
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }



        return this.setResultSuccess();
    }

    @Override
    public Result<JsonObject> initStaticHTMLTemplate() {

        //准备数据
        //将所有的商品都生成静态模板
        //多少个spu的数据就生成多少个模板

        //获取所有的spu数据
        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(new SpuDTO());
        if (spuInfoResult.getCode() == 200) {

            List<SpuDTO> spuDTOList = spuInfoResult.getData();
            spuDTOList.stream().forEach(spuDTO -> {
                //可以创建上下文了
                Map<String, Object> map = this.getPageInfoBySpuId(spuDTO.getId());
                //模板引擎上下文
                Context context = new Context();
                //将准备的所有数据放入到模板中
                context.setVariables(map);

                //创建文件 param1:文件路径 param2:文件名称
                File file = new File(staticHTMLPath, spuDTO.getId() + ".html");
                //创建文件输出流
                PrintWriter writer = null;

                try {
                    writer = new PrintWriter(file, "UTF-8");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } finally {

                    //根据模板生成静态文件
                    //item:模板名称 context:模板上下文[上下文中包含了需要填充的数据],writer:文件输出流
                    templateEngine.process("item",context,writer);

                    writer.close();
                }



            });

        }

        return this.setResultSuccess();
    }



    private Map<String, Object> getPageInfoBySpuId(Integer spuId) {

        Map<String, Object> map = new HashMap<>();

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);

        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(spuDTO);

        if (spuInfoResult.getCode() == 200) {
            if (spuInfoResult.getData().size() == 1) {

                SpuDTO spuInfo = spuInfoResult.getData().get(0);
                map.put("spuInfo", spuInfo);

                //查询分类信息
                List<String> cateCids = Arrays.asList(spuInfo.getCid1() + "", spuInfo.getCid2() + "", spuInfo.getCid3() + "");
                String cateCidString = String.join(",", cateCids);
                Result<List<CategoryEntity>> cateResult = categoryFeign.getCateByIds(cateCidString);
                if (cateResult.getCode() == 200) {
                    map.put("cateList", cateResult.getData());
                }

                //查询spuDetail信息
                Result<SpuDetailEntity> spuDetailBydSpu = goodsFeign.getSpuDetailBydSpu(spuId);
                if (spuDetailBydSpu.getCode() == 200) {
                    SpuDetailEntity spuDetailInfo = spuDetailBydSpu.getData();
                    map.put("spuDetailInfo", spuDetailInfo);
                }

                //查询特有规格参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spuInfo.getCid3());
                specParamDTO.setGeneric(false);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
                if (specParamResult.getCode() == 200) {
                    HashMap<Integer, String> specMap = new HashMap<>();
                    specParamResult.getData().stream().forEach(spec -> {
                        specMap.put(spec.getId(), spec.getName());
                        map.put("specParamMap", specMap);
                    });
                }

                //查询规格组和规格参数
                SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                specGroupDTO.setCid(spuInfo.getCid3());
                Result<List<SpecGroupEntity>> sepcGroupResult = specificationFeign.getSepcGroupInfo(specGroupDTO);
                if (sepcGroupResult.getCode() == 200) {
                    List<SpecGroupEntity> specGroupdata = sepcGroupResult.getData();

                    //规格组和规格参数
                    List<SpecGroupDTO> specGroupDTOList = specGroupdata.stream().map(specGroup -> {

                        SpecGroupDTO sgd = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);
                        //规格参数  通用参数
                        SpecParamDTO specParamDTO1 = new SpecParamDTO();
                        specParamDTO1.setGroupId(sgd.getId());
                        specParamDTO1.setGeneric(true);
                        Result<List<SpecParamEntity>> specParamResult1 = specificationFeign.getSpecParamInfo(specParamDTO1);

                        if (specParamResult1.getCode() == 200) {
                            sgd.setParamList(specParamResult1.getData());
                        }
                        return sgd;
                    }).collect(Collectors.toList());

                    map.put("specGroupDTOList", specGroupDTOList);
                }


                //查询sku信息
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
                if (skuResult.getCode() == 200) {
                    List<SkuDTO> skuList = skuResult.getData();
                    map.put("skuList", skuList);
                }


                //品牌信息
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setId(spuInfo.getBrandId());
                Result<PageInfo<BrandEntity>> brandInfo = brandFeign.getBrandInfo(brandDTO);

                if (brandInfo.getCode() == 200) {
                    PageInfo<BrandEntity> data = brandInfo.getData();

                    List<BrandEntity> list = data.getList();
                    if (list.size() == 1) {
                        map.put("brandInfo", list.get(0));
                    }
                }

            }
        }

        return map;
    }

}
