import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField, emailField, adminKeyField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;
    private JButton registerBtn;

    public RegisterFrame() {
        setTitle("Register"); 
        setSize(400,300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Name
        c.gridx=0; c.gridy=0; panel.add(new JLabel("Name:"), c);
        c.gridx=1; nameField = new JTextField(20); panel.add(nameField, c);

        // Row 1: Email
        c.gridx=0; c.gridy=1; panel.add(new JLabel("Email:"), c);
        c.gridx=1; emailField = new JTextField(20); panel.add(emailField, c);

        // Row 2: Password
        c.gridx=0; c.gridy=2; panel.add(new JLabel("Password:"), c);
        c.gridx=1; passField = new JPasswordField(20); panel.add(passField, c);

        // Row 3: Role
        c.gridx=0; c.gridy=3; panel.add(new JLabel("Role:"), c);
        c.gridx=1; roleBox = new JComboBox<>(new String[]{"Employee","Admin"}); panel.add(roleBox, c);

        // Row 4: Admin Key
        c.gridx=0; c.gridy=4; panel.add(new JLabel("Admin Key:"), c);
        c.gridx=1; adminKeyField = new JTextField(20); panel.add(adminKeyField, c);

        // Row 5: Register Button (span 2 columns)
        c.gridx=0; c.gridy=5; c.gridwidth=2;
        registerBtn = new JButton("Register"); panel.add(registerBtn, c);

        add(panel);

        registerBtn.addActionListener(e -> register());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void register() {
        try {
            java.util.List<User> users = loadUsers();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String role = (String) roleBox.getSelectedItem();

            if(role.equals("Admin") && !adminKeyField.getText().equals("SuperSecret123")){
                JOptionPane.showMessageDialog(this,"Invalid Admin Key!");
                return;
            }

            users.add(new User(name,email,pass,role));
            saveUsers(users);

            JOptionPane.showMessageDialog(this,"Registered Successfully ✅");
            dispose();
        } catch(Exception e){ e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private java.util.List<User> loadUsers() {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))){
            return (java.util.List<User>) ois.readObject();
        } catch(Exception e){ return new ArrayList<>(); }
    }

    private void saveUsers(java.util.List<User> users) {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat"))){
            oos.writeObject(users);
        } catch(Exception e){ e.printStackTrace(); }
    }
}
