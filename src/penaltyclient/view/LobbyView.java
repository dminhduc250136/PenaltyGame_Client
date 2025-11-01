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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import penaltyclient.controller.LobbyController;
import penaltyclient.model.MatchRecord;
import share.MatchHistoryRecord;
import share.OnlinePlayer;
import share.RankingData;

public class LobbyView {

    private LobbyController lobbyController;
    private BorderPane rootPane;
    private String username;

    // Tab 1: Online Players
    private TableView<OnlinePlayer> tblPlayers;
    private ObservableList<OnlinePlayer> playerList; 
    private Button btnReloadPlayers;

    // Tab 2: Match History
    private TableView<MatchHistoryRecord> tblMatchHistory;
    private ObservableList<MatchHistoryRecord> historyList;

    // Tab 3: Ranking
    private TableView<RankingData> tblRanking;
    private ObservableList<RankingData> rankingList;

    public LobbyView(String username, LobbyController lobbyController) {
        this.username = username;
        this.lobbyController = lobbyController;
        createLobbyPane();
    }

    private void createLobbyPane() {
        rootPane = new BorderPane();
        rootPane.setStyle("-fx-background-color: #f0f0f0;");

        HBox headerPanel = new HBox(10);
        headerPanel.setAlignment(Pos.CENTER_LEFT);
        headerPanel.setPadding(new Insets(10));
        headerPanel.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Text lblTitle = new Text("Penalty Game Lobby");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2E7D32;"); 

        HBox userPanel = new HBox(10);
        userPanel.setAlignment(Pos.CENTER_RIGHT);
        Label lblUserInfo = new Label("Welcome: " + username);
        lblUserInfo.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle(
            "-fx-background-color: #D32F2F; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 5;"
        );
        btnLogout.setOnAction(e -> this.lobbyController.handleLogout());
        userPanel.getChildren().addAll(lblUserInfo, btnLogout);

        HBox.setHgrow(userPanel, Priority.ALWAYS); 
        headerPanel.getChildren().addAll(lblTitle, userPanel);
        
        rootPane.setTop(headerPanel);

        TabPane mainTabPane = new TabPane();
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Không cho phép đóng tab

        Tab tabLobby = new Tab("Lobby");
        tabLobby.setContent(createOnlinePlayersPane());
        
        Tab tabHistory = new Tab("Lịch sử đấu");
        tabHistory.setContent(createMatchHistoryPane());
        // Thêm sự kiện để load data khi nhấn vào tab
        tabHistory.setOnSelectionChanged(e -> {
            if (tabHistory.isSelected()) {
                lobbyController.loadMatchHistory();
            }
        });

        Tab tabRanking = new Tab("Bảng xếp hạng");
        tabRanking.setContent(createRankingPane());
        // Thêm sự kiện để load data khi nhấn vào tab
        tabRanking.setOnSelectionChanged(e -> {
            if (tabRanking.isSelected()) {
                lobbyController.loadRanking();
            }
        });

        mainTabPane.getTabs().addAll(tabLobby, tabHistory, tabRanking);
        
        rootPane.setCenter(mainTabPane);
    }

    // --- Pane cho Tab 1: Online Players ---
    private Parent createOnlinePlayersPane() {
        BorderPane lobbyPane = new BorderPane();
        lobbyPane.setPadding(new Insets(10));

        // Nút Reload
        btnReloadPlayers = new Button("Reload 🔄"); // 🔄
        btnReloadPlayers.setStyle("-fx-font-weight: bold; -fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5;");
        btnReloadPlayers.setOnAction(e -> lobbyController.handleReloadPlayers());
        
        HBox topBox = new HBox(btnReloadPlayers);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPadding(new Insets(0, 0, 10, 0)); // Cách bảng 10px
        lobbyPane.setTop(topBox);

        // Bảng Player
        setupPlayersTable();
        ScrollPane scrollPane = new ScrollPane(tblPlayers);
        scrollPane.setFitToWidth(true);
        lobbyPane.setCenter(scrollPane);
        
        return lobbyPane;
    }

    // --- Pane cho Tab 2: Match History ---
    private Parent createMatchHistoryPane() {
        BorderPane historyPane = new BorderPane();
        historyPane.setPadding(new Insets(10));
        
        setupMatchHistoryTable();
        ScrollPane scrollPane = new ScrollPane(tblMatchHistory);
        scrollPane.setFitToWidth(true);
        historyPane.setCenter(scrollPane);

        return historyPane;
    }

    // --- Pane cho Tab 3: Ranking ---
    private Parent createRankingPane() {
        BorderPane rankingPane = new BorderPane();
        rankingPane.setPadding(new Insets(10));
        
        setupRankingTable();
        ScrollPane scrollPane = new ScrollPane(tblRanking);
        scrollPane.setFitToWidth(true);
        rankingPane.setCenter(scrollPane);

        return rankingPane;
    }
    
    private void setupPlayersTable() {
        playerList = FXCollections.observableArrayList();
        tblPlayers = new TableView<>(playerList);
        tblPlayers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); 

        TableColumn<OnlinePlayer, String> colName = new TableColumn<>("Username");
        colName.setCellValueFactory(new PropertyValueFactory<OnlinePlayer, String>("username")); 

        TableColumn<OnlinePlayer, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<OnlinePlayer, String>("status"));

        TableColumn<OnlinePlayer, Integer> colScore = new TableColumn<>("Score");
        colScore.setCellValueFactory(new PropertyValueFactory<OnlinePlayer, Integer>("score"));

        TableColumn<OnlinePlayer, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(createButtonCellFactory()); 

        tblPlayers.getColumns().addAll(colName, colStatus, colScore, colAction);
    }
    
    private void setupMatchHistoryTable() {
        historyList = FXCollections.observableArrayList();
        tblMatchHistory = new TableView<>(historyList);
        tblMatchHistory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<MatchHistoryRecord, String> colOpponent = new TableColumn<>("Đối thủ");
        colOpponent.setCellValueFactory(new PropertyValueFactory<>("opponentUsername")); 
        TableColumn<MatchHistoryRecord, String> colResult = new TableColumn<>("Kết quả");
        colResult.setCellValueFactory(new PropertyValueFactory<>("resultString")); // Từ "result"
        TableColumn<MatchHistoryRecord, String> colDate = new TableColumn<>("Thời gian");
        colDate.setCellValueFactory(new PropertyValueFactory<>("startTime")); // Từ "date"

        tblMatchHistory.getColumns().addAll(colOpponent, colResult, colDate);
    }

    private void setupRankingTable() {
        rankingList = FXCollections.observableArrayList();
        tblRanking = new TableView<>(rankingList);
        tblRanking.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RankingData, Integer> colRank = new TableColumn<>("Hạng");
        colRank.setCellValueFactory(new PropertyValueFactory<>("rank")); // (Dòng này ĐÚNG)

        TableColumn<RankingData, String> colUser = new TableColumn<>("Người chơi");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username")); // (Dòng này ĐÚNG)

        TableColumn<RankingData, Integer> colScore = new TableColumn<>("Điểm");
        // SỬA DÒNG NÀY:
        colScore.setCellValueFactory(new PropertyValueFactory<>("totalScore")); // Từ "score"

        TableColumn<RankingData, Integer> colWins = new TableColumn<>("Số trận thắng");
        // SỬA DÒNG NÀY:
        colWins.setCellValueFactory(new PropertyValueFactory<>("totalWins")); // Từ "wins"

        tblRanking.getColumns().addAll(colRank, colUser, colScore, colWins);
    }

    public void addPlayer(String name, String status, int score) {
        OnlinePlayer player = new OnlinePlayer(name, status, score);
        Platform.runLater(new Runnable() { 
            @Override
            public void run() {
                playerList.add(player);
            }
        });
    }

    // Xóa tất cả người chơi khỏi Tab 1 (cho việc reload)
    public void clearOnlinePlayers() {
        Platform.runLater(new Runnable() { 
            @Override
            public void run() {
                playerList.clear();
            }
        });
    }

    // Cập nhật bảng Lịch sử đấu (Tab 2)
    public void updateMatchHistory(java.util.List<MatchHistoryRecord> records) {
         Platform.runLater(new Runnable() { 
            @Override
            public void run() {
                historyList.clear();
                historyList.addAll(records);
            }
        });
    }

    // Cập nhật bảng Xếp hạng (Tab 3)
    public void updateRanking(java.util.List<RankingData> entries) {
         Platform.runLater(new Runnable() { 
            @Override
            public void run() {
                rankingList.clear();
                rankingList.addAll(entries);
            }
        });
    }

    // Trả về node root của view này
    public Parent getView() {
        return rootPane;
    }
    
    // Hàm tạo nút "Invite" (Không đổi)
    private Callback<TableColumn<OnlinePlayer, Void>, TableCell<OnlinePlayer, Void>> createButtonCellFactory() {
        return new Callback<TableColumn<OnlinePlayer, Void>, TableCell<OnlinePlayer, Void>>() {
            @Override
            public TableCell<OnlinePlayer, Void> call(final TableColumn<OnlinePlayer, Void> param) {
                final TableCell<OnlinePlayer, Void> cell = new TableCell<OnlinePlayer, Void>() {
                    private final Button btn = new Button("Invite");
                    {
                        btn.setStyle(
                            "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-background-radius: 5;"
                        );
                        btn.setOnAction(event -> {
                            OnlinePlayer player = getTableView().getItems().get(getIndex());
                            lobbyController.handleInvite(player.getUsername());
//                            btn.setText("Invited"); 
//                            btn.setDisable(true);   
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
}