/**
 * Copyright (C) 2014 Envidatec GmbH <info@envidatec.com>
 *
 * This file is part of JEApplication.
 *
 * JEApplication is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation in version 3.
 *
 * JEApplication is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JEApplication. If not, see <http://www.gnu.org/licenses/>.
 *
 * JEApplication is part of the OpenJEVis project, further project information
 * are published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.application.statusbar;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.application.resource.ResourceLoader;

public class Statusbar extends ToolBar {

    private final int ICON_SIZE = 20;
    private JEVisDataSource _ds;
    Label userName = new Label("Max Musterman");

    public Statusbar(JEVisDataSource ds) {
        super();
        _ds = ds;

        HBox root = new HBox();

        root.setSpacing(10);
        root.setAlignment(Pos.CENTER_LEFT);

        ImageView userIcon = ResourceLoader.getImage("user.png", ICON_SIZE, ICON_SIZE);

        Label onlineInfo = new Label("Online");

//        Label userLabel = new Label("User:");
        ImageView connectIcon = ResourceLoader.getImage("network-connected.png", ICON_SIZE, ICON_SIZE);
        ImageView notification = ResourceLoader.getImage("note_3.png", ICON_SIZE, ICON_SIZE);

        Label serverL = new Label("Server");
        Pane spacer = new Pane();
        spacer.setMaxWidth(Integer.MAX_VALUE);

        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(onlineInfo, Priority.NEVER);
        HBox.setHgrow(userName, Priority.NEVER);

        root.getChildren().addAll(userIcon, userName, spacer, notification, connectIcon, onlineInfo);

        HBox.setHgrow(root, Priority.ALWAYS);
        getItems().add(root);

        setBar();
    }

    private void setBar() {
        try {
            String name = _ds.getCurrentUser().getAttribute("First Name").getLatestSample().getValueAsString();
            String lastName = _ds.getCurrentUser().getAttribute("Last Name").getLatestSample().getValueAsString();
            userName.setText(name + " " + lastName + " (" + _ds.getCurrentUser().getName() + ")");
        } catch (JEVisException ex) {
            Logger.getLogger(Statusbar.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Thread() {

            @Override
            public void run() {
                //TODO checlk is alive

            }

        }.start();
    }

// TODO implement status bar for JEVis applications
}
