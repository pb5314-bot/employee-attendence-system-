import java.io.Serializable;

public class User implements Serializable {
    private String name,email,password,role;

    public User(String name,String email,String password,String role){
        this.name=name;
        this.email=email;
        this.password=password;
        this.role=role;
    }

    public String getName(){ return name; }
    public String getEmail(){ return email; }
    public String getPassword(){ return password; }
    public String getRole(){ return role; }
}
