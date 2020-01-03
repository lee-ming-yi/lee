package cn.lee.lucene.domain;

import lombok.Data;

@Data
public class Book {
    // 图书id
    private Integer id;
    // 图书名称
    private String bookName;
    // 图书价格
    private float price;
    // 图书图片
    private String pic;
    // 图书描述
    private String bookDesc;

}
