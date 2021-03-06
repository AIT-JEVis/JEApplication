/**
 * Copyright (C) 2014-2015 Envidatec GmbH <info@envidatec.com>
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisOption;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.application.resource.ResourceLoader;
import org.jevis.commons.config.CommonOptions;

/**
 * Status bar with user and connection infos.
 *
 * @author Florian Simon
 */
public class Statusbar extends ToolBar {

    private final int ICON_SIZE = 20;
    private JEVisDataSource _ds;
    private String lastUsername = "";
    private String lastPassword = "";

    private final int WAIT_TIME = 5000;//MSEC
    private final int RETRY_COUNT = 720;//count

    Label userName = new Label("");
    Label onlineInfo = new Label("Online");
    HBox conBox = new HBox();
    ImageView connectIcon = ResourceLoader.getImage("network-connected.png", ICON_SIZE, ICON_SIZE);
    ImageView notConnectIcon = ResourceLoader.getImage("network-disconnected.png", ICON_SIZE, ICON_SIZE);
    private Tooltip tt = new Tooltip("Warning:\nConnection to server lost. Trying to reconnect...  ");
    private int retryCount = 0;
    public BooleanProperty connectedProperty = new SimpleBooleanProperty(true);

    public Statusbar(JEVisDataSource ds) {
        super();
        _ds = ds;

        HBox root = new HBox();

        root.setSpacing(10);
        root.setAlignment(Pos.CENTER_LEFT);

        ImageView userIcon = ResourceLoader.getImage("user.png", ICON_SIZE, ICON_SIZE);

//        Label userLabel = new Label("User:");
        ImageView notification = ResourceLoader.getImage("note_3.png", ICON_SIZE, ICON_SIZE);

        conBox.getChildren().setAll(connectIcon);

        Pane spacer = new Pane();
        spacer.setMaxWidth(Integer.MAX_VALUE);

        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(onlineInfo, Priority.NEVER);
        HBox.setHgrow(userName, Priority.NEVER);

        //TODO implement notification
        root.getChildren().addAll(userIcon, userName, spacer, conBox, onlineInfo);

        String sinfo = "";

        for (JEVisOption opt : ds.getConfiguration()) {
            if (opt.getKey().equals(CommonOptions.DataSource.DataSource.getKey())) {
                for (JEVisOption dsOption : opt.getOptions()) {
                    sinfo += dsOption.getKey() + ": " + dsOption.getValue() + "\n";
                }
            }
        }

        if (ds instanceof JEVisDataSourceSQL) {
            JEVisDataSourceSQL dsSQL = (JEVisDataSourceSQL) ds;
            sinfo += "Request Count: " + dsSQL.getCount();

        }
        NumberFormat numberFormate = DecimalFormat.getNumberInstance(java.util.Locale.getDefault());
        double memNumber = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);

        sinfo += "\nMemory usage: " + numberFormate.format(memNumber) + " mb";

        Tooltip serverTip = new Tooltip("Connection Info:\n"
                + sinfo);
        onlineInfo.setTooltip(serverTip);

        HBox.setHgrow(root, Priority.ALWAYS);
        getItems().add(root);

        setBar();
    }

    private void setBar() {
        String name = "";
        String lastName = "";
        String userAccount = "";
        try {
            if (_ds.getCurrentUser().getAttribute("First Name").hasSample()) {
                name = _ds.getCurrentUser().getAttribute("First Name").getLatestSample().getValueAsString();
            }

            if (_ds.getCurrentUser().getAttribute("Last Name").hasSample()) {
                lastName = _ds.getCurrentUser().getAttribute("Last Name").getLatestSample().getValueAsString();
            }

            userAccount = _ds.getCurrentUser().getName();
//            userName.setText(name + " " + lastName + " (" + _ds.getCurrentUser().getName() + ")");
        } catch (Exception ex) {
//            Logger.getLogger(Statusbar.class.getName()).log(Level.SEVERE, "Cound not fetch Username", ex);
        }

        if (name.isEmpty() && lastName.isEmpty()) {
            userName.setText(userAccount);
        } else {
            userName.setText(name + " " + lastName);
        }

        try {
            lastUsername = _ds.getCurrentUser().getName();
        } catch (JEVisException ex) {
            Logger.getLogger(Statusbar.class.getName()).log(Level.SEVERE, null, ex);
        }

        Thread checkOnline = new Thread() {

            @Override
            public void run() {
                try {
                    while (true) {
                        sleep(WAIT_TIME);
                        if (_ds.isConnectionAlive()) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
//                                    System.out.println("still online");
                                    onlineInfo.setText("Online");
                                    onlineInfo.setTextFill(Color.BLACK);
                                    conBox.getChildren().setAll(connectIcon);

                                    if (tt.isShowing()) {
                                        tt.hide();
                                    }
                                    connectedProperty.setValue(Boolean.TRUE);
                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
//                                    System.out.println("whaa were are offline");
                                    onlineInfo.setText("Offline");
                                    onlineInfo.setTextFill(Color.web("#D62748"));//red
                                    conBox.getChildren().setAll(notConnectIcon);

//                                    onlineInfo.setTooltip(tt);
                                    final Point2D nodeCoord = onlineInfo.localToScene(0.0, 0.0);
                                    if (!tt.isShowing()) {
                                        tt.show(onlineInfo, nodeCoord.getX(), nodeCoord.getY());
                                    }
                                    connectedProperty.setValue(Boolean.FALSE);
                                    reconnect();
                                }
                            });

                        }
                    }

                    //TODO checlk is alive
                } catch (InterruptedException ex) {
                    Logger.getLogger(Statusbar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JEVisException ex) {
                    Logger.getLogger(Statusbar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        };
        checkOnline.setDaemon(true);
        checkOnline.start();
    }

    private void reconnect() {
        Thread reConn = new Thread() {

            @Override
            public void run() {
                try {
                    if (retryCount < RETRY_COUNT) {
                        System.out.println("try Reconnect");
                        _ds.reconnect();
                        ++retryCount;
                    } else {
                        System.out.println("No Connection Possible .. giving up");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        reConn.setDaemon(true);
        reConn.start();
    }

// TODO implement status bar for JEVis applications
}
