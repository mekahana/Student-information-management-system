package cn.stuInfoMgt.jdbc;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class myDBConn {
    //只有静态数据才能被静态代码块访问
    //通过读取配置文件jdbc.properties来获得数据内容
    private static String dbUsername;
    private static String dbPassword;
    private static String driverStr;
    private static String connStr;

    //静态代码块，先于构造函数执行。————笔记java_32
    //静态代码块中，只能处理异常而不能抛出异常，抛出需要借助方法
    static {
        try {
            //创建Properties集合类
            Properties properties = new Properties();
            //获取src路径下文件的方式：ClassLoader（类加载器）
            ClassLoader classLoader = myDBConn.class.getClassLoader();
            //URL：统一资源定位符号，可以定位文件的绝对路径（中文路径报错，许多应用不能使用中文路径的原因！？）
            URL res = ClassLoader.getSystemResource("jdbc.properties");
            //获取文件的字符串路径
            String path = res.getPath();
            //加载文件，两种方式，直接src/加文件名居然可以直接读取到（那我还这么麻烦定位绝对位置干啥子...）
            //properties.load(new FileReader("src/jdbc.properties"));
            properties.load(new FileReader(path));
            //获取文件内容
            dbUsername = properties.getProperty("dbUsername");
            dbPassword = properties.getProperty("dbPassword");
            driverStr = properties.getProperty("driverStr");
            connStr = properties.getProperty("connStr");
            //加载驱动，可以省略
            Class.forName(driverStr);
            System.out.println("驱动加载成功！");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("驱动加载失败！");
        }
    }

    private Connection conn = null;
    private Statement stmt = null;

    //构造函数
    public myDBConn() {
        try {
            conn = DriverManager.getConnection(connStr, dbUsername, dbPassword);
            stmt = conn.createStatement();//取得SQL语句对象
            System.out.println("数据库连接成功！");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据连接失败！");
        }
    }

    public Connection getConn() {
        return conn;
    }

    public Statement getStmt() {
        return stmt;
    }
}
