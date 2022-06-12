package utils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class ConnectionUtil {
    static int lport=3332;
    public static Connection conDB() throws SQLException {

        lport+=2;

        String rhost=""; //your MySQL Hostname
        String host=""; //your SSH Hostname
        int rport=3306;
        String user=""; //your SSH Username
        String password=""; //your SSH Password
        String dbuserName = ""; //your database username
        String dbpassword = ""; //your database password
        String url = "jdbc:mysql://localhost:"+lport+"/****"; //your database name instead of ****
        String driverName="com.mysql.jdbc.Driver";
        Connection conn = null;
        Session session= null;
        try{
            //Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            int assinged_port=session.setPortForwardingL(lport, rhost, rport);
            System.out.println("localhost:"+assinged_port+" -> "+rhost+":"+rport);
            System.out.println("Port Forwarded");

            //mysql database connectivity
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            conn = DriverManager.getConnection (url, dbuserName, dbpassword);
            System.out.println ("Database connection established");
            System.out.println("DONE");

        }catch(Exception e){
            e.printStackTrace();

        }
        return conn;
    }
    //make sure you add the lib
}
