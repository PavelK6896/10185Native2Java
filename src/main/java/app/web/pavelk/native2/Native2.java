package app.web.pavelk.native2;

import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Native2 {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) throws SQLException {
        connect();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String next = scanner.next();
            if (next.equals("exit")) {
                try {
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            } else if (next.equals("get")) {
                getAll();
            } else if (next.equals("add")) {
                add(scanner.next());
                getAll();
            } else {
                System.out.println(next);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void add(String s) throws SQLException {
        String query = "INSERT INTO nat (text) VALUES (?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, s);
            preparedStatement.executeUpdate();
        }
    }

    public static void getAll() throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT n.* FROM nat n ");
        while (resultSet.next()) {
            long id = resultSet.getLong(1);
            String string = resultSet.getString(2);
            System.out.println(id + " " + string);
        }
    }

    public static void connect() {
        try {
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl == null) {
                dbUrl = System.getProperty("db");
                System.out.println("property " + dbUrl);

                URL resource = Native2.class.getClassLoader().getResource("");
                if (resource != null) {
                    String path = resource.getPath();
                    System.out.println("dev mode " + path);
                    dbUrl = path.replace("build/classes/java/main/", "") + "native1.sqlite";
                }

                if (dbUrl == null) {
                    try (InputStream is = Native2.class.getClassLoader().getResourceAsStream("application.properties")) {
                        Properties properties = new Properties();
                        if (is != null) {
                            properties.load(is);
                            dbUrl = properties.getProperty("db");
                        }
                        if (dbUrl == null) {
                            System.out.println("static path");
                            dbUrl = "../../native1.sqlite";
                        }
                    }
                }
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbUrl);
            statement = connection.createStatement();
            System.out.println("connect db " + dbUrl);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                statement.close();
                connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

}
