/*
 * Copyright (C) 2018 LuedeckeA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package vacationedit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author LuedeckeA
 */
public class VacationEdit extends Application {
    // GUI members
    private final Button     btnClose     = new Button();
    private final Button     btnDown      = new Button();
    private final Button     btnOpen      = new Button();
    private final Button     btnSave      = new Button();
    private final Button     btnUpload    = new Button();
    private final Label      lblCopyRight = new Label();
    private final Label      lblFile      = new Label();
    private final Label      lblOut       = new Label();
    private final Label      lblUpload    = new Label();
    private final TextField  txtFile      = new TextField();
    private final TextArea   txtEditor    = new TextArea();
    private final Group      root         = new Group();
    
    // Button status
    private boolean server_action     = false;
    private boolean status_btn_save   = false;
    private boolean status_btn_upload = false;
    
    // Context menue
    ContextMenu ctxMenuConfig = new ContextMenu();
    MenuItem    itmConfig     = new MenuItem("Einstellungen anpassen ...");
    
    // Display members
    private Scene scene;
    private Stage main_window;
    
    // IO members
    private final FileIO vacationFile = new FileIO();
    
    // private methods
    private String chooseFilePath(Stage window, String ini_path) {
        ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                                          "Vacation File (*.txt)"
                                         ,"*.txt");
        FileChooser chooser = new FileChooser();
        File path = new File(ini_path);
        String name = "";
        
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(path.toString().replaceAll(path.getName(), "")));
        File file = chooser.showOpenDialog(window);
        
        if (file != null) {
            name = file.getAbsolutePath();
        }
        
        return name;
    }

    private boolean confirmSave() {
        boolean save = false;

        if (!btnSave.isDisabled()) {
            Optional<ButtonType> dlg_result;
            ConfirmDlg dlg = new ConfirmDlg(
                                     "Änderungen nicht gespeichert",
                                     "Sollen die Änderungen vor dem Beenden gespeichert werden?"
                                 );
            dlg_result = dlg.show();
            
            if (dlg_result.get() == dlg.getBtnYes()) {
                save = true;
            }
        }
        
        return save;
    }

    private boolean confirmSvrLoad(String title, String text) {
        boolean confirm = false;
        Optional<ButtonType> dlg_result;
        ConfirmDlg dlg = new ConfirmDlg(
                                 title,
                                 text
                             );
        dlg.setBack_color("#FFFFF0");
        
        dlg_result = dlg.show();
            
        if (dlg_result.get() == dlg.getBtnYes()) {
            confirm = true;
        }
        
        return confirm;
    }
    
    private boolean discardChanges() {
        boolean discard = true;

        if (!btnSave.isDisabled()) {
            Optional<ButtonType> dlg_result;
            ConfirmDlg dlg = new ConfirmDlg(
                                     "Änderungen nicht gespeichert",
                                     "Sollen die Änderungen verworfen werden?"
                                 );
            dlg_result = dlg.show();
            
            if (dlg_result.get() == dlg.getBtnNo()) {
                discard = false;
            }
        }
        
        return discard;
    }

    private void handleBtnClose() {
        if (!discardChanges()) {
            return;
        }
        if (!btnUpload.isDisabled()) {
            if (!confirmSvrLoad("Upload ausstehend", "Änderungen wurden nicht hochgeladen.\nTrotzdem beenden?")) {
                return;
            }
        }
        
        System.exit(0);
    }
    
    private void handleBtnDown() {
        String file_name = txtFile.getText();
        String up_name   = file_name.substring(file_name.lastIndexOf("\\") + 1);
        
        if (!confirmSvrLoad("Download", "Letzte Version \"" + up_name + "\" vom Server holen?")) {
            return;
        } else {
            setControls(true);
        }
        
        server_action = true;
        Task task = new Task() {
            @Override
            protected String call() throws Exception {
                String msg;

                scene.setCursor(Cursor.WAIT); //Change cursor to wait style
                if (    vacationFile.getConfig().getFtp_port().equals("22")
                     || vacationFile.getConfig().getFtp_protocol().equals("SFTP")) {
                    msg = vacationFile.downloadFileSFTP(file_name);
                } else {
                    msg = vacationFile.downloadFileFTP(file_name);
                }
                updateMessage(msg);
                if (vacationFile.getError_msg().equals("")) {
                    lblOut.setTextFill(Color.LAWNGREEN);
                } else {
                    lblOut.setTextFill(Color.RED);
                }
                setControls(false);
                scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
                server_action = false;
                return msg;
            }
        };
        lblOut.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }
    
    private void handleBtnOpen() {
        if (!discardChanges()) {
            return;
        }
        if (!btnUpload.isDisabled()) {
            if (!confirmSvrLoad("Upload ausstehend", "Änderungen wurden nicht hochgeladen.\nTrotzdem fortfahren?")) {
                return;
            }
        }

        final String ini_path; 
        final String name;
        ini_path = txtFile.getText();
        name = chooseFilePath(main_window, ini_path);

        if(!name.equals("")) {
            txtFile.setText(name);
            try {
                txtEditor.setText(vacationFile.openFile(name));
                setButtons(false, false, true);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(VacationEdit.class.getName()).log(Level.INFO, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(VacationEdit.class.getName()).log(Level.INFO, null, ex);
            }
        }
    }
    
    private void handleBtnSave() {
        try {
            vacationFile.saveFile(txtFile.getText(), txtEditor.getText());
            setButtons(false, true, true);
        } catch (IOException ex) {
            Logger.getLogger(VacationEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleBtnUpload(boolean confirm) {
        String file_name = txtFile.getText();
        
        if (confirm) {
            if (!confirmSvrLoad("Upload", "Änderungen in  \"" + file_name + "\" auf den Server hochladen?")) {
                return;
            }
        }

        server_action = true;
        setControls(server_action);
        Task task;
        task = new Task() {
            @Override
            protected String call() throws Exception {
                String msg;
                
                scene.setCursor(Cursor.WAIT); //Change cursor to wait style
                if (    vacationFile.getConfig().getFtp_port().equals("22")
                     || vacationFile.getConfig().getFtp_protocol().equals("SFTP")) {
                    msg = vacationFile.uploadFileSFTP(file_name);
                } else {
                    msg = vacationFile.uploadFileFTP(file_name);
                }
                updateMessage(msg);
                setControls(false);
                if (vacationFile.getError_msg().equals("")) {
                    lblOut.setTextFill(Color.LAWNGREEN);
                    setButtons(false, false, false) ;
                } else {
                    lblOut.setTextFill(Color.RED);
                }
                scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
                server_action = false;
                return msg;
            }
        };
        lblOut.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }
    
    private void handleCloseRequest() {
        if (confirmSave()) {
            handleBtnSave();
        }
        if (!btnUpload.isDisabled()) {
            if (confirmSvrLoad("Upload ausstehend", "Änderungen wurden nicht hochgeladen.\nVor Beenden hochladen?")) {
                handleBtnUpload(false);
            }
        }
    }

    private void handleLblUpload() {
        lblOut.textProperty().unbind();
        lblOut.setText(vacationFile.getConfig().editConfig());
        
        if (vacationFile.getConfig().isWith_error()) {
            lblOut.setTextFill(Color.RED);
        } else {
            lblOut.setTextFill(Color.LAWNGREEN);
        }
        
        if (!vacationFile.getConfig().isDlg_canceled()) {
            lblUpload.setText(
                "   " + vacationFile.getConfig().getFtp_protocol() +
                "://" +  vacationFile.getConfig().getFtp_user() +
                "@" + vacationFile.getConfig().getFtp_server() + 
                ":" + vacationFile.getConfig().getFtp_port()
            );

            txtFile.setText(vacationFile.getConfig().getDefault_path());
            
            try {
                txtEditor.setText (vacationFile.openFile(txtFile.getText()));
            } catch (IOException ex) {
                Logger.getLogger(VacationEdit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void initGui() {
        // Context menu
        itmConfig.setOnAction((ActionEvent event) -> {
            handleLblUpload();
        });
        
        ctxMenuConfig.getItems().add(itmConfig);
                
        // UI controls
        btnClose.setLayoutX(1210);
        btnClose.setLayoutY(720);
        btnClose.setText("Beenden");
        btnClose.setOnAction((ActionEvent event) -> {
            handleBtnClose();
        });

        btnDown.setLayoutX(545);
        btnDown.setLayoutY(10);
        btnDown.setText(" Download from Server");
        btnDown.setOnAction((ActionEvent event) -> {
            handleBtnDown();
        });

        btnOpen.setLayoutX(515);
        btnOpen.setLayoutY(10);
        btnOpen.setText("...");
        btnOpen.setOnAction((ActionEvent event) -> {
            handleBtnOpen();
        });

        btnSave.setLayoutX(10);
        btnSave.setLayoutY(720);
        btnSave.setText("Speichern");
        btnSave.setOnAction((ActionEvent event) -> {
            handleBtnSave();
        });

        btnUpload.setLayoutX(90);
        btnUpload.setLayoutY(720);
        btnUpload.setText("Upload");
        btnUpload.setOnAction((ActionEvent event) -> {
            handleBtnUpload(true);
        });

        lblCopyRight.setLayoutX(10);
        lblCopyRight.setLayoutY(755);
        lblCopyRight.setStyle("-fx-font: normal 10px 'arial'");
        lblCopyRight.setText("Copyright (c)  A. Luedecke  2018");

        lblFile.setLayoutX(10);
        lblFile.setLayoutY(13);
        lblFile.setText("Lokale Datei:");
        
        lblOut.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        lblOut.setLayoutX(155);
        lblOut.setLayoutY(723);
        lblOut.setPrefWidth(1045);
        lblOut.setTextFill(Color.LIGHTGREEN);
        
        lblUpload.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        lblUpload.setLayoutX(690);
        lblUpload.setLayoutY(13);
        lblUpload.setPrefWidth(450);
        lblUpload.setText(
            "   " + vacationFile.getConfig().getFtp_protocol() +
            "://" +  vacationFile.getConfig().getFtp_user() +
            "@" + vacationFile.getConfig().getFtp_server() + 
            ":" + vacationFile.getConfig().getFtp_port()
        );
        lblUpload.setTextFill(Color.WHITE);
        lblUpload.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2) {
                    handleLblUpload();
                }
            }
        });
        lblUpload.setOnContextMenuRequested((ContextMenuEvent event) -> {
            ctxMenuConfig.show(lblUpload, event.getScreenX(), event.getScreenY());
        });
        
        txtFile.setLayoutX(80);
        txtFile.setLayoutY(10);
        txtFile.setPrefWidth(425);
        txtFile.setText(vacationFile.getConfig().getDefault_path());
        
        txtEditor.setLayoutX(10);
        txtEditor.setLayoutY(50);
        txtEditor.setPrefHeight(660);
        txtEditor.setPrefWidth(1260);
        txtEditor.addEventHandler(KeyEvent.KEY_TYPED,  (KeyEvent event) -> {
            setButtons(true, false, true);
        });
        txtEditor.addEventHandler(KeyEvent.KEY_PRESSED,  (KeyEvent event) -> {
            setButtons(true, false, true);
        });
        
        try {
            txtEditor.setText(vacationFile.openFile(txtFile.getText()));
        } catch (IOException ex) {
            Logger.getLogger(VacationEdit.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Display
        root.getChildren().add(txtEditor);
        root.getChildren().add(lblFile);
        root.getChildren().add(txtFile);
        root.getChildren().add(btnOpen);
        root.getChildren().add(btnDown);
        root.getChildren().add(btnSave);
        root.getChildren().add(btnUpload);
        root.getChildren().add(lblUpload);
        root.getChildren().add(lblOut);
        root.getChildren().add(btnClose);
        root.getChildren().add(lblCopyRight);
        
        setButtons(false, false, true);
    }
    
    private void setButtons(boolean save, boolean upload, boolean reset_output) {
        if (reset_output) {
            lblOut.textProperty().unbind();
            lblOut.setText("");
        }
        btnSave.setDisable(!save);
        btnUpload.setDisable(!upload);
        status_btn_save   = btnSave.isDisabled();
        status_btn_upload = btnUpload.isDisabled();
    }
    
    private void setControls(boolean server_action) {
        if (server_action) {
            btnSave.setDisable(true);
            btnUpload.setDisable(true);
        } else {
            btnSave.setDisable(status_btn_save);
            btnUpload.setDisable(status_btn_upload);            
        }

        btnClose.setDisable(server_action);
        btnDown.setDisable(server_action);
        btnOpen.setDisable(server_action);
        txtEditor.setDisable(server_action);
        txtFile.setDisable(server_action);
    }
    
    @Override
    public void start(Stage primaryStage) {
        initGui();
        
        main_window = primaryStage;
        scene = new Scene(root, 1275, 768, Color.rgb(0xEA, 0xF0, 0xFF, .99));
        
        main_window.getIcons().add(new Image("file:res/app_icon.png"));
        main_window.setTitle("Vacation Editor - Version 1.0.0");
        main_window.setScene(scene);
        main_window.setResizable(false);
        main_window.setOnCloseRequest((WindowEvent event) -> {
            handleCloseRequest();
        });
        main_window.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}
