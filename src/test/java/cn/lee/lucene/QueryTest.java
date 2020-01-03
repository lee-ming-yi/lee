package cn.lee.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * 高级搜索测试
 */
public class QueryTest {

    /**
     * 需求：查询图书名称域中包含有java的图书
     * TermQuery: 词条查询
     */
    @Test
    public void test1() {
        TermQuery termQuery = new TermQuery(new Term("bookName", "java"));
        search(termQuery);
    }

    private void search(Query query) {
        System.out.println("查询语法：" + query);
        try {
            // 创建索引库存储目录
            Directory directory = FSDirectory.open(new File("E:\\index"));
            // 创建索引读取对象(把索引库中索引数据加载到内存中)
            IndexReader indexReader = DirectoryReader.open(directory);
            // 创建索引搜索对象
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            // 创建分词器(对查询条件做分词)
            Analyzer analyzer = new IKAnalyzer();
            // 搜索，得到最前面的文档对象
            TopDocs topDocs = indexSearcher.search(query, 10);
            System.out.println("命中的数量：" + topDocs.totalHits);
            System.out.println("最大分数：" + topDocs.getMaxScore());

            // 获取分数文档数组
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            // 迭代分数文档数组
            // scoreDoc: 文档doc 分数:score
            for (ScoreDoc scoreDoc : scoreDocs) {
                System.out.println("=====华丽分割线=====");
                System.out.println("文档id: " + scoreDoc.doc + "\t文档分数："
                        + scoreDoc.score);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     *	需求：查询图书价格在80到100之间的图书。不包含边界值80和100
     * NumericRangeQuery: 数字范围查询
     */
    @Test
    public void test2() throws Exception{
        // 数字范围查询
        // String field: 字段名称
        // Double min: 最小值
        // Double max: 最大值
        // boolean minInclusive: 是否包含最小值
        // boolean maxInclusive: 是否包含最大值
        Query query = NumericRangeQuery.newDoubleRange("bookPrice", 70d, 100d, true, true);
        search(query);
    }

    /**
     * 需求：查询图书名称中包含有lucene，并且图书价格在80到100之间的图书
     * BooleanQuery: 布尔查询(多条件组合查询)
     */
    @Test
    public void test3(){
        //查询图书名称中包含有lucene
        TermQuery termQuery = new TermQuery(new Term("bookName", "lucene"));
        // 图书价格在80到100之间的图书
        NumericRangeQuery<Double> bookPrice = NumericRangeQuery.newDoubleRange("bookPrice", 80d, 100d, true, true);

        // 创建布尔查询
        BooleanQuery booleanClauses = new BooleanQuery();
        // 添加组合条件 与 组合方式
        // 组合方式:
        // 1. BooleanClause.Occur.MUST : 必须
        // 2. BooleanClause.Occur.MUST_NOT: 必须不
        // 3. BooleanClause.Occur.SHOULD: 有可能
        booleanClauses.add(termQuery,BooleanClause.Occur.MUST);
        booleanClauses.add(bookPrice,BooleanClause.Occur.MUST);
        search(booleanClauses);
    }

    /**
     * 需求：查询图书名称域中，包含有java，并且包含有lucene的图书。
     * QueryParser: 查询解析器，把查询表达式解析成Query对象。
     */
    @Test
    public void test4() throws Exception {
        //创建分词器
        IKAnalyzer analyzer = new IKAnalyzer();
        //创建queryParser对象
        QueryParser bookName = new QueryParser("bookName", analyzer);
        // 设置查询条件
        // 含义：搜索bookName中包含java的，或者包含lucene的文档
        //Query parse = bookName.parse("bookName:java OR bookName:lucene");
        // 含义：搜索bookName中包含java的，但是不包含lucene的文档
        //Query parse = bookName.parse("bookName:java NOT bookName:lucene");
        // 含义：搜索bookName中包含java的，并且包含lucene的文档
        Query parse = bookName.parse("bookName:java AND bookName:lucene");
        search(parse);
    }
}


