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
 * JEApplicationL. If not, see <http://www.gnu.org/licenses/>.
 *
 * JEApplication is part of the OpenJEVis project, further project information
 * are published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.application.jevistree;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class JEVisTree extends TreeTableView {

    private ViewFilter _filter = ViewFilterFactory.createDefaultGraphFilter();
    private ObservableList<TreePlugin> plugins = FXCollections.observableArrayList();
//    private ObservableList<ViewFilter> filter = FXCollections.observableArrayList();

    public JEVisTree(JEVisDataSource ds) {
        super();

        try {
            JEVisTreeItem root = new JEVisTreeItem(this, ds);
            root.setExpanded(true);

            setRoot(root);
            setShowRoot(false);

            setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
            setTableMenuButtonVisible(true);

            plugins.addListener(new ListChangeListener<TreePlugin>() {

                @Override
                public void onChanged(ListChangeListener.Change<? extends TreePlugin> c) {
                    System.out.println("Plugin List changed");
                    while (c.next()) {
                        if (c.wasAdded()) {
                            for (TreePlugin plugin : c.getAddedSubList()) {
                                plugin.setTree(JEVisTree.this);
                                for (TreeTableColumn<JEVisTreeRow, Long> column : plugin.getColumns()) {
                                    JEVisTree.this.getColumns().add(column);
                                }
                            }

                        }
                    }

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(JEVisTree.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ObservableList<TreePlugin> getPlugins() {
        return plugins;
    }

//    public ObservableList<ViewFilter> getFilter(){
//        return filter;
//    }
    public void setUserSelectionEnded() {
        for (TreePlugin plugin : plugins) {
            plugin.selectionFinished();
        }
    }

    public void setFiler(ViewFilter filter) {
        _filter = filter;
    }

    public ViewFilter getFilter() {
        return _filter;
    }

}
