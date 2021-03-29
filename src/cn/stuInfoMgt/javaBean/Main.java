package cn.stuInfoMgt.javaBean;

import cn.stuInfoMgt.jdbc.dbOperation;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Student student1 = new Student(202101, "202101");
        //创建数据库操作对象，其构造方法实现连接数据库功能
        dbOperation dboperation = new dbOperation();
        //参数1为查询的数据，参数2为学生对象
        dboperation.queryStu(student1.getUserId(), student1);
        //使用ArrayList<Object>展示学生所有基本信息
        ArrayList<Object> stuInfo = new ArrayList<>();
        student1.getAll(stuInfo);


    }
}
