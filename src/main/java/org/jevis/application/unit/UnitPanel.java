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
package org.jevis.application.unit;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisUnit;
import org.jevis.commons.unit.UnitManager;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class UnitPanel extends GridPane {

    private JEVisUnit _returnUnit;

    public UnitPanel(JEVisDataSource ds, JEVisUnit unit, boolean showRemember) {
        super();
        _returnUnit = unit;
        buildUnit(ds, unit, showRemember);
    }

    private void buildUnit(final JEVisDataSource ds, JEVisUnit unit, boolean showRemember) {
        Label prefixL = new Label("Prefix:");
        Label unitL = new Label("Unit:");
        Label example = new Label("Symbol: ");

        final JEVisUnit.Prefix _prefix = unit.getPrefix();

        ComboBox<String> prefixBox = new ComboBox(FXCollections.observableArrayList(UnitManager.getInstance().getPrefixes()));
        prefixBox.setMaxWidth(520);
//        prefixBox.getSelectionModel().select("");//toto get elsewhere?!

        prefixBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String t, boolean bln) {
                super.updateItem(t, bln); //To change body of generated methods, choose Tools | Templates.
                if (!bln) {
                    setAlignment(Pos.CENTER);
                    setText(t);//TODo: replace this dirty workaround to center the text in line with he buttonbelow
                }
            }
        });
        prefixBox.getSelectionModel().select(UnitManager.getInstance().getPrefixName(_prefix, Locale.getDefault()));

        final TextField exampleFiled = new TextField();
        exampleFiled.setEditable(false);
        final Button changeBaseUnit = new Button();//new Button("Basic Unit");
        changeBaseUnit.setText(unit.toString());
//
        HBox unitBox = new HBox(5);
        unitBox.getChildren().setAll(changeBaseUnit);

        unitBox.setMaxWidth(520);
        exampleFiled.setPrefWidth(100);
        changeBaseUnit.setPrefWidth(100);
        prefixBox.setPrefWidth(100);

//        changeBaseUnit.setMinWidth(100);
//        prefixBox.setMinWidth(100);
        setHgap(5);
        setVgap(5);
        setPadding(new Insets(10, 10, 10, 10));

        add(prefixL, 0, 0);
        add(unitL, 0, 1);
        add(example, 0, 2);

        add(prefixBox, 1, 0);
        add(unitBox, 1, 1);
        add(exampleFiled, 1, 2);
//        if (showRemember) {
//            add(setDefault, 2, 2, 1, 1);
//        }

        prefixBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {

                _returnUnit.setPrefix(UnitManager.getInstance().getPrefix(t1, Locale.getDefault()));
                printExample(exampleFiled, _returnUnit);
            }
        });

        changeBaseUnit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                SimpleTreeUnitChooser stc = new SimpleTreeUnitChooser();
                try {
                    if (stc.show(new Point2D(100, 100), ds) == SimpleTreeUnitChooser.Response.YES) {
                        System.out.println("user seletet unit: " + stc.getUnit());
                        JEVisUnit.Prefix oldPrefix = _returnUnit.getPrefix();
                        _returnUnit = stc.getUnit();
                        _returnUnit.setPrefix(oldPrefix);
                        changeBaseUnit.setText(_returnUnit.toString());
                        printExample(exampleFiled, _returnUnit);
                    }
                } catch (JEVisException ex) {
                    Logger.getLogger(UnitPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        printExample(exampleFiled, _returnUnit);
    }

    private void printExample(final TextField tf, final JEVisUnit unit) {
        Platform
                .runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            System.out.println("printExample: " + unit.toJSON());
                            tf.setText(UnitManager.getInstance().formate(unit));
                        } catch (JEVisException ex) {
                            Logger.getLogger(UnitPanel.class
                                    .getName()).log(Level.SEVERE, null, ex);
                            System.out.println(
                                    "Error while formating: " + ex);
                        }
                    }
                });

    }

    public JEVisUnit getSelectedUnit() {
        return _returnUnit;
    }

}
