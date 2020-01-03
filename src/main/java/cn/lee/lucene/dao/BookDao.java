package cn.lee.lucene.dao;

import cn.lee.lucene.domain.Book;

import java.util.List;

public interface BookDao {

    //查询所有图书
    List<Book> findAll();
}
