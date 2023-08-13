import java.sql.*;
import java.util.*;

public class ToDoApp {
    
    private static Connection conn;  // Attribute of class so as to access it in all member functions

    // Main method
    public static void main(String[] args) {
        
        conn = getConnection();  // Connect with mysql database
        
        createTable();  // Create user_info table

        while (true) {

            //Menu
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("\nEnter your choice: ");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("\nThank You for using this app....");
                    System.out.println("\n====================================================================================================\n");
                    closeConnection();
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again....");
            
            }

            System.out.println("\n====================================================================================================");

        }
    
    }

    private static Connection getConnection() {
        
        // Required information to connect with mysql to_do database
        String url = "jdbc:mysql://localhost:3306/to_do";
        String user = "root";
        String password = "12345678";

        try {

            // Establish connection
            return DriverManager.getConnection(url, user, password);  // Will be stored in conn variable
        
        } catch (SQLException e) {
            
            e.printStackTrace();
        
        }

        return null;
    
    }

    private static void createTable() {
        
        try {
            
            Statement statement = conn.createStatement();  // Required for query execution
            String sql = "CREATE TABLE IF NOT EXISTS user_info ("
                    + "Email VARCHAR(100) PRIMARY KEY,"
                    + "Name VARCHAR(100),"
                    + "Phone VARCHAR(20),"
                    + "Age INT,"
                    + "Password VARCHAR(100)"
                    + ")";
            statement.executeUpdate(sql);  // Execute the above query, this is mainly used to modify the database like CREATE, INSERT, UPDATE, etc
            statement.close();
        
        } catch (SQLException e) {
            
            e.printStackTrace();
        }

    }

    private static void register() {
        
        Scanner scanner = new Scanner(System.in);

        // Taking user information as input
        System.out.print("\nEmail Id: ");
        String email = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            
            Statement statement = conn.createStatement();  // Statement object is used to execute sql queries and return results

            // Check whether user data already exists in user_info table
            String checkQuery = "SELECT * FROM user_info WHERE Email='" + email + "'";
            ResultSet resultSet = statement.executeQuery(checkQuery);  // ResultSet object is used to store the result of a sql query with datatype 'ResultSet'

            if (resultSet.next()) {
                
                System.out.println("\nUser already exists....");
                return;
            
            }

            // Inserting the user data into user_info table
            String insertQuery = "INSERT INTO user_info (Email, Name, Phone, Age, Password) VALUES ('"
                    + email + "', '" + name + "', '" + phone + "', " + age + ", '" + password + "')";
            statement.executeUpdate(insertQuery);

            // Creating a table with its name as email id of user
            String createTableQuery = "CREATE TABLE `" + email + "` ("
                    + "Todo VARCHAR(100),"
                    + "Date DATE"
                    + ")";
            statement.executeUpdate(createTableQuery);

            statement.close();  // Closing Statement object

            System.out.println("\nRegistration successful....");
        
        } catch (SQLException e) {
           
            e.printStackTrace();
        
        }
    
    }

    private static void login() {
        
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nEmail: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            
            Statement statement = conn.createStatement();

            String checkQuery = "SELECT * FROM user_info WHERE Email='" + email + "'";
            ResultSet resultSet = statement.executeQuery(checkQuery);

            // Check whether email is valid or not
            if (!resultSet.next()) {
                
                System.out.println("\nInvalid email....");
                return;
            
            }

            String passwordDB = resultSet.getString("Password");

            // Comparing user entered and password corresponding to email id in database
            if (!password.equals(passwordDB)) {
                
                System.out.println("\nIncorrect password....");
                return;
            
            }

            while (true) {
                
                // Performing some operations on Todo list
                System.out.println("\n1. Show Todos");
                System.out.println("2. Add Todo");
                System.out.println("3. Delete Todo");
                System.out.println("4. Modify Todo");
                System.out.println("5. Logout");
                System.out.print("\nEnter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    
                    case 1:
                        showTodos(email);
                        break;
                    case 2:
                        addTodo(email);
                        break;
                    case 3:
                        deleteTodo(email);
                        break;
                    case 4:
                        modifyTodo(email);
                        break;
                    case 5:
                        System.out.println("\nLogged out successfully....");
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please try again....");
                
                }

                System.out.println("\n----------------------------------------------\n");
            
            }
        
        } catch (SQLException e) {
            
            e.printStackTrace();
        
        }
    
    }

    private static void showTodos(String email) {
        
        try {
            
            Statement statement = conn.createStatement();

            String query = "SELECT Todo, Date FROM `" + email + "` ORDER BY Date";
            ResultSet resultSet = statement.executeQuery(query);

            if (!resultSet.next()) {
                
                System.out.println("\nNo todo list....");
                return;
            
            }

            System.out.println();
            System.out.printf("%-30s %s\n\n","Todo","Date");
            
            do {
                String todo = resultSet.getString("Todo");
                String date = resultSet.getString("Date");
                System.out.printf("%-30s %s\n",todo,date);
            } while (resultSet.next());

            statement.close();
        
        } catch (SQLException e) {
            
            e.printStackTrace();
        
        }
    
    }

    private static void addTodo(String email) {
        
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nEnter todo: ");
        String todo = scanner.nextLine();
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        try {
            
            Statement statement = conn.createStatement();

            String insertQuery = "INSERT INTO `" + email + "` (Todo, Date) VALUES ('" + todo + "', '" + date + "')";
            statement.executeUpdate(insertQuery);

            statement.close();

            System.out.println("\nTodo added successfully....");
        
        } catch (SQLException e) {
            
            e.printStackTrace();
        
        }
    
    }

    private static void deleteTodo(String email) {
        
        Scanner scanner = new Scanner(System.in);

        try {
            
            Statement statement = conn.createStatement();

            String query = "SELECT * FROM `" + email + "`";
            ResultSet resultSet = statement.executeQuery(query);

            if (!resultSet.next()) {
                
                System.out.println("\nNo todo list....");
                return;
            
            }

            System.out.print("\nEnter todo to delete: ");
            String todo = scanner.nextLine();

            String checkQuery = "SELECT * FROM `" + email + "` WHERE Todo='" + todo + "'";
            resultSet = statement.executeQuery(checkQuery);

            if (!resultSet.next()) {
                
                System.out.println("\nEntered todo not found....");
                return;
            
            }

            String deleteQuery = "DELETE FROM `" + email + "` WHERE Todo='" + todo + "'";
            statement.executeUpdate(deleteQuery);

            statement.close();

            System.out.println("\nTodo deleted successfully....");
        
        } catch (SQLException e) {
           
            e.printStackTrace();
        
        }
    
    }

    private static void modifyTodo(String email) {
        
        Scanner scanner = new Scanner(System.in);

        try {
            
            Statement statement = conn.createStatement();

            String query = "SELECT * FROM `" + email + "`";
            ResultSet resultSet = statement.executeQuery(query);

            if (!resultSet.next()) {
                
                System.out.println("\nNo todo list....");
                return;
            
            }

            System.out.print("\nEnter todo to modify: ");
            String oldTodo = scanner.nextLine();

            String checkQuery = "SELECT * FROM `" + email + "` WHERE Todo='" + oldTodo + "'";
            resultSet = statement.executeQuery(checkQuery);

            if (!resultSet.next()) {
                
                System.out.println("\nEntered todo not found....");
                return;
            
            }

            System.out.print("Enter new todo: ");
            String newTodo = scanner.nextLine();
            System.out.print("Enter new date (YYYY-MM-DD): ");
            String newDate = scanner.nextLine();

            String updateQuery = "UPDATE `" + email + "` SET Todo='" + newTodo + "', Date='" + newDate + "' WHERE Todo='" + oldTodo + "'";
            statement.executeUpdate(updateQuery);

            statement.close();

            System.out.println("\nTodo modified successfully....");
        
        } catch (SQLException e) {
            
            e.printStackTrace();
        
        }
    
    }

    private static void closeConnection() {
        
        try {
            
            conn.close();
        
        } catch (SQLException e) {
            
            e.printStackTrace();
        
        }
    
    }

}