package cn.stuInfoMgt.jdbc;

import cn.stuInfoMgt.javaBean.Student;

import java.sql.*;
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
                student.setMajor_id(rs_1.getInt("major_id"));
                student.setClass_id(rs_1.getInt("class_id"));
                student.setRemakes(rs_1.getString("remarks"));
            }
            int class_id = student.getClass_id();
            //查询学生班级与专业
            String sql_2 = "select major_name from major " +
                    "where major_id = '" + student.getMajor_id() + "'";
            //执行查询
            ResultSet rs_2 = stmt.executeQuery(sql_2);
            while (rs_2.next()) {
                String major_name = rs_2.getString("major_name");
                student.setClass_name(major_name + class_id + "班");
                System.out.println("查询学生信息成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查询学生信息失败！");
        }
    }

    //方法功能：插入学生信息
    /*
    备注：权限不需要传输，数据库中已经设置默认值
    注意：
        1.学号不能重复
        2.学生权限不能使用该方法

    错误总结：要先对学生插入的信息进行检查（班级是否已经存在、学号是否重复）
    * */
    public boolean insertStu(Student student) {
        System.out.println("\n学生信息插入中...");
        //执行学号重复检查
        if (!checkId(student.getUserId(), "student")) {
            //未检查到学号重复
            int stu_id = student.getUserId();
            String stu_name = student.getUserName();
            int age = student.getAge();
            String gender = student.getGender();
            int major_id = student.getMajor_id();
            int class_id = student.getClass_id();
            String remarks = student.getRemakes();
            //插入数据检查（班级必须存在）
            if (checkId(class_id, "class")) {
                //如果班级存在，这里借用了重复检查方法
                String sql_2 = "insert into student" +
                        "(stu_id, stu_name, age, gender, major_id, class_id, remarks)values" +
                        "('" + stu_id + "', '" + stu_name + "', '" + age + "', '" + gender + "', '" + major_id + "', '" + class_id + "', '" + remarks + "')";
                if (commonInsertResult(sql_2)) {
                    System.out.println("插入学生信息成功！");
                    showTable("student");
                    return true;
                } else {
                    System.out.println("插入学生信息失败！");
                    return false;
                }
            } else {
                System.out.println("插入学生信息失败，学生班级不存在，请检查学生信息！");
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
        System.out.println(table_name + "表：");
        String sql_1 = "select * from " + table_name;
        //可以使用switch语句，选择不同的SQL语句，查询不同的表
        try {
            ResultSet rs_1 = stmt.executeQuery(sql_1);
            ResultSetMetaData data = rs_1.getMetaData();
            //获得一行记录的字段数
            int columnCount = data.getColumnCount();
            //存储tableLine
            ArrayList<ArrayList<Object>> tableList = new ArrayList<>();
            while (rs_1.next()) {
                //使用tableLine存储一行数据
                ArrayList<Object> tableLine = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
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
        if (commonInsertResult(sql_1)) {
            //操作成功
            System.out.println("学生数据删除成功！");
            showTable("student");
            return true;
        } else {
            //操作失败
            System.out.println("删除失败，学生信息不存在！");
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
        System.out.println("\n更新学生信息中...");
        //定义参数，用来传递学生对象中的数据至数据库
        int stu_id = student.getUserId();
        //学号副本存储原来学号，这样就支持了修改学号
        int stu_id_copy = student.getStu_id_copy();
        //如果学号和副本数据不同，则表示需要修改学号，则要对新学号进行学号查重检查
        if ((stu_id != stu_id_copy && checkId(stu_id, "student") == false)
                || stu_id == stu_id_copy) {
            //需要修改学号且通过了学号重复检查
            //或者不需要修改学号
            String stu_name = student.getUserName();
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
            if (commonInsertResult(sql_1)) {
                //数据更新成功
                System.out.println("学生信息修改成功！");
                showTable("student");
                return true;
            } else {
                System.out.println("学生信息修改失败，该学生不存在！");
                return false;
            }
        } else {
            System.out.println("学生信息修改失败！\n" +
                    "重复的学号：" + stu_id + "，请重新修改录入的学号!");
            return false;
        }
    }

    //方法功能：创建学院
    /**
     * 备注：
     * 1.只有管理员才可以设置学院
     * 2.学院代号进行查重
     */
    public boolean insertCollege(int college_id, String college_name) {
        String table_name = "college";
        String sql_1 = "insertStu into college (college_id, college_name) " +
                "values('" + college_id + "', '" + college_name + "')";
        //学院创建预设，使用预设的默认值
        System.out.println("\n正在创建学院中...");
        //进行id重复检查
        if (!checkId(college_id, table_name)) {
            //不存在id重复
            if (commonInsertResult(sql_1)) return true;
            else return false;
        } else {
            //存在id重复，返回false插入专业失败
            System.out.println("信息已存在，创建失败！");
            return false;
        }
    }

    //方法功能：创建专业
    /*
     * 注意：
     *   1.要先有学院才可以创建专业
     *   2.要进行专业id查重
     * 错误总结：变量college_id用来存放学院代号，因为学院代号是通过参数学院名称查询到的，所以变量初始化为0；
     *               当定义了插入语句sql_2时，将college_id赋值进去，因为字符串是不能改变的，所以无论后面college_id的值如何改变，sql_2对应的字符串的值是永远不会再变的
     * */
    public boolean insertMajor(int major_id, String major_name, String college_name) {
        System.out.println("\n正在创建专业表...");
        String sql_1 = "select college_id from college " +
                "where college_name = '" + college_name + "'";
        int college_id = 0;
        try {
            ResultSet rs = stmt.executeQuery(sql_1);
            if (rs.next()) {
                //学院存在，获取学院id
                college_id = rs.getInt("college_id");
                //专业id重复检查
                if (!checkId(major_id, "major")) {
                    //不存在id重复，插入数据
                    String sql_2 = "insertStu into major (major_id, major_name, college_id)" +
                            "values('" + major_id + "', '" + major_name + "', '" + college_id + "')";
                    System.out.println("正在插入数据：" + sql_2);
                    if (commonInsertResult(sql_2)) return true;
                    else return false;
                } else {
                    //存在重复
                    System.out.println("信息已存在，创建失败！");
                    return false;
                }
            } else {
                System.out.println("专业插入失败，学院不存在，请先创建学院！");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库出错，专业创建失败！");
            return false;
        }
    }

    //方法功能：创建班级
    /*
     * 备注：
     *   1.要先有对应专业才能创建班级
     *   2.管理员才能创建班级
     *   3.教师id可以为空，在本方法中暂不设置教师id（教师选择班级）
     *   4.班级编号查重
     *
     * 错误总结：与创建专业同样的原因，字符串是不可改变的
     * */
    public boolean insertClass(int class_id, String major_name) {
        System.out.println("\n正在创建班级...");
        //设置班级名称
        String class_name = major_name + class_id + "班";
        int major_id = 0;
        String sql_1 = "select major_id from major " +
                "where major_name = '" + major_name + "'";
        //班级id检查
        if (!checkId(class_id, "class")) {
            //不存在重复
            try {
                ResultSet rs = stmt.executeQuery(sql_1);
                if (rs.next()) {
                    //专业存在，获取major_id
                    major_id = rs.getInt("major_id");
                    //不存在重复，插入数据
                    String sql_2 = "insertStu into class " +
                            "(class_id, class_name, major_id)" +
                            "values('" + class_id + "', '" + class_name + "', '" + major_id + "')";
                    System.out.println(sql_2);
                    if (commonInsertResult(sql_2)) {
                        System.out.println("班级创建成功！");
                        return true;
                    } else {
                        System.out.println("数据库出错，班级创建失败！");
                        return false;
                    }
                } else {
                    //专业不存在
                    System.out.println("专业不存在，班级创建失败！");
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("数据库出错，班级创建失败！");
                return false;
            }
        } else {
            //班级已经存在
            System.out.println("班级已存在，创建失败！");
            return false;
        }
    }

    //方法功能：管理员创建教师
    /*
     * 备注：权限检查
     * */
    public boolean insertTeacher(int teacher_id, String teacher_name, String rights) {
        System.out.println("\n正在创建教师信息中...");
        //权限检查
        if (rights.equals("管理员")) {
            //检查教师id是否重复
            if (!checkId(teacher_id, "teacher")) {
                //没有重复
                String sql_1 = "insertStu into teacher " +
                        "(teacher_id, teacher_name) " +
                        "values('" + teacher_id + "', '" + teacher_name + "')";
                if (commonInsertResult(sql_1)) {
                    System.out.println("教师信息创建成功！");
                    return true;
                } else {
                    System.out.println("数据库未响应，教师信息创建失败！");
                    return false;
                }

            } else {
                //存在重复
                System.out.println("教师已存在，教师信息创建失败！");
                return false;
            }
        } else {
            System.out.println("权限不足");
            return false;
        }
    }

    //方法功能：管理员设置专业对应课程
    /**
     * 备注：
     * 1.课程是否存在（重复检查）
     * 2.专业是否存在（重复检查）
     * 3.教师是否存在（重复检查）
     * <p>
     * 优化：
     * 1.输入的参数应该将teacher_id替换为teacher_name
     * 2.major_id替换为major_name
     */
    public boolean insertCourse(int course_id, String course_name, int teacher_id, int major_id, String rights) {
        System.out.println("\n正在创建课程中...");
        //权限检查
        if (rights.equals("管理员")) {
            //专业存在检查
            if (checkId(major_id, "major")) {
                //专业id输入正确，课程id重复检查
                if (!checkId(course_id, "course")) {
                    //课程id不存在重复，教师存在检查
                    if (checkId(teacher_id, "teacher")) {
                        //教师存在
                        System.out.println("教师存在！");
                        String sql_1 = "insertStu into course " +
                                "(course_id, course_name, teacher_id, major_id) " +
                                "values('" + course_id + "', '" + course_name + "', '" + teacher_id + "', '" + major_id + "')";
                        if (commonInsertResult(sql_1)) {
                            //数据插入成功
                            System.out.println("课程创建成功！");
                            return true;
                        } else {
                            System.out.println("数据库未响应，课程创建失败！");
                            return false;
                        }
                    } else {
                        //教师不存在
                        System.out.println("教师不存在，课程创建失败！");
                        return false;
                    }
                } else {
                    //课程id存在重复
                    System.out.println("课程号重复，课程创建失败！");
                    return false;
                }
            } else {
                //专业不存在
                System.out.println("课程所属专业不存在，课程创建失败！");
                return false;
            }
        } else {
            System.out.println("权限不足，课程创建失败！");
            return false;
        }
    }

    //方法功能：数据插入和结果处理
    /*
     * 备注：
     * 1.支持专业id插入处理
     * 2.支持学号插入处理
     * 3.支持学院代号插入处理
     * 4.支持账号插入处理
     * 2.返回值真则存在重复，假则不存在重复
     * */
    public boolean commonInsertResult(String sql) {
        try {
            int record = stmt.executeUpdate(sql);
            if (record != 0) {
                System.out.println("数据库操作成功！");
                return true;
            } else {
                System.out.println("数据库未响应，操作失败！");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库出错，创建失败！");
            return false;
        }
    }

    //方法功能：id重复检查（用于创建、更新数据时的重复检查）
    /**
     * 备注：
     * 1.支持专业id重复检查
     * 2.支持学号重复检查
     * 3.支持学院代号重复检查
     * 4.支持账号重复检查
     * 2.返回值真则存在重复，假则不存在重复
     */
    private boolean checkId(int id, String table_name) {
        System.out.println("正在进行重复检查...");
        String sql_1 = null;
        //根据表名查询不同的表
        if (table_name.equals("major")) {
            //查询专业表
            sql_1 = "select major_id from major " +
                    "where major_id= '" + id + "'";
            System.out.println("正在检查专业表：" + sql_1);
        } else if (table_name.equals("college")) {
            //查询学院表
            sql_1 = "select college_id from college " +
                    "where college_id= '" + id + "'";
            System.out.println("正在检查学院表：" + sql_1);
        } else if (table_name.equals("student")) {
            //查询学生表
            sql_1 = "select stu_id from student "
                    + "where stu_id = '" + id + "'";
            System.out.println("正在检查学生表：" + sql_1);
        } else if (table_name.equals("account")) {
            //查询账户表
            sql_1 = "select userId from account "
                    + "where userId = '" + id + "'";
            System.out.println("正在检查账户表：" + sql_1);
        } else if (table_name.equals("class")) {
            sql_1 = "select class_id from class " +
                    "where class_id = '" + id + "'";
            System.out.println("正在检查课程表：" + sql_1);
        } else if (table_name.equals("teacher")) {
            sql_1 = "select teacher_id from teacher " +
                    "where teacher_id = '" + id + "'";
            System.out.println("正在检查教师表：" + sql_1);
        } else if (table_name.equals("course")) {
            sql_1 = "select course_id from course " +
                    "where course_id = '" + id + "'";
            System.out.println("正在检查班级表：" + sql_1);
        } else {
            System.out.println("不存在的表：" + table_name + "！");
            return false;
        }
        try {
            ResultSet rs = stmt.executeQuery(sql_1);
            if (rs.next()) {
                //存在重复
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //重复查询，针对两个字段为主键的表
    private boolean checkId(int id1, int id2, String table_name) {
        System.out.println("正在进行重复检查...");
        String sql_1 = null;
        if (table_name.equals("stu_course")) {
            //查询stu_course表
            sql_1 = "select * from stu_course " +
                    "where stu_id= '" + id1 + "' " +
                    "and course_id = '" + id2 + "'";
            System.out.println("正在检查学生选课表：" + sql_1);
        } else if (table_name.equals("grade")) {
            //查询grade表
            sql_1 = "select * from grade " +
                    "where stu_id= '" + id1 + "' " +
                    "and course_id = '" + id2 + "'";
            System.out.println("正在检查成绩表：" + sql_1);
        } else {
            System.out.println("不存在的表：" + table_name + "！");
            return false;
        }
        try {
            ResultSet rs = stmt.executeQuery(sql_1);
            if (rs.next()) {
                //存在重复
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //方法功能：查询本专业下的课程
    /*
     * 备注：配合选课使用
     * */
    public boolean queryCourse(int major_id) {
        //查询学生自身专业，查询该专业下课程
        String sql_1 = "select * from course " +
                "where major_id='" + major_id + "'";
        System.out.println("正在查询专业下课程：" + sql_1);
        try {
            //输出专业下的课程信息
            ResultSet rs = stmt.executeQuery(sql_1);
            ResultSetMetaData data = null;
            data = rs.getMetaData();
            //获得一行记录的字段数
            int columnCount = data.getColumnCount();
            //用于存储一行记录
            ArrayList<Object> arrayListRecord = new ArrayList<>();
            ArrayList<ArrayList<Object>> arrayLists = new ArrayList<>();
            while (rs.next()) {
                for (int i = 1; i < columnCount; i++) {
                    arrayListRecord.add(rs.getObject(i));
                }
                arrayLists.add(arrayListRecord);
            }
            System.out.println(arrayLists);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //方法功能：学生选课course
    /*
    备注：
        1.要先进行专业选择
        2.只能选择本专业下的课程
        3.最多只能选择5门课程
    * */
    public boolean chooseCourse(int stu_id, int[] course_id) {
        System.out.println("正在选课...");
        //课程是否已经被选
        //进行选课
        String sql_2 = null;
        for (int i = 0; i < course_id.length; i++) {
            if (course_id[i] != 0) {
                sql_2 = "insert into stu_course " +
                        "(stu_id, course_id) " +
                        "values('" + stu_id + "', '" + course_id[i] + "')";
                //查询主键是否存在重复
                if (!checkId(stu_id, course_id[i], "stu_course")) {
                    //没有重复
                    if (commonInsertResult(sql_2)) {
                        System.out.println("选课成功！");
                        return true;
                    } else {
                        System.out.println("存在已经选择的课程，选课失败！");
                        return false;
                    }
                } else {
                    //存在重复
                    System.out.println("存在已经选择的课程，选课失败");
                    return false;
                }

            } else {
                //当遇到课程号为0表示之后都没有数据，直接退出循环
                break;
            }
        }
        System.out.println("数据库操作出错，选课失败！");
        return false;
    }

    //方法功能：教师查询自己课程下的学生信息

    /**
     * 备注：只有教师或者管理员可以查看
     */
    public void queryStuInCourse(int teacher_id, int course_id) {
        String sql_1 = "select stu_id, stu_name from student " +
                "where stu_id = " +
                "(select stu_id from stu_course " +
                "where course_id = " +
                "(select course_id from course " +
                "where course_id = '" + course_id + "' " +
                "and teacher_id = '" + teacher_id + "'))";
        System.out.println("\n教师查询自己某课程下的学生信息：" + sql_1);
        try {
            ResultSet rs = stmt.executeQuery(sql_1);
            System.out.println("教师id：" + teacher_id + "，课程id：" + course_id + "的学生名单：");
            while (rs.next()) {
                System.out.println(rs.getInt("stu_id") + "，" + rs.getString("stu_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //方法功能：教师打分
    /*
    备注：
        1.一个老师带多个班
        2.可视界面显示不同课程的班级学生名单，老师为其打分
    安全性：该课程是否已经被打分
    * */
    public boolean insertGrade(int stu_id, int course_id, int grade) {
        System.out.println("\n正在打分...");
        if (!checkId(stu_id, course_id, "grade")) {
            //没有重复打分
            String sql_1 = "insert into grade " +
                    "(stu_id, course_id, grade)" +
                    "values('" + stu_id + "', '" + course_id + "', '" + grade + "')";
            if (commonInsertResult(sql_1)) {
                System.out.println("打分完成！");
                return true;
            } else {
                System.out.println("数据库未响应，打分失败！");
                return false;
            }
        } else {
            //重复打分
            System.out.println("打分失败，学生成绩已经被录入！");
            return false;
        }

    }
    //方法功能：学生成绩查询
    /*
    注意：
        1.查询成绩需要先登陆账号
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
        System.out.println("\n正在注册账号...");
        //首先进行账号重复检查
        if (checkId(userId, "account") == false) {
            //不存在账号重复
            String sql_1 = "insert into account(userId,password,rights)values('" + userId + "','" + password + "','" + rights + "')";
            if (commonInsertResult(sql_1)) {
                System.out.println("账号注册成功，账号：" + userId + ",身份：" + rights);
                return true;
            } else {
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
