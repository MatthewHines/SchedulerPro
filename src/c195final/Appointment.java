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
public class Appointment {
    
    private int id;
    private Customer customer;
    private String custName;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String url = "";
    private Date start;
    private Date end;
    private int duration;
    private Date apptCreationDate;
    private String apptCreator;
    private Date lastUpdate;
    private String lastEditor;
    private boolean newAppt = false;
    
    public Appointment(){
        String query = "SELECT MAX(appointmentid) AS max FROM appointment;";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(rs.getInt("max")+1);
                this.newAppt = true;
            }
        } catch(SQLException e){
            DBManager.failedDBConnect();
            System.out.println(e);
        }
    }
    
    public Appointment(int newId){
        String query = "SELECT customerId,title,description,location,contact,url,start,end,createDate,createdBy,lastUpdate,lastUpdateBy FROM appointment WHERE appointmentid = '"+newId+"';";
        try(ResultSet rs = DBManager.query(query)){
            if(rs.next()){
                this.setID(newId);
                this.setCustomer(new Customer(rs.getInt("customerId")));
                this.setTitle(rs.getString("title"));
                this.setDescription(rs.getString("description"));
                this.setLocation(rs.getString("location"));
                this.setContact(rs.getString("contact"));
                this.setURL(rs.getString("url"));
                this.setStartTime(rs.getTimestamp("start"));
                this.setEndTime(rs.getTimestamp("end"));
                this.setCreationDate(rs.getTimestamp("createDate"));
                this.setCreator(rs.getString("createdBy"));
                this.setLastUpdate(rs.getTimestamp("lastUpdate"));
                this.setLastEditor(rs.getString("lastUpdateBy"));
                this.setDuration();
            }
        } catch(SQLException e){
            DBManager.failedDBConnect();
            System.out.println(e);
        }
    }
    
    public void save(String updater){
        PreparedStatement stmt;
        if (newAppt){
            try{
                stmt = DBManager.getConnection().prepareStatement("INSERT INTO appointment (customerId,title,description,location,contact,url,start,end,createDate,createdBy,lastUpdate,lastUpdateBy) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                stmt.setInt(1, this.getCustomer().getID());
                stmt.setString(2, this.getTitle());
                stmt.setString(3, this.getDescription());
                stmt.setString(4, this.getLocation());
                stmt.setString(5, this.getContact());
                stmt.setString(6, this.getURL());
                stmt.setTimestamp(7, new Timestamp(this.getStartTime().getTime()));
                stmt.setTimestamp(8, new Timestamp(this.getEndTime().getTime()));
                stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(10, updater);
                stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(12, updater);
                stmt.executeUpdate();
            } catch(SQLException e){
                System.out.println("Unable to prepare statment. Appointment not saved.");
                System.err.print(e);
            }   
        } else{
            try{
                stmt = DBManager.getConnection().prepareStatement("UPDATE appointment SET customerId = ?, title = ?, description = ?, location = ?, contact = ?, url = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ? WHERE appointmentId = "+this.getID()+";");
                stmt.setInt(1, this.getCustomer().getID());
                stmt.setString(2, this.getTitle());
                stmt.setString(3, this.getDescription());
                stmt.setString(4, this.getLocation());
                stmt.setString(5, this.getContact());
                stmt.setString(6, this.getURL());
                stmt.setTimestamp(7, new Timestamp(this.getStartTime().getTime())); 
                stmt.setTimestamp(8, new Timestamp(this.getEndTime().getTime()));
                stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT"))));
                stmt.setString(10, updater);
                stmt.executeUpdate();
            } catch(SQLException e){
                System.out.println("Unable to prepare statment. Appointment not saved.");
                System.err.print(e);
            }

        }
    }
    
    public void delete(){
        try{
            PreparedStatement stmt = DBManager.getConnection().prepareStatement("DELETE FROM appointment WHERE appointmentid = ?;");
            stmt.setInt(1, this.getID());
            stmt.executeUpdate();
        } catch (SQLException e){
            System.err.print(e);
        }
    }
    
    //Sets
    public final void setID(int newId){
        this.id = newId;
    }
    public final void setCustomer(Customer newCust){
        this.customer = newCust;
        this.setCustName();
    }
    public final void setTitle(String newTitle){
        this.title = newTitle;
    }
    public final void setDescription(String newDesc){
        this.description = newDesc;
    }
    public final void setLocation(String newLoc){
        this.location = newLoc;
    }
    public final void setContact(String newContact){
        this.contact = newContact;
    }
    public final void setURL(String newURL){
        this.url = newURL;
    }
    public final void setStartTime(Date newStart){
        this.start = newStart;
    }
    public final void setEndTime(Date newEnd){
        this.end = newEnd;
    }
    public final void setCreationDate(Date date){
        this.apptCreationDate = date;
    }
    public final void setCreator(String creator){
        this.apptCreator = creator;
    }
    public final void setLastUpdate(Date date){
        this.lastUpdate = date;
    }
    public final void setLastEditor(String editor){
        this.lastEditor = editor;
    }
    public final void setCustName(){
        this.custName = this.getCustomer().getName();
    }
    public final void setDuration(){
        this.duration = (int) ((end.getTime() - start.getTime())/60/1000);
    }
    public final void getNewAppt(boolean b){
        this.newAppt = b;
    }
    
    
    //Gets
    public final int getID(){
        return this.id;
    }
    public final Customer getCustomer(){
        return this.customer;
    }
    public final String getTitle(){
        return this.title;
    }
    public final String getDescription(){
        return this.description;
    }
    public final String getLocation(){
        return this.location;
    }
    public final String getContact(){
        return this.contact;
    }
    public final String getURL(){
        return this.url;
    }
    public final Date getStartTime(){
        return this.start;
    }
    public final Date getEndTime(){
        return this.end;
    }
    public final Date getCreationDate(){
        return this.apptCreationDate;
    }
    public final String getCreator(){
        return this.apptCreator;
    }
    public final Date getLastUpdate(){
        return this.lastUpdate;
    }
    public final String getLastEditor(){
        return this.lastEditor;
    }
    public final String getCustName(){
        return this.custName;
    }
    public final int getDuration(){
        return this.duration;
    }
    public final boolean getNewAppt(){
        return this.newAppt;
    }
}