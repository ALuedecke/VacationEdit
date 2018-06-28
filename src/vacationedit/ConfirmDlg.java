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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author LuedeckeA
 */
public class ConfirmDlg {
    // Dialog members
    private final Alert      alert  = new Alert(AlertType.CONFIRMATION);
    private final ButtonType btnYes = new ButtonType("Ja");
    private final ButtonType btnNo  = new ButtonType("Nein");

    // Default background color
    private String back_color = "#FFF0F0";
    
    // Constructor
    public ConfirmDlg(String title, String text) {
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.setContentText(null);
        alert.getButtonTypes().setAll(btnYes, btnNo);
        alert.getDialogPane().setStyle("-fx-background-color:" + back_color);
        
        // Dialog icon
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:res/dlg_icon.png"));
    }

    // Getters / Setters
    public ButtonType getBtnYes() {    
        return btnYes;
    }

    public ButtonType getBtnNo() {    
        return btnNo;
    }

    public void setBack_color(String back_color) {
        this.back_color = back_color;
        alert.getDialogPane().setStyle("-fx-background-color:" + this.back_color);
    }

    // Public methods
    public Optional<ButtonType> show() {
        return alert.showAndWait();
    }
}
