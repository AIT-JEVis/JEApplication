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
 * JEConfig. If not, see <http://www.gnu.org/licenses/>.
 *
 * JEApplication is part of the OpenJEVis project, further project information
 * are published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.application.login;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static java.util.Locale.UK;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisOption;
import org.jevis.application.ParameterHelper;
import org.jevis.application.resource.ResourceLoader;
import org.jevis.commons.config.CommonOptions;
import org.jevis.commons.datasource.DataSourceLoader;

/**
 * The FXLogin represents the common JEVis login dialog with all nessasary
 * parameters like username, passw, server and so on.
 *
 *
 * @author Florian Simon
 */
public class FXLogin extends AnchorPane {

    private final Stage mainStage;
    private final Button loginButton = new Button("Login");
    private final Button closeButton = new Button("Close");
    private final TextField userName = new TextField();
    private final CheckBox storeConfig = new CheckBox("Remember me");
    private final PasswordField userPassword = new PasswordField();
    private final GridPane authGrid = new GridPane();
    private JEVisDataSource _ds;
    private final Preferences jevisPref = Preferences.userRoot().node("JEVis");
//    private final Preferences serverPref = Preferences.userRoot().node("JEVis.Server");
    private List<JEVisObject> rootObjects = new ArrayList<>();
    private List<JEVisClass> classes = new ArrayList<>();
//    private final ComboBox<JEVisConfiguration> serverSelection = new ComboBox<>();
    private String css = "";

    private int lastServer = -1;
    private Stage statusDialog = new Stage(StageStyle.TRANSPARENT);

    //workaround, need some coll OO implementaion
    private List<PreloadTask> tasks = new ArrayList<>();

    private SimpleBooleanProperty loginStatus = new SimpleBooleanProperty(false);
//    private final String URL_SYNTAX = "user:password@server:port/jevis";

//    private final ObservableList<JEVisConfiguration> serverConfigurations = FXCollections.observableList(new ArrayList<>());
    private VBox mainHBox = new VBox();
    private ProgressIndicator progress = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);

    //Workaround replace later, in the moment i have problems showing the xeporion in an thread as an Alart
    private Exception lastExeption = null;

    private Application.Parameters parameters;

    private List<JEVisOption> configuration;
    private JEVisOption fxoptions;
    private boolean useCSSFile = false;

    public interface Color {

        public static String MID_BLUE = "#005782";
        public static String MID_GREY = "#666666";
        public static String LIGHT_BLUE = "#1a719c";
        public static String LIGHT_BLUE2 = "#0E8CCC";
        public static String LIGHT_GREY = "#efefef";
        public static String LIGHT_GREY2 = "#f4f4f4";
    }

    private FXLogin() {
        mainStage = null;
    }

    /**
     * Create an new Loginpanel
     *
     * @param stage The Stage will be used of eventuell dialogs
     * @param parameters
     */
    public FXLogin(Stage stage, Application.Parameters parameters) {
        super();
        mainStage = stage;
        this.parameters = parameters;

        configuration = parseConfig(parameters);
        for (JEVisOption opt : configuration) {
            if (opt.equals(CommonOptions.FXLogin.FXLogin)) {
                fxoptions = opt;
            }
        }

        try {
            _ds = loadDataSource(configuration);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FXLogin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(FXLogin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FXLogin.class.getName()).log(Level.SEVERE, null, ex);
        }

        setStyleSheet();

        init();
    }

    /**
     * Load the DataSource from an configuration object.
     *
     * @param config
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private JEVisDataSource loadDataSource(List<JEVisOption> config) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (JEVisOption opt : config) {
            if (opt.getKey().equals(CommonOptions.DataSoure.DataSoure.getKey())) {
                DataSourceLoader dsl = new DataSourceLoader();
                JEVisDataSource ds = dsl.getDataSource(opt);
//            config.completeWith(ds.getConfiguration());
                ds.setConfiguration(config);
                return ds;
            }
        }

        return null;
    }

    /**
     * Try to set the StyleSheet is the user set the parameter
     */
    private void setStyleSheet() {
        try {
            if (fxoptions != null) {
                if (fxoptions.hasOption(CommonOptions.FXLogin.URL_CSS.getKey())) {
                    JEVisOption opt = fxoptions.getOption(CommonOptions.FXLogin.URL_CSS.getKey());
                    mainStage.getScene().getStylesheets().add(opt.getValue());
                    useCSSFile = true;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Parse a JEVisConfiguration out of the Application.Parameters
     *
     * @param parameter
     * @return
     */
    private List<JEVisOption> parseConfig(Application.Parameters parameter) {
        List<JEVisOption> config = ParameterHelper.ParseJEVisConfiguration(parameter);
        return config;
    }

    /**
     * Returns all JEVisClasses after successfull login.
     *
     * @return List of every JEVisClass on the server
     */
    public List<JEVisClass> getAllClasses() {
        return classes;
    }

    /**
     * Returns all root JEVisOBjects which the user has access to after an
     * successfull login.
     *
     * @return
     */
    public List<JEVisObject> getRootObjects() {
        return rootObjects;
    }

    /**
     * Internal helper which for the time JavaFX had no Dialogs. Now it has so
     * we can replace this.
     *
     * @deprecated
     * @param ex
     * @return
     */
    private Alert buildAlarm(Exception ex) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(ex.getMessage());

        return alert;

    }

    /**
     * This function will try to connect to the JEVisDataSource with the
     * configures server settings.
     *
     * @throws JEVisException
     */
    private void doLogin() throws JEVisException {

        Task<Void> loginTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {

//                    System.out.println("Login: " + _ds + " userName: " + userName.getText() + " pass: " + userPassword.getText());
//                    System.out.println("Config:" + _ds.getConfiguration().toString());
                    if (_ds.connect(userName.getText(), userPassword.getText())) {
                        classes = _ds.getJEVisClasses();
                        rootObjects = _ds.getRootObjects();
                        this.succeeded();
                    }

                } catch (Exception jex) {
                    jex.printStackTrace();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(jex.getMessage());
//                    alert.setContentText(Arrays.toString(jex.getStackTrace()));
                    alert.showAndWait();

                    lastExeption = jex;
                    this.failed();
                    this.cancel();
                    loginButton.setDisable(false);
                }
                return null;
            }
        };

        loginTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("loginTask.sucess");
//                SimpleServerConfig serverConfig = serverSelection.getSelectionModel().getSelectedItem();
//                serverConfig.save();

                if (storeConfig.isSelected()) {
                    storePreference();
                }

                statusDialog.hide();
                loginStatus.setValue(Boolean.TRUE);
            }
        });

        EventHandler<WorkerStateEvent> faildEvent = new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("faild");

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        authGrid.setDisable(false);
                        statusDialog.hide();
                        progress.setVisible(false);
                    }
                });
                Alert alart = buildAlarm(lastExeption);
                alart.showAndWait();

            }
        };
        loginTask.setOnCancelled(faildEvent);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progress.setVisible(true);
            }
        });

        new Thread(loginTask).start();
    }

    /**
     * returns the JEVisDataSource after an successfull login
     *
     * @return
     */
    public JEVisDataSource getDataSource() {
        return _ds;
    }

    private void setDefaultStyle(Node node, String style) {
        if (!useCSSFile) {
            node.setStyle(style);
        }
    }

    /**
     * Build an header GUI element.
     *
     * @return
     */
    private Node buildHeader() {
        AnchorPane header = new AnchorPane();
        header.setId("fxlogin-header");
        setDefaultStyle(header, "-fx-background-color: " + Color.LIGHT_BLUE);

        ImageView logo = null;

        String defaultLogo = "/icons/openjevis_longlogo.png";

        if (fxoptions != null) {
            if (fxoptions.hasOption(CommonOptions.FXLogin.URL_LOGO.getKey())) {
                JEVisOption opt = fxoptions.getOption(CommonOptions.FXLogin.URL_LOGO.getKey());
                if (opt.getValue() != null && !opt.getValue().isEmpty()) {
                    try {
                        logo = new ImageView(new Image(opt.getValue()));
                    } catch (Exception ex) {
                        logo = new ImageView(new Image(defaultLogo));
                    }
                } else {
                    logo = new ImageView(new Image(defaultLogo));
                }
            }
        } else {
            logo = new ImageView(new Image(defaultLogo));
        }

        header.getChildren().add(logo);
        AnchorPane.setBottomAnchor(logo, 0.0);
        AnchorPane.setLeftAnchor(logo, 0.0);

        logo.setId("fxlogin-header-logo");

        return header;
    }

    /**
     * Build an footer GUI element
     *
     * @return
     */
    private Node buildFooter() {
        AnchorPane footer = new AnchorPane();
        footer.setId("fx-login-footer");
        setDefaultStyle(footer, "-fx-background-color: " + Color.LIGHT_BLUE);

        Node buildInfo = buldBuildInfos();
        buildInfo.setId("fx-login-footer-info");
        AnchorPane.setBottomAnchor(buildInfo, 5.0);
        AnchorPane.setRightAnchor(buildInfo, 5.0);
        footer.getChildren().add(buildInfo);

        return footer;
    }

    /**
     * Build an bottom button bar element
     *
     * @return
     */
    private Node buidButtonsbar() {
        Region spacer = new Region();
        setDefaultStyle(spacer, "-fx-background-color: transparent;");

        HBox buttonBox = new HBox(10);
        Node link = buildLink();
        buttonBox.getChildren().setAll(link, spacer, loginButton, closeButton);
        HBox.setHgrow(link, Priority.NEVER);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(loginButton, Priority.NEVER);
        return buttonBox;
    }

    /**
     * Build an authentification (user, passwort-..) GUI element
     *
     * @return
     */
    private Node buildAuthForm() {

        Node buttonBox = buidButtonsbar();
//        Node serverConfigBox = buildServerSelection();
        Region serverConfigBox = new Region();
        ComboBox langSelect = buildLanguageBox();
        loadPreference(true);

        Label userL = new Label("Username:");
        userL.setId("fxlogin-form-user-label");
        Label passwordL = new Label("Password:");
        passwordL.setId("fxlogin-form-password-label");
        Label serverL = new Label("Server:");
        serverL.setId("fxlogin-form-server-label");
        Label languageL = new Label("Language: ");
        languageL.setId("fxlogin-form-language-label");

        loginButton.setDefaultButton(true);
        closeButton.setCancelButton(true);

        langSelect.setId("fxlogin-form-language");
        loginButton.setId("fxlogin-form-login");
        closeButton.setId("fxlogin-form-close");
        userName.setId("fxlogin-form-username");
        userPassword.setId("fxlogin-form-password");
        authGrid.setId("fxlogin-form");

        closeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                statusDialog.hide();
                loginStatus.setValue(Boolean.FALSE);
            }
        });

        userName.requestFocus();

        loginButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Login!");
                try {
                    loginButton.setDisable(true);
                    doLogin();

                } catch (JEVisException ex) {
                    Logger.getLogger(FXLogin.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        authGrid.setHgap(10);
        authGrid.setVgap(5);
        authGrid.setPadding(new Insets(10, 10, 10, 10));

        int columns = 2;
        //x,x...
        int row = 0;
        authGrid.add(userL, 0, row);
        authGrid.add(userName, 1, row);

        row++;
        authGrid.add(passwordL, 0, row);
        authGrid.add(userPassword, 1, row);

        row++;
        authGrid.add(languageL, 0, row);
        authGrid.add(langSelect, 1, row);

        row++;
//        authGrid.add(serverL, 0, row);
//        authGrid.add(serverConfigBox, 1, row);
        authGrid.add(new Region(), 0, row);
        authGrid.add(new Region(), 1, row);

        row++;
//        grid.add(serverL, 0, row);
        authGrid.add(storeConfig, 1, row);

        Region cTobSpacer = new Region();
        setDefaultStyle(cTobSpacer, "-fx-background-color: transparent;");
        cTobSpacer.setPrefHeight(30);

        row++;
        authGrid.add(cTobSpacer, 0, row, columns, 1);

        row++;
        authGrid.add(buttonBox, 0, row, columns, 1);

        storeConfig.setId("fxlogin-form-remeberme");

        return authGrid;
    }

    /**
     * Initilize this class, this will build most of the GUI
     */
    private void init() {
        loadPreference(true);

        //TODO load from URL/RESOURCE
        ImageView logo = new ImageView(new Image("/icons/openjevislogo_simple2.png"));
        logo.setPreserveRatio(true);

        AnchorPane leftSpacer = new AnchorPane();
        AnchorPane rightSpacer = new AnchorPane();

        leftSpacer.setId("fxlogin-body-left");
        rightSpacer.setId("fxlogin-body-right");
        progress.setId("fxlogin-body-progress");

        progress.setPrefSize(80, 80);
        progress.setVisible(false);
        AnchorPane.setTopAnchor(progress, 70d);
        AnchorPane.setLeftAnchor(progress, 100d);
        rightSpacer.getChildren().setAll(progress);

        leftSpacer.setMinWidth(200);//todo 20%

        Node authForm = buildAuthForm();

        HBox body = new HBox();
        body.setId("fxlogin-body");
        body.getChildren().setAll(leftSpacer, authForm, rightSpacer);
        HBox.setHgrow(authForm, Priority.NEVER);
        HBox.setHgrow(leftSpacer, Priority.NEVER);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Node header = buildHeader();
        Node footer = buildFooter();

        mainHBox = new VBox();
        mainHBox.getChildren().setAll(header, body, footer);
        VBox.setVgrow(body, Priority.NEVER);
        VBox.setVgrow(header, Priority.ALWAYS);
        VBox.setVgrow(footer, Priority.ALWAYS);

        setDefaultStyle(body, "-fx-background-color: white;");
        setDefaultStyle(leftSpacer, "-fx-background-color: white;");
        setDefaultStyle(rightSpacer, "-fx-background-color: white;");
        setDefaultStyle(mainHBox, "-fx-background-color: yellow;");

        AnchorPane.setTopAnchor(mainHBox, 0.0);
        AnchorPane.setRightAnchor(mainHBox, 0.0);
        AnchorPane.setLeftAnchor(mainHBox, 0.0);
        AnchorPane.setBottomAnchor(mainHBox, 0.0);

        getChildren().setAll(mainHBox);

    }

    /**
     * This version has no function to add new servers. this may change in te
     * future again.
     *
     * @return
     * @deprecated
     */
    @Deprecated
    private Node buildServerSelection() {
        VBox root = new VBox(10);
        Label titel = new Label("Server Configuration");
        setDefaultStyle(titel, "-fx-font-weight: bold;");

        Label nameLabel = new Label("Name:");
        TextField nameF = new TextField();
        Label urlLabel = new Label("Server:");
        TextField urlF = new TextField();
        Label portLabel = new Label("Port:");
        TextField portF = new TextField();
        Label schema = new Label("Schema:");
        TextField schemaF = new TextField();
        Label userL = new Label("Username:");
        TextField userF = new TextField();
        Label passL = new Label("Password:");
        TextField passF = new TextField();

        Button ok = new Button("Save");
        ok.setDefaultButton(true);
        Button addNewButton = new Button("Save as new");
        ok.setDefaultButton(true);
        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);
        Region spacer = new Region();

        HBox buttons = new HBox(8);
        buttons.getChildren().setAll(spacer, ok, addNewButton, cancel);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        int columns = 2;
        //x,x...
        int row = 0;
        grid.add(nameLabel, 0, row);
        grid.add(nameF, 1, row);
        row++;
        grid.add(urlLabel, 0, row);
        grid.add(urlF, 1, row);
        row++;
        grid.add(portLabel, 0, row);
        grid.add(portF, 1, row);
        row++;
        grid.add(schema, 0, row);
        grid.add(schemaF, 1, row);
        row++;
        grid.add(userL, 0, row);
        grid.add(userF, 1, row);
        row++;
        grid.add(passL, 0, row);
        grid.add(passF, 1, row);

        VBox.setMargin(titel, new Insets(10, 30, 10, 30));
        Region bottomSpacer = new Region();
        bottomSpacer.setPrefHeight(10);

        root.getChildren().setAll(titel, grid, buttons, bottomSpacer);
        root.setPadding(new Insets(10));

//        Button configureServer = new Button("", JEConfig.getImage("Service Manager.png", 16, 16));
        Button configureServer = new Button("", ResourceLoader.getImage("Service Manager.png", 16, 16));

        PopOver serverConfigPop = new PopOver(root);
        serverConfigPop.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        serverConfigPop.setDetachable(true);
        serverConfigPop.setHideOnEscape(true);
        serverConfigPop.setAutoFix(true);

//        serverSelection.setItems(serverConfigurations);
//        serverSelection.getSelectionModel().selectFirst();
        HBox serverConfBox = new HBox(10);
//        serverConfBox.getChildren().setAll(serverSelection, configureServer);

        HBox.setHgrow(configureServer, Priority.NEVER);
//        HBox.setHgrow(serverSelection, Priority.ALWAYS);

        ok.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                serverConfigPop.hide(Duration.seconds(0.3));
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                serverConfigPop.hide(Duration.seconds(1));
            }
        });

        addNewButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

            }
        });

        return serverConfBox;
    }

    /**
     * Load usersettings from Prederence.
     *
     * @param showServer
     * @param defaultServer
     */
    private void loadPreference(boolean showServer) {
//        System.out.println("load from disk");
        if (!jevisPref.get("JEVisUser", "").isEmpty()) {
            storeConfig.setSelected(true);
//            System.out.println("username: " + jevisPref.get("JEVisUser", ""));
            userName.setText(jevisPref.get("JEVisUser", ""));
            userPassword.setText(jevisPref.get("JEVisPW", ""));
        } else {
            storeConfig.setSelected(false);
        }
    }

    /**
     * Build an language selection box
     *
     * @return
     */
    private ComboBox buildLanguageBox() {
        List<Locale> availableLang = new ArrayList<>();
        availableLang.add(UK);

        Callback<ListView<Locale>, ListCell<Locale>> cellFactory = new Callback<ListView<Locale>, ListCell<Locale>>() {
            @Override
            public ListCell<Locale> call(ListView<Locale> param) {
                final ListCell<Locale> cell = new ListCell<Locale>() {
                    {
                        super.setPrefWidth(260);
                    }

                    @Override
                    public void updateItem(Locale item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {

                            HBox box = new HBox(5);
                            box.setAlignment(Pos.CENTER_LEFT);

                            Image img = new Image("/icons/" + item.getLanguage() + ".png");
                            ImageView iv = new ImageView(img);
                            iv.fitHeightProperty().setValue(20);
                            iv.fitWidthProperty().setValue(20);
                            iv.setSmooth(true);

                            Label name = new Label(item.getDisplayLanguage());
                            name.setTextFill(javafx.scene.paint.Color.BLACK);

                            box.getChildren().setAll(iv, name);
                            setGraphic(box);

                        }
                    }
                };
                return cell;
            }
        };

        ObservableList<Locale> options = FXCollections.observableArrayList(availableLang);

        final ComboBox<Locale> comboBox = new ComboBox<Locale>(options);
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));

        //TODO: load default lange from Configfile or so
        comboBox.getSelectionModel().select(UK);//Default

        comboBox.setMinWidth(250);
        comboBox.setMaxWidth(Integer.MAX_VALUE);//workaround

        return comboBox;

    }

    /**
     * Build an remote link for the userregistration
     *
     * @param url remote linkt to open in default brwoser
     * @return
     */
    private Node buildLink() {
        Hyperlink link = new Hyperlink();
        link.setId("fxlogin-form-register");
        link.setText("Register");

        final String url;

        if (fxoptions != null) {
            if (fxoptions.hasOption(CommonOptions.FXLogin.URL_REGISTER.getKey())) {
                JEVisOption opt = fxoptions.getOption(CommonOptions.FXLogin.URL_REGISTER.getKey());
                if (opt.getValue().equals("off")) {
                    link.setText("");
                    link.setVisible(false);
                    url = "";
                } else {
                    url = opt.getValue();
                }
            } else {
                url = "http://openjevis.org/account/register";
            }
        } else {
            url = "http://openjevis.org/account/register";

        }

        link.setOnAction(event -> new Thread(() -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        }).start());

        return link;
    }

    /**
     * Build an infoBox where some information about the application are
     * displayed
     *
     * @return
     */
    public Node buldBuildInfos() {
        VBox vbox = new VBox();
        setDefaultStyle(vbox, "-fx-background-color: transparent;");
//        Label name = new Label(JEConfig.PROGRAMM_INFO.getName());
//        Label version = new Label("Version: " + JEConfig.PROGRAMM_INFO.getVersion());
//        Label version = new Label("Version: " + JEConfig.PROGRAMM_INFO.getVersion());
        Label coypLeft = new Label("©Envidatec GmbH 2014-2015");
        Label java = new Label("Java: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"));

//        name.setTextFill(javafx.scene.paint.Color.WHITE);
//        version.setTextFill(javafx.scene.paint.Color.WHITE);
        coypLeft.setTextFill(javafx.scene.paint.Color.WHITE);
        java.setTextFill(javafx.scene.paint.Color.WHITE);

//        vbox.getChildren().setAll(name, version, coypLeft, java);
        vbox.getChildren().setAll(coypLeft, java);
        return vbox;
    }

    /**
     * The an BooleanProperty which is true if the user logged in successfully
     *
     * @return
     */
    public SimpleBooleanProperty getLoginStatus() {
        return loginStatus;
    }

    /**
     * Stroe the current setting like username and password in the java
     * preferenc contex. Warning the password is in plantext i guess, maye be
     * find a better solution but the JEVisDataSource needs an plaintext pw.
     */
    private void storePreference() {
        System.out.println("save to disk");
        final String jevisUser = userName.getText();
        final String jevisPW = userPassword.getText();

        jevisPref.put("JEVisUser", jevisUser);
        jevisPref.put("JEVisPW", jevisPW);
        try {
            jevisPref.sync();
        } catch (BackingStoreException ex) {
            Logger.getLogger(FXLogin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
