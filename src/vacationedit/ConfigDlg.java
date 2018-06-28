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

import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author LuedeckeA
 */
public class ConfigDlg {
    // Dialog members
    private final Dialog<ButtonType> dialog    = new Dialog<>();
    private final ButtonType         btnSave   = new ButtonType("Speichern");
    private final ButtonType         btnCancel = new ButtonType("Abbrechen");

    private final GridPane      grid           = new GridPane();
    private final Label         lblCharCode    = new Label("Zeichen Codierung:");
    private final Label         lblDefPath     = new Label("Standard Ordner:");
    private final Label         lblFtpPort     = new Label("FTP Port:");
    private final Label         lblFtpProtocol = new Label("FTP Protokoll:");
    private final Label         lblFtpServer   = new Label("FTP Server:");
    private final Label         lblFtpUser     = new Label("Benutzername:");
    private final Label         lblFtpPassword = new Label("Passwort:");
    private final TextField     txtCharCode    = new TextField();
    private final TextField     txtDefPath     = new TextField();
    private final TextField     txtFtpPort     = new TextField();
    private final TextField     txtFtpProtocol = new TextField();
    private final TextField     txtFtpServer   = new TextField();
    private final TextField     txtFtpUser     = new TextField();
    private final PasswordField txtFtpPassword = new PasswordField();
    
    // Variable members
    private String char_code;
    private String default_path;
    private String ftp_port;
    private String ftp_protocol;
    private String ftp_server;    
    private String ftp_user;
    private String ftp_password;
    
    // Constructor
    public ConfigDlg( String char_code
                     ,String default_path
                     ,String ftp_port
                     ,String ftp_protocol
                     ,String ftp_server
                     ,String ftp_user
                     ,String ftp_password) {
        this.char_code    = char_code;
        this.default_path = default_path;
        this.ftp_port     = ftp_port;
        this.ftp_protocol = ftp_protocol;
        this.ftp_server   = ftp_server;
        this.ftp_user     = ftp_user;
        this.ftp_password = ftp_password;
        
        initGui();
    }
    
    // Getters
    public ButtonType getBtnSave() {
        char_code    = txtCharCode.getText();
        default_path = txtDefPath.getText();
        ftp_port     = txtFtpPort.getText();
        ftp_protocol = txtFtpProtocol.getText();
        ftp_server   = txtFtpServer.getText();
        ftp_user     = txtFtpUser.getText();
        ftp_password = txtFtpPassword.getText();
        
        return btnSave;
    }

    public ButtonType getBtnCancel() {
        return btnCancel;
    }
    
    public String getChar_code() {
        return char_code;
    }

    public String getDefault_path() {
        return default_path;
    }

    public String getFtp_port() {
        return ftp_port;
    }

    public String getFtp_protocol() {
        return ftp_protocol;
    }

    public String getFtp_server() {
        return ftp_server;
    }

    public String getFtp_user() {
        return ftp_user;
    }

    public String getFtp_password() {
        return ftp_password;
    }
    
    // Public methods
    public Optional<ButtonType> show() {
        return dialog.showAndWait();
    }
    
    // Private methods
    private void initGui() {
        // Grid layout
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));
        grid.add(lblCharCode, 0, 0);
        grid.add(txtCharCode, 1, 0);
        grid.add(lblDefPath, 0, 1);
        grid.add(txtDefPath, 1, 1);
        grid.add(lblFtpServer, 0, 2);
        grid.add(txtFtpServer, 1, 2);
        grid.add(lblFtpPort, 0, 3);
        grid.add(txtFtpPort, 1, 3);
        grid.add(lblFtpProtocol, 0, 4);
        grid.add(txtFtpProtocol, 1, 4);
        grid.add(lblFtpUser, 0, 5);
        grid.add(txtFtpUser, 1, 5);
        grid.add(lblFtpPassword, 0, 6);
        grid.add(txtFtpPassword, 1, 6);

        // Dialog
        dialog.setTitle("Vacation Editor");
        dialog.setHeaderText("Standardeinstellungen");
        dialog.getDialogPane().getButtonTypes().setAll(btnSave, btnCancel);
        dialog.getDialogPane().lookupButton(btnSave).setDisable(true);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color:#F0FFF0");
        
        // Dialog icon
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:res/dlg_icon.png"));

        // User interface
        txtCharCode.setText(char_code);
        txtCharCode.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
                dialog.getDialogPane().lookupButton(btnSave).setDisable(false);
            }
        });
                
        txtDefPath.setPrefWidth(425);
        txtDefPath.setText(default_path);
        txtDefPath.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
                dialog.getDialogPane().lookupButton(btnSave).setDisable(false);
            }
        });
        
        txtFtpPort.setText(ftp_port);
        txtFtpPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
                dialog.getDialogPane().lookupButton(btnSave).setDisable(false);
            }
        });

        txtFtpProtocol.setText(ftp_protocol);
        txtFtpProtocol.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
                dialog.getDialogPane().lookupButton(btnSave).setDisable(false);
            }
        });

        txtFtpServer.setText(ftp_server);
        txtFtpServer.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
                dialog.getDialogPane().lookupButton(btnSave).setDisable(false);
            }
        });
        
        txtFtpUser.setText(ftp_user);
        txtFtpUser.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
                dialog.getDialogPane().lookupButton(btnSave).setDisable(false);
            }
        });
        
        txtFtpPassword.setText(ftp_password);
        txtFtpPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
                dialog.getDialogPane().lookupButton(btnSave).setDisable(false);
            }
        });
    }    
}
