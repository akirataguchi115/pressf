package pressf.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pressf.dao.DbUserDao;
import pressf.domain.Finder;
import pressf.domain.PressFService;

public class PressFUI extends Application {

    private Finder finder;
    private PressFService service;

    public PressFUI() {
        this.finder = new Finder();
    }

    @Override
    public void init() throws Exception {
        DbUserDao userDao = new DbUserDao(false);
        this.service = new PressFService(userDao);
    }

    @Override
    public void start(Stage stage) {
        String un = ":)";
        BorderPane searchBp = new BorderPane();
        searchBp.setMinSize(500, 500);
        VBox buttons = new VBox();
        buttons.setSpacing(10);
        Button button = new Button("Press F!");
        Button quit = new Button("Quit");
        buttons.getChildren().addAll(button, quit);
        TextField searchWord = new TextField();
        Text result = new Text();
        Button add = new Button("Add");
        TextField link = new TextField("https://");
        Text linkText = new Text("Search from URL:(\"https://\")");
        Text searchWordText = new Text("Search for a word:");
        Text welcome = new Text();
        VBox searchWordBp = new VBox();
        searchWordBp.getChildren().addAll(searchWordText, searchWord, welcome);
        VBox linkVb = new VBox();
        linkVb.getChildren().addAll(add, linkText, link);
        button.setOnAction(event -> {
            try {
                if (this.finder.etsi(searchWord.getText(), link.getText())) {
                    result.setText("Word \"" + searchWord.getText() + "\" was found");
                    if (Desktop.isDesktopSupported()) {
                        new Thread(() -> {
                            try {
                                Desktop.getDesktop().browse(new URI(link.getText()));
                            } catch (IOException | URISyntaxException e1) {
                                System.out.println("Error occurred: " + e1.getMessage());
                            }
                        }).start();
                    } else {
                        result.setText("Word \"" + searchWord.getText() + "\" wasn't found");
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
        );
        quit.setOnAction(event
                -> {
            stage.close();
        }
        );
        add.setOnAction(event
                -> {
            if (this.finder.add(link.getText()));
        }
        );
        searchBp.setCenter(result);

        searchBp.setRight(searchWordBp);

        searchBp.setBottom(linkVb);

        searchBp.setLeft(buttons);
        Scene searchScene = new Scene(searchBp);

        BorderPane loginBp = new BorderPane();

        loginBp.setMinSize(
                300, 300);
        TextField userName = new TextField();
        Text userNameText = new Text("UserName");
        PasswordField passWord = new PasswordField();
        Text passWordText = new Text("Password");
        Button loginButton = new Button("Login");

        loginButton.setOnAction(event
                -> {
            stage.setScene(searchScene);
        }
        );
        Button createButton = new Button("Create an account");
        Text error = new Text();

        createButton.setOnAction(event
                -> {
            if (!this.service.createUser(userName.getText(), passWord.getText())) {
                error.setText("User already defined or password too short (4 chars)");
            } else {
                stage.setScene(searchScene);
                String name = userName.getText();
                String pwrod = passWord.getText();
                welcome.setText("Welcome " + service.getLoggedUser().getUsername() + "!");
            }
        }
        );
        Button loginQuitButton = new Button("Quit");

        loginQuitButton.setOnAction(blaah
                -> {
            stage.close();
        }
        );
        HBox loginButtons = new HBox();

        loginButtons.getChildren()
                .addAll(loginButton, createButton, loginQuitButton);
        VBox vbox = new VBox();

        vbox.getChildren()
                .addAll(userNameText, userName, passWordText, passWord, error);
        loginBp.setCenter(vbox);

        loginBp.setBottom(loginButtons);
        Scene loginScene = new Scene(loginBp);

        stage.setScene(loginScene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
