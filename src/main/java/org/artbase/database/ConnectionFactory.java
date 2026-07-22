package org.artbase.database;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private Connection connection;

    public Connection getConnection() throws SQLException,
            ClassNotFoundException, IOException {
        Class.forName("org.postgresql.Driver");
        Properties props = new Properties();
        props.load(new FileInputStream("database.properties"));
        return DriverManager.getConnection(props.getProperty("url"), props);
    }

}
