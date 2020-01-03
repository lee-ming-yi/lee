package cn.lee.lucene;

import cn.lee.lucene.dao.impl.BookDaoImpl;
import cn.lee.lucene.domain.Book;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexManagerTwo {

    //创建索引库
    @Test
    public void createIndex() {
        //采集数据
        BookDaoImpl bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.findAll();

        //创建文档集合对象
        List<Document> documents = new ArrayList<>();
        for (Book book : bookList) {
            //创建文档对象
            Document document = new Document();
            /**
             * 图书id
             是否分词：不需要分词
             是否索引：需要索引
             是否存储：需要存储
             -- StringField
             */
            document.add(new StringField("id", book.getId() + "", Field.Store.YES));
            /**
             * 图书名称
             是否分词：需要分词
             是否索引：需要索引
             是否存储：需要存储
             -- TextField
             */
            document.add(new TextField("bookName", book.getBookName(), Field.Store.YES));
            /**
             * 图书价格
             是否分词：（数值型的Field lucene使用内部的分词）
             是否索引：需要索引
             是否存储：需要存储
             -- DoubleField
             */
            document.add(new DoubleField("bookPrice", book.getPrice(), Field.Store.YES));
            /**
             * 图书图片
             是否分词：不需要分词
             是否索引：不需要索引
             是否存储：需要存储
             -- StoredField
             */
            document.add(new StoredField("bookPic", book.getPic()));
            /**
             * 图书描述
             是否分词：需要分词
             是否索引：需要索引
             是否存储：不需要存储
             -- TextField
             */
            document.add(new TextField("bookDesc", book.getBookDesc(), Field.Store.NO));
            documents.add(document);
        }
        try {
            //创建分词器
            IKAnalyzer analyzer = new IKAnalyzer();
            // 创建索引库配置对象，用于配置索引库
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
            //设置索引库打开模式
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            //创建索引库目录对象，用于指定索引库存储位置
            FSDirectory open = FSDirectory.open(new File("E:/index"));
            //创建索引库操作对象，用于把文档写入索引库
            IndexWriter indexWriter = new IndexWriter(open, indexWriterConfig);
            // 循环文档，写入索引库
            for (Document document : documents) {
                /** addDocument方法：把文档对象写入索引库 */
                indexWriter.addDocument(document);
                // 提交事务
                indexWriter.commit();
            }

            // 释放资源
            indexWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加文档
    @Test
    public void save(){
        try {
            //创建索引库
            FSDirectory open = FSDirectory.open(new File("e:/index"));
            //创建分词器
            IKAnalyzer analyzer = new IKAnalyzer();
            //创建索引写配置信息对象
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
            //设置索引库打开模式
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            //创建写索引对象
            IndexWriter indexWriter = new IndexWriter(open, iwc);

            //创建文档对象
            Document doc = new Document();
            // 图书Id
            doc.add(new StringField("id", "6", Field.Store.YES));
            // 图书名称
            doc.add(new TextField("bookName", "mysql数据库", Field.Store.YES));
            // 图书价格
            doc.add(new DoubleField("bookPrice", 80, Field.Store.YES));
            // 图书图片
            doc.add(new StoredField("bookPic", "6.jpg"));
            // 图书描述
            doc.add(new TextField("bookDesc", "删库到跑路", Field.Store.NO));

            //添加文档
            indexWriter.addDocument(doc);
            //提交到索引库
            indexWriter.commit();
            //关闭资源
            indexWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除文档
    @Test
    public void delete(){
        try {
            //创建索引库存储目录
            FSDirectory open = FSDirectory.open(new File("e:/index"));
            //创建分词器
            IKAnalyzer analyzer = new IKAnalyzer();
            //创建索引写配置信息对象
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
            //设置索引库打开模式
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            //创建写索引对象
            IndexWriter indexWriter = new IndexWriter(open, indexWriterConfig);
            //根据主键删除
            //indexWriter.deleteDocuments(new Term("id","6"));
            //删除全部
            indexWriter.deleteAll();
            //提交到索引库
            indexWriter.commit();
            //关闭资源
            indexWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //修改文档
    @Test
    public void update(){
        try {
            //创建索引库
            FSDirectory open = FSDirectory.open(new File("e:/index"));
            //创建分词器
            IKAnalyzer analyzer = new IKAnalyzer();
            //创建索引写配置对象
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
            //设置索引库打开模式
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter indexWriter = new IndexWriter(open, iwc);
            // 创建文档对象
            Document doc = new Document();
            // 图书Id
            doc.add(new StringField("id", "6", Field.Store.YES));
            // 图书名称
            doc.add(new TextField("bookName", "数据库", Field.Store.YES));
            // 图书价格
            doc.add(new DoubleField("bookPrice", 80, Field.Store.YES));
            // 图书图片
            doc.add(new StoredField("bookPic", "6.jpg"));
            // 图书描述
            doc.add(new TextField("bookDesc", "删库到跑路", Field.Store.NO));
            // 修改文档
            indexWriter.updateDocument(new Term("id", "6"),doc);

            // 提交到索引库
            indexWriter.commit();
            // 释放资源
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
