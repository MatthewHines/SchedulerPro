/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195final;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Matthew
 */
public class AppointmentFXMLController implements Initializable {

    @FXML javafx.scene.control.Label currentTimeLocal;
    @FXML javafx.scene.control.Label currentTimeGMT;
    @FXML javafx.scene.control.Label editorID;
    @FXML javafx.scene.control.Label appointmentID;
    @FXML javafx.scene.control.Label schedulerModeLbl;
    @FXML javafx.scene.control.Label locationLbl;
    
    @FXML javafx.scene.control.ComboBox timezoneCBox;
    @FXML javafx.scene.control.ComboBox hourCBox;
    @FXML javafx.scene.control.ComboBox minCBox;
    @FXML javafx.scene.control.ComboBox ampmCBox;
    @FXML javafx.scene.control.ComboBox customerCBox;
    @FXML javafx.scene.control.ComboBox userCBox;
    @FXML javafx.scene.control.ComboBox durationCBox;
    @FXML javafx.scene.control.ComboBox locationCBox;
    
    @FXML javafx.scene.control.TextField locationTF;
    @FXML javafx.scene.control.TextField urlTF;
    @FXML javafx.scene.control.TextField titleTF;
    @FXML javafx.scene.control.TextArea descriptionTA;
    
    @FXML javafx.scene.control.Button editApptBtn;
    @FXML javafx.scene.control.Button deleteApptBtn;
    @FXML javafx.scene.control.Button saveApptBtn;
    @FXML javafx.scene.control.Button cancelBtn;
    @FXML javafx.scene.control.Button clearDateBtn;
    @FXML javafx.scene.control.Button viewCustomerBtn;
    
    @FXML javafx.scene.control.DatePicker dateField;
    
    //Table Columns
    @FXML javafx.scene.control.TableColumn<Appointment, Date> startTimeCol;
    @FXML javafx.scene.control.TableColumn<Appointment, Number> durationCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> custNameCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> contactNameCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> titleCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> locationCol;
    @FXML javafx.scene.control.TableColumn<Appointment, Date> dateCol;
    
    @FXML javafx.scene.layout.StackPane loadingOverlay;
    @FXML javafx.scene.layout.StackPane formLoadingOverlay;
    
     //TableView
    @FXML javafx.scene.control.TableView apptTable;
    @FXML private ObservableList<Appointment> apptData = FXCollections.observableArrayList();
    
    //Search function filters + lambda
    @FXML private FilteredList<Appointment> apptSearchFilter = new FilteredList<>(apptData, p -> true);
    @FXML private SortedList<Appointment> sortedApptData = new SortedList<>(apptSearchFilter);
    
    @FXML private final ObservableList<TimeZone> timezones = FXCollections.observableArrayList();
    @FXML private StringConverter<TimeZone> converter;
    
    @FXML private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    @FXML private StringConverter<Customer> customerConverter;
    
    @FXML private final ObservableList<String> users = FXCollections.observableArrayList();
    
    ObservableList<Integer> hoursList = FXCollections.observableArrayList();
    ObservableList<String> minutesList = FXCollections.observableArrayList();
    ObservableList<Integer> durationList = FXCollections.observableArrayList();
    ObservableList<String> ampmList = FXCollections.observableArrayList();
    ObservableList<String> locationList = FXCollections.observableArrayList();
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private String userID;
    private TimeZone selectedTimeZone = TimeZone.getDefault();
    
    Appointment selectedAppt;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        
        hoursList.add(1);
        hoursList.add(2);
        hoursList.add(3);
        hoursList.add(4);
        hoursList.add(5);
        hoursList.add(6);
        hoursList.add(7);
        hoursList.add(8);
        hoursList.add(9);
        hoursList.add(10);
        hoursList.add(11);
        hoursList.add(12);
        
        minutesList.add("00");
        minutesList.add("15");
        minutesList.add("30");
        minutesList.add("45");
        
        durationList.add(15);
        durationList.add(30);
        durationList.add(45);
        durationList.add(60);
        durationList.add(90);
        durationList.add(120);
        
        ampmList.add("AM");
        ampmList.add("PM");
        
        locationList.add("Phoenix, Arizona");
        locationList.add("New York, New York");
        locationList.add("London, England");
        locationList.add("Phone");
        locationList.add("Online");
        locationList.add("Other");
        
        hourCBox.setItems(hoursList);
        hourCBox.getSelectionModel().selectFirst();
        minCBox.setItems(minutesList);
        minCBox.getSelectionModel().selectFirst();
        ampmCBox.setItems(ampmList);
        ampmCBox.getSelectionModel().selectFirst();
        durationCBox.setItems(durationList);
        durationCBox.getSelectionModel().selectFirst();
        locationCBox.setItems(locationList);
        locationCBox.getSelectionModel().selectFirst();
    }

    public void stageSetup(TimeZone tz, String id){
        userID = id;
        editorID.setText("User ID: " + userID);
        selectedTimeZone = tz;
        editApptBtn.setVisible(false);
        deleteApptBtn.setVisible(false);
        selectedAppt = new Appointment();
        appointmentID.setText("Appointment ID: "+Integer.toString(selectedAppt.getID()));
        textLimits();
        buildTimezoneList();
        timezoneCBox.getSelectionModel().select(tz);
        updateCurrentTime();
        asyncFormBuilder();
    }
    
    public void stageSetup(TimeZone tz, String id, Appointment appt){
        userID = id;
        editorID.setText("User ID: " + userID);
        selectedTimeZone = tz;
        selectedAppt = appt;
        appointmentID.setText("Appointment ID: "+Integer.toString(selectedAppt.getID()));
        textLimits();
        //preSelectMatchingData();
        buildTimezoneList();
        timezoneCBox.getSelectionModel().select(tz);
        updateCurrentTime();
        disableForm();
        asyncFormBuilder();
    }
    
    private void disableForm(){
        timezoneCBox.setDisable(true);
        hourCBox.setDisable(true);
        minCBox.setDisable(true);
        ampmCBox.setDisable(true);
        customerCBox.setDisable(true);
        userCBox.setDisable(true);
        durationCBox.setDisable(true);
        locationCBox.setDisable(true);
        locationTF.setDisable(true);
        urlTF.setDisable(true);
        titleTF.setDisable(true);
        descriptionTA.setDisable(true);
        deleteApptBtn.setDisable(true);
        saveApptBtn.setDisable(true);
        clearDateBtn.setDisable(true);
        dateField.setDisable(true);
        editApptBtn.setDisable(true);
        viewCustomerBtn.setDisable(true);
    }
    
    @FXML
    private void enableForm(){
        timezoneCBox.setDisable(false);
        hourCBox.setDisable(false);
        minCBox.setDisable(false);
        ampmCBox.setDisable(false);
        customerCBox.setDisable(false);
        userCBox.setDisable(false);
        durationCBox.setDisable(false);
        locationCBox.setDisable(false);
        locationTF.setDisable(false);
        urlTF.setDisable(false);
        titleTF.setDisable(false);
        descriptionTA.setDisable(false);
        deleteApptBtn.setDisable(false);
        saveApptBtn.setDisable(false);
        clearDateBtn.setDisable(false);
        editApptBtn.setDisable(true);
        dateField.setDisable(false);
        viewCustomerBtn.setDisable(false);
    }
    
    private void preSelectMatchingData(){
        List<Customer> custMatch = customers.stream()
            .filter(c -> c.getID() == (selectedAppt.getCustomer().getID()))
            .collect(Collectors.toList());
        customerCBox.getSelectionModel().select(custMatch.get(0));
        
        List<String> contactMatch = users.stream()
            .filter(c -> c.equals(selectedAppt.getContact()))
            .collect(Collectors.toList());
        userCBox.getSelectionModel().select(contactMatch.get(0));
        
        List<String> ampmMatch = ampmList.stream()
            .filter(c -> c.toString().equalsIgnoreCase(DateTimeFormatter.ofPattern("a").format(localApptTime(selectedAppt.getStartTime()))))
            .collect(Collectors.toList());
        ampmCBox.getSelectionModel().select(ampmMatch.get(0));
        
        List<String> locationMatch = locationList.stream()
            .filter(c -> c.equalsIgnoreCase(selectedAppt.getLocation()))
            .collect(Collectors.toList());
        if(locationMatch.isEmpty()){
            locationCBox.getSelectionModel().select(5);
            locationTF.setText(selectedAppt.getLocation());
        } else{
            locationCBox.getSelectionModel().select(locationMatch.get(0));
            if(locationMatch.get(0).equalsIgnoreCase("Online")){
                urlTF.setText(selectedAppt.getURL());
            } 
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        dateField.setValue(LocalDate.parse(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(localApptTime(selectedAppt.getStartTime())),formatter));
        
        List<Integer> hourMatch = hoursList.stream()
            .filter(c -> c.toString().equals(DateTimeFormatter.ofPattern("h").format(localApptTime(selectedAppt.getStartTime()))))
            .collect(Collectors.toList());
        hourCBox.getSelectionModel().select(hourMatch.get(0));
        
        List<String> minMatch = minutesList.stream()
            .filter(c -> c.equals(DateTimeFormatter.ofPattern("mm").format(localApptTime(selectedAppt.getStartTime()))))
            .collect(Collectors.toList());
        minCBox.getSelectionModel().select(minMatch.get(0));
        
        List<Integer> durMatch = durationList.stream()
            .filter(c -> c.equals(selectedAppt.getDuration()))
            .collect(Collectors.toList());
        durationCBox.getSelectionModel().select(durMatch.get(0));
        
        titleTF.setText(selectedAppt.getTitle());
        descriptionTA.setText(selectedAppt.getDescription());
        
    }
    
    @FXML
    private void locationSwitch(){
        if (locationCBox.getSelectionModel().getSelectedItem().toString().matches("Other")){
            locationLbl.setText("Location: ");
            urlTF.clear();
            urlTF.setDisable(true);
            urlTF.setVisible(false);
            locationTF.setDisable(false);
            locationTF.setVisible(true);
        } else if(locationCBox.getSelectionModel().getSelectedItem().toString().matches("Online")){
            locationLbl.setText("URL: ");
            locationTF.clear();
            locationTF.setDisable(true);
            locationTF.setVisible(false);
            urlTF.setDisable(false);
            urlTF.setVisible(true);
        } else{
            locationLbl.setText("Location: ");
            locationTF.clear();
            locationTF.setDisable(true);
            locationTF.setVisible(true);
            urlTF.clear();
            urlTF.setDisable(true);
            urlTF.setVisible(false);
        }
    }
    
    private void textLimits(){
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String newText = c.getControlNewText();
            if (newText.matches("^.{0,255}")) {
                return c ;
            } else {
                return null ;
            }
        };
        titleTF.setTextFormatter(new TextFormatter<>(filter));
        locationTF.setTextFormatter(new TextFormatter<>(filter));
        urlTF.setTextFormatter(new TextFormatter<>(filter));
        
        UnaryOperator<TextFormatter.Change> descriptionFilter = c -> {
            String newText = c.getControlNewText();
            if (newText.matches("^.{0,5000}")) {
                return c ;
            } else {
                return null ;
            }
        };
        
        descriptionTA.setTextFormatter(new TextFormatter<>(descriptionFilter));
        
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
        
        Date today = Calendar.getInstance().getTime();
        //sorts timezone list by GMT offset using lambda & collections to shorten syntax
        Collections.sort(timezones, (TimeZone s1, TimeZone s2) -> s1.getOffset(today.getTime()) - s2.getOffset(today.getTime()));
        timezoneCBox.setItems(timezones);
        List<TimeZone> match = timezones.stream()
            .filter(tz -> tz.getID().equals(selectedTimeZone.getID()))
            .collect(Collectors.toList());
        timezoneCBox.getSelectionModel().select(match.get(0));
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
        try(ResultSet rs = DBManager.query("SELECT customerid FROM customer;")){  
            while (rs.next()){
                Customer current = new Customer(rs.getInt("customerid"));
                customers.add(current); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build customer list.");
                System.out.println(e);
        }

        customerCBox.setItems(customers);
        //converter for easy reading
        customerConverter = new StringConverter<Customer>() {
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
    
    private void buildUserList(){
        users.clear();
        //get usernames
        try(ResultSet rs = DBManager.query("SELECT userName FROM user;")){  
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
    
    private void buildApptList(){
        apptData.clear();
        setCVF();
        apptTable.setItems(sortedApptData);
        sortedApptData.comparatorProperty().bind(apptTable.comparatorProperty());
        columnDataFormatter();
              
        try(ResultSet rs = DBManager.query("SELECT appointmentid FROM appointment;")){  
            while (rs.next()){
                Appointment current = new Appointment(rs.getInt("appointmentid"));
                apptData.add(current); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build appointment table.");
                System.out.println(e);
        }
        Platform.runLater(() -> {
                apptTable.getSelectionModel().selectFirst();
        });
        
    }  
    
    private String formattedTimeZone(TimeZone tz) {
        Date today = Calendar.getInstance().getTime();
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
    
    private void setCVF(){
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("custName"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    }
    
    private void columnDataFormatter(){
        
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
    
    public void shutdownExecutor(){
        if(!executor.isShutdown()){
            executor.shutdownNow();
        }
    }
    
    @FXML
    private void clearFilter(){
        dateField.setValue(null);
    }
    
    
    private void asyncFormBuilder(){
        //Threading! Lambdas!
        if(selectedAppt.getNewAppt()){
            loadingOverlay.setVisible(true);
            formLoadingOverlay.setVisible(true);
            executor.submit(() -> {
                buildCustomerList();
                buildUserList();
                Platform.runLater(() -> {
                    formLoadingOverlay.setVisible(false);
                });
                buildApptList();
                Platform.runLater(() -> {
                    loadingOverlay.setVisible(false);
                });
            });
        } else{
            loadingOverlay.setVisible(true);
            formLoadingOverlay.setVisible(true);
            executor.submit(() -> {
                buildCustomerList();
                buildUserList();
                Platform.runLater(() -> {
                    formLoadingOverlay.setVisible(false);
                });
                buildApptList();
                Platform.runLater(() -> {
                    loadingOverlay.setVisible(false);
                    preSelectMatchingData();
                    urlTF.setDisable(true);
                    locationTF.setDisable(true);
                    editApptBtn.setDisable(false);
                });
            });
        }
    }
    
    @FXML
    private void setFilterPredicate(){
        //Contains lambdas for syntax benefits.
        Customer customer = (Customer) customerCBox.getSelectionModel().getSelectedItem();
    
        if(dateField.getValue() != null){
            //Shows only conflicting appointments, including date and time. Only overlapping appointments including either the contact or customer will be shown.
            apptSearchFilter.predicateProperty().bind(Bindings.createObjectBinding(() ->
                    appointment ->  (   //puts any appts with the same customer or contact on the list
                                        appointment.getContact().contains(userCBox.getSelectionModel().getSelectedItem().toString())
                                        |   
                                        appointment.getCustomer().getID() == customer.getID()
                                    )
                                    &&
                                    //Check whether appointment would overlap with selected time
                                    (
                                        localApptTime(appointment.getStartTime()).isBefore(getZonedEnd()) 
                                        && 
                                        localApptTime(appointment.getEndTime()).isAfter(getZonedStart())
                                    )
                                    &&
                                    //Excludes current appointment from list. Only matters when editing an existing appt.
                                    appointment.getID() != selectedAppt.getID()
            ));
            if(apptSearchFilter.isEmpty()){
                schedulerModeLbl.setText("No conflicting appointments found.");
            } else{
                schedulerModeLbl.setText("Showing all conflicting appointments. The table must be empty before the appointment may be created.");
            }
        } else{
            //shows any appointments matching criteria, excluding date (eg, same customer and contact, for planning purposes)
            schedulerModeLbl.setText("Showing all appointments with either matching customer or matching contact.");
            apptSearchFilter.predicateProperty().bind(Bindings.createObjectBinding(() ->
                    appointment ->  appointment.getContact().contains(userCBox.getSelectionModel().getSelectedItem().toString())
                                |  appointment.getCustomer().getID() == customer.getID()  
            ));
        }
    }
    
    @FXML
    private void clearDateFilter(){
        dateField.setValue(null);
        setFilterPredicate();
    }
    
    private ZonedDateTime getZonedStart(){
        int hour;
        int min = Integer.parseInt(minCBox.getSelectionModel().getSelectedItem().toString());
        
        if(ampmCBox.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("AM")){
            if(hourCBox.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("12")){
                hour = Integer.parseInt(hourCBox.getSelectionModel().getSelectedItem().toString())-12;
            } else{
                hour = Integer.parseInt(hourCBox.getSelectionModel().getSelectedItem().toString());
            }
        }else{
            if(hourCBox.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("12")){
                hour = Integer.parseInt(hourCBox.getSelectionModel().getSelectedItem().toString());
            } else{
                hour = Integer.parseInt(hourCBox.getSelectionModel().getSelectedItem().toString())+12;
            }
        }
        ZonedDateTime selectedStart = ZonedDateTime.of(dateField.getValue().getYear(), dateField.getValue().getMonthValue(), dateField.getValue().getDayOfMonth(), hour, min, 0, 0, selectedTimeZone.toZoneId());
        return selectedStart;
    }
    
    private ZonedDateTime getZonedEnd(){
        return getZonedStart().plusMinutes(Integer.parseInt(durationCBox.getSelectionModel().getSelectedItem().toString()));
    }
    
    @FXML
    private void saveAppointment(){
        if(apptSearchFilter.isEmpty()){
            if(appointmentIsValid()){
                selectedAppt.setCustomer((Customer) customerCBox.getSelectionModel().getSelectedItem());
                selectedAppt.setContact(userCBox.getSelectionModel().getSelectedItem().toString());
                selectedAppt.setTitle(titleTF.getText());
                selectedAppt.setDescription(descriptionTA.getText());
                if(locationCBox.getSelectionModel().getSelectedItem().toString().matches("Other")){
                    selectedAppt.setLocation(locationTF.getText());
                } else if(locationCBox.getSelectionModel().getSelectedItem().toString().matches("Online")){
                    selectedAppt.setLocation("Online");
                    selectedAppt.setURL(urlTF.getText());
                } else{
                    selectedAppt.setLocation(locationCBox.getSelectionModel().getSelectedItem().toString());
                }
                selectedAppt.setStartTime(Date.from(getZonedStart().withZoneSameInstant(ZoneId.of("GMT")).toInstant()));
                selectedAppt.setEndTime(Date.from(getZonedEnd().withZoneSameInstant(ZoneId.of("GMT")).toInstant()));
                selectedAppt.save(userID);

                ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                Alert alert = new Alert(Alert.AlertType.NONE,"Save successful.",ok);
                alert.setTitle("Scheduler Pro - v1.0");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    exitForm();
                } else {
                    exitForm();
                }
                
            }
        } else if(dateField.getValue() == null){
            printErrMsg("Please enter a date.");
        } else{
            printErrMsg("At least one appointment overlaps with the selected time.\nCheck the table for a list of conflicting appointments and address any scheduling errors.");
        }
    }
    
    @FXML
    private void showAddEditStage() throws IOException{
        //gets FXML controller & parent
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerEditorFXML.fxml"));
        Parent root = loader.load();
        CustomerEditorFXMLController controller = loader.getController();       
        //passes user ID to editor form
        Customer selected = (Customer) customerCBox.getSelectionModel().getSelectedItem();
        controller.stageSetup(selected, userID);
        //assigns current stage as parent and new stage as child
        Stage parentStage = (Stage) viewCustomerBtn.getScene().getWindow();
        Stage childStage = new Stage();
        Scene scene = new Scene(root);
        
        //sets stage properties
        childStage.setMinHeight(440);
        childStage.setMinWidth(420);
        parentStage.hide();
        childStage.setScene(scene);
        childStage.setTitle("Scheduler Pro - v1.0");
        childStage.show();
        //Lambda to set closeout action
        childStage.setOnCloseRequest((WindowEvent we) -> {
            controller.shutdownExecutor();
            childStage.close();
            asyncFormBuilder();
            parentStage.show();
        });
    }
    
    @FXML
    private void exitForm(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage,WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    
    @FXML
    private void deleteAppt(){
        ButtonType yes = new ButtonType("Yes, Delete.", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No.", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.NONE,"Are you sure you want to delete this appointment? This cannot be undone.",yes,no);
        alert.setTitle("Scheduler Pro - v1.0");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yes) {
            selectedAppt.delete();
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert = new Alert(Alert.AlertType.NONE,"Appointment deleted.",ok);
            alert.setTitle("Scheduler Pro - v1.0");
            result = alert.showAndWait();
            if (result.isPresent()) {
                exitForm();
            } else{
                exitForm();
            }
        }  
    }
    
    private boolean appointmentIsValid(){
        boolean valid = false;
        
        if(customerCBox.getSelectionModel().getSelectedItem() == null){
            printErrMsg("Please select a customer.");
            return valid;
        } else if(userCBox.getSelectionModel().getSelectedItem() == null){
            printErrMsg("Please select a contact/user.");
            return valid;
        } else if(locationCBox.getSelectionModel().getSelectedItem() == null){
            printErrMsg("Please select a location.");
            return valid;
        } else if(locationCBox.getSelectionModel().getSelectedItem().toString().matches("Other") && (locationTF.getText() == null || locationTF.getText().isEmpty())){
            printErrMsg("Please enter a location.");
            return valid;
        }  else if(locationCBox.getSelectionModel().getSelectedItem() == null){
            printErrMsg("Please select a location.");
            return valid;
        } else if(getZonedStart().isBefore(ZonedDateTime.now(selectedTimeZone.toZoneId()))){
            printErrMsg("The selected appointment date/time is in the past.\nPlease select a future date to schedule.");
            return valid;
        } else if(titleTF.getText() == null || titleTF.getText().isEmpty()){
            printErrMsg("Please enter a title.");
            return valid;
        } else if(descriptionTA.getText() == null || descriptionTA.getText().isEmpty()){
            printErrMsg("Please enter a description.");
            return valid;
        } else if(locationCBox.getSelectionModel().getSelectedItem().toString().matches("Online") && (urlTF.getText() == null || urlTF.getText().isEmpty())){       
            printErrMsg("Please enter a URL.");
            return valid;
        } else if(locationCBox.getSelectionModel().getSelectedItem().toString().matches("Phoenix, Arizona")){
            ZonedDateTime localOpen = ZonedDateTime.of(dateField.getValue().getYear(), dateField.getValue().getMonthValue(), dateField.getValue().getDayOfMonth(), 9, 0, 0, 0, ZoneId.of("US/Arizona") );
            ZonedDateTime localClose = ZonedDateTime.of(dateField.getValue().getYear(), dateField.getValue().getMonthValue(), dateField.getValue().getDayOfMonth(), 17, 0, 0, 0, ZoneId.of("US/Arizona") );
            if (getZonedStart().isBefore(localOpen)|| getZonedEnd().isAfter(localClose)|| getZonedStart().getDayOfWeek() == DayOfWeek.SATURDAY || getZonedStart().getDayOfWeek() == DayOfWeek.SUNDAY){
                printErrMsg("Be advised that this appointment may fall outside of normal business hours.\nPlease make arrangements for facility availability.");
                return true;
            }
            return true;
        } else if(locationCBox.getSelectionModel().getSelectedItem().toString().matches("New York, New York")){
            ZonedDateTime localOpen = ZonedDateTime.of(dateField.getValue().getYear(), dateField.getValue().getMonthValue(), dateField.getValue().getDayOfMonth(), 9, 0, 0, 0, ZoneId.of("America/New_York") );
            ZonedDateTime localClose = ZonedDateTime.of(dateField.getValue().getYear(), dateField.getValue().getMonthValue(), dateField.getValue().getDayOfMonth(), 17, 0, 0, 0, ZoneId.of("America/New_York") );
            if (getZonedStart().isBefore(localOpen)|| getZonedEnd().isAfter(localClose)|| getZonedStart().getDayOfWeek() == DayOfWeek.SATURDAY || getZonedStart().getDayOfWeek() == DayOfWeek.SUNDAY){
                printErrMsg("Be advised that this appointment may fall outside of normal business hours.\nPlease make arrangements for facility availability.");
                return true;
            }
            return true;
        } else if(locationCBox.getSelectionModel().getSelectedItem().toString().matches("London, England")){
            ZonedDateTime localOpen = ZonedDateTime.of(dateField.getValue().getYear(), dateField.getValue().getMonthValue(), dateField.getValue().getDayOfMonth(), 9, 0, 0, 0, ZoneId.of("Europe/London") );
            ZonedDateTime localClose = ZonedDateTime.of(dateField.getValue().getYear(), dateField.getValue().getMonthValue(), dateField.getValue().getDayOfMonth(), 17, 0, 0, 0, ZoneId.of("Europe/London") );
            if (getZonedStart().isBefore(localOpen)|| getZonedEnd().isAfter(localClose)|| getZonedStart().getDayOfWeek() == DayOfWeek.SATURDAY || getZonedStart().getDayOfWeek() == DayOfWeek.SUNDAY){
                printErrMsg("Be advised that this appointment may fall outside of normal business hours.\nPlease make arrangements for facility availability.");
                return true;
            }
            return true;
        } else{ 
            if(getZonedStart().getDayOfWeek() == DayOfWeek.SATURDAY || getZonedStart().getDayOfWeek() == DayOfWeek.SUNDAY){
                printErrMsg("Be advised that this appointment may fall outside of normal business hours.\nPlease make arrangements for facility availability.");
                return true;
            }
            return true;
        }
    }
    
    private void printErrMsg(String msg){
        ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.NONE,msg,ok);
        alert.setTitle("Scheduler Pro - v1.0");
        alert.show(); 
    }
    
    private ZonedDateTime localApptTime(Date d) {
        ZonedDateTime in = ZonedDateTime.ofInstant(d.toInstant(), ZoneId.of("GMT"));
        ZonedDateTime out = in.withZoneSameInstant(selectedTimeZone.toZoneId());
        return out;
    }
    
    @FXML
    private void updateTimeZone(){
        selectedTimeZone = (TimeZone) timezoneCBox.getSelectionModel().getSelectedItem();
        updateCurrentTime();
        columnDataFormatter();
        setFilterPredicate();
    }
    
    private void updateCurrentTime(){
        currentTimeLocal.setText(DateTimeFormatter.ofPattern("EEE MMM dd, yyyy\nhh:mm:ss a z").format(ZonedDateTime.now(selectedTimeZone.toZoneId())));
        currentTimeGMT.setText(DateTimeFormatter.ofPattern("EEE MMM dd, yyyy\nhh:mm:ss a z").format(ZonedDateTime.now(ZoneId.of("GMT"))));
    }
    
}
