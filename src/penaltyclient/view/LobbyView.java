/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package penaltyclient.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Callback;
import penaltyclient.controller.LobbyController;
import penaltyclient.model.Player; // Import class Player mới

/**
 * Giao diện Lobby được xây dựng bằng JavaFX.
 * KHÔNG "extends JFrame"
 */
public class LobbyView {

    private LobbyController lobbyController;
    private BorderPane rootPane;
    private TableView<Player> tblPlayers;
    private ObservableList<Player> playerList; // Danh sách người chơi cho TableView
    private String username;

    public LobbyView(String username, LobbyController lobbyController) {
        this.username = username;
        this.lobbyController = lobbyController;
        createLobbyPane();
    }

    private void createLobbyPane() {
        rootPane = new BorderPane();
        rootPane.setPadding(new Insets(10));
        
        // === THÊM MÀU SẮC ===
        // Nền màu xám nhạt
        rootPane.setStyle("-fx-background-color: #f0f0f0;");

        // Header (Phần trên cùng)
        HBox headerPanel = new HBox(10);
        headerPanel.setAlignment(Pos.CENTER_LEFT);
        headerPanel.setPadding(new Insets(5, 10, 5, 10));
        // Nền trắng cho header
        headerPanel.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5;");

        Text lblTitle = new Text("Penalty Game Lobby");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2E7D32;"); // Chữ xanh

        // Panel thông tin người dùng (Bên phải header)
        HBox userPanel = new HBox(10);
        userPanel.setAlignment(Pos.CENTER_RIGHT);
        Label lblUserInfo = new Label("Welcome: " + username);
        lblUserInfo.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        Button btnLogout = new Button("Logout");
        // Nút Logout màu đỏ
        btnLogout.setStyle(
            "-fx-background-color: #D32F2F; " + // Màu đỏ
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5;"
        );
        btnLogout.setOnAction(e -> this.lobbyController.handleLogout());
        userPanel.getChildren().addAll(lblUserInfo, btnLogout);

        HBox.setHgrow(userPanel, Priority.ALWAYS); // Đẩy userPanel sang phải
        headerPanel.getChildren().addAll(lblTitle, userPanel);
        
        rootPane.setTop(headerPanel);

        // Bảng người chơi (Ở giữa)
        setupTable();
        
        // Đặt bảng vào ScrollPane để có thể cuộn nếu danh sách quá dài
        ScrollPane scrollPane = new ScrollPane(tblPlayers);
        scrollPane.setFitToWidth(true); // Tự động co dãn chiều ngang
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: #cccccc;");
        BorderPane.setMargin(scrollPane, new Insets(10, 0, 0, 0)); // Cách header 10px
        
        rootPane.setCenter(scrollPane);
    }

    private void setupTable() {
        playerList = FXCollections.observableArrayList();
        tblPlayers = new TableView<>(playerList);
        tblPlayers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Các cột vừa với bảng

        // Cột Name
        TableColumn<Player, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name")); // "name" là tên biến trong class Player

        // Cột Status
        TableColumn<Player, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Cột Score
        TableColumn<Player, Integer> colScore = new TableColumn<>("Score");
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));

        // Cột Action (Chứa nút)
        TableColumn<Player, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(createButtonCellFactory()); // Gọi hàm tạo nút

        tblPlayers.getColumns().addAll(colName, colStatus, colScore, colAction);
    }

    // Hàm này tạo ra các nút "Invite" cho cột Action
    private Callback<TableColumn<Player, Void>, TableCell<Player, Void>> createButtonCellFactory() {
        
 
        return new Callback<TableColumn<Player, Void>, TableCell<Player, Void>>() {
            @Override
            public TableCell<Player, Void> call(final TableColumn<Player, Void> param) {
                
                final TableCell<Player, Void> cell = new TableCell<Player, Void>() {
                    private final Button btn = new Button("Invite");
                    {
                        btn.setStyle(
                            "-fx-background-color: #4CAF50; " + 
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 5;"
                        );
                        
                        btn.setOnAction(event -> {
                            Player player = getTableView().getItems().get(getIndex());
                            lobbyController.handleInvite(player.getName());
                            btn.setText("Invited"); 
                            btn.setDisable(true);   
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
    }

    /**
     * Thêm người chơi vào danh sách (an toàn cho luồng).
     */
    public void addPlayer(String name, String status, int score) {
        Player player = new Player(name, status, score);
        Platform.runLater(() -> {
            playerList.add(player);
        });
    }


    public Parent getView() {
        return rootPane;
    }
    
    // Xóa toàn bộ code Swing cũ, bao gồm cả hàm initComponents() do NetBeans tạo.
}
