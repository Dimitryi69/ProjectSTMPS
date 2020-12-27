package dca.bstu.project.Data;

public class User {
    private String Login;
    private String Password;
    private int Id;

    public User(String login, String password, int id) {
        Login = login;
        Password = password;
        Id = id;
    }

    public User() {
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
