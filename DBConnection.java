import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnection {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/bankdb";
        String user = "root";
        String password = "ABHI@4903";   // put your MySQL password here

        try {

            Connection con = DriverManager.getConnection(url, user, password);

            System.out.println("Connected Successfully!");

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            System.out.println("User Data:");

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                double balance = rs.getDouble("balance");

                System.out.println(id + " " + name + " " + email + " " + balance);
            }

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}