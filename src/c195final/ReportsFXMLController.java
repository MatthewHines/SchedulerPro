/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195final;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Matthew
 */
public class ReportsFXMLController implements Initializable {

    @FXML javafx.scene.control.Label currentTime;
    @FXML javafx.scene.control.Label username;
    
    @FXML javafx.scene.control.Button consultantReportBtn;
    @FXML javafx.scene.control.Button typeByMonthReportBtn;
    @FXML javafx.scene.control.Button CustomerApptReportBtn;
    @FXML javafx.scene.control.Button exitBtn;
    
    @FXML javafx.scene.control.ComboBox userCBox;
    @FXML javafx.scene.control.ComboBox<ZonedDateTime> monthCBox;
    @FXML javafx.scene.control.ComboBox<Customer> customerCBox;
    @FXML javafx.scene.control.ComboBox timezoneCBox;
    
    @FXML javafx.scene.control.CheckBox filterChk;
    @FXML javafx.scene.control.RadioButton todayRBtn;
    @FXML javafx.scene.control.RadioButton weekRBtn;
    @FXML javafx.scene.control.RadioButton monthRBtn;
    
    //Table Columns
    @FXML javafx.scene.control.TableColumn<Appointment, Date> startTimeCol;
    @FXML javafx.scene.control.TableColumn<Appointment, Number> durationCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> custNameCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> contactNameCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> titleCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> locationCol;
    @FXML javafx.scene.control.TableColumn<Appointment, Date> dateCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> countCol;
    
    //Overlay for loading
    @FXML javafx.scene.layout.StackPane loadingOverlay;
    
    //TableView and DB
    @FXML javafx.scene.control.TableView apptTable;
    @FXML private ObservableList<Appointment> apptData = FXCollections.observableArrayList();
    
    //For count-by-month report
    @FXML private ObservableList<Appointment> summaryData = FXCollections.observableArrayList();
    
    //Search function filters (and a little lambda action)
    @FXML private FilteredList<Appointment> apptSearchFilter = new FilteredList<>(apptData, p -> true);
    @FXML private SortedList<Appointment> sortedApptData = new SortedList<>(apptSearchFilter);
    
    //usersList
    @FXML private final ObservableList<String> users = FXCollections.observableArrayList();
    
    //List of Months
    ObservableList<ZonedDateTime> monthList = FXCollections.observableArrayList();
    
    //List of Customers
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    TimeZone selectedTimeZone = TimeZone.getDefault();
    Date today = Calendar.getInstance().getTime();
    
    private String userID;
    private int lastRunReport = 1;
    private final ObservableList<TimeZone> timezones = FXCollections.observableArrayList();
    private StringConverter<TimeZone> converter;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void stageSetup(String user, TimeZone tz){
        userID = user;
        selectedTimeZone = tz;
        username.setText("User ID: "+userID);
        //Threading!
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (true) {
                    //lambda to shorten current time label update loop
                    Platform.runLater (() -> {
                        updateCurrentTime();
                    });
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        
        buildTimezoneList();
        asyncFormBuilder();
        populateMonthList();
    }
    
    private void populateMonthList(){
            for(int i = 0; i<12; i++){
                monthList.add(ZonedDateTime.of(ZonedDateTime.now().getYear()-1, i+1, 1, 0, 0, 0, 0, ZoneId.of("GMT")));
            }
            for(int i = 0; i<12; i++){
                monthList.add(ZonedDateTime.of(ZonedDateTime.now().getYear(), i+1, 1, 0, 0, 0, 0, ZoneId.of("GMT")));
            }
            
            StringConverter dateConverter = new StringConverter<ZonedDateTime>() {
            @Override
            public String toString(ZonedDateTime zdt) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yyyy");
                return dtf.format(zdt);
            }

            @Override
            public ZonedDateTime fromString(String string) {
                return null;
            }
            };
            monthCBox.setItems(monthList);
            monthCBox.setConverter(dateConverter);
            monthCBox.getSelectionModel().selectFirst();
    }
    
    private void buildUserList(){
        users.clear();
        //get usernames
        try(ResultSet rs = DBManager.query("SELECT userName FROM users;")){  
            while (rs.next()){
                users.add(rs.getString("userName")); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build user list.");
                System.out.println(e);
        }
        userCBox.setItems(users);
        Platform.runLater(() -> {
            userCBox.getSelectionModel().selectFirst();
        });
    }
    
    private void buildSummaryList(){
        summaryData.clear();
        try(ResultSet rs = DBManager.query("SELECT title, COUNT(title) AS count FROM appointment WHERE MONTH(start) = "+monthCBox.getSelectionModel().getSelectedItem().getMonthValue()+" AND YEAR(start) = "+monthCBox.getSelectionModel().getSelectedItem().getYear()+" GROUP BY title;")){
            while (rs.next()){
                Appointment current = new Appointment();
                current.setTitle(rs.getString("title"));
                current.setDescription(rs.getString("count"));
                summaryData.add(current); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build appointment table.");
                System.out.println(e);
        }
    }  
    
    @FXML
    private void typeByMonthReport(){
        lastRunReport = 3;
        buildSummaryList();
        apptTable.setItems(summaryData);
        //Table Columns
        startTimeCol.setVisible(false);
        durationCol.setVisible(false);
        custNameCol.setVisible(false);
        contactNameCol.setVisible(false);
        locationCol.setVisible(false);
        dateCol.setVisible(false);
        
        countCol.setText("count");
        countCol.setVisible(true);
    }
    
    @FXML
    private void customerApptReport(){
        resetColumns();
        lastRunReport = 2;
        if (filterChk.isSelected()){
            apptSearchFilter.predicateProperty().bind(Bindings.createObjectBinding(() ->
                    appointment ->  {               

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    ZonedDateTime zdt1 = localApptTime(appointment.getStartTime());
                    ZonedDateTime zdt2 = ZonedDateTime.now(selectedTimeZone.toZoneId());
                    Customer selectedCustomer = (Customer) customerCBox.getSelectionModel().getSelectedItem();

                    if(weekRBtn.isSelected()){
                        dtf = DateTimeFormatter.ofPattern("yyyy");
                        WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
                        int filterWeek = zdt2.get(weekFields.weekOfWeekBasedYear());
                        LocalDate date = appointment.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int apptWeek = date.get(weekFields.weekOfWeekBasedYear());
                        return (apptWeek == filterWeek && dtf.format(zdt1).equals(dtf.format(zdt2)) && appointment.getCustomer().getID() == selectedCustomer.getID());
                    } else if(monthRBtn.isSelected()){
                        dtf = DateTimeFormatter.ofPattern("MM/yyyy");
                        return (dtf.format(zdt1).equals(dtf.format(zdt2))  && appointment.getCustomer().getID() == selectedCustomer.getID());
                    } else{
                        return (dtf.format(zdt1).equals(dtf.format(zdt2))  && appointment.getCustomer().getID() == selectedCustomer.getID());
                    }
                })
            );
        } else{
            apptSearchFilter.predicateProperty().bind(Bindings.createObjectBinding(() ->
                appointment ->  appointment.getCustomer().getID() == customerCBox.getSelectionModel().getSelectedItem().getID()
            ));
        }
        
        
    }
    
    @FXML
    private void filterUpdate(){
        switch (lastRunReport) {
            case 1:
                scheduleReport();
                break;
            case 2:
                customerApptReport();
                break;
            case 3:
                break;
            default:
                scheduleReport();
                break;
        }
    }
    
    @FXML
    private void scheduleReport(){
        resetColumns();
        lastRunReport = 1;
        if (filterChk.isSelected()){
            apptSearchFilter.predicateProperty().bind(Bindings.createObjectBinding(() ->
                    appointment ->  {               

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    ZonedDateTime zdt1 = localApptTime(appointment.getStartTime());
                    ZonedDateTime zdt2 = ZonedDateTime.now(selectedTimeZone.toZoneId());
                    String selectedContact = userCBox.getSelectionModel().getSelectedItem().toString();

                    if(weekRBtn.isSelected()){
                        dtf = DateTimeFormatter.ofPattern("yyyy");
                        WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
                        int filterWeek = zdt2.get(weekFields.weekOfWeekBasedYear());
                        LocalDate date = appointment.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int apptWeek = date.get(weekFields.weekOfWeekBasedYear());
                        return (apptWeek == filterWeek && dtf.format(zdt1).equals(dtf.format(zdt2)) && appointment.getContact().matches(selectedContact));
                    } else if(monthRBtn.isSelected()){
                        dtf = DateTimeFormatter.ofPattern("MM/yyyy");
                        return (dtf.format(zdt1).equals(dtf.format(zdt2))  && appointment.getContact().matches(selectedContact));
                    } else{
                        return (dtf.format(zdt1).equals(dtf.format(zdt2))  && appointment.getContact().matches(selectedContact));
                    }
                })
            );
        } else{
            String selectedContact = userCBox.getSelectionModel().getSelectedItem().toString();
            apptSearchFilter.predicateProperty().bind(Bindings.createObjectBinding(() ->
                appointment ->  appointment.getContact().matches(selectedContact)
            ));
        }
    }
    
    private void resetColumns(){
        apptTable.setItems(sortedApptData);
        startTimeCol.setVisible(true);
        durationCol.setVisible(true);
        custNameCol.setVisible(true);
        contactNameCol.setVisible(true);
        locationCol.setVisible(true);
        dateCol.setVisible(true);
        
        countCol.setText("Description");
        countCol.setVisible(false);
    }
    
    private void buildTimezoneList(){
    //builds timezone selector list dynamically
        timezones.clear();
        //get timezone IDs
        String[] tzList = TimeZone.getAvailableIDs();
        //add to observable list
        for (String id : tzList) {
            timezones.add(TimeZone.getTimeZone(id));
        }
        
        //sorts timezone list by GMT offset using lambda & collections to shorten syntax
        Collections.sort(timezones, (TimeZone s1, TimeZone s2) -> s1.getOffset(today.getTime()) - s2.getOffset(today.getTime()));
        timezoneCBox.setItems(timezones);
        preSelectTZ();
        //converter for easy reading
        converter = new StringConverter<TimeZone>() {
            @Override
            public String toString(TimeZone tz) {
                return formattedTimeZone(tz);
            }

            @Override
            public TimeZone fromString(String string) {
                return null;
            }
        };
        timezoneCBox.setConverter(converter);
    }
    
    private void buildCustomerList(){
        customers.clear();
        //get customer IDs
        try(ResultSet rs = DBManager.query("SELECT customerId FROM customer;")){  
            while (rs.next()){
                Customer current = new Customer(rs.getInt("customerId"));
                customers.add(current); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build customer list.");
                System.out.println(e);
        }

        customerCBox.setItems(customers);
        //converter for easy reading
        StringConverter<Customer> customerConverter = new StringConverter<Customer>() {
            @Override
            public String toString(Customer c) {
                return c.getName();
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        };
        customerCBox.setConverter(customerConverter);
        Platform.runLater(() -> {
            customerCBox.getSelectionModel().selectFirst();
        });
    }
    
    private void buildApptList(){
        apptData.clear();
        setCVF();
        apptTable.setItems(sortedApptData);
        sortedApptData.comparatorProperty().bind(apptTable.comparatorProperty());
        columnDataFormatter();
        
        try(ResultSet rs = DBManager.query("SELECT appointmentId FROM appointment;")){  
            while (rs.next()){
                Appointment current = new Appointment(rs.getInt("appointmentId"));
                apptData.add(current); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build appointment table.");
                System.out.println(e);
        }
    }  
    
    private void columnDataFormatter(){
        //Lambda to shorten syntax.
        dateCol.setCellFactory((TableColumn<Appointment, Date> col) -> 
            new TableCell<Appointment, Date>() {
                @Override 
                public void updateItem(Date date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(localApptTime(date)));
                    }
                }
            }
        );
        //Lambda to shorten syntax.
        startTimeCol.setCellFactory((TableColumn<Appointment, Date> col) -> 
            new TableCell<Appointment, Date>() {
                @Override 
                public void updateItem(Date start, boolean empty) {
                    super.updateItem(start, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(DateTimeFormatter.ofPattern("hh:mm a").format(localApptTime(start)));
                    }
                }
            }
        );
        //Lambda to shorten syntax.
        durationCol.setCellFactory((TableColumn<Appointment, Number> col) -> 
            new TableCell<Appointment, Number>() {
                @Override 
                public void updateItem(Number dur, boolean empty) {
                    super.updateItem(dur, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(dur+" min");
                    }
                }
            }
        );
    }
    
    private String formattedTimeZone(TimeZone tz) {

        long hours = TimeUnit.MILLISECONDS.toHours(tz.getOffset(today.getTime()));
        long minutes = Math.abs(TimeUnit.MILLISECONDS.toMinutes(tz.getOffset(today.getTime())) - TimeUnit.HOURS.toMinutes(hours));
        String result;
        if (hours > 0) {
            if (tz.getDSTSavings() != 0){
                result = String.format("(GMT+%d:%02d) %s (%s) (DST) ", hours, minutes, tz.getDisplayName(),tz.getID());
            } else{
                result = String.format("(GMT+%d:%02d) %s (%s)", hours, minutes, tz.getDisplayName(), tz.getID());
            }
        } else {
            if (tz.getDSTSavings() != 0){
                result = String.format("(GMT%d:%02d) %s (%s) (DST)", hours, minutes, tz.getDisplayName(), tz.getID());
            } else{
                result = String.format("(GMT%d:%02d) %s (%s)", hours, minutes, tz.getDisplayName(), tz.getID());
            }
        }
        return result;
    }
    
    private ZonedDateTime localApptTime(Date d) {
        ZonedDateTime in = ZonedDateTime.ofInstant(d.toInstant(), ZoneId.of("GMT"));
        ZonedDateTime out = in.withZoneSameInstant(selectedTimeZone.toZoneId());
        return out;
    }
    
    private void preSelectTZ(){
        //Uses lambda expressions/streams to create a list of TimeZones that match the system
        List<TimeZone> match = timezones.stream()
            .filter(tz -> tz.equals(selectedTimeZone))
            .collect(Collectors.toList());
        //Selects the system timezone in the combobox.
        timezoneCBox.getSelectionModel().select(match.get(0));
        selectedTimeZone = match.get(0);
        updateCurrentTime();
        columnDataFormatter();
    }
    
    private void updateCurrentTime(){
        currentTime.setText(DateTimeFormatter.ofPattern("EEE MMM dd, yyyy\nhh:mm:ss a z").format(ZonedDateTime.now(selectedTimeZone.toZoneId())));
    }
    
    private void asyncFormBuilder(){
        //Threading! Lambdas! This runs on the background thread.
        executor.submit(() -> {
            //Lambda to run on main thread.
            Platform.runLater(() -> {
                loadingOverlay.setVisible(true);
            });
            buildCustomerList();
            buildApptList();
            buildUserList();
            //Lambda to run on main thread.
            Platform.runLater(() -> {
                loadingOverlay.setVisible(false);
            });
        });
    }
    
    private void setCVF(){
        countCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("custName"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    }
    
    public void shutdownExecutor(){
        if(!executor.isShutdown()){
            executor.shutdownNow();
        }
    }
    
    @FXML
    private void exitForm(){
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage,WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    
    @FXML
    private void updateTimeZone(){
        selectedTimeZone = (TimeZone) timezoneCBox.getSelectionModel().getSelectedItem();
        updateCurrentTime();
        columnDataFormatter();
    }
    
}
