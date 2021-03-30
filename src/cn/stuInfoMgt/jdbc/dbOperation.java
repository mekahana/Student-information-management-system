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

    //方法功能：按照学号查询学生基本信息
    /*
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
            System.out.println("\n执行查询语句：" + sql_1);
            ResultSet rs_1 = stmt.executeQuery(sql_1);
            while (rs_1.next()) {
                //直接调用学生对象的setUserName()方法
                student.setUserName(rs_1.getString("stu_name"));
                student.setAge(rs_1.getInt("age"));
                student.setGender(rs_1.getString("gender"));
                student.setClass_id(rs_1.getInt("class_id"));
                student.setRemakes(rs_1.getString("remarks"));
            }
            int class_id = student.getClass_id();
            //查询学生班级与专业
            String sql_2 = "select major_name from major " +
                    "where major_id " +
                    "=(select major_id from class " +
                    "where class_id = '" + class_id + "')";
            //执行查询
            ResultSet rs_2 = stmt.executeQuery(sql_2);
            System.out.println("执行查询语句：" + sql_2);
            while (rs_2.next()) {
                student.setMajor(rs_2.getString("major_name"));
                student.setClass_name(student.getMajor() + class_id + "班");
                System.out.println("查询学生信息成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查询学生信息失败！");
        }
    }

    //方法功能：学号重复检查
    /*
    备注：
        1.用于插入学生信息时的数据检查
        2.存在重复为真，不存在为假
    * */
    public boolean checkId(int stu_id) {
        String sql_1 = "select stu_id from student "
                + "where stu_id = '" + stu_id + "'";
        try {
            //执行查询
            System.out.println("\n执行学号重复检查...");
            ResultSet rs_1 = stmt.executeQuery(sql_1);
            if (rs_1.next()) {
                System.out.println("检查到学号重复！");
                return true;
            }
            System.out.println("未检查到学号重复！");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("学号重复检查失败！");
            return true;
        }
    }

    //方法功能：插入学生信息
    /*
    备注：权限不需要传输，数据库中已经设置默认值
    注意：
        1.学号不能重复
        2.学生权限不能使用该方法
    * */
    public void insert(Student student) {
        //执行学号重复检查
        if (checkId(student.getUserId()) == false) {
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
    }

    //方法功能：打印指定表中所有当前数据
    /*
    注意：数据库返回集ResultSet类型数据的索引从1开始
    警告：此方法要在数据库操作更新后使用，若出现数据滞后，请检查方法使用的位置
    备注：
        1.ArrayList<E>嵌套使用
        2.使用了ArrayList<Object>来接受查询到的返回集数据
        3.使用ArrayList<ArrayList<Object>>来存储ArrayList<Object>数据
            （虽然可以直接对ArrayList<Object>进行循环操作...）
        4.表名作为SQL语句变量时不需要使用单引号，只使用双引号即可
    * */
    public void showTable(String table_name) {
        if (table_name.equals(null)) {
            table_name = "student";
        }
        System.out.println(table_name + "表：");
        String sql_1 = "select * from " + table_name;
        //可以使用switch语句，选择不同的SQL语句，查询不同的表
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

    //方法功能：删除指定学生信息
    public void delete(int stu_id) {
        System.out.println("\n正在删除学号为" + stu_id + "的学生信息...");
        String sql_1 = "delete from student " +
                "where stu_id = '" + stu_id + "'";
        try {
            //影响的行数
            int record = stmt.executeUpdate(sql_1);
            if (record != 0) {
                //record不为0，表示正常删除
                System.out.println("学生数据删除成功！");
                showTable("student");
            } else {
                //record为0，表示删除了个寂寞
                System.out.println("删除操作失败，不存在该条记录！");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("删除操作失败！");
        }
    }

    //方法功能：按照学号修改学生数据
    /*
    警告：
        1.不能修改学号，若要修改学号，步骤如下
            a.新参数用于传递原本学号（对象中的数据已经先完成更新）
                1）
            b.查重，学号是主键，具有唯一标识
            c.修改
    备注：无
    注意：
        1.在一条UPDATE语句中，如果要更新多个字段，
            字段间不能使用“AND”，而应该用逗号分隔。

    思路：
        1.无论请求修改的内容是什么，直接对记录中的数据覆盖重写
        2.在可视化界面中修改数据，修改的是对应Student类中的属性，
            更新数据库直接将学生对象的全部数据更新至数据库即可
    * */
    public void alert(Student student) {
        //定义参数，用来传递学生对象中的数据至数据库
        int stu_id = student.getUserId();
        //学号副本存储原来学号，这样就支持了修改学号
        int stu_id_copy = student.getStu_id_copy();
        //如果学号和副本数据不同，则表示需要修改学号，则要对新学号进行学号查重检查
        if ((stu_id != stu_id_copy && checkId(stu_id) == false)
                || stu_id == stu_id_copy) {
            //需要修改学号且通过了学号重复检查
            //或者不需要修改学号
            String stu_name = student.getUserName();
            System.out.println(stu_name);
            int age = student.getAge();
            String gender = student.getGender();
            int class_id = student.getClass_id();
            String remarks = student.getRemakes();
            String sql_1 = "update student set " +
                    "stu_id = '" + stu_id + "'," +
                    "stu_name = '" + stu_name + "'," +
                    "age = '" + age + "'," +
                    "gender = '" + gender + "'," +
                    "class_id = '" + class_id + "'," +
                    "remarks = '" + remarks + "' " +
                    "where stu_id = '" + stu_id_copy + "'";
            System.out.println("修改SQL语句为：" + sql_1);
            try {
                stmt.executeUpdate(sql_1);
                System.out.println("学生数据修改成功！");
                showTable("student");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("重复的学号：" + stu_id + "，请重新修改录入的学号!");
        }

    }

    //方法功能：关闭数据库连接
    public void close() {
        try {
            stmt.close();
            conn.close();
            System.out.println("\n数据库已关闭！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
