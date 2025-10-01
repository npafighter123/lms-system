package libms;
import java.sql.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LibMS {
    public static final String DB_URL = "jdbc:sqlite:database.db";
    
    public Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            if (conn != null) {
                System.out.println("Connection successful!");
                }
            return conn;
                } catch (SQLException e) {
                    System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
    
    public boolean verifyLogin(String Username, String Password){
        String sql = "SELECT * from Users WHERE username = ? AND password =?";
        
        try(Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1, Username);
            pstmt.setString(2, Password);       
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next();        
            
        }catch(SQLException e){
            System.out.println("Connection failed: " + e.getMessage());
        }
        
        return false;
    }
    
    public static String hashPassword(String Password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(Password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static SecretKey secretKey;
    static {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
    }

    public static String decrypt(String strToDecrypt) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }
    
        public void fetchAll(JTable table){
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setRowCount(0);
        model.addColumn("ID");
        model.addColumn("BookName");
        model.addColumn("Author");
        model.addColumn("Category");
        model.addColumn("Amount");
        model.addColumn("Status");
        
        
        } 
        
        public ResultSet getAllBooks() {
            Connection conn = connect();
        try {
               String sql = "Select * FROM Library";
               PreparedStatement ps = conn.prepareStatement(sql);
               
               return ps.executeQuery();
               
            }catch (Exception e) {
                e.printStackTrace();
                
                return null;
            }
        }
        public void fetchAllBooks(JTable table){
            String sql = "Select * FROM Library";
            String[] columnNames = {"ID", "Book Name" , "Author", "Category" , "Amount", "Status"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            try{
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    int id = rs.getInt("ID");
                    String bookName = rs.getString("BookName");
                    String author = rs.getString("Author");
                    String category = rs.getString("Category");
                    int amount = rs.getInt("Amount");
                    String status = rs.getString("Status");
                    
                    Object[] row = {id,bookName,author,category,amount,status};
                    model.addRow(row);
                }
                table.setModel(model);
            }catch(SQLException e){
            System.out.println("Connection failed: " + e.getMessage());
            }  
        }
    }

