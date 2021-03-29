package cn.stuInfoMgt.jdbc;

import cn.stuInfoMgt.javaBean.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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

    /*
    方法功能：按照学号查询学生基本信息
    备注：参数1为学号，参数2为学生对象实例
    学生基本信息：
      1.学号
      2.姓名
      3.年龄
      4.性别
      5.班级
      6.专业
      7.备注
    **/
    public void queryStu(int stu_id, Student student) {
        String sql_1 = "select * from student where stu_id = '" + stu_id + "'";
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
                student.setClass_id(rs_1.getInt("class_id"));
                student.setRemakes(rs_1.getString("remarks"));
                System.out.println("学生姓名为：" + student.getUserName());
            }
            int class_id = student.getClass_id();
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

    /*
    方法功能：插入学生信息
    备注：权限不需要传输，数据库中已经设置默认值
    注意：
        1.学号不能重复
        2.学生权限不能使用该方法
    * */
    public void insert(Student student) {
        int stu_id = student.getUserId();
        String stu_name = student.getUserName();
        int age = student.getAge();
        String gender = student.getGender();
        int class_id = student.getClass_id();
        String remarks = student.getRemakes();
        String sql_1 = "insert into student" +
                "(stu_id, stu_name, age, gender, class_id, remarks)values" +
                "('" + stu_id + "', '" + stu_name + "', '" + age + "', '" + gender + "', '" + class_id + "', '" + remarks + "')";
        try {
            //执行插入
            System.out.println("执行插入语句：\n" + sql_1);
            stmt.executeUpdate(sql_1);
            System.out.println("学生信息插入成功！");
            showTable("student");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("插入学生信息失败！");
        }
    }

    /*
    方法功能：打印表中所有当前数据
    注意：数据库返回集ResultSet类型数据的索引从1开始
    备注：
        1.ArrayList<E>嵌套使用
        2.使用了ArrayList<Object>来接受查询到的返回集数据
        3.使用ArrayList<ArrayList<Object>>来存储ArrayList<Object>数据
            （虽然可以直接对ArrayList<Object>进行循环操作...）
    * */
    public void showTable(String table_name) {
        if (table_name.equals(null)) {
            table_name = "student";
        }
        System.out.println("查询的表为" + table_name);
        String sql_1 = "select * from " + table_name;
        System.out.println("查询语句为" + sql_1);
        try {
            ResultSet rs_1 = stmt.executeQuery(sql_1);

            //存储tableLine
            ArrayList<ArrayList<Object>> tableList = new ArrayList<>();
            while (rs_1.next()) {
                //使用tableLine存储一行数据
                ArrayList<Object> tableLine = new ArrayList<>();
                for (int i = 1; i <= 8; i++) {
                    tableLine.add(rs_1.getObject(i));
                }
                //将存储了一行数据的ArrayList存入
                tableList.add(tableLine);
                System.out.println(tableLine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //删除指定学生信息
    public void delete(int stu_id) {
        String sql_1 = "delete from student " +
                "where stu_id = '" + stu_id + "'";
        try {
            stmt.executeUpdate(sql_1);
            System.out.println("学生数据删除成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
