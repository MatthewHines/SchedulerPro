/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195final;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author Matthew
 */
public class Country {
    
    private int id;
    private String name;
    private Date creationDate;
    private String creator;
    private Date lastUpdate;
    private String lastEditor;
    
    public Country(){
        String query = "SELECT MAX(countryid) AS max FROM country;";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(rs.getInt("max")+1);
            }
        } catch(SQLException e){
            DBManager.failedDBConnect();
            System.out.println(e);
        }
    }
    
    public Country(String newName){
        String query = "SELECT MAX(countryid) AS max FROM country;";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(rs.getInt("max")+1);
                this.setName(newName);
            }
        } catch(SQLException e){
            DBManager.failedDBConnect();
            System.out.println(e);
        }
    }
    
    public Country(int newId){
        String query = "SELECT country,createDate,createdBy,lastUpdate,lastUpdateBy FROM country WHERE countryid = '"+newId+"';";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(newId);
                this.setName(rs.getString("country"));
                this.setCreationDate(rs.getTimestamp("createDate"));
                this.setCreator(rs.getString("createdBy"));
                this.setLastUpdate(rs.getTimestamp("lastUpdate"));
                this.setLastEditor(rs.getString("lastUpdateBy"));
            }
        } catch(SQLException e){
            DBManager.failedDBConnect();
            System.out.println(e);
        }
    }
    
    public void save(String updater){
        PreparedStatement stmt;
        try{
            stmt = DBManager.getConnection().prepareStatement("INSERT INTO country (country,createDate,createdBy,lastUpdate,lastUpdateBy) VALUES (?,?,?,?,?);");
            stmt.setString(1, this.getName());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
            stmt.setString(3, updater);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
            stmt.setString(5, updater);
            stmt.executeUpdate();
        } catch(SQLException e){
            System.out.println("Unable to prepare statment. Country not saved.");
            System.err.print(e);
        }   
    }
    
    //Sets
    public final void setID(int newId){
        this.id = newId;
    }
    public final void setName(String name){
        this.name = name;
    }
    public final void setCreationDate(Timestamp created){
        this.creationDate = created;
    }
    public final void setCreator(String newCreator){
        this.creator = newCreator;
    }
    public final void setLastUpdate(Timestamp last){
        this.lastUpdate = last;
    }
    public final void setLastEditor(String last){
        this.lastEditor = last;
    }
    
    //Gets
    public final int getID(){
        return this.id;
    }
    public final String getName(){
        return this.name;
    }
    public final Date getCreationDate(){
        return this.creationDate;
    }
    public final String getCreator(){
        return this.creator;
    }
    public final Date getLastUpdate(){
        return this.lastUpdate;
    }
    public final String getLastEditor(){
        return this.lastEditor;
    }
}