import java.sql.*;
import java.util.Scanner;

public class BankApp {

    static final String url = "jdbc:mysql://localhost:3306/bankdb";
    static final String user = "root";
    static final String password = "ABHI@4903"; 
    public static void createAccount(Connection con, Scanner sc) throws Exception {

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(name,email,password,balance) VALUES(?,?,?,0)",
                Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, pass);

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            int id = rs.getInt(1);
            System.out.println("Account Created Successfully!");
            System.out.println("Your User ID is: " + id);
        }
    }

public static void deposit(Connection con, Scanner sc) throws Exception {

    System.out.print("Enter User ID: ");
    int id = sc.nextInt();

    System.out.print("Enter Amount: ");
    double amount = sc.nextDouble();

    if(amount <= 0){
        System.out.println("Invalid amount!");
        return;
    }

    PreparedStatement ps = con.prepareStatement(
            "UPDATE users SET balance = balance + ? WHERE id=?");

    ps.setDouble(1, amount);
    ps.setInt(2, id);

    int rows = ps.executeUpdate();

    if(rows > 0){
        PreparedStatement tr = con.prepareStatement(
                "INSERT INTO transactions(user_id,type,amount) VALUES(?,?,?)");

        tr.setInt(1, id);
        tr.setString(2, "Deposit");
        tr.setDouble(3, amount);
        tr.executeUpdate();

        System.out.println("Money Deposited Successfully!");
    }
    else{
        System.out.println("User not found!");
    }
}

  public static void withdraw(Connection con, Scanner sc) throws Exception {

    System.out.print("Enter User ID: ");
    int id = sc.nextInt();

    System.out.print("Enter Amount: ");
    double amount = sc.nextDouble();

    PreparedStatement ps = con.prepareStatement(
            "UPDATE users SET balance = balance - ? WHERE id=?");

    ps.setDouble(1, amount);
    ps.setInt(2, id);
    ps.executeUpdate();

    PreparedStatement tr = con.prepareStatement(
            "INSERT INTO transactions(user_id,type,amount) VALUES(?,?,?)");

    tr.setInt(1, id);
    tr.setString(2, "Withdraw");
    tr.setDouble(3, amount);
    tr.executeUpdate();

    System.out.println("Money Withdrawn Successfully!");
}

    public static void checkBalance(Connection con, Scanner sc) throws Exception {

        System.out.print("Enter User ID: ");
        int id = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
                "SELECT balance FROM users WHERE id=?");

        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Current Balance: " + rs.getDouble("balance"));
        } else {
            System.out.println("User not found.");
        }
    }

    public static void transactionHistory(Connection con, Scanner sc) throws Exception {

    System.out.print("Enter User ID: ");
    int id = sc.nextInt();

    PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM transactions WHERE user_id=?");

    ps.setInt(1, id);

    ResultSet rs = ps.executeQuery();

    System.out.println("---- Transaction History ----");

    while(rs.next()) {

        System.out.println(
            rs.getInt("tid") + " | " +
            rs.getString("type") + " | " +
            rs.getDouble("amount") + " | " +
            rs.getTimestamp("date")
        );
    }
}

public static void viewUsers(Connection con) throws Exception {

    Statement st = con.createStatement();

    ResultSet rs = st.executeQuery("SELECT id,name,email,balance FROM users");

    System.out.println("---- All Users ----");

    while(rs.next()){
        System.out.println(
            rs.getInt("id") + " | " +
            rs.getString("name") + " | " +
            rs.getString("email") + " | " +
            rs.getDouble("balance")
        );
    }
}

    public static void main(String[] args) {

        try {

            Connection con = DriverManager.getConnection(url, user, password);
            Scanner sc = new Scanner(System.in);

            while (true) {

                System.out.println("\n===== Online Banking System =====");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Check Balance");
                System.out.println("5. Transaction History");
                System.out.println("6. View All Users");
                System.out.println("7. Exit");

                System.out.print("Choose Option: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:
                        createAccount(con, sc);
                        break;

                    case 2:
                        deposit(con, sc);
                        break;

                    case 3:
                        withdraw(con, sc);
                        break;

                    case 4:
                        checkBalance(con, sc);
                        break;

                    case 5:
                        transactionHistory(con, sc);
                        break;

                    case 6:
                        viewUsers(con);
                        break;

                    case 7:
                        System.exit(0);

                    default:
                        System.out.println("Invalid Choice");
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}