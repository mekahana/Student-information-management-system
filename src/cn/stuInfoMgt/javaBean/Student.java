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
    private int major_id;
    //备注
    private String remakes;
    //学号副本，用于在数据库中，根据原学号来修改学号
    //stu_id_copy未设置setter方法，其值的改变在userId的setter方法中实现
    private int stu_id_copy;

    public Student(int userId) {
        super(userId);
        //设置权限
        this.setRights("学生");
    }

    //对应数据库，学生表数据顺序
    public Student(int userId, String stu_name, int age, String gender, int major_id, int class_id, String remakes) {
        super(userId);
        //对象初始化时保持学号副本与学号相同
        this.stu_id_copy = super.getUserId();
        this.setUserName(stu_name);
        this.age = age;
        this.gender = gender;
        this.major_id = major_id;
        this.class_id = class_id;
        this.remakes = remakes;
    }

    //重写User类中getUserId方法
    @Override
    public void setUserId(int userId) {
        //在每次对对象的学号进行更新时，将原有学号存储在副本中
        stu_id_copy = this.getUserId();
        System.out.println("副本数据：" + stu_id_copy + "已经存储！");
        super.setUserId(userId);
    }

    public int getMajor_id() {
        return major_id;
    }

    public void setMajor_id(int major_id) {
        this.major_id = major_id;
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

    public int getStu_id_copy() {
        return stu_id_copy;
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
