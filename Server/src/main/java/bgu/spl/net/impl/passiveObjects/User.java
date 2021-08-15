package bgu.spl.net.impl.passiveObjects;

public class User {
    /*---------------------------------fields---------------------------------*/
    private String username;
    private String password;
    /*-------------------------------constructors------------------------------*/
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    /*---------------------------------getters---------------------------------*/
    public String getUsername(){
        return username;
    }
    public String getPassword() {
        return password;
    }
    /*---------------------------------getters---------------------------------*/
}
