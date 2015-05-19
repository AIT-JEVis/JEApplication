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

import javafx.scene.control.TreeTableColumn;
import org.jevis.api.JEVisDataSource;
import org.jevis.application.jevistree.plugin.BarchartPlugin;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class JEVisTreeFactory {

    public static JEVisTree buildDefaultGraphTree(JEVisDataSource ds) {
        JEVisTree tree = new JEVisTree(ds);

        ViewFilter filter = ViewFilterFactory.createDefaultGraphFilter();
        tree.setFiler(filter);

        TreePlugin bp = new BarchartPlugin();
//        bp.setTree(tree);

//        getColumns().addAll(ColumnFactory.buildName(), ColumnFactory.buildID(), ColumnFactory.buildClass(), ColumnFactory.buildColor(this), ColumnFactory.buildBasicRowSelection(this));
//        tree.getColumns().addAll(ColumnFactory.buildName(), ColumnFactory.buildID(), ColumnFactory.buildClass(), ColumnFactory.buildBasicGraph(tree));
        tree.getColumns().addAll(ColumnFactory.buildName(), ColumnFactory.buildID());

        tree.getPlugins().add(bp);

//        for (TreeTableColumn<SelectionTreeRow, Long> column : bp.getColumns()) {
//            tree.getColumns().add(column);
//        }
        return tree;

    }

}
