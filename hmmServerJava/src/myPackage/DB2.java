/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author XN
 */
public class DB2 {

    public DB2() {
    }
    
    public Connection connect() {
        String driverPath = "com.mysql.jdbc.Driver";
        String Url = "jdbc:mysql://localhost:3306/";
        String dbName = "hajira_khata";
        String UserName = "root";
        String PassWord = "xn5719";
        Connection conn = null;
        try {
            Class.forName(driverPath).newInstance();
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException ex) {
            Logger.getLogger(DB2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            conn = DriverManager.getConnection(Url+dbName,UserName,PassWord);
        } catch (SQLException ex) {
            Logger.getLogger(DB2.class.getName()).log(Level.SEVERE, null, ex);
        }

         return conn;
    }

    
    public int checkSignIn(String uname, String pass, String orgC) {
        int x=0;
        Connection conn;
        Statement s = null ;
        ResultSet rs = null;
        conn=connect();
        try {
            s = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(DB2.class.getName()).log(Level.SEVERE, null, ex);
        }
        String sql = "SELECT user_name FROM sign_in" +
        " WHERE user_name='" + uname + "'" +
        " AND password='" + pass + "'" +
        " AND organization_code='" + orgC+"'"; 
            
        try {
            rs = s.executeQuery(sql);
            
            if (rs.next()) {
                
                x=1;
                //System.out.println("found");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            rs.close();
            s.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DB2.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        if(x==1)
        {
            return 1;
        }
        else return 0;
        
    }
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DB2 s = new DB2();
        //s.getDistAttendence("0800384E5628", "2016", "02");
        //String x;
        //s.getOnlineUser();
        //s.checkValidity("xn", "1234");
        int x = s.checkSignIn("xnn", "123", "org01");
        //System.out.println("hello");
        System.out.println(x);
    }
    
}
