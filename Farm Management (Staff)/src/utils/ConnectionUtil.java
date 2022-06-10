/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

        String rhost="selimh.mysql.pythonanywhere-services.com";
        String host="ssh.pythonanywhere.com";
        int rport=3306;
        String user="selimh";
        String password="COE4182021";
        String dbuserName = "selimh";
        String dbpassword = "COE4182021";
        String url = "jdbc:mysql://localhost:"+lport+"/selimh$FARM1";
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
