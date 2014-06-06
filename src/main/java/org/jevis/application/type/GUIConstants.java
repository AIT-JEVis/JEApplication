/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.application.type;

import java.util.ArrayList;
import java.util.List;
import org.jevis.api.JEVisConstants;

/**
 * @deprecated This class is an temporary solution and maybe not used in the
 * futures
 *
 * @author fs
 */
public class GUIConstants {

    public static DisplayType BASIC_TEXT = new DisplayType("Text", JEVisConstants.PrimitiveType.STRING);
    public static DisplayType BASIC_NUMBER = new DisplayType("Number", JEVisConstants.PrimitiveType.DOUBLE);
    public static DisplayType BASIC_FILER = new DisplayType("File Selector", JEVisConstants.PrimitiveType.FILE);
    public static DisplayType BASIC_BOOLEAN = new DisplayType("Check Box", JEVisConstants.PrimitiveType.BOOLEAN);

    public static List<DisplayType> ALL = new ArrayList<DisplayType>() {
        {
            add(BASIC_TEXT);
            add(BASIC_NUMBER);
            add(BASIC_FILER);
            add(BASIC_BOOLEAN);
        }
    };

}
