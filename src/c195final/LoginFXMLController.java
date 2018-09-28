/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195final;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Matthew
 */
public class LoginFXMLController implements Initializable {

    @FXML private javafx.scene.control.TextField userField;
    @FXML private javafx.scene.control.PasswordField passField;
    @FXML private javafx.scene.control.Button loginBtn;
    @FXML private javafx.scene.control.Label warningLabel;
    
    //Comment out line below and uncomment chosen locale to test various regions. Tested and confirmed worked by changing windows language settings.
    private final Locale curLoc = Locale.getDefault();
    //Locale curLoc = Locale.US;
    //Locale curLoc = Locale.CHINA;
    //Locale curLoc = Locale.FRANCE;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    
    
    @FXML
    private boolean validateLogin(int userID) throws IOException, InterruptedException{
         
        if(userID>0){
            userField.setStyle("-fx-border-color: green; -fx-focus-color: green;");
            passField.setStyle("-fx-border-color: green; -fx-focus-color: green;");
            warningLabel.setStyle("-fx-text-fill: green");
            warningLabel.setText("Success! Logging in...");
            return true;
        } else {
            userField.setStyle("-fx-border-color: red; -fx-focus-color: red;");
            passField.setStyle("-fx-border-color: red; -fx-focus-color: red;");
            setFailedLoginMessage();
            return false;
        }
    }
    
    private void accessLogger(String user) { 
        File logins = new File("C:/temp/logins.txt");
        //Creates file if it doesn't already exist
        try {
            if (!logins.exists()) {
                logins.createNewFile();
            }
        } catch (IOException e) {
            System.err.print(e);
        }
        //writes username & GMT time of login
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logins, true))){
            if (!logins.exists()) {
                logins.createNewFile();
            }
            bw.append(user+" - "+ZonedDateTime.now(ZoneId.of("GMT")));
            bw.newLine();
        } catch (IOException e) {
            System.err.print(e);
        }
    }
    
    private void setFailedLoginMessage(){
        warningLabel.setStyle("-fx-text-fill: red");
        if(curLoc == Locale.US || curLoc == Locale.ENGLISH || curLoc == Locale.UK){
            warningLabel.setText("User not found. Please verify your username and password.");
        } else if(curLoc == Locale.FRANCE || curLoc == Locale.CANADA_FRENCH){
            warningLabel.setText("Utilisateur non trouvé. Veuillez vérifier votre nom d'utilisateur et votre mot de passe.");
        } else if(curLoc == Locale.CHINA || curLoc == Locale.CHINESE || curLoc == Locale.SIMPLIFIED_CHINESE || curLoc == Locale.TRADITIONAL_CHINESE || curLoc == Locale.TAIWAN || curLoc == Locale.PRC){
            warningLabel.setText("未找到用户。请检查您的用户名和密码。\nWèi zhǎodào yònghù. Qǐng jiǎnchá nín de yònghù mínghé mìmǎ.");
        } else{
            warningLabel.setText("User not found. Please verify your username and password.");
        }
    }
    
    //Tries to connect and query database
    private int verifyUser(String user, String pass){
        
        int returnID = 0;
        
        if (user.isEmpty() || pass.isEmpty())
            return returnID;
        
        String SQL = "SELECT userid FROM user WHERE userName = '"+user+"' AND password = '"+pass+"';";
        try(ResultSet rs = DBManager.query(SQL)){
            if(rs.next())
                returnID = rs.getInt("userid");
        }catch(SQLException e){
            //Lambda to run on main thread - More readable syntax.
            Platform.runLater(() -> {
                DBManager.failedDBConnect();
            });
            System.out.println(e);
        }
        
        return returnID;
    }
    
    private void showMainStage(String userId) throws IOException{
        //gets FXML controller & parent
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFXML.fxml"));
        Parent root = loader.load();
        MainFXMLController controller = loader.getController();
        
        //passes user ID to main form
        controller.stageSetup(userId);
        
        //assigns current stage as parent and new stage as child
        Stage parentStage = (Stage) loginBtn.getScene().getWindow();
        Stage childStage = new Stage();
        Scene scene = new Scene(root);
        
        //sets stage properties
        childStage.setMinHeight(375);
        childStage.setMinWidth(650);
        parentStage.hide();
        childStage.setScene(scene);
        childStage.setTitle("Scheduler Pro - v1.0");
        childStage.show();
        //Lambda to set on-close action - More readable syntax.
        childStage.setOnCloseRequest((WindowEvent we) -> {
            userField.clear();
            userField.setStyle("");
            passField.clear();
            passField.setStyle("");
            warningLabel.setText("");
            controller.shutdownExecutor();
            childStage.close();
            parentStage.show();
        });
    }
    
    @FXML
    private void login(){
        //Trying out threading... Woo!
        String user = userField.getText();
        //Lambda to create a callable - More elgant/readable syntax.
        Callable<Integer> task = () -> {
            return verifyUser(user,passField.getText());
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(task);
        try{
            int userID = future.get();
            executor.shutdown();
            if(validateLogin(userID)){
                accessLogger(user);
                showMainStage(user);
            }
        } catch(InterruptedException | ExecutionException | IOException e){
            DBManager.failedDBConnect();
            executor.shutdownNow();
        }
    }
    
}