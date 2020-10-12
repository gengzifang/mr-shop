import com.baidu.Repository.GoodsEsRepository;
import com.baidu.RunTestEsApplication;
import com.baidu.common.ESHighLightUtil;
import com.baidu.entity.GoodsEntity;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @ClassName test
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/14
 * @Version V1.0
 **/
//让测试在spring容器环境下运行
@RunWith(SpringRunner.class)
//声明启动类,当测试方法运行的时候会帮我们自动启动容器
@SpringBootTest(classes = {RunTestEsApplication.class})
public class test {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private GoodsEsRepository goodsEsRepository;

    //创建索引
    @Test
    public void createGoodsIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("goods"));

        //创建索引
        indexOperations.create();

        System.out.println(indexOperations.exists() ? "索引创建成功" : "索引创建失败");
    }

    //创建映射
    @Test
    public void createGoodsMapping() {
        //此构造函数会检查有没有索引存在,如果没有则创建该索引,如果有则使用原来的索引
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsEntity.class);

        //indexOperations.createMapping();//创建映射,不调用此函数也可以创建映射,这就是高版本的强大之处

        System.out.println("映射创建成功");
    }

    //删除索引
    @Test
    public void deleteGoodsIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsEntity.class);

        indexOperations.delete();

        System.out.println("索引删除成功");
    }

    /*
    新增文档
     */
    @Test
    public void saveData() {

        GoodsEntity entity = new GoodsEntity();
        entity.setId(1L);
        entity.setBrand("小米");
        entity.setCategory("手机");
        entity.setImages("xiaomi.jpg");
        entity.setPrice(1000D);
        entity.setTitle("小米3");

        goodsEsRepository.save(entity);
        //可以增加 删除 更新

        System.out.println("新增成功");
    }

    /*
   批量新增文档
    */
    @Test
    public void saveAllData() {

        GoodsEntity entity = new GoodsEntity();
        entity.setId(2L);
        entity.setBrand("苹果");
        entity.setCategory("手机");
        entity.setImages("pingguo.jpg");
        entity.setPrice(5000D);
        entity.setTitle("iphone11手机");

        GoodsEntity entity2 = new GoodsEntity();
        entity2.setId(3L);
        entity2.setBrand("三星");
        entity2.setCategory("手机");
        entity2.setImages("sanxing.jpg");
        entity2.setPrice(3000D);
        entity2.setTitle("w2019手机");

        GoodsEntity entity3 = new GoodsEntity();
        entity3.setId(4L);
        entity3.setBrand("华为");
        entity3.setCategory("手机");
        entity3.setImages("huawei.jpg");
        entity3.setPrice(4000D);
        entity3.setTitle("华为mate30手机");

        List<GoodsEntity> objects = new ArrayList<GoodsEntity>();
//        objects.add(entity);
        objects.add(entity2);
//        objects.add(entity3);

        //goodsEsRepository.saveAll(Arrays.asList(entity,entity2,entity3));
        goodsEsRepository.saveAll(objects);

        System.out.println("批量新增成功");
    }

    //查询所有
    @Test
    public void searchAll() {
        //查询总条数
        long count = goodsEsRepository.count();
        System.out.println(count);
        //查询数据
        Iterable<GoodsEntity> all = goodsEsRepository.findAll();
        all.forEach(goods -> {
            System.out.println(goods);
        });

//        List<GoodsEntity> text = goodsEsRepository.findByTitleAndBrand("小米3","小米");
//        System.out.println(text);
        //mapper 可以自定义方法 不用实现

//        List<GoodsEntity> byAndPriceBetween = goodsEsRepository.findByAndPriceBetween(1000D,3000D);
//        System.out.println(byAndPriceBetween);

    }

    //删除文档
    @Test
    public void delData() {
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setId(3l);

        goodsEsRepository.delete(goodsEntity);
        System.out.println("删除成功");
    }


    //高级查询
    //自定义查询
    @Test
    public void customizeSearch() {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //Collector list 和 set 集合的顶层接口
        //Collectors  Collectors 是 Collector 的工具类

        queryBuilder.withQuery(QueryBuilders.boolQuery()//布尔组合
                        .must(QueryBuilders.matchQuery("title", "华为"))//匹配查询
                        .must(QueryBuilders.rangeQuery("price").gte(1000).lte(10000))//范围查询
                //`must`（与）、`must_not`（非）、`should`（或）
        );

        //排序   根据价钱排序                             降序 排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));

        //分页  当前页-1   每页10
        queryBuilder.withPageable(PageRequest.of(0, 2));


        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        List<SearchHit<GoodsEntity>> searchH = search.getSearchHits();

        search.getSearchHits().stream().forEach(hit -> {

            System.out.println(hit.getContent());
        });

    }

    //高亮
    @Test
    public void customizeSearchHighLight() {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //new个高亮对象
        HighlightBuilder highlightBuilder = new HighlightBuilder();

//        //HighlightBuilder.Field  调用了 HighlightBuilder 的静态内部类
//        HighlightBuilder.Field title = new HighlightBuilder.Field("title");
//        //设置高亮标签
//        title.preTags("<font style='color:red'>");
//        title.postTags("</font>");
//        //设置字段
//        highlightBuilder.field(title);


        //高亮查询
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        queryBuilder.withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("title", "华为"))
                .must(QueryBuilders.rangeQuery("price").gte(1000).lte(10000))
        );

        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(0, 2));


        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        List<SearchHit<GoodsEntity>> searchHits = search.getSearchHits();

        List<SearchHit<GoodsEntity>> highLightHit = ESHighLightUtil.getHighLightHit(searchHits);

//        List<SearchHit<GoodsEntity>> title1 = search.getSearchHits().stream().map(hit -> {
//
//            GoodsEntity entity = hit.getContent();
//
//            //获取高亮查询结果 多
//            //Map<String, List<String>> highlightFields = hit.getHighlightFields();
//
//            //通过字段名  获取 高亮查询结果
//            List<String> title2 = hit.getHighlightField("title");
//
//            //将加上标签的高亮字段  放入
//            entity.setTitle(title2.get(0));
//
//            return hit;
//        }).collect(Collectors.toList());

        System.out.println(highLightHit);
    }


    @Test
    public void searchAgg() {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.addAggregation(
                //聚合 自命名   聚合的字段
                AggregationBuilders.terms("brand_agg").field("brand")
        );

        //查询
        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        //获取桶
        Aggregations aggregations = search.getAggregations();

        //terms 是Aggregation的子类
        //Aggregation brand_agg = aggregations.get("brand_agg");/
        //Aggregation 无法操作  所以 返回它的 实现类 Terms来操作
        Terms terms = aggregations.get("brand_agg");

        //聚合的值
        List<? extends Terms.Bucket> buckets = terms.getBuckets();

        buckets.forEach(bucket -> {
            //获取的值数组自动转换为string
            System.out.println(bucket.getKeyAsString() + ":" + bucket.getDocCount());
        });
        System.out.println(search);
    }


    @Test
    public void searchAggMethod() {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.addAggregation(
                AggregationBuilders.terms("brand_agg")
                        .field("brand")
                        //聚合函数
                        .subAggregation(AggregationBuilders.max("max_price").field("price"))
        );

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        Aggregations aggregations = search.getAggregations();

        Terms terms = aggregations.get("brand_agg");

        List<? extends Terms.Bucket> buckets = terms.getBuckets();

        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString() + ":" + bucket.getDocCount());

            //获取聚合
            Aggregations aggregations1 = bucket.getAggregations();

            //得到map 转为map
            Map<String, Aggregation> map = aggregations1.asMap();

            //需要强转,Aggregations是一个类 Terms是他的子类,Aggregation是一个接口Max是他的子接口,而且Max是好几个接口的子接口
            Max max_price = (Max) map.get("max_price");

            System.out.println(max_price.getValue());
        });
    }

}
