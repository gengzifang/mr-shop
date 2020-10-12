package com.baidu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @ClassName GoodsEntity
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/14
 * @Version V1.0
 **/
//声明当前类是一个文档(indexName = "索引名称",shards = "索引的分片数",replicas = "索引的副本数")
@Document(indexName = "goods",shards = 1,replicas = 0)
public class GoodsEntity {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")//分词器
    private String title; //标题

    @Field(type = FieldType.Keyword)
    private String category;// 分类

    @Field(type = FieldType.Keyword)
    private String brand; // 品牌

    @Field(type = FieldType.Double)
    private Double price; // 价格

    //index = false 不参与索引搜索
    /*
     * 设置index为to 的好处false是，当您为文档建立索引时，Elasticsearch将不必为该字段构建反向索引。结果，索引文档将稍快一些。同样，由于该字段在磁盘上将没有持久化的反向索引，因此您将使用更少的磁盘空间。*/
    @Field(index = false,type = FieldType.Keyword)
    private String images; // 图片地址


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public Double getPrice() {
        return price;
    }

    public String getImages() {
        return images;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "GoodsEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", images='" + images + '\'' +
                '}';
    }
}
