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
package org.jevis.application;

import java.util.Map;
import javafx.application.Application;
import org.jevis.api.JEVisConfiguration;
import org.jevis.commons.config.BasicConfiguration;
import org.jevis.commons.config.OptionFactory;

/**
 * This class helps with common task relatet to the Application.Parameters.
 *
 * @author Florian Simon
 */
public class ParameterHelper {

    /**
     * Converts parameters to an JEVisConfiguration object
     *
     * @param parameters
     * @return
     */
    public static JEVisConfiguration ParseJEVisConfiguration(Application.Parameters parameters) {
        JEVisConfiguration conf = new BasicConfiguration();
        for (Map.Entry<String, String> entrySet : parameters.getNamed().entrySet()) {
            String[] keyGroup = entrySet.getKey().split("\\.", 2);

            if (keyGroup.length > 1) {
                conf.addOption(OptionFactory.BuildOption(keyGroup[0], keyGroup[1], entrySet.getValue(), "", true), true);
            } else {
                //Groupless option
                conf.addOption(OptionFactory.BuildOption("", entrySet.getKey(), entrySet.getValue(), "", true), true);
            }
        }
        return conf;
    }

}
