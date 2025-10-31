package penaltyclient.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import penaltyclient.controller.LoginController;

public class LoginView { 

    private LoginController loginController;
    private TextField txtUsername;
    private PasswordField txtPassword;
    private Button btnLogin;
    private Button btnExit;
    private GridPane grid;

    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        createLoginPane(); 
        addEventHandlers();
    }

    private void createLoginPane() {
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        grid.setStyle("-fx-background-color: #2E7D32;");

        Text scenetitle = new Text("Welcome to Penalty Game");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        scenetitle.setStyle("-fx-fill: white; -fx-font-weight: bold;");
        grid.add(scenetitle, 0, 0, 2, 1);

        Label lbUsername = new Label("Username:");
        lbUsername.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        grid.add(lbUsername, 0, 1);

        txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");
        txtUsername.setStyle("-fx-background-radius: 5;");
        grid.add(txtUsername, 1, 1);

        Label lbPassword = new Label("Password:");
        lbPassword.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        grid.add(lbPassword, 0, 2);
        
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        // Đặt kiểu cho ô nhập liệu
        txtPassword.setStyle("-fx-background-radius: 5;");
        grid.add(txtPassword, 1, 2);


        btnLogin = new Button("Login");
        btnLogin.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: #2E7D32; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5;"
        );
        
        btnExit = new Button("Exit");
        btnExit.setStyle(
            "-fx-background-color: #f0f0f0; " +
            "-fx-text-fill: black; " +
            "-fx-background-radius: 5;"
        );

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(btnLogin, btnExit);
        grid.add(hbBtn, 1, 4);
    }

    private void addEventHandlers() {
        btnLogin.setOnAction(e -> {
            String user = txtUsername.getText();
            String pass = txtPassword.getText();
            loginController.login(user, pass);
        });

        btnExit.setOnAction(e -> System.exit(0));

        // Cho phép nhấn Enter để login
        txtPassword.setOnAction(e -> btnLogin.fire());
    }

    public Parent getView() {
        return grid;
    }
    
}