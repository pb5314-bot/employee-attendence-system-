import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passField;
    private JButton loginBtn, registerBtn;

    public LoginFrame(){
        setTitle("Employee Login"); 
        setSize(400,220); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; panel.add(new JLabel("Email:"), c);
        c.gridx=1; emailField=new JTextField(20); panel.add(emailField,c);

        c.gridx=0; c.gridy=1; panel.add(new JLabel("Password:"), c);
        c.gridx=1; passField=new JPasswordField(20); panel.add(passField,c);

        c.gridx=0; c.gridy=2; loginBtn=new JButton("Login"); panel.add(loginBtn,c);
        c.gridx=1; registerBtn=new JButton("Register"); panel.add(registerBtn,c);

        add(panel);

        loginBtn.addActionListener(e->login());
        registerBtn.addActionListener(e->new RegisterFrame().setVisible(true));

        setLocationRelativeTo(null); 
        setVisible(true);
    }

    private void login(){
        try{
            java.util.List<User> users=loadUsers();
            String email=emailField.getText().trim();
            String pass=new String(passField.getPassword()).trim();
            for(User u:users){
                if(u.getEmail().equals(email) && u.getPassword().equals(pass)){
                    JOptionPane.showMessageDialog(this,"Login Successful ✅");
                    new DashboardFrame(u); 
                    dispose(); 
                    return;
                }
            }
            JOptionPane.showMessageDialog(this,"Invalid login!");
        }catch(Exception e){ e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private java.util.List<User> loadUsers(){
        try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream("users.dat"))){
            return (java.util.List<User>) ois.readObject();
        }catch(Exception e){ return new ArrayList<>(); }
    }

    public static void main(String[] args){ new LoginFrame(); }
}
