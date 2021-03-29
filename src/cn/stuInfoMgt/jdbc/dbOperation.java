package cn.stuInfoMgt.jdbc;

import cn.stuInfoMgt.javaBean.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class dbOperation {
    //数据库操作相关变量
    private Connection conn;
    private Statement stmt;

    //将myDBConn类的对象作为构造函数的参数
    public dbOperation() {
        //连接数据库
        myDBConn myDB = new myDBConn();
        stmt = myDB.getStmt();
        conn = myDB.getConn();
    }

    /**
     * 查询学生基本信息
     * 参数1为学号，参数2为学生对象实例
     * 学生基本信息：
     * 1.学号
     * 2.姓名
     * 3.年龄
     * 4.性别
     * 5.班级
     * 6.专业
     * 6.备注
     */
    public void queryStu(int stu_id, Student student) {
        String sql_1 = "select * from student where stu_id = '" + stu_id + "'";
        int class_id = 0;
        //查询专业
        try {
            //返回结果集
            System.out.println("执行查询语句：\n" + sql_1);
            ResultSet rs_1 = stmt.executeQuery(sql_1);
            while (rs_1.next()) {
                //直接调用学生对象的setUserName()方法
                student.setUserName(rs_1.getString("stu_name"));
                student.setAge(rs_1.getInt("age"));
                student.setGender(rs_1.getString("gender"));
                class_id = rs_1.getInt("class_id");
                student.setRemakes(rs_1.getString("remarks"));
                System.out.println("学生姓名为：" + student.getUserName());
            }
            //查询学生班级与专业
            String sql_2 = "select major_name from major " +
                    "where major_id " +
                    "=(select major_id from class " +
                    "where class_id = '" + class_id + "')";
            //执行查询
            ResultSet rs_2 = stmt.executeQuery(sql_2);
            System.out.println("执行查询语句：\n" + sql_2);
            while (rs_2.next()) {
                student.setMajor(rs_2.getString("major_name"));
                student.setClass_name(student.getMajor() + class_id + "班");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查询学生信息失败！");
        }
    }

    public void insert() {

    }

    public void delete() {

    }

    public void alert() {

    }

    public void close() {
        try {
            stmt.close();
            conn.close();
            System.out.println("数据库已关闭！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
