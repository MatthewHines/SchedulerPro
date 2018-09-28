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
public class Address {
    
    private int id;
    private String address;
    private String address2;
    private City city;
    private String postalCode;
    private String phone;
    private Date creationDate;
    private String creator;
    private Date lastUpdate;
    private String lastEditor;
    private boolean isNew = false;
    
    public Address(){
        String query = "SELECT MAX(addressid) AS max FROM address;";
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
    
    public Address(int newId){
        String query = "SELECT address,address2,cityId,postalCode,phone,createDate,createdBy,lastUpdate,lastUpdateBy FROM address WHERE addressid = '"+newId+"';";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(newId);
                this.setAddress(rs.getString("address"));
                this.setAddress2(rs.getString("address2"));
                this.setCity(new City(rs.getInt("cityId")));
                this.setPhone(rs.getString("phone"));
                this.setPostalCode(rs.getString("postalCode"));
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
        if(isNew){
            try{
                stmt = DBManager.getConnection().prepareStatement("INSERT INTO address (address,address2,cityId,postalCode,phone,createDate,createdBy,lastUpdate,lastUpdateBy) VALUES (?,?,?,?,?,?,?,?,?);");
                stmt.setString(1, this.getAddress());
                stmt.setString(2, this.getAddress2());
                stmt.setInt(3, this.getCity().getID());
                stmt.setString(4, this.getPostalCode());
                stmt.setString(5, this.getPhone());
                stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(7, updater);
                stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(9, updater);
                stmt.executeUpdate();
            } catch(SQLException e){
                System.out.println("Unable to prepare statment. Address not saved.");
                System.err.print(e);
            }  
        } else{
            try{
                stmt = DBManager.getConnection().prepareStatement("UPDATE address SET address = ?,address2 = ?,cityId = ?,postalCode = ?,phone = ?,createDate = ?,createdBy = ?,lastUpdate = ?,lastUpdateBy = ? WHERE addressid = ?;");
                stmt.setString(1, this.getAddress());
                stmt.setString(2, this.getAddress2());
                stmt.setInt(3, this.getCity().getID());
                stmt.setString(4, this.getPostalCode());
                stmt.setString(5, this.getPhone());
                stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(7, updater);
                stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(9, updater);
                stmt.setInt(10, this.getID());
                stmt.executeUpdate();
            } catch(SQLException e){
                System.out.println("Unable to prepare statment. Address not saved.");
                System.err.print(e);
            }  
        }
    }
    
    //Sets
    public final void setID(int newId){
        this.id = newId;
    }
    public final void setAddress(String newAddress){
        this.address = newAddress;
    }
    public final void setAddress2(String newAddress){
        this.address2 = newAddress;
    }
    public final void setCity(City newCity){
        this.city = newCity;
    }
    public final void setPostalCode(String newPostCode){
        this.postalCode = newPostCode;
    }
    public final void setPhone(String newPhone){
        this.phone = newPhone;
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
    public final String getAddress(){
        return this.address;
    }
    public final String getAddress2(){
        return this.address2;
    }
    public final City getCity(){
        return this.city;
    }
    public final String getPostalCode(){
        return this.postalCode;
    }
    public final String getPhone(){
        return this.phone;
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