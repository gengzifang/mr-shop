package com.baidu.Repository;

import com.baidu.entity.GoodsEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface GoodsEsRepository extends ElasticsearchRepository<GoodsEntity,Long> {

    List<GoodsEntity> findByTitle (String title);

    List<GoodsEntity> findByTitleAndBrand (String title,String brand);

    List<GoodsEntity> findByAndPriceBetween (Double start,Double end);
}
