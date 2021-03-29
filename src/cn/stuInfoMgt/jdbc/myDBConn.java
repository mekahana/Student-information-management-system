package cn.stuInfoMgt.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class myDBConn {
    private static final String dbUsername = "sa";
    private static final String dbPassword = "526092902";
    private static String driverStr = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String connStr = "jdbc:sqlserver://localhost:1433; DatabaseName=myDatabase";

    //静态代码块，先于构造函数执行。————笔记java_32
    static {
        try {
            Class.forName(driverStr);
            System.out.println("驱动加载成功！");
        } catch (Exception ex) {
            ex.printStackTrace();
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
