import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardFrame extends JFrame {

    private User currentUser;
    private JButton markBtn, viewBtn, leaveBtn, leaveHistoryBtn, empBtn, logoutBtn;

    public DashboardFrame(User u){
        currentUser = u;
        setTitle("Dashboard - " + u.getRole());
        setSize(900, 550);
        setLayout(new BorderLayout(10, 10));

        JLabel welcomeLabel = new JLabel("Welcome " + u.getName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        markBtn = new JButton("Mark Attendance");
        viewBtn = new JButton("View Attendance History");
        leaveBtn = new JButton("Request Leave");
        leaveHistoryBtn = new JButton("Leave Requests");
        empBtn = new JButton("View Employees");
        logoutBtn = new JButton("Logout");

        btnPanel.add(markBtn);
        btnPanel.add(viewBtn);
        btnPanel.add(leaveBtn);
        if(u.getRole().equalsIgnoreCase("Admin")) {
            btnPanel.add(leaveHistoryBtn);
            btnPanel.add(empBtn);
        }
        btnPanel.add(logoutBtn);

        add(btnPanel, BorderLayout.WEST);

        // Button actions
        markBtn.addActionListener(e -> markAttendance());
        viewBtn.addActionListener(e -> showAttendanceHistory());
        leaveBtn.addActionListener(e -> requestLeave());
        leaveHistoryBtn.addActionListener(e -> showLeaveRequests());
        empBtn.addActionListener(e -> showEmployeeList());
        logoutBtn.addActionListener(e -> { new LoginFrame(); dispose(); });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ========== MARK ATTENDANCE ==========
    private void markAttendance(){
        boolean success = AttendanceManager.markAttendance(currentUser.getEmail());
        if(success)
            JOptionPane.showMessageDialog(this,"Attendance marked ✅");
        else
            JOptionPane.showMessageDialog(this,"You already marked attendance within the last 12 hours ⚠","Warning",JOptionPane.WARNING_MESSAGE);
    }

    // ========== VIEW ATTENDANCE ==========
    private void showAttendanceHistory(){
        try{
            File file = new File("attendance.txt");
            if(!file.exists()){
                JOptionPane.showMessageDialog(this,"No attendance records found!");
                return;
            }

            List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
            String[] columns = {"Name","Email","Date & Time"};
            DefaultTableModel model = new DefaultTableModel(columns,0);

            List<User> allUsers = loadUsers();
            for(String line: lines){
                String[] parts = line.split(";");
                if(parts.length < 2) continue;
                String email = parts[0];
                String timestamp = parts[1];
                String name = email;

                for(User u: allUsers){
                    if(u.getEmail().equals(email)){ name = u.getName(); break; }
                }

                if(currentUser.getRole().equals("Admin") || email.equals(currentUser.getEmail()))
                    model.addRow(new Object[]{name,email,timestamp});
            }

            JTable table = new JTable(model);
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(700,300));
            JOptionPane.showMessageDialog(this, scroll, "Attendance History", JOptionPane.INFORMATION_MESSAGE);

        }catch(Exception e){ e.printStackTrace(); }
    }

    // ========== REQUEST LEAVE ==========
    private void requestLeave(){
        JTextField dateField = new JTextField();
        JTextField reason = new JTextField();
        Object[] message = {"Leave Date (YYYY-MM-DD):", dateField,"Reason:",reason};
        int option = JOptionPane.showConfirmDialog(this, message, "Request Leave", JOptionPane.OK_CANCEL_OPTION);
        if(option==JOptionPane.OK_OPTION){
            try{
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                LeaveRequest lr = new LeaveRequest(currentUser.getEmail(), date, reason.getText().trim());
                List<LeaveRequest> leaves = AttendanceManager.loadLeaves();
                leaves.add(lr);
                AttendanceManager.saveLeaves(leaves);
                JOptionPane.showMessageDialog(this,"Leave requested successfully ✅");
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,"Invalid date format! Use YYYY-MM-DD","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ========== LEAVE REQUESTS TABLE ==========
    private void showLeaveRequests(){
        try{
            List<LeaveRequest> leaves = AttendanceManager.loadLeaves();
            if(leaves.isEmpty()){
                JOptionPane.showMessageDialog(this,"No leave requests yet.");
                return;
            }

            String[] columns = {"Email","Date","Reason","Status"};
            DefaultTableModel model = new DefaultTableModel(columns,0);
            for(LeaveRequest lr: leaves){
                if(currentUser.getRole().equals("Admin") || lr.getEmail().equals(currentUser.getEmail())){
                    model.addRow(new Object[]{lr.getEmail(), lr.getDate(), lr.getReason(), lr.getStatus()});
                }
            }

            JTable table = new JTable(model);
            if(currentUser.getRole().equalsIgnoreCase("Admin")){
                table.getColumnModel().getColumn(3).setCellEditor(
                        new DefaultCellEditor(new JComboBox<>(new String[]{"Pending","Approved","Rejected"}))
                );
            }

            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(700,300));
            int opt = JOptionPane.showConfirmDialog(this, scroll,
                    currentUser.getRole().equals("Admin")?"Manage Leave Requests":"Leave Status",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if(opt == JOptionPane.OK_OPTION && currentUser.getRole().equals("Admin")){
                for(int i=0; i<table.getRowCount(); i++){
                    String email = (String)table.getValueAt(i,0);
                    String status = (String)table.getValueAt(i,3);
                    for(LeaveRequest lr : leaves){
                        if(lr.getEmail().equals(email)){
                            lr.setStatus(status);
                        }
                    }
                }
                AttendanceManager.saveLeaves(leaves);
                JOptionPane.showMessageDialog(this,"Leave requests updated ✅");
            }

        }catch(Exception e){ e.printStackTrace(); }
    }

    // ========== EMPLOYEE LIST (POPUP BOX) ==========
    private void showEmployeeList(){
        List<User> users = loadUsers();
        String[] columns = {"Name","Email"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        for(User user: users){
            if(user.getRole().equalsIgnoreCase("Employee"))
                model.addRow(new Object[]{user.getName(), user.getEmail()});
        }

        JTable empTable = new JTable(model);
        JScrollPane scroll = new JScrollPane(empTable);
        scroll.setPreferredSize(new Dimension(700,300));

        JOptionPane.showMessageDialog(this, scroll,
                "Total Employees: " + model.getRowCount(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ========== LOAD USERS ==========
    private List<User> loadUsers(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))){
            return (List<User>) ois.readObject();
        }catch(Exception e){ return new ArrayList<>(); }
    }
}
