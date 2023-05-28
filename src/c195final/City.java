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
public class City {
    
    private int id;
    private String name;
    private Country country;
    private Date creationDate;
    private String creator;
    private Date lastUpdate;
    private String lastEditor;
    
    public City(){
        String query = "SELECT MAX(cityId) AS max FROM city;";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(rs.getInt("max")+1);
            }
        } catch(SQLException e){
            DBManager.failedDBConnect();
            System.out.println(e);
        }
    }
    
    public City(String newName){
        String query = "SELECT MAX(cityId) AS max FROM city;";
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
    
    public City(int newId){
        String query = "SELECT city,countryId,createDate,createdBy,lastUpdate,lastUpdateBy FROM city WHERE cityId = '"+newId+"';";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(newId);
                this.setName(rs.getString("city"));
                this.setCountry(new Country(rs.getInt("countryId")));
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
            stmt = DBManager.getConnection().prepareStatement("INSERT INTO city (city,countryId,createDate,createdBy,lastUpdate,lastUpdateBy) VALUES (?,?,?,?,?,?);");
            stmt.setString(1, this.getName());
            stmt.setInt(2, this.getCountry().getID());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
            stmt.setString(4, updater);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
            stmt.setString(6, updater);
            stmt.executeUpdate();
        } catch(SQLException e){
            System.out.println("Unable to prepare statment. City not saved.");
            System.err.print(e);
        }   
    }
    
    //Sets
    public final void setID(int newId){
        this.id = newId;
    }
    public final void setName(String newName){
        this.name = newName;
    }
    public final void setCountry(Country newCountry){
        this.country = newCountry;
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
    public final Country getCountry(){
        return this.country;
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