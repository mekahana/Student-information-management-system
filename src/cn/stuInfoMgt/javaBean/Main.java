package cn.stuInfoMgt.javaBean;

import cn.stuInfoMgt.jdbc.dbOperation;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Student student1 = new Student(202101);
        //创建数据库操作对象，其构造方法实现连接数据库功能
        dbOperation dboperation = new dbOperation();

        //参数1为查询的数据，参数2为学生对象
        dboperation.queryStu(student1.getUserId(), student1);

        //使用ArrayList<Object>展示学生所有基本信息
        ArrayList<Object> stuInfo = new ArrayList<>();
        student1.getAll(stuInfo);

        //删除学生信息，返回值为boolean类型
        dboperation.delete(202103);
        dboperation.delete(0);



        //注册账户，返回值为boolean类型
        dboperation.registerAccount(202101, "202101", "学生");
        dboperation.registerAccount(001, "001", "管理员");

        //登陆账户，返回值为boolean类型
        dboperation.loginAccount(202101, "202101");

        //修改账户密码，返回值为boolean类型
        dboperation.updatePassword(202101, "202101", "新密码202101");

        //管理员删除账户
        dboperation.deleteAccount(001, "001", "管理员", 202101);

        //注册账户，返回值为boolean类型
        dboperation.registerAccount(202101, "202101", "学生");

        //管理员创建学院
        dboperation.insertCollege(1, "计算机与软件学院");

        //创建专业
        dboperation.insertMajor(1, "软件工程", "计算机与软件学院");

        //创建班级
        dboperation.insertClass(1, "软件工程");

        //插入学生信息，返回值为boolean类型
        Student student2 = new Student(202103, "钱老板", 22, "男", 1, 1, "无");
        dboperation.insertStu(student2);

        //修改学生信息，返回值为boolean类型
        student2.setUserName("钱天龙");
        student2.setUserId(202104);
        dboperation.update(student2);

        //管理员创建教师信息
        dboperation.insertTeacher(2, "徐万里", "管理员");

        //管理员创建课程
        dboperation.insertCourse(1, "Java", 2, 1, "管理员");

        //学生选课
        int[] course_id = new int[5];
        course_id[0] = 1;
        for (int i = 0; i < 5; i++) {
            System.out.println(course_id[i]);
        }
        dboperation.queryCourse(1);
        dboperation.chooseCourse(202104, course_id);

        //教师查询课程的学生信息
        dboperation.queryStuInCourse(1, 1);
        //教师打分
        dboperation.insertGrade(202103, 1, 100);
        //关闭数据库连接
        dboperation.close();
    }
}
