package cn.stuInfoMgt.javaBean;

public class Teacher extends User {
    public Teacher(int userId) {
        super(userId);
        //设置权限
        this.setRights("教师");
    }

}
