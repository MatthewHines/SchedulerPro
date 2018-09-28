/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195final;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Matthew
 */
public class CustomerEditorFXMLController implements Initializable {
    //CBoxes
    @FXML javafx.scene.control.ComboBox<Customer> existingCustomers;
    @FXML javafx.scene.control.ComboBox<City> existingCities;
    @FXML javafx.scene.control.ComboBox<Country> existingCountries;
    //Text fields
    @FXML javafx.scene.control.TextField nameTF;
    @FXML javafx.scene.control.TextField address1TF;
    @FXML javafx.scene.control.TextField address2TF;
    @FXML javafx.scene.control.TextField zipTF;
    @FXML javafx.scene.control.TextField cityTF;
    @FXML javafx.scene.control.TextField countryTF;
    @FXML javafx.scene.control.TextField phoneTF;
    //Modifiable labels
    @FXML javafx.scene.control.Label customerIDLbl;
    @FXML javafx.scene.control.Label userIDLbl;
    @FXML javafx.scene.control.Label dateCreatedLbl;
    @FXML javafx.scene.control.Label creatorLbl;
    @FXML javafx.scene.control.Label lastEditedLbl;
    @FXML javafx.scene.control.Label editorLbl;
    //Buttons
    @FXML javafx.scene.control.Button cancelBtn;
    @FXML javafx.scene.control.Button saveBtn;
    @FXML javafx.scene.control.Button deleteBtn;
    //Other
    @FXML javafx.scene.control.CheckBox activeChkBox;
    @FXML javafx.scene.layout.StackPane loadingOverlay;
    //Customer list & converter
    private final ObservableList<Customer> existingCustList = FXCollections.observableArrayList();
    private StringConverter<Customer> customerConverter;
    //City list & converter
    private final ObservableList<City> existingCityList = FXCollections.observableArrayList();
    private StringConverter<City> cityConverter;
    //Country list & converter
    private final ObservableList<Country> existingCountryList = FXCollections.observableArrayList();
    private StringConverter<Country> countryConverter;
    
    Customer newCustomer = new Customer("Add New Customer");
    Customer selectedCustomer = newCustomer;
    
    City newCity = new City("Add New City");
    City selectedCity = newCity;
    
    Country newCountry = new Country("Add New Country");
    Country selectedCountry = newCountry;

    private String editor;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        newCustomer.setAddress(new Address());
        newCity.setCountry(newCountry);
        newCustomer.getAddress().setCity(newCity);
    }

    public void stageSetup(Customer sel, String currentUser){
        editor = currentUser;
        userIDLbl.setText("User ID: " + editor);
        selectedCustomer = sel;
        selectedCity = sel.getAddress().getCity();
        selectedCountry = sel.getAddress().getCity().getCountry();
        nameTF.setText(sel.getName());
        asyncFormBuilder();
    }
    
    public void stageSetup(String currentUser){
        editor = currentUser;
        selectedCustomer = newCustomer;
        userIDLbl.setText("User ID: " + editor);
        asyncFormBuilder();
    }
    
    @FXML
    private void deleteCustomer(){
        if(existingCustomers.getSelectionModel().getSelectedIndex() == 0){
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.NONE,"Please select an existing customer to use this function.",ok);
            alert.setTitle("Scheduler Pro - v1.0");
            alert.show();
        } else{
            ButtonType yes = new ButtonType("Yes, delete.", ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType("No.", ButtonBar.ButtonData.NO);
            Alert alert = new Alert(Alert.AlertType.NONE,"Are you sure you want to delete this customer? This action cannot be undone.",yes,no);
            alert.setTitle("Scheduler Pro - v1.0");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yes) {
                existingCustomers.getSelectionModel().getSelectedItem().delete();
                ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                alert = new Alert(Alert.AlertType.NONE,"Customer deleted.",ok);
                result = alert.showAndWait();
                if (result.isPresent()) {
                    cancelButtonAction();
                } else{
                    cancelButtonAction();
                }
                
            } 
        }
    }
    
    @FXML
    private void saveCustomer(){
        if(formIsValid()){
            if(existingCountries.getSelectionModel().getSelectedIndex() == 0){
                selectedCountry = newCountry;
                selectedCountry.setName(countryTF.getText());
                selectedCountry.save(editor);
            }
            if(existingCities.getSelectionModel().getSelectedIndex() == 0){
                selectedCity = newCity;
                selectedCity.setCountry(selectedCountry);
                selectedCity.setName(cityTF.getText());
                selectedCity.save(editor);
            }
            if(existingCustomers.getSelectionModel().getSelectedIndex() == 0){
                selectedCustomer = newCustomer;
            }
            selectedCustomer.setName(nameTF.getText());
            selectedCustomer.setActive(activeChkBox.isSelected());
            selectedCustomer.getAddress().setCity(selectedCity);
            selectedCustomer.getAddress().setAddress(address1TF.getText());
            selectedCustomer.getAddress().setAddress2(address2TF.getText());
            selectedCustomer.getAddress().setPhone(phoneTF.getText());
            selectedCustomer.getAddress().setPostalCode(zipTF.getText());
            selectedCustomer.getAddress().save(editor);
            
            selectedCustomer.save(editor);
            
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                Alert alert = new Alert(Alert.AlertType.NONE,"Save successful.",ok);
                alert.setTitle("Scheduler Pro - v1.0");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    cancelButtonAction();
                } else {
                    cancelButtonAction();
                }
        }
    }
    
    private boolean formIsValid(){
        
        if(existingCountries.getSelectionModel().getSelectedIndex() == 0){
            if(countryTF.getText() == null || countryTF.getText().isEmpty()){
                printErrMsg("Please enter a country name or select an existing country.");
                return false;
            } 
        } 
        if(existingCities.getSelectionModel().getSelectedIndex() == 0){
            if(cityTF.getText() == null || cityTF.getText().isEmpty()){
                printErrMsg("Please enter a city name or select an existing city.");
                return false;
            } 
        } 
        if(zipTF.getText() == null || zipTF.getText().isEmpty()){
                printErrMsg("Please enter a ZIP code.");
                return false;
        } else if(phoneTF.getText() == null || phoneTF.getText().isEmpty()){
                printErrMsg("Please enter a phone number.");
                return false;
        } else if(nameTF.getText() == null || nameTF.getText().isEmpty()){
                printErrMsg("Please enter a customer name.");
                return false;
        } else if(address1TF.getText() == null || address1TF.getText().isEmpty()){
                printErrMsg("Please enter a customer name.");
                return false;
        } else{
            return true;
        }

    }
    
    private void textLimits(){
       //Lambdaaaaas
        UnaryOperator<TextFormatter.Change> filter1 = c -> {
            String newText = c.getControlNewText();
            if (newText.matches("^.{0,50}")) {
                return c ;
            } else {
                return null ;
            }
        };
        address1TF.setTextFormatter(new TextFormatter<>(filter1));
        address2TF.setTextFormatter(new TextFormatter<>(filter1));
        cityTF.setTextFormatter(new TextFormatter<>(filter1));
        countryTF.setTextFormatter(new TextFormatter<>(filter1));
        
        UnaryOperator<TextFormatter.Change> filter2 = c -> {
            String newText = c.getControlNewText();
            if (newText.matches("^.{0,20}")) {
                return c ;
            } else {
                return null ;
            }
        };
        phoneTF.setTextFormatter(new TextFormatter<>(filter2));
        
        UnaryOperator<TextFormatter.Change> filter3 = c -> {
            String newText = c.getControlNewText();
            if (newText.matches("^.{0,10}")) {
                return c ;
            } else {
                return null ;
            }
        };
        zipTF.setTextFormatter(new TextFormatter<>(filter3));
        
        UnaryOperator<TextFormatter.Change> filter4 = c -> {
            String newText = c.getControlNewText();
            if (newText.matches("^.{0,45}")) {
                return c ;
            } else {
                return null ;
            }
        };
        nameTF.setTextFormatter(new TextFormatter<>(filter4));
    }
    
    //builds existing customer list dynamically
    private void buildCustomerList(){
        
        String query = "SELECT customerid FROM customer;";
        try(ResultSet rs = DBManager.query(query)){        
            while (rs.next()){
                Customer current = new Customer(rs.getInt("customerid"));
                existingCustList.add(current); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build appointment table.");
                System.out.println(e);
        }       
        //sorts list w/ lambdas
        existingCustList
            .stream()
            .sorted((object1, object2) -> object1.getName().compareToIgnoreCase(object2.getName()));
        existingCustList.add(0,newCustomer);
        existingCustomers.setItems(existingCustList);
        //creates converter to display name
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
        existingCustomers.setConverter(customerConverter);
    }
    
    private void buildCityList(){
        
        String query = "SELECT cityid FROM city;";
        try(ResultSet rs = DBManager.query(query);){        
            while (rs.next()){
                City current = new City(rs.getInt("cityid"));
                existingCityList.add(current); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build city table.");
                System.out.println(e);
        }
        //sorts list w/ lambdas
        existingCityList
            .stream()
            .sorted((object1, object2) -> object1.getName().compareToIgnoreCase(object2.getName()));
        //Adds the blank "Add New" city to the top of the list
        existingCityList.add(0, newCity);
        existingCities.setItems(existingCityList);
        //creates converter to display name
        cityConverter = new StringConverter<City>() {
            @Override
            public String toString(City c) {
                return c.getName();
            }
            @Override
            public City fromString(String string) {
                return null;
            }
        };
        existingCities.setConverter(cityConverter);
    }
    
    private void buildCountryList(){
        
        String query = "SELECT countryid FROM country;";
        try(ResultSet rs = DBManager.query(query);){        
            while (rs.next()){
                Country current = new Country(rs.getInt("countryid"));
                existingCountryList.add(current); 
            }
        }catch(SQLException e){
                System.out.println("Unable to build country table.");
                System.out.println(e);
        }
        //sorts list w/ lambdas
        existingCountryList
            .stream()
            .sorted((object1, object2) -> object1.getName().compareToIgnoreCase(object2.getName()));
        //Adds the blank "Add New" country to the top of the list
        existingCountryList.add(0, newCountry);
        existingCountries.setItems(existingCountryList);
        //creates converter to display name
        countryConverter = new StringConverter<Country>() {
            @Override
            public String toString(Country c) {
                return c.getName();
            }
            @Override
            public Country fromString(String string) {
                return null;
            }
        };
        existingCountries.setConverter(countryConverter);
    }
    
    @FXML
    private void selectCustomer(){
        if(existingCustomers.getSelectionModel().getSelectedItem().equals(newCustomer)){
            clearForm();
            selectedCustomer = newCustomer;
            selectedCity = newCity;
            selectedCountry = newCountry;           
            customerIDLbl.setText("Customer ID: "+newCustomer.getID());           
            selectMatchingCustomer();
            selectMatchingCity();
            selectMatchingCountry();
        } else{
            selectedCustomer = (Customer) existingCustomers.getSelectionModel().getSelectedItem();
            
            selectedCity = selectedCustomer.getAddress().getCity();
            selectedCountry = selectedCustomer.getAddress().getCity().getCountry();
            
            nameTF.setText(selectedCustomer.getName());
            customerIDLbl.setText("Customer ID: "+selectedCustomer.getID());
            dateCreatedLbl.setText("Date Created: "+selectedCustomer.getCreationDate());
            creatorLbl.setText("Creator: " + selectedCustomer.getCreator());
            lastEditedLbl.setText("Last Updated: " + selectedCustomer.getLastUpdate());
            editorLbl.setText("Last Editor: " + selectedCustomer.getLastEditor());
            address1TF.setText(selectedCustomer.getAddress().getAddress());
            address2TF.setText(selectedCustomer.getAddress().getAddress2());
            zipTF.setText(selectedCustomer.getAddress().getPostalCode());
            phoneTF.setText(selectedCustomer.getAddress().getPhone());
            cityTF.setText(selectedCustomer.getAddress().getCity().getName());
            countryTF.setText(selectedCustomer.getAddress().getCity().getCountry().getName());
            activeChkBox.setSelected(selectedCustomer.isActive());
            selectMatchingCustomer();
            selectMatchingCity();
            selectMatchingCountry();
        }
    }
    
    @FXML
    private void selectMatchingCustomer(){
        //Lambdas to select customer
        if (!selectedCustomer.equals(newCustomer)){
            List<Customer> match = existingCustList.stream()
                .filter(c -> c.getID() == selectedCustomer.getID())
                .collect(Collectors.toList());
            existingCustomers.getSelectionModel().select(match.get(0));
        } else{
            existingCustomers.getSelectionModel().select(0);
        }
    }
    
    @FXML
    private void selectMatchingCity(){
        //Lambdas to select city
        if (!selectedCity.equals(newCity)){
            List<City> match = existingCityList.stream()
                .filter(c -> c.getID() == selectedCity.getID())
                .collect(Collectors.toList());
            existingCities.getSelectionModel().select(match.get(0));
            cityTF.setText(selectedCity.getName());
            cityTF.setDisable(true);            
        } else {
            existingCities.getSelectionModel().select(0);
            cityTF.setText("");
            cityTF.setDisable(false);
        }
    }
    
    @FXML
    private void selectMatchingCountry(){
        //Lambdas to select country
        if (!selectedCountry.equals(newCountry)){
            List<Country> match = existingCountryList.stream()
                .filter(c -> c.getID() == selectedCountry.getID())
                .collect(Collectors.toList());
            existingCountries.getSelectionModel().select(match.get(0));
            countryTF.setText(selectedCountry.getName());
            existingCountries.setDisable(true);
            countryTF.setDisable(true);
        } else{
            existingCountries.getSelectionModel().select(0);
            countryTF.setText("");
            countryTF.setDisable(false);
            existingCountries.setDisable(false);
        }
    }
    
    @FXML
    private void changeCityAction(){
        selectedCity = (City) existingCities.getSelectionModel().getSelectedItem();
        selectedCountry = selectedCity.getCountry();
        if(selectedCity.equals(newCity)){
            cityTF.setDisable(false);
            cityTF.setText("");
        } else{
            cityTF.setDisable(true);
            cityTF.setText(selectedCity.getName());
        }
        selectMatchingCountry();
        
    }
    
    @FXML
    private void changeCountryAction(){
        selectedCountry = (Country) existingCountries.getSelectionModel().getSelectedItem();
        if(selectedCountry.equals(newCountry)){
            countryTF.setDisable(false);
            countryTF.setText("");
            existingCountries.setDisable(false);
        } else{
            countryTF.setDisable(true);
            countryTF.setText(selectedCountry.getName());
        }
    }
    
    private void clearForm(){
    
        nameTF.clear();
        address1TF.clear();
        address2TF.clear();
        zipTF.clear();
        cityTF.clear();
        countryTF.clear();
        phoneTF.clear();

        customerIDLbl.setText("Customer ID: ");
        userIDLbl.setText("User ID: " + editor);
        dateCreatedLbl.setText("Date Created: N/A");
        creatorLbl.setText("Creator: N/A");
        lastEditedLbl.setText("Last Updated: N/A");
        editorLbl.setText("Last Editor: N/A");
        activeChkBox.setSelected(true);
        
        existingCities.getSelectionModel().select(0);
        existingCountries.getSelectionModel().select(0);
    }
    
    @FXML
    private void cancelButtonAction(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage,WindowEvent.WINDOW_CLOSE_REQUEST));      
    }
    
    public void shutdownExecutor(){
        try {
            executor.shutdown();
            executor.awaitTermination(250, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        
        }
        finally {
            executor.shutdownNow();
        }
        
    }
    
    private void asyncFormBuilder(){ 
        //lambda for scheduling background & main thread tasks
        executor.submit(() -> {
            Platform.runLater(() -> {
                loadingOverlay.setVisible(true);
            });
            buildCustomerList();
            buildCityList();
            buildCountryList();
            Platform.runLater(() -> {
                selectMatchingCustomer();
                selectCustomer();
                loadingOverlay.setVisible(false);
            });
        });
    }
    
    private void printErrMsg(String msg){
        ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.NONE,msg,ok);
        alert.setTitle("Scheduler Pro - v1.0");
        alert.show(); 
    }
    
}
