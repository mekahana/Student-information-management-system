package cn.stuInfoMgt.javaBean;

public class Teacher extends User {
    public Teacher(int userId, String password) {
        super(userId, password);
        //设置权限
        this.setRights("教师");
    }

}
