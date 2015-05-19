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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.application.object.tree.JEVisRootObject;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class JEVisTreeItem extends TreeItem<JEVisTreeRow> {

//    final JEVisObject _obj;
    private boolean _childLoaded = false;
    private final JEVisTree _tree;

    public JEVisTreeItem(JEVisTree tree, JEVisDataSource ds) throws JEVisException {
        JEVisObject _obj = new JEVisRootObject(ds);
        JEVisTreeRow sobj = new JEVisTreeRow(_obj);
        setValue(sobj);
        _childLoaded = true;
        _tree = tree;
        for (JEVisObject child : _obj.getChildren()) {
            super.getChildren().add(new JEVisTreeItem(_tree, child));
        }
    }

    public JEVisTreeItem(JEVisTree tree, JEVisObject obj) {
        JEVisTreeRow sobj = new JEVisTreeRow(obj);
        setValue(sobj);
        _tree = tree;
    }

    public JEVisTreeItem(JEVisTree tree, JEVisAttribute att) {
        JEVisTreeRow sobj = new JEVisTreeRow(att);
        setValue(sobj);
        _tree = tree;
    }

    @Override
    public ObservableList<TreeItem<JEVisTreeRow>> getChildren() {

        if (!_childLoaded) {
            _childLoaded = true;
            try {

                ViewFilter filter = _tree.getFilter();

                if (getValue().getType() == JEVisTreeRow.TYPE.OBJECT) {
                    for (JEVisObject child : getValue().getJEVisObject().getChildren()) {
                        if (filter.showJEvisClass(child.getJEVisClass())) {
                            super.getChildren().add(new JEVisTreeItem(_tree, child));
                        }
                    }

                    for (JEVisAttribute att : getValue().getJEVisObject().getAttributes()) {
                        if (filter.showAttribute(att)) {
                            super.getChildren().add(new JEVisTreeItem(_tree, att));
                        }
                    }
                }

            } catch (JEVisException ex) {
                Logger.getLogger(JEVisTreeItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return super.getChildren();
//        return super.getChildren(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

//    public JEVisObject getObject() {
//        return _obj;
//    }
}
