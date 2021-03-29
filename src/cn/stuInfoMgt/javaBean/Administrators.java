package cn.stuInfoMgt.javaBean;

public class Administrators extends User {
    public Administrators(int userId, String password) {
        super(userId, password);
        //设置权限
        this.setRights("Administrators");
    }
}
