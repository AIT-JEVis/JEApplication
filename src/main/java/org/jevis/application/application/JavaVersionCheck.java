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
package org.jevis.application.application;

import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 * Simplistic check for the java Version. The Envidatec GUI need minumim JAVA
 * 1.7.55
 *
 * @author fs
 */
public class JavaVersionCheck {

    boolean versionK = false;

    public JavaVersionCheck() {

        String version = System.getProperty("java.version");
        System.out.println("JAVA Version: " + version);
        String[] split = version.split("\\.");
        int first = Integer.parseInt(split[0]);
        int majorV = Integer.parseInt(split[1]);
        int patch = Integer.parseInt(split[2].split("_")[1]);

        if (majorV == 7 && patch >= 55) {
            versionK = true;
        } else if (majorV > 7) {
            versionK = true;
        } else if (first > 1) {
            versionK = true;
        }

        if (!versionK) {
            JOptionPane.showMessageDialog(null,
                    "JEVis needs JAVA version 1.7.55 or newer but now JAVA 8",
                    "JAVA Version Warning",
                    JOptionPane.WARNING_MESSAGE);

        }

    }

    public boolean isVersionOK() {
        return versionK;
    }

}
