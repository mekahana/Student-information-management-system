package cn.stuInfoMgt.javaBean;

import java.util.ArrayList;

public class Student extends User {
    //年龄
    private int age;
    //性别
    private String gender;
    //班级编号，与数据库学生表对应，作用于数据库操作
    private int class_id;
    //班级名称，用于展示学生基本信息
    private String class_name;
    //专业
    private String major;
    //备注
    private String remakes;


    public Student(int userId, String password) {
        super(userId, password);
        //设置权限
        this.setRights("学生");
    }

    public Student(int userId, String password, String stu_name, int age, String gender, int class_id, String remakes) {
        super(userId, password);
        this.setUserName(stu_name);
        this.age = age;
        this.gender = gender;
        this.class_id = class_id;
        this.remakes = remakes;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getRemakes() {
        return remakes;
    }

    public void setRemakes(String remakes) {
            this.remakes = remakes;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public void getAll(ArrayList<Object> stuInfo) {
        stuInfo.add(this.getUserId());
        stuInfo.add(this.getUserName());
        stuInfo.add(this.getAge());
        stuInfo.add(this.getGender());
        stuInfo.add(this.getClass_name());
        stuInfo.add(this.getRemakes());
        System.out.println("学生基本信息：" + stuInfo);
    }

}
