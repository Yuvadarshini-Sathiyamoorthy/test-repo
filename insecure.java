// Vulnerable Java Application
import java.sql.*;
import java.io.*;
import java.security.MessageDigest;
import javax.servlet.http.*;

public class VulnerableApp {
    
    // CRITICAL: Hardcoded credentials
    private static final String DB_PASSWORD = "admin123";
    private static final String API_KEY = "AKIA1234567890ABCDEF";
    
    // HIGH: SQL Injection
    public User login(String username, String password) throws SQLException {
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost/mydb", "root", DB_PASSWORD);
        
        // Vulnerable SQL query
        String query = "SELECT * FROM users WHERE username = '" + username + 
                      "' AND password = '" + password + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        if (rs.next()) {
            return new User(rs.getString("username"));
        }
        return null;
    }
    
    // CRITICAL: Deserialization vulnerability
    public Object loadObject(InputStream input) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(input);
        // Unsafe deserialization
        return ois.readObject();
    }
    
    // HIGH: Path traversal
    public String readFile(String filename) throws IOException {
        // No path validation
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();
        return content.toString();
    }
    
    // MEDIUM: Weak cryptography
    public String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString();
    }
    
    // HIGH: Command injection
    public String executeCommand(String userInput) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        // Dangerous command execution
        Process process = runtime.exec("ping " + userInput);
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }
    
    // HIGH: XSS vulnerability
    public void displayUserInput(HttpServletResponse response, String input) 
            throws IOException {
        PrintWriter out = response.getWriter();
        // No input sanitization
        out.println("<h1>Hello " + input + "</h1>");
    }
    
    // MEDIUM: Information disclosure
    public void handleError(Exception e, HttpServletResponse response) 
            throws IOException {
        PrintWriter out = response.getWriter();
        // Exposing stack trace
        out.println("Error: " + e.getMessage());
        e.printStackTrace(out);
    }
}