package Project.auxiliary_classes;

import java.io.Serializable;

public class User implements Serializable{
    protected String ID, password;

    public User(){};
    public User(String iD, String pass){
        ID = new String(iD);
        password = new String(pass);
    }

    public String getUserID(){
        return ID;
    }

    public String getPassword(){
        return password;
    }
}
