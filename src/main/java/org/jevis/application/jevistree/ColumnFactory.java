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
package org.jevis.application.jevistree;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.application.resource.ImageConverter;
import org.jevis.application.resource.ResourceLoader;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ColumnFactory {

    public static final String OBJECT_NAME = "Object Name";
    public static final String OBJECT_ID = "ID";
    public static final String COLOR = "Color";
    public static final String OBJECT_CLASS = "Type";
    public static final String SELECT_OBJECT = "Select";
    public static final String ATTRIBUTE_LAST_MOD = "Last Modified";

    public static TreeTableColumn<JEVisTreeRow, String> buildName() {
        TreeTableColumn<JEVisTreeRow, String> column = new TreeTableColumn(OBJECT_NAME);
        column.setPrefWidth(300);
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<JEVisTreeRow, String> p) -> {
            try {
                if (p != null && p.getValue() != null && p.getValue().getValue() != null && p.getValue().getValue().getJEVisObject() != null) {
                    TreeItem<JEVisTreeRow> item = p.getValue();
                    JEVisTreeRow selectionObject = item.getValue();

                    if (selectionObject.getType() == JEVisTreeRow.TYPE.OBJECT) {
                        JEVisObject obj = selectionObject.getJEVisObject();
                        return new ReadOnlyObjectWrapper<String>(obj.getName());
                    } else if (selectionObject.getType() == JEVisTreeRow.TYPE.ATTRIBUTE) {
                        JEVisAttribute att = selectionObject.getJEVisAttribute();
                        return new ReadOnlyObjectWrapper<String>(att.getName());
                    } else {
                        return new ReadOnlyObjectWrapper<String>("");
                    }
                } else {
                    return new ReadOnlyObjectWrapper<String>("Null");
                }

            } catch (Exception ex) {
                System.out.println("Error in Column Fatory: " + ex);
                return new ReadOnlyObjectWrapper<String>("Error");
            }

        });

        column.setCellFactory(new Callback<TreeTableColumn<JEVisTreeRow, String>, TreeTableCell<JEVisTreeRow, String>>() {

            @Override
            public TreeTableCell<JEVisTreeRow, String> call(TreeTableColumn<JEVisTreeRow, String> param) {

                TreeTableCell<JEVisTreeRow, String> cell = new TreeTableCell<JEVisTreeRow, String>() {

                    @Override
                    public void commitEdit(String newValue) {
                        super.commitEdit(newValue);
//                        getTreeTableRow().getItem().getColorProperty().setValue(newValue);
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            HBox hbox = new HBox(10);
                            Label nameLabel = new Label();
                            Node icon = new Region();

                            nameLabel.setText(item);
                            try {
                                if (getTreeTableRow().getItem() != null) {
                                    if (getTreeTableRow().getItem().getType() == JEVisTreeRow.TYPE.OBJECT) {
                                        icon = ImageConverter.convertToImageView(getTreeTableRow().getItem().getJEVisObject().getJEVisClass().getIcon(), 20, 20);
                                    } else {//Attribute
                                        HBox hbox2 = new HBox(10);
                                        Region spacer = new Region();
                                        spacer.setPrefWidth(12);
                                        hbox2.getChildren().setAll(spacer, ResourceLoader.getImage("down_right-24.png", 10, 10));
                                        icon = hbox2;
                                    }
                                }
                            } catch (JEVisException ex) {
                                Logger.getLogger(ColumnFactory.class.getName()).log(Level.SEVERE, null, ex);
                                ex.printStackTrace();
                            }

                            hbox.getChildren().setAll(icon, nameLabel);
                            setText(null);
                            setGraphic(hbox);
                        } else {
                            setText(null);
                            setGraphic(null);
                        }

                    }

                };

                return cell;
            }
        });

        return column;

    }

    public static TreeTableColumn<JEVisTreeRow, String> buildClass() {
        TreeTableColumn<JEVisTreeRow, String> column = new TreeTableColumn(OBJECT_CLASS);
        column.setPrefWidth(190);
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<JEVisTreeRow, String> p) -> {
            try {
                if (p != null && p.getValue() != null && p.getValue().getValue() != null && p.getValue().getValue().getJEVisObject() != null) {
                    TreeItem<JEVisTreeRow> item = p.getValue();
                    JEVisTreeRow selectionObject = item.getValue();
//                    JEVisObject obj = selectionObject.getJEVisObject();
//                    return new ReadOnlyObjectWrapper<String>(obj.getJEVisClass().getName());

                    if (selectionObject.getType() == JEVisTreeRow.TYPE.OBJECT) {
                        JEVisObject obj = selectionObject.getJEVisObject();
                        return new ReadOnlyObjectWrapper<String>(obj.getJEVisClass().getName());
                    } else if (selectionObject.getType() == JEVisTreeRow.TYPE.ATTRIBUTE) {
                        JEVisAttribute att = selectionObject.getJEVisAttribute();
                        return new ReadOnlyObjectWrapper<String>(att.getType().getName());
                    } else {
                        return new ReadOnlyObjectWrapper<String>("");
                    }

                } else {
                    return new ReadOnlyObjectWrapper<String>("Null");
                }

            } catch (Exception ex) {
                System.out.println("Error in Column Fatory: " + ex);
                return new ReadOnlyObjectWrapper<String>("Error");
            }

        });
        return column;

    }

    public static TreeTableColumn<JEVisTreeRow, Long> buildBasicGraph(JEVisTree tree) {
        TreeTableColumn<JEVisTreeRow, Long> column = new TreeTableColumn("JEGraph");

        column.getColumns().addAll(buildColor(tree), buildBasicRowSelection(tree));

        return column;

    }

    public static TreeTableColumn<JEVisTreeRow, Long> buildID() {
        TreeTableColumn<JEVisTreeRow, Long> column = new TreeTableColumn(OBJECT_ID);
        column.setPrefWidth(70);
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<JEVisTreeRow, Long> p) -> {
            try {
                if (p != null && p.getValue() != null && p.getValue().getValue() != null && p.getValue().getValue().getJEVisObject() != null) {
                    TreeItem<JEVisTreeRow> item = p.getValue();
                    JEVisTreeRow selectionObject = item.getValue();
                    JEVisObject obj = selectionObject.getJEVisObject();
                    return new ReadOnlyObjectWrapper<Long>(obj.getID());

//                    if (p.getValue().getValue().getType() == SelectionTreeRow.TYPE.OBJECT) {
//                        JEVisObject obj = selectionObject.getJEVisObject();
//                        return new ReadOnlyObjectWrapper<Long>(obj.getID());
//                    } else {
//                        return new ReadOnlyObjectWrapper<Long>(-3l);
//                    }
                } else {
                    return new ReadOnlyObjectWrapper<Long>(-2l);
                }

            } catch (Exception ex) {
                System.out.println("Error in Column Fatory: " + ex);
                return new ReadOnlyObjectWrapper<Long>(-1l);
            }

        });

        column.setCellFactory(new Callback<TreeTableColumn<JEVisTreeRow, Long>, TreeTableCell<JEVisTreeRow, Long>>() {

            @Override
            public TreeTableCell<JEVisTreeRow, Long> call(TreeTableColumn<JEVisTreeRow, Long> param) {

                TreeTableCell<JEVisTreeRow, Long> cell = new TreeTableCell<JEVisTreeRow, Long>() {

                    StackPane hbox = new StackPane();
                    Label label = new Label();

                    @Override
                    public void commitEdit(Long newValue) {
                        super.commitEdit(newValue);
//                        getTreeTableRow().getItem().getColorProperty().setValue(newValue);
                    }

                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        if (!empty) {
                            hbox.getChildren().setAll(label);
                            StackPane.setAlignment(hbox, Pos.CENTER_RIGHT);
                            if (getTreeTableRow().getItem() != null) {
                                if (getTreeTableRow().getItem().getType() == JEVisTreeRow.TYPE.OBJECT) {
                                    label.setText(item + "");
                                } else {
                                    label.setText("");
                                }
                            }

                            setText(null);
                            setGraphic(hbox);
                        } else {
                            setText(null);
                            setGraphic(null);
                        }

                    }

                };

                return cell;
            }
        });

        return column;

    }

    public static TreeTableColumn<JEVisTreeRow, Color> buildColor(JEVisTree tree) {
        TreeTableColumn<JEVisTreeRow, Color> column = new TreeTableColumn(COLOR);
        column.setPrefWidth(130);
        column.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<JEVisTreeRow, Color>, ObservableValue<Color>>() {

            @Override
            public ObservableValue<Color> call(TreeTableColumn.CellDataFeatures<JEVisTreeRow, Color> param) {
                return param.getValue().getValue().getColorProperty();
            }
        });

        column.setCellFactory(new Callback<TreeTableColumn<JEVisTreeRow, Color>, TreeTableCell<JEVisTreeRow, Color>>() {

            @Override
            public TreeTableCell<JEVisTreeRow, Color> call(TreeTableColumn<JEVisTreeRow, Color> param) {

                TreeTableCell<JEVisTreeRow, Color> cell = new TreeTableCell<JEVisTreeRow, Color>() {

                    @Override
                    public void commitEdit(Color newValue) {
                        super.commitEdit(newValue);
                        getTreeTableRow().getItem().getColorProperty().setValue(newValue);
                    }

                    @Override
                    protected void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        if (!empty) {
                            StackPane hbox = new StackPane();

                            if (getTreeTableRow().getItem() != null && tree.getFilter().showColumn(getTreeTableRow().getItem(), COLOR)) {
                                ColorPicker colorPicker = new ColorPicker();
                                hbox.getChildren().setAll(colorPicker);
                                StackPane.setAlignment(hbox, Pos.CENTER_LEFT);
                                colorPicker.setValue(item);

                                colorPicker.setOnAction(new EventHandler<ActionEvent>() {

                                    @Override
                                    public void handle(ActionEvent event) {
                                        commitEdit(colorPicker.getValue());
                                    }
                                });
                            }

                            setText(null);
                            setGraphic(hbox);
                        } else {
                            setText(null);
                            setGraphic(null);
                        }

                    }

                };

                return cell;
            }
        });

        return column;

    }

    public static TreeTableColumn<JEVisTreeRow, Boolean> buildBasicRowSelection(JEVisTree tree) {
        TreeTableColumn<JEVisTreeRow, Boolean> column = new TreeTableColumn(SELECT_OBJECT);
        column.setPrefWidth(60);
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<JEVisTreeRow, Boolean> param) -> param.getValue().getValue().getObjectSelecedProperty());

        column.setEditable(true);

        column.setCellFactory(new Callback<TreeTableColumn<JEVisTreeRow, Boolean>, TreeTableCell<JEVisTreeRow, Boolean>>() {

            @Override
            public TreeTableCell<JEVisTreeRow, Boolean> call(TreeTableColumn<JEVisTreeRow, Boolean> param) {

                TreeTableCell<JEVisTreeRow, Boolean> cell = new TreeTableCell<JEVisTreeRow, Boolean>() {

                    @Override
                    public void commitEdit(Boolean newValue) {
                        super.commitEdit(newValue);
                        getTreeTableRow().getItem().getObjectSelecedProperty().setValue(newValue);
                    }

                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        if (!empty) {
                            StackPane hbox = new StackPane();
                            CheckBox cbox = new CheckBox();

                            if (getTreeTableRow().getItem() != null && tree.getFilter().showColumn(getTreeTableRow().getItem(), SELECT_OBJECT)) {
                                hbox.getChildren().setAll(cbox);
                                StackPane.setAlignment(hbox, Pos.CENTER_LEFT);
                                cbox.setSelected(item);

                                cbox.setOnAction(new EventHandler<ActionEvent>() {

                                    @Override
                                    public void handle(ActionEvent event) {
                                        commitEdit(cbox.isSelected());
                                    }
                                });
                            }

                            setText(null);
                            setGraphic(hbox);
                        } else {
                            setText(null);
                            setGraphic(null);
                        }

                    }

                };

                return cell;
            }
        });

        return column;

    }

}
