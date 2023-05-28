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
public class Customer {
    
    private int id;
    private String name;
    private Address address;
    private boolean active;
    private Date creationDate;
    private String creator;
    private Date lastUpdate;
    private String lastEditor;
    private boolean isNew = false;
    
    public Customer(){
        String query = "SELECT MAX(customerId) AS max FROM customer;";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(rs.getInt("max")+1);
                this.isNew = true;
            }
        } catch(SQLException e){
            DBManager.failedDBConnect();
            System.out.println(e);
        }
    }
    
    public Customer(String newName){
        String query = "SELECT MAX(customerId) AS max FROM customer;";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(rs.getInt("max")+1);
                this.setName(newName);
                this.isNew = true;
            }
        } catch(SQLException e){
            DBManager.failedDBConnect();
            System.out.println(e);
        }
    }
    
    public Customer(int newId){
        String query = "SELECT customerName,addressId,active,createDate,createdBy,lastUpdate,lastUpdateBy FROM customer WHERE customerId = '"+newId+"';";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(newId);
                this.setName(rs.getString("customerName"));
                this.setAddress(new Address(rs.getInt("addressId")));
                if(rs.getInt("active") == 1){
                    this.setActive(true);
                } else{
                    this.setActive(false);
                }
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
    
    public void delete(){
        try{
            PreparedStatement stmt = DBManager.getConnection().prepareStatement("DELETE FROM customer WHERE customerId = ?;");
            stmt.setInt(1, this.getID());
            stmt.executeUpdate();
        } catch (SQLException e){
            System.err.print(e);
        }
    }
    
    public void save(String updater){
        PreparedStatement stmt;
        if (isNew){
            try{
                stmt = DBManager.getConnection().prepareStatement("INSERT INTO customer (customerName,addressId,active,createDate,createdBy,lastUpdate,lastUpdateBy) VALUES (?,?,?,?,?,?,?);");
                stmt.setString(1, this.getName());
                stmt.setInt(2, this.getAddress().getID());
                stmt.setBoolean(3, this.isActive());
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(5, updater);
                stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(7, updater);
                stmt.executeUpdate();
            } catch(SQLException e){
                System.out.println("Unable to prepare statment. Appointment not saved.");
                System.err.print(e);
            }
        } else{
            try{
                stmt = DBManager.getConnection().prepareStatement("UPDATE customer SET customerName = ?, addressId = ?, active = ?,lastUpdate = ?,lastUpdateBy = ? WHERE customerid = ?;");
                stmt.setString(1, this.getName());
                stmt.setInt(2, this.getAddress().getID());
                stmt.setBoolean(3, this.isActive());
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(5, updater);
                stmt.setInt(6,this.getID());
                stmt.executeUpdate();
            } catch(SQLException e){
                System.out.println("Unable to prepare statment. Appointment not saved.");
                System.err.print(e);
            }   

        }
        
    }
    
    //Sets
    public final void setID(int newId){
        this.id = newId;
    }
    public final void setName(String name){
        this.name = name;
    }
    public final void setAddress(Address newAddress){
        this.address = newAddress;
    }
    public final void setActive(boolean status){
        this.active = status;
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
    public final Address getAddress(){
        return this.address;
    }
    public final boolean isActive(){
        return this.active;
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
    public final boolean isNew(){
        return this.isNew;
    }
    
}
