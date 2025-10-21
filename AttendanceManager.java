import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceManager {

    private static final String ATT_FILE = "attendance.txt";
    private static final String LEAVE_FILE = "leaves.dat";

    // Mark attendance with 12-hour check
    public static boolean markAttendance(String email) {
        try {
            File file = new File(ATT_FILE);
            List<String> lines = new ArrayList<>();
            if(file.exists()) lines = java.nio.file.Files.readAllLines(file.toPath());

            for(String line : lines){
                String[] parts = line.split(";");
                if(parts[0].equals(email)){
                    LocalDateTime last = LocalDateTime.parse(parts[1]);
                    if(last.plusHours(12).isAfter(LocalDateTime.now())) return false;
                }
            }

            String entry = email + ";" + LocalDateTime.now().toString();
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(file,true))){
                bw.write(entry);
                bw.newLine();
            }
            return true;
        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    // Load leave requests
    public static List<LeaveRequest> loadLeaves() {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LEAVE_FILE))){
            return (List<LeaveRequest>) ois.readObject();
        }catch(Exception e){ return new ArrayList<>(); }
    }

    // Save leave requests
    public static void saveLeaves(List<LeaveRequest> leaves){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEAVE_FILE))){
            oos.writeObject(leaves);
        }catch(Exception e){ e.printStackTrace(); }
    }
}
