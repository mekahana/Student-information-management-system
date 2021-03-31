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

    //在构造函数中创建myDBConn类对象
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
            System.out.println("\n信息查询中...");
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
    public boolean insert(Student student) {
        //执行学号重复检查
        if (checkId(student.getUserId()) == false) {
            //未检查到学号重复
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
                System.out.println("学生信息插入中...");
                stmt.executeUpdate(sql_1);
                System.out.println("学生信息插入成功！");
                showTable("student");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("插入学生信息失败！");
                return false;
            }
        } else {
            //检查到学号重复
            System.out.println("插入学生信息失败！");
            return false;
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

    //方法功能：按照学号删除指定学生信息
    public boolean delete(int stu_id) {
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
                return true;
            } else {
                //record为0，表示删除了个寂寞
                System.out.println("删除操作失败，不存在该条记录！");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("删除操作失败！");
            return false;
        }
    }

    //方法功能：按照学号修改学生数据
    /*
    注意：
        1.在一条UPDATE语句中，如果要更新多个字段，
            字段间不能使用“AND”，而应该用逗号分隔。
    思路：
        1.无论请求修改的内容是什么，直接对记录中的数据覆盖重写
        2.在可视化界面中修改数据，修改的是对应Student类中的属性，
            更新数据库直接将学生对象的全部数据更新至数据库即可
    * */
    public boolean update(Student student) {
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
            try {
                stmt.executeUpdate(sql_1);
                System.out.println("学生信息修改成功！");
                showTable("student");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("学生信息修改失败，数据库错误！");
                return false;
            }
        } else {

            System.out.println("学生信息修改失败！\n" +
                    "重复的学号：" + stu_id + "，请重新修改录入的学号!");
            return false;
        }

    }

    //方法功能：学生选专业
    /*
    备注：专业中有多门课程，且不存在课程同时属于多个专业
    * */

    //方法功能：学生选课course
    /*
    备注：
        1.要先进行专业选择
        2.只能选择本专业下的课程
        3.
    * */

    //方法功能：教师打分
    /*
    备注：
        1.一个老师带多个班
        2.可视界面显示不同课程的班级学生名单，老师为其打分
    * */

    //方法功能：学生成绩查询
    /*
    注意：
        1.注册账号需要查重
        2.账号为各对象的userId
        3.注册需要的参数：账号，密码，权限
    * */

    //方法功能：系统账号注册
    /*
    备注：
        1.首先要进行账号重复检查
        2.账号就是用户的id，即教师编号或学生学号，管理员除外
    * */
    public boolean registerAccount(int userId, String password, String rights) {
        //首先进行账号重复检查
        if (checkAccount(userId) == false) {
            //不存在账号重复
            try {
                System.out.println("正在注册账号...");
                String sql_1 = "insert into account(userId,password,rights)values('" + userId + "','" + password + "','" + rights + "')";
                stmt.executeUpdate(sql_1);
                System.out.println("账号注册成功，账号：" + userId + ",身份：" + rights);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
                System.out.println("账号注册失败！");
                return false;
            }
        } else {
            System.out.println("账号注册失败，账号已存在！");
            return false;
        }
    }

    //方法功能：系统登陆
    public boolean loginAccount(int userId, String password) {
        String sql_1 = "select password from account " +
                "where userId = '" + userId + "'";
        try {
            System.out.println("\n正在登陆...");
            ResultSet rs_1 = stmt.executeQuery(sql_1);
            //因为账号都是唯一的，所以这样用if而不是while
            if (rs_1.next()) {
                //查询到账号，核对密码
                if (password.equals(rs_1.getString("password"))) {
                    //密码匹配成功
                    System.out.println("登陆成功！");
                    return true;
                } else {
                    System.out.println("登陆失败，密码或账号错误！");
                    return false;
                }
            } else {
                //未查询到账号
                System.out.println("登陆失败，账号不存在！");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("登陆失败，未知错误！");
            return false;
        }

    }

    //方法功能：系统账号修改密码
    /*
    备注：暂无
    * */
    public boolean updatePassword(int userId, String password, String new_password) {
        //当查询到账号与密码与参数一致的记录时，修改密码
        String sql_1 = "update account set " +
                "password = '" + new_password + "' " +
                "where userId = '" + userId + "' and " +
                "password = '" + password + "'";
        try {
            System.out.println("\n密码修改中...");
            int record = stmt.executeUpdate(sql_1);
            if (record != 0) {
                //数据更新成功
                System.out.println("密码修改成功！");
                return true;
            } else {
                System.out.println("原密码输入错误，密码修改失败！");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库出错，密码修改失败！");
            return false;
        }
    }

    //方法功能：系统账号删除
    /*
    警告：
        1.管理员与普通用户看到的界面不同，但使用同一个功能
        2.能否使用该方法是在可视化界面有限制的，但凡能在界面中使用该功能的即是合法的
        3.具体限制在UI中设置
    建议：
        1.前三个参数可以直接用对象参数传入替代，然后加一个实时的确认密码
    注意：
        1.只有管理员拥有账号删除的权利（或者自己删除自己的账号）
        2.账户与其他学生信息没有关系，即使账户删除，学生表、成绩表中的记录依旧存在
        3.参数1和2是用于账号验证，参数4是删除的账号id，其中参数134是对象自动传入，参数2是临时确认的密码
        4.如果是自己账号，删除后自动登出系统
    * */
    public boolean deleteAccount(int userId, String password, String rights, int targetAccount) {
        //不需要查询账号是否是管理员账号，因为能实现这个功能是具有局面约束的
        String sql_1 = "delete from account " +
                "where userId = '" + targetAccount + "'";
        try {
            System.out.println("\n账号：" + targetAccount + "删除中...");
            //结果最多只有一条记录，所以用if而不是while
            int record = stmt.executeUpdate(sql_1);
            if (record != 0) {
                System.out.println("账户删除成功！");
                return true;
            } else {
                System.out.println("账户删除失败，账户不存在！");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("账户删除失败，数据库出错！");
            return false;
        }
    }

    //方法功能：账号重复检查
    /*
    备注：
        1.用于账户注册时数据检查
        2.存在重复为真，不存在为假
    * */
    public boolean checkAccount(int userId) {
        String sql_1 = "select userId from account "
                + "where userId = '" + userId + "'";
        try {
            //执行查询
            System.out.println("\n执行账号重复检查...");
            ResultSet rs_1 = stmt.executeQuery(sql_1);
            if (rs_1.next()) {
                System.out.println("检查到账号重复！");
                return true;
            }
            System.out.println("未检查到账号重复！");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("账号重复检查失败！");
            return true;
        }
    }

    //方法功能：关闭数据库连接，释放资源
    public void close() {
        try {
            System.out.println("\n正在释放资源...");
            stmt.close();
            conn.close();
            System.out.println("资源释放完毕，数据库已关闭！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
