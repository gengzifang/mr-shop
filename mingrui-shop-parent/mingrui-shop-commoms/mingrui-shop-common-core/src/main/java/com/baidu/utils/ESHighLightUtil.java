package com.baidu.utils;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ESHighLightUtil
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/16
 * @Version V1.0
 **/
public class ESHighLightUtil<T> {

    //构建高亮字段buiilder
    //...可以传一个参数也可以传多个参数  在方法不确定传多少个参数的时候...  这些参数必须得是相同类型的
    public static HighlightBuilder getHighlightBuilder(String ...highLightField){

        HighlightBuilder highlightBuilder = new HighlightBuilder();

        //将传过来的参数 转为list集合
        Arrays.asList(highLightField).forEach(hlf ->{

            //遍历后放入参数字段
            HighlightBuilder.Field field = new HighlightBuilder.Field(hlf);

            //设置高亮标签
            //设置高亮标签
            field.preTags("<font style='color:#e4393c'>");
            field.postTags("</font>");

            //设置字段
            highlightBuilder.field(field);
        });

        return highlightBuilder;
    }


    //将返回的内容替换为高亮
    public static <T> List<SearchHit<T>> getHighLightHit(List<SearchHit<T>> list){

        //直接返回list
        return list.stream().map(hit -> {

            //得到高亮字段的集合
            Map<String, List<String>> highlightFields = hit.getHighlightFields();

            //遍历获取的几个  key高亮字段  value集合
            highlightFields.forEach((key,value) -> {
                try {
                    T content = hit.getContent();//当前文档 T为当前文档类型

                    //content.getClass()获取当前文档类型,并且得到排序字段的set方法  获取首字母并转换为大写T + itle 获取后面的字母
                    Method method = content.getClass().getMethod("set" + String.valueOf(key.charAt(0)).toUpperCase() + key.substring(1),String.class);
                    //getMethod 通过方法名 获取方法

                    //执行set方法并赋值
                    method.invoke(content,value.get(0));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            });

            return hit;

        }).collect(Collectors.toList());
    }

}
