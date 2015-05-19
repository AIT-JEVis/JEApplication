/**
 * Copyright (C) 2015 Envidatec GmbH <info@envidatec.com>
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
package org.jevis.application.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.jevis.application.jevistree.JEVisTree;
import org.jevis.application.jevistree.JEVisTreeFactory;
import org.jevis.application.jevistree.TreePlugin;
import org.jevis.application.jevistree.plugin.BarchartPlugin;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class GraphSelectionDialog {

    public static enum Response {

        OK, CANCEL
    };
    private Response _response = Response.CANCEL;
    private Stage stage;
    private final JEVisDataSource _ds;
    private String ICON = "1404313956_evolution-tasks.png";
    Map<String, BarchartPlugin.DataModel> data = new HashMap<>();

    public GraphSelectionDialog(JEVisDataSource ds) {
        _ds = ds;
    }

    public Response show(Stage owner) {

        stage = new Stage();

        stage.setTitle("Selection");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

        VBox root = new VBox();

        DialogHeader header = new DialogHeader();
        Node headerNode = header.getDialogHeader(ICON, "Selection Dialog");

        Separator sep = new Separator(Orientation.HORIZONTAL);

        AnchorPane treePane = new AnchorPane();
        JEVisTree tree = JEVisTreeFactory.buildDefaultGraphTree(_ds);

        treePane.getChildren().setAll(tree);
        AnchorPane.setTopAnchor(tree, 0d);
        AnchorPane.setRightAnchor(tree, 0d);
        AnchorPane.setBottomAnchor(tree, 0d);
        AnchorPane.setLeftAnchor(tree, 0d);

        HBox buttonBox = new HBox(10);
        Region spacer = new Region();
        Button ok = new Button("Load");
        ok.setDefaultButton(true);

        HBox.setHgrow(ok, Priority.NEVER);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setMargin(ok, new Insets(10));
        buttonBox.getChildren().setAll(spacer, ok);
        root.getChildren().setAll(headerNode, treePane, sep, buttonBox);

        VBox.setVgrow(treePane, Priority.ALWAYS);
        VBox.setVgrow(sep, Priority.NEVER);
        VBox.setVgrow(buttonBox, Priority.NEVER);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(1124);
        stage.setHeight(768);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
//        stage.getIcons().setAll(ResourceLoader.getImage(ICON, 64, 64).getImage());

        ok.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                tree.setUserSelectionEnded();
                _response = Response.OK;

                System.out.println("Results");
                for (TreePlugin plugin : tree.getPlugins()) {
                    if (plugin instanceof BarchartPlugin) {
                        System.out.println("Found Barchart plugin");
                        BarchartPlugin bp = (BarchartPlugin) plugin;

                        data = bp.getSelectedData();

//                        for (Map.Entry<String, BarchartPlugin.DataModel> entrySet : bp.getSelectedData().entrySet()) {
//                            Object key = entrySet.getKey();
//                            BarchartPlugin.DataModel value = entrySet.getValue();
//
//                            if (value.getSelected()) {
//                                System.out.println("sample for " + key);
//                                System.out.println("Color: " + value.getColor());
//                                for (JEVisSample sample : value.getSamples()) {
//                                    try {
//                                        System.out.println(sample.getTimestamp() + "  " + sample.getValue());
//                                    } catch (JEVisException ex) {
//                                        Logger.getLogger(GraphSelectionDialog.class.getName()).log(Level.SEVERE, null, ex);
//                                    }
//                                }
//                            }
//                        }
                    }
                }
                stage.hide();
            }
        });

        stage.showAndWait();

        return _response;
    }

    public Map<String, BarchartPlugin.DataModel> getSelectedData() {
        return data;
    }

}
