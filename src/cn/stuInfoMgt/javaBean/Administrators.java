package cn.stuInfoMgt.javaBean;

public class Administrators extends User {
    public Administrators(int userId) {
        super(userId);
        //设置权限
        this.setRights("管理员");
    }
}
