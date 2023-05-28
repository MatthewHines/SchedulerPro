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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;


/**
 *
 * @author Matthew
 */
public class MainFXMLController implements Initializable {
    
    //Buttons
    @FXML javafx.scene.control.Button scheduleBtn;
    @FXML javafx.scene.control.Button viewBtn;
    @FXML javafx.scene.control.Button addEditCustomerBtn;
    @FXML javafx.scene.control.Button clearFilterBtn;
    @FXML javafx.scene.control.Button reportBtn;
    
    //Labels
    @FXML javafx.scene.control.Label username;
    @FXML javafx.scene.control.Label currentTime;
    @FXML javafx.scene.control.Label locationLabel;
    
    //Other elements
    @FXML javafx.scene.control.ComboBox timezoneCBox;
    @FXML javafx.scene.control.DatePicker dateField;
    @FXML javafx.scene.control.RadioButton dayFilter;
    @FXML javafx.scene.control.RadioButton weekFilter;
    @FXML javafx.scene.control.RadioButton monthFilter;
    
    //Table Columns
    @FXML javafx.scene.control.TableColumn<Appointment, Date> startTimeCol;
    @FXML javafx.scene.control.TableColumn<Appointment, Number> durationCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> custNameCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> contactNameCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> titleCol;
    @FXML javafx.scene.control.TableColumn<Appointment, String> locationCol;
    @FXML javafx.scene.control.TableColumn<Appointment, Date> dateCol;
    @FXML javafx.scene.layout.StackPane loadingOverlay;
    
     //TableView and DB
    @FXML javafx.scene.control.TableView apptTable;
    @FXML private ObservableList<Appointment> apptData = FXCollections.observableArrayList();
    
    //Search function filters (and a little lambda action)
    @FXML private FilteredList<Appointment> apptSearchFilter = new FilteredList<>(apptData, p -> true);
    @FXML private SortedList<Appointment> sortedApptData = new SortedList<>(apptSearchFilter);
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    TimeZone selectedTimeZone = TimeZone.getDefault();
    Date today = Calendar.getInstance().getTime();
    
    private String userID;
    private final ObservableList<TimeZone> timezones = FXCollections.observableArrayList();
    private StringConverter<TimeZone> converter;
    
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

        apptTable.getSelectionModel().selectFirst();
  
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
    
    @FXML
    private void showAddEditStage() throws IOException{
        //gets FXML controller & parent
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerEditorFXML.fxml"));
        Parent root = loader.load();
        CustomerEditorFXMLController controller = loader.getController();       
        //passes user ID to editor form
        if(apptTable.getSelectionModel().getSelectedItem() != null){
            Appointment selAppt = (Appointment) apptTable.getSelectionModel().getSelectedItem();
            Customer selected = selAppt.getCustomer();
            controller.stageSetup(selected, userID);
        } else{
            controller.stageSetup(userID);
        }
        //assigns current stage as parent and new stage as child
        Stage parentStage = (Stage) addEditCustomerBtn.getScene().getWindow();
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
    private void showSchedulerStage() throws IOException{
        //gets FXML controller & parent
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AppointmentFXML.fxml"));
        Parent root = loader.load();
        AppointmentFXMLController controller = loader.getController();          
        controller.stageSetup(selectedTimeZone, userID);
        
        //assigns current stage as parent and new stage as child
        Stage parentStage = (Stage) scheduleBtn.getScene().getWindow();
        Stage childStage = new Stage();
        Scene scene = new Scene(root);
        
        //sets stage properties
        childStage.setMinHeight(500);
        childStage.setMinWidth(850);
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
    private void showViewApptStage() throws IOException{
        //gets FXML controller & parent
        if(!apptTable.getSelectionModel().isEmpty()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AppointmentFXML.fxml"));
            Parent root = loader.load();
            AppointmentFXMLController controller = loader.getController();          
            controller.stageSetup(selectedTimeZone, userID, (Appointment) apptTable.getSelectionModel().getSelectedItem());

            //assigns current stage as parent and new stage as child
            Stage parentStage = (Stage) viewBtn.getScene().getWindow();
            Stage childStage = new Stage();
            Scene scene = new Scene(root);

            //sets stage properties
            childStage.setMinHeight(500);
            childStage.setMinWidth(850);
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
        } else{
            ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.NONE,"An appointment must be selected first.",ok);
            alert.setTitle("Scheduler Pro - v1.0");
            alert.show(); 
        }
    }
    
    @FXML
    private void showReportStage() throws IOException{
        //gets FXML controller & parent

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportsFXML.fxml"));
        Parent root = loader.load();
        ReportsFXMLController controller = loader.getController();          
        controller.stageSetup(userID, selectedTimeZone);

        //assigns current stage as parent and new stage as child
        Stage parentStage = (Stage) reportBtn.getScene().getWindow();
        Stage childStage = new Stage();
        Scene scene = new Scene(root);

        //sets stage properties
        childStage.setMinHeight(500);
        childStage.setMinWidth(850);
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
    
    @FXML
    private void updateTimeZone(){
        selectedTimeZone = (TimeZone) timezoneCBox.getSelectionModel().getSelectedItem();
        updateCurrentTime();
        columnDataFormatter();
    }
    
    public void stageSetup(String user){
        userID = user;
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
        filterListenerSetup();
        upcomingApptCheck();
        asyncFormBuilder();
    }
    
    private void filterListenerSetup(){
        //if DatePicker field is empty, show all. Else, show matching dates. + lambda for simpler syntax
        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            apptSearchFilter.setPredicate(appointment -> {               
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                if(weekFilter.isSelected() && newValue != null){
                    dtf = DateTimeFormatter.ofPattern("yyyy");
                    WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
                    int filterWeek = newValue.get(weekFields.weekOfWeekBasedYear());
                    LocalDate date = appointment.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int apptWeek = date.get(weekFields.weekOfWeekBasedYear());
                    return (newValue.toString().isEmpty() || 
                        (apptWeek == filterWeek && dtf.format(localApptTime(appointment.getStartTime())).equals(dtf.format(newValue))));
                } else if(monthFilter.isSelected() && newValue != null){
                    dtf = DateTimeFormatter.ofPattern("MM/yyyy");
                    return (newValue.toString().isEmpty() || 
                        dtf.format(localApptTime(appointment.getStartTime())).equals(dtf.format(newValue)));
                } else{
                    return (newValue == null || 
                        newValue.toString().isEmpty() || 
                        dtf.format(localApptTime(appointment.getStartTime())).equals(dtf.format(newValue)));
                }
            });
        });
    }
    
    private void upcomingApptCheck(){
        //Lambda to run on background thread - More readable syntax.
        executor.submit(() -> {
            try(ResultSet rs = DBManager.query("SELECT * FROM appointment WHERE contact = '"+userID+"';")){  
                while (rs.next()){
                    Appointment current = new Appointment(rs.getInt("appointmentId"));
                    int minutesUntil = (int) ZonedDateTime.now(selectedTimeZone.toZoneId()).until(localApptTime(current.getStartTime()), ChronoUnit.MINUTES);
                    if( minutesUntil < 15 && minutesUntil >= 0 ){
                        String name = current.getCustName();
                        //Lambda to run on main thread - More readable syntax.
                        Platform.runLater(() -> {
                            ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                            Alert alert = new Alert(Alert.AlertType.NONE,"You have a scheduled appointment with "+name+" starting within 15 minutes.",ok);
                            alert.setTitle("Scheduler Pro - v1.0");
                            alert.show(); 
                        });
                        rs.last();
                    }
                }
            }catch(SQLException e){
                    System.out.println("Unable to build appointment table.");
                    System.out.println(e);
            }
        });
    }
    
    @FXML
    private void triggerFilterListenerUpdate(){
        LocalDate ld = dateField.getValue();
        clearFilter();
        dateField.setValue(ld);
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
    
    private void updateCurrentTime(){
        currentTime.setText(DateTimeFormatter.ofPattern("EEE MMM dd, yyyy\nhh:mm:ss a z").format(ZonedDateTime.now(selectedTimeZone.toZoneId())));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    private void asyncFormBuilder(){
        //Threading! Lambdas! This runs on the background thread.
        executor.submit(() -> {
            //Lambda to run on main thread.
            Platform.runLater(() -> {
                loadingOverlay.setVisible(true);
            });
            Platform.runLater(() -> {
                buildApptList();
            });
            //Lambda to run on main thread.
            Platform.runLater(() -> {
                loadingOverlay.setVisible(false);
            });
        });
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
    
    
    
}
