/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.application.unit;

import java.util.Iterator;
import java.util.Locale;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javax.measure.unit.Unit;
import org.jevis.commons.unit.UnitManager;

/**
 *
 * @author fs
 */
public class UnitChooserPanel {

    private Unit _unit = Unit.ONE;
    private TextField altSymbolField = new TextField("");
    private String _altSymbol = "";
    private ComboBox<?> boxMeaning = new ComboBox<Object>();
    private ComboBox<String> boxPrefix = new ComboBox<String>();
    private ComboBox<Unit> boxQuantity = new ComboBox<Unit>();
    private ComboBox<Unit> boxUnit = new ComboBox<Unit>();
    private Label example = new Label("1234.45");
    private TextField searchField = new TextField();
    private GridPane root = new GridPane();
    private Label lQuantity = new Label("Quantity:");
    private Label lUnit = new Label("Unit:");
    private Label lAltUnit = new Label("Alternativ Symbol:");
    private Label lMeaning = new Label("Meaning:");
    private Label lExample = new Label("Example:");

    private final UnitManager um = UnitManager.getInstance();

    public UnitChooserPanel(Unit unit, String altSymbol) {
        _unit = unit;
        _altSymbol = altSymbol;

        fillQuantitys();
        fillPrifix();
        fillUnits(unit);
        initGUI();
        setUnit();
        addListeners();
        printExample();

    }

    public void setPadding(Insets inset) {
        root.setPadding(inset);
    }

    public Node getView() {

        return root;
    }

    private void initGUI() {
        root.setHgap(10);
        root.setVgap(5);

        //javaFX 2.0 Workaround
        boxUnit.setMaxWidth(Double.MAX_VALUE);
        boxPrefix.setMaxWidth(Double.MAX_VALUE);
        boxQuantity.setMaxWidth(Double.MAX_VALUE);
        boxMeaning.setMaxWidth(Double.MAX_VALUE);

        HBox unitBox = new HBox(5);
        unitBox.getChildren().setAll(boxPrefix, boxUnit);
        HBox.setHgrow(boxPrefix, Priority.NEVER);
        HBox.setHgrow(boxUnit, Priority.ALWAYS);

        GridPane.setHgrow(lQuantity, Priority.NEVER);
        GridPane.setHgrow(lUnit, Priority.NEVER);
        GridPane.setHgrow(lAltUnit, Priority.NEVER);
        GridPane.setHgrow(lMeaning, Priority.NEVER);
        GridPane.setHgrow(lExample, Priority.NEVER);

        GridPane.setHgrow(boxQuantity, Priority.ALWAYS);
        GridPane.setHgrow(unitBox, Priority.ALWAYS);
        GridPane.setHgrow(altSymbolField, Priority.ALWAYS);
        GridPane.setHgrow(boxMeaning, Priority.ALWAYS);
        GridPane.setHgrow(example, Priority.ALWAYS);

        int row = 0;
        root.add(lQuantity, 0, row);
        root.add(boxQuantity, 1, row);

        root.add(lUnit, 0, ++row);
        root.add(unitBox, 1, row);

        root.add(lAltUnit, 0, ++row);
        root.add(altSymbolField, 1, row);

        root.add(lMeaning, 0, ++row);
        root.add(boxMeaning, 1, row);

        root.add(lExample, 0, ++row);
        root.add(example, 1, row);

    }

    private void addListeners() {
        boxQuantity.valueProperty().addListener(new ChangeListener<Unit>() {

            @Override
            public void changed(ObservableValue<? extends Unit> observable, Unit oldValue, Unit newValue) {
                fillUnits(newValue);
                printExample();
            }
        });

        boxPrefix.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                printExample();
            }
        });

        boxUnit.valueProperty().addListener(new ChangeListener<Unit>() {

            @Override
            public void changed(ObservableValue<? extends Unit> observable, Unit oldValue, Unit newValue) {
                printExample();
            }
        });
    }

    private void setUnit() {
        try {
            System.out.println("set Unit: " + _unit);
            System.out.println("set alt Unit: " + altSymbolField);
            System.out.println("Unit.one: " + Unit.ONE);

            if (_unit != null) {
                boxQuantity.getSelectionModel().select(_unit.getStandardUnit());
                boxUnit.getSelectionModel().select(_unit.getStandardUnit());
                //TODO set Prefix, maybe we need to store this also seperate?

                if (_altSymbol != null && !_altSymbol.isEmpty()) {
                    altSymbolField.setText(_altSymbol);
                }

            } else {
                System.out.println("no unit create new default unit");
                Unit newUnit = Unit.ONE;
                boxQuantity.getSelectionModel().select(newUnit.getStandardUnit());
                boxUnit.getSelectionModel().select(newUnit.getStandardUnit());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void fillUnits(Unit unit) {
        System.out.println("fill units");
        boxUnit.setButtonCell(new UnitListCell());
        boxUnit.setCellFactory(new Callback<ListView<Unit>, ListCell<Unit>>() {
            @Override
            public ListCell<Unit> call(ListView<Unit> p) {
                return new UnitListCell();
            }
        });

        ObservableList<Unit> siList = FXCollections.observableList(um.getCompatibleSIUnit(unit));
        ObservableList<Unit> nonSIList = FXCollections.observableList(um.getCompatibleNonSIUnit(unit));
        ObservableList<Unit> addList = FXCollections.observableList(um.getCompatibleAdditionalUnit(unit));

        boxUnit.getItems().clear();
        boxUnit.getSelectionModel().clearSelection();
        boxUnit.getItems().add(unit);
        boxUnit.getItems().addAll(siList);
//        boxQuantity.getItems().addAll(new Separator());
        boxUnit.getItems().addAll(nonSIList);
//        boxQuantity.getItems().addAll(new Separator());
        boxUnit.getItems().addAll(addList);

        boxUnit.getSelectionModel().selectFirst();

    }

    private void fillPrifix() {
        System.out.println("fill prefix");
        ObservableList<String> list = FXCollections.observableList(um.getPrefixes());

        boxPrefix.getItems().clear();
        boxPrefix.getSelectionModel().clearSelection();
        boxPrefix.getItems().addAll(list);
        boxPrefix.getSelectionModel().selectFirst();//noUnit
    }

    private void fillQuantitys() {
        System.out.println("fill quantitys");
        boxQuantity.setButtonCell(new QuantitiesListCell());
        boxQuantity.setCellFactory(new Callback<ListView<Unit>, ListCell<Unit>>() {
            @Override
            public ListCell<Unit> call(ListView<Unit> p) {
                return new QuantitiesListCell();
            }
        });

        ObservableList<Unit> favList = FXCollections.observableList(um.getFavoriteQuantitys());
        ObservableList<Unit> allList = FXCollections.observableList(um.getQuantities());

//        gb.getChildren().remove(boxQuantity);
//        boxQuantity = new ComboBox<>();
//        gb.getChildren().add(boxQuantity);
        boxQuantity.getSelectionModel().clearSelection();
        boxQuantity.getItems().clear();
        boxQuantity.getSelectionModel().clearSelection();
        boxQuantity.getItems().addAll(favList);
//        boxQuantity.getItems().addAll(new Separator());
        boxQuantity.getItems().addAll(allList);

        boxQuantity.getSelectionModel().selectFirst();

    }

    public void printExample() {
        Unit finalUnit = getFinalUnit();
        if (_altSymbol != null && !_altSymbol.equals("")) {
            example.setText("1245.67 " + UnitManager.getInstance().formate(finalUnit.alternate(_altSymbol)));
        } else {
            example.setText("1245.67 " + UnitManager.getInstance().formate(finalUnit));
        }
    }

    public Unit getFinalUnit() {
        Unit baseUnit = null;
        Unit altUnit = null;
        String prefixUnit = null;

        //check Unit
        Object baseUnitObj = boxUnit.getSelectionModel().getSelectedItem();
        if (baseUnitObj instanceof Unit) {
            baseUnit = (Unit) baseUnitObj;
        }

        //check Prefix
        Object prefixObj = boxPrefix.getSelectionModel().getSelectedItem();
        if (boxPrefix.getSelectionModel().getSelectedIndex() != 0 && prefixObj instanceof String && baseUnit != null) {
            prefixUnit = (String) prefixObj;
        }

        System.out.println("altSymbolField: " + altSymbolField);
        System.out.println("altSymbolField.text: " + altSymbolField.getText());
        //check altSymbol
        if (altSymbolField.getText() != null && !altSymbolField.getText().isEmpty() && baseUnit != null) {
            try {
                altUnit = baseUnit.alternate(altSymbolField.getText());
                altSymbolField.setStyle("-fx-text-fill: black;");
            } catch (Exception ex) {
                altUnit = null;
                altSymbolField.setStyle("-fx-text-fill: #F18989;");
            }
        }

        if (baseUnit != null) {
            if (prefixUnit != null && !prefixUnit.isEmpty()) {
                return UnitManager.getInstance().getUnitWithPrefix(baseUnit, prefixUnit);
            } else {
                return baseUnit;
            }
        } else {
            return Unit.ONE;
        }

    }

    public String getAlternativSysbol() {
        return altSymbolField.getText();
    }

    public void searchUnit() {
        try {
            Unit searchUnit = Unit.valueOf(searchField.getText().trim());

            altSymbolField.setText("");
            boxPrefix.getSelectionModel().selectFirst();
            boxQuantity.getSelectionModel().selectFirst();
            boxUnit.getSelectionModel().selectFirst();

            System.out.println("standart Unit: " + searchUnit.getStandardUnit());
            Iterator<Unit> iterator = boxQuantity.getItems().iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Unit) {
                    Unit unit = (Unit) obj;
                    if (unit.getStandardUnit().equals(searchUnit)) {
                        System.out.println("found unit");
                        boxQuantity.getSelectionModel().select(unit);

                        Iterator<Unit> iterator2 = boxUnit.getItems().iterator();
                        while (iterator2.hasNext()) {
                            Object obj2 = iterator2.next();
                            if (obj2 instanceof Unit) {
                                Unit unit2 = (Unit) obj2;
                                if (unit2.equals(searchUnit)) {
                                    boxUnit.getSelectionModel().select(unit2);
                                }
                            }
                        }
                    }
                }
            }
            searchField.setStyle("-fx-text-fill: black;");

        } catch (Exception ex) {
            System.out.println("Unkown Unit");
            ex.printStackTrace();
            searchField.setStyle("-fx-text-fill: red;");

        }

    }

    class UnitListCell extends ListCell<Unit> {

        @Override
        protected void updateItem(Unit item, boolean empty) {
            super.updateItem(item, empty);

            if (item instanceof Unit) {
                String label = String.format("%s [%s]", item.toString(), UnitManager.getInstance().getUnitName((Unit) item, Locale.ENGLISH));
                setText(label);
//                setText(UnitManager.getInstance().getQuantitiesName((Unit) item, Locale.getDefault()));
            }

        }
    }

    class QuantitiesListCell extends ListCell<Unit> {

        @Override
        protected void updateItem(Unit item, boolean empty) {
            super.updateItem(item, empty);

            if (item instanceof Unit) {
                String label = String.format("%s", UnitManager.getInstance().getQuantitiesName((Unit) item, Locale.ENGLISH));
                setText(label);
//                setText(UnitManager.getInstance().getQuantitiesName((Unit) item, Locale.getDefault()));
            }

        }
    }
}
