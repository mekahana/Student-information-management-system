package cn.stuInfoMgt.javaBean;

public class User {
    //用户编号
    private int userId;
    //姓名
    private String userName;
    //密码
    private String password;
    //身份（权限）
    private String rights;

    public User(int userId) {
        this.userId = userId;
    }

    //增删改查
    public void query() {

    }

    //权限设置
    public void modify() {
        if (this.getRights().equals("Student")) {
            System.out.println("权限缺失！");
        }
    }

    public void delete() {
        if (this.getRights().equals("Student")) {
            System.out.println("权限缺失！");
        }
    }

    public void insert() {
        if (this.getRights().equals("Student")) {
            System.out.println("权限缺失！");
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }
}
