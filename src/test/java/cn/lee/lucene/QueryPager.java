package cn.lee.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * 搜索分页与排序
 */
public class QueryPager {

    //搜素分页与排序
    @Test
    public void queryForPage() throws Exception {

        //创建索引库
        FSDirectory open = FSDirectory.open(new File("e:/index"));
        //创建索引读取对象
        IndexReader indexReader = DirectoryReader.open(open);
        //创建索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建分词器
        IKAnalyzer analyzer = new IKAnalyzer();

        //创建查询对象
        QueryParser queryParser = new QueryParser("bookName", analyzer);
        Query query = queryParser.parse("bookName:java OR bookName:lucene");

        //设置当前页码
        int pageNum = 1;
        //每页显示的记录数
        int pageSize = 2;

        // 创建排序字段对象: false: 升序 true : 降序
        SortField sortField = new SortField("bookPrice", SortField.Type.DOUBLE, false);
        //创建排序对象
        Sort sort = new Sort(sortField);

        // 搜索，得到最前面的文档对象
        TopFieldDocs search = indexSearcher.search(query, pageNum * pageSize, sort);
        System.out.println("命中的数量：" + search.totalHits);

        //获取分数文档数组
        ScoreDoc[] scoreDocs = search.scoreDocs;
        // 迭代分数文档数组
        // scoreDoc: 文档doc 分数:score
        for (int i = (pageNum - 1) * pageSize; i < scoreDocs.length; i++) {
            System.out.println("-*-*-*-*-华丽分割线-*-*-*-*-");
            ScoreDoc scoreDoc = scoreDocs[i];
            System.out.println("文档id: " + scoreDoc.doc
                    + "\t文档分数：" + scoreDoc.score);
            // 根据文档获取文档
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println("图书id:" + doc.get("id"));
            System.out.println("图书名称:" + doc.get("bookName"));
            System.out.println("图书价格:" + doc.get("bookPrice"));
            System.out.println("图书图片:" + doc.get("bookPic"));
            System.out.println("图书描述:" + doc.get("bookDesc"));
        }
        // 释放资源
        indexReader.close();
    }
}
