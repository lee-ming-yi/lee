package cn.lee.lucene.dao.impl;

import cn.lee.lucene.dao.BookDao;
import cn.lee.lucene.domain.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    @Override
    public List<Book> findAll() {

        //创建List集合封装Book数据
        List<Book> bookList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //创建数据库链接对象
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/book", "root", "749867");
            //创建statement
            psmt = connection.prepareStatement("select * from book");
            //执行查询
            rs = psmt.executeQuery();

            //处理结果集
            while (rs.next()) {
                //创建图书对象
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookName(rs.getString("bookName"));
                book.setPrice(rs.getFloat("price"));
                book.setPic(rs.getString("pic"));
                book.setBookDesc(rs.getString("bookDesc"));
                bookList.add(book);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (connection != null) connection.close();
                if (psmt != null) psmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bookList;
    }
}
