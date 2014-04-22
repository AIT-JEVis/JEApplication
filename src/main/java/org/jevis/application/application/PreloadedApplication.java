package org.jevis.application.application;

import org.jevis.application.connection.Connection;

import javafx.scene.Parent;

public interface PreloadedApplication {
    /**
     * @param Connection connection to JEVis
     * 
     * This function of the main application is called by the preloader 
     */
    public void startMainApplication(Connection jevis);

    /**
     * @return
     */
    public Parent getParentNode();
}