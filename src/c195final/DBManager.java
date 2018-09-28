/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195final;

import java.sql.DriverManager; 
import java.sql.Connection; 
import java.sql.ResultSet;
import java.sql.SQLException;   
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Matthew
 */
public class DBManager {     

    private static Connection conn;
    private static final String DB = "U04s3z";
    private static final String URL = "jdbc:mysql://52.206.157.109/" + DB + "?useSSL=true";
    private static final String USER = "U04s3z";
    private static final String PASS = "53688332541";

    public static Connection connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException cnfe){
            System.err.println("Error: "+cnfe.getMessage());
        }
        try{
            conn = DriverManager.getConnection(URL,USER,PASS);
        } catch(SQLException e){
            failedDBConnect();
            System.err.println(e);
        }
        return conn;
    }
    
    public static void close(){
        try{
            if(conn !=null && !conn.isClosed())
                conn.close();
        } catch(SQLException e){
            System.err.print(e);
        }
    }
    
    public static void failedDBConnect(){
        ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.NONE,"Action failed. Could not connect to the server.\nPlease check your connection or contact an administrator.",ok);
        alert.setTitle("Scheduler Pro - v1.0");
        alert.show(); 
    }
    
    public static Connection getConnection() {
        try{
            if(conn !=null && !conn.isClosed())
                return conn;
        } catch(SQLException e){
            failedDBConnect();
            System.err.print(e);
        }
        connect();
        return conn;

    }
    
    public static ResultSet query(String query){
        ResultSet rs = null;
        try{
            rs = getConnection().createStatement().executeQuery(query);
        } catch(SQLException e){
            failedDBConnect();
            System.err.print(e);
        }
        return rs;
    }
    
} 
