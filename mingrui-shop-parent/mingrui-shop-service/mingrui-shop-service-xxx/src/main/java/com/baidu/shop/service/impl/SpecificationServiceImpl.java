package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.BaiduBeanUtil;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecificationServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/3
 * @Version V1.0
 **/
@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    @Override
    public Result<List<SpecGroupEntity>> getSepcGroupInfo(SpecGroupDTO specGroupDTO) {

        Example example = new Example(SpecGroupEntity.class);
        example.createCriteria().andEqualTo("cid", specGroupDTO.getCid());

        List<SpecGroupEntity> list = specGroupMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<List<SpecGroupEntity>> save(SpecGroupDTO specGroupDTO) {

        specGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpecGroupEntity>> updates(SpecGroupDTO specGroupDTO) {

        specGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpecGroupEntity>> deletes(SpecGroupDTO specGroupDTO) {

        Example example = new Example(SpecParamEntity.class);

        example.createCriteria().andEqualTo("groupId", specGroupDTO.getId());

        List<SpecParamEntity> list = specParamMapper.selectByExample(example);

        if (list.size() >= 1) {
            return this.setResultError("规格内有规格信息不能删除");
        }

        specGroupMapper.deleteByPrimaryKey(specGroupDTO.getId());

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpecParamEntity>> getSpecParamInfo(SpecParamDTO specParamDTO) {

        if (null == specParamDTO.getGroupId()) return this.setResultError("规格组id不能为空");


        Example example = new Example(SpecParamEntity.class);

        example.createCriteria().andEqualTo("groupId", specParamDTO.getGroupId());


        List<SpecParamEntity> list = specParamMapper.selectByExample(example);


        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> saveSpecparam(SpecParamDTO specParamDTO) {

        specParamMapper.insertSelective(BaiduBeanUtil.copyProperties(specParamDTO, SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> updateSpecparam(SpecParamDTO specParamDTO) {

        specParamMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO, SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteSpecparam(Integer id) {

        specParamMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }


}
