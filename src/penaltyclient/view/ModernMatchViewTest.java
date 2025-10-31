
package penaltyclient.view;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;
import static javafx.application.Application.launch;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import penaltyclient.controller.MatchController;


public class MatchView extends Application {

    private static final double SCENE_WIDTH = 900;
    private static final double SCENE_HEIGHT = 700;
    
    private Pane gamePane;
    private Rectangle[] goalZones = new Rectangle[6]; // Giữ lại 6 ô ẩn để bắt sự kiện click
    private ImageView goalView;         // Cho ảnh khung thành
    private ImageView ballView;         // Cho ảnh quả bóng
    private ImageView goalkeeperView;   // Cho ảnh thủ môn

    // Biến lưu các trạng thái của thủ môn
    private Image imgKeeperIdle; // Ảnh đứng yên
    private Image[] imgKeeperSaves = new Image[6]; // Mảng 6 ảnh bay lượn
    private MatchController controller;
    private String playerName;
    private String opponentName;
    
    private Label scoreLabel;
    private Label myScore = new Label("0");
    private Label opponentScore = new Label("0");
    private Label messageLabel;
    private Label playerNameLabel = new Label("BAR");
    private Label opponentNameLabel = new Label("RMA");
    private Button confirmButton;
    private Label timerLabel = new Label("00:00");
    
    private boolean inputEnabled = false;
    private int selectedZone = -1;
    
    
    
    public void setController (MatchController controller) {
        this.controller = controller;
    }
    
    @Override
    public void start(Stage primaryStage) {
        // 1. Lấy controller từ biến static
        this.controller = MatchController.getStartingController();
        if (this.controller == null) {
            System.err.println("FATAL ERROR: MatchView started without a MatchController!");
            // Hiển thị lỗi và đóng
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not initialize match. Controller not found.");
                alert.showAndWait();
                primaryStage.close();
            });
            return;
        }

        // 2. Báo cho controller biết instance View này đã được tạo
        this.controller.registerViewInstance(this);

        // 3. Xây dựng UI (code tạo VBox, Pane, HBox, etc. như cũ)
        VBox root = new VBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #90EE90);");
        createGameView();
        VBox scoreboard = createScoreBoard();
        scoreboard.setLayoutX(80);
        scoreboard.setLayoutY(30);
        HBox bottomUI = createBottomUI();
        root.getChildren().addAll(gamePane, bottomUI);
        
        Pane overlay = new Pane(scoreboard);
        StackPane pane = new StackPane(root, overlay);
        StackPane.setAlignment(overlay, Pos.TOP_LEFT);
//        overlay.setPickOnBounds(false); 
//        overlay.setFocusTraversable(false);
        
        VBox.setVgrow(gamePane, Priority.ALWAYS);

        // 4. Tạo Scene và thiết lập Stage
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);
        setupControls(scene); // Thiết lập sự kiện phím

        primaryStage.setScene(scene);
        primaryStage.setTitle("Penalty Shootout");
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(e -> {
            e.consume(); // Ngăn đóng ngay
            if (controller != null) {
                // Tạo dialog xác nhận thoát
                Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION, "Surrender?");
                confirmExit.initOwner(primaryStage);
                confirmExit.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        controller.onWindowClose();
                        primaryStage.close();
                    }
                });
            } else {
                primaryStage.close();
            }
        });

        primaryStage.show();

        System.out.println("MatchView started and UI initialized.");
    }
    
    private void createGameView() {
        gamePane = new Pane();
//        gamePane.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT - 100);
        
        // Sky background (1/3 of screen)
        Rectangle sky = new Rectangle(0, 0, SCENE_WIDTH, 200);
        Stop[] skyStops = new Stop[] {
            new Stop(0, Color.rgb(135, 206, 235)),
            new Stop(1, Color.rgb(173, 216, 230))
        };
        sky.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, skyStops));
        
        // Stadium/crowd area (simple representation)
        Rectangle stadium = new Rectangle(0, 100, SCENE_WIDTH, 100);
        stadium.setFill(Color.rgb(100, 100, 150, 0.3));
        
        // Grass field (2/3 of screen with perspective)
        Polygon field = new Polygon();
        field.getPoints().addAll(new Double[]{
            0.0, 200.0,                    // Top left
            SCENE_WIDTH, 200.0,            // Top right  
            SCENE_WIDTH, SCENE_HEIGHT-100.0,  // Bottom right
            0.0, SCENE_HEIGHT-100.0        // Bottom left
        });
        field.setFill(createGrassPattern());
        
        gamePane.getChildren().addAll(sky, stadium, field);
        
        createFieldMarkings();
        createGoal();
        createGoalkeeper();
        createBall();
    }
    
    private Paint createGrassPattern() {
        // Grass with stripes
        Stop[] stops = new Stop[] {
            new Stop(0, Color.rgb(46, 125, 50)),
            new Stop(0.15, Color.rgb(46, 125, 50)),
            new Stop(0.15, Color.rgb(56, 142, 60)),
            new Stop(0.3, Color.rgb(56, 142, 60)),
            new Stop(0.3, Color.rgb(46, 125, 50)),
            new Stop(0.45, Color.rgb(46, 125, 50)),
            new Stop(0.45, Color.rgb(56, 142, 60)),
            new Stop(0.6, Color.rgb(56, 142, 60)),
            new Stop(0.6, Color.rgb(46, 125, 50)),
            new Stop(0.75, Color.rgb(46, 125, 50)),
            new Stop(0.75, Color.rgb(56, 142, 60)),
            new Stop(0.9, Color.rgb(56, 142, 60)),
            new Stop(0.9, Color.rgb(46, 125, 50)),
            new Stop(1, Color.rgb(46, 125, 50))
        };
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
    }
    
    private void createFieldMarkings() {
        // Penalty area lines với perspective
        Polygon penaltyArea = new Polygon();
        penaltyArea.getPoints().addAll(new Double[]{
            200.0, 250.0,   // Top left
            700.0, 250.0,   // Top right
            800.0, 400.0,   // Bottom right
            100.0, 400.0    // Bottom left
        });
        penaltyArea.setFill(Color.TRANSPARENT);
        penaltyArea.setStroke(Color.WHITE);
        penaltyArea.setStrokeWidth(3);
        
        // Goal area (smaller box)
        Polygon goalArea = new Polygon();
        goalArea.getPoints().addAll(new Double[]{
            320.0, 250.0,
            580.0, 250.0,
            620.0, 320.0,
            280.0, 320.0
        });
        goalArea.setFill(Color.TRANSPARENT);
        goalArea.setStroke(Color.WHITE);
        goalArea.setStrokeWidth(3);
        
        // Penalty spot (chấm phạt đền) - đặt trong vòng cấm
        Circle penaltySpot = new Circle(450, 350, 5);  // đưa lên trong penaltyArea
        penaltySpot.setFill(Color.WHITE);
        // Penalty arc (nửa vòng tròn) - nằm ngoài vòng cấm
        Arc penaltyArc = new Arc(450, 400, 100, 60, 0, -180); 
        // tâm đặt ở chính giữa vạch 16m50 (y = 250), vẽ ra ngoài
        penaltyArc.setType(ArcType.OPEN);
        penaltyArc.setFill(Color.TRANSPARENT);
        penaltyArc.setStroke(Color.WHITE);
        penaltyArc.setStrokeWidth(3);

        gamePane.getChildren().addAll(penaltyArea, goalArea, penaltySpot, penaltyArc);
    }
    
    private void createGoal() {
        double goalX = 300;
        double goalY = 150;
        double goalWidth = 300;
        double goalHeight = 100;
        
        // Goal frame
        // Left post
        Rectangle leftPost = new Rectangle(goalX - 5, goalY, 10, goalHeight);
        leftPost.setFill(Color.WHITE);
        leftPost.setStroke(Color.GRAY);
        leftPost.setStrokeWidth(1);
        
        // Right post
        Rectangle rightPost = new Rectangle(goalX + goalWidth - 5, goalY, 10, goalHeight);
        rightPost.setFill(Color.WHITE);
        rightPost.setStroke(Color.GRAY);
        rightPost.setStrokeWidth(1);
        
        // Crossbar
        Rectangle crossbar = new Rectangle(goalX - 5, goalY - 5, goalWidth + 10, 10);
        crossbar.setFill(Color.WHITE);
        crossbar.setStroke(Color.GRAY);
        crossbar.setStrokeWidth(1);
        
        // Net background
        Rectangle netBg = new Rectangle(goalX, goalY, goalWidth, goalHeight);
        netBg.setFill(Color.rgb(200, 200, 200, 0.3));
        
        // Net pattern - vertical lines
        for (int i = 0; i <= 15; i++) {
            Line line = new Line(
                goalX + i * 20, goalY,
                goalX + i * 20, goalY + goalHeight
            );
            line.setStroke(Color.LIGHTGRAY);
            line.setStrokeWidth(1);
            line.setOpacity(0.5);
            gamePane.getChildren().add(line);
        }
        
        // Net pattern - horizontal lines
        for (int i = 0; i <= 5; i++) {
            Line line = new Line(
                goalX, goalY + i * 20,
                goalX + goalWidth, goalY + i * 20
            );
            line.setStroke(Color.LIGHTGRAY);
            line.setStrokeWidth(1);
            line.setOpacity(0.5);
            gamePane.getChildren().add(line);
        }
        
        gamePane.getChildren().addAll(netBg, leftPost, rightPost, crossbar);

        // Create 6 shooting zones (3 columns x 2 rows)
        double zoneWidth = goalWidth / 3;
        double zoneHeight = goalHeight / 2;
        
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                final int index = row * 3 + col;
                
                goalZones[index] = new Rectangle(
                    goalX + col * zoneWidth,
                    goalY + row * zoneHeight,
                    zoneWidth,
                    zoneHeight
                );
                
                goalZones[index].setFill(Color.TRANSPARENT);
                goalZones[index].setStroke(Color.YELLOW);
                goalZones[index].setStrokeWidth(2);
                goalZones[index].setOpacity(0);
                
                // Zone labels
                String[] zoneNames = {"Top Left", "Top Center", "Top Right", 
                                     "Bottom Left", "Bottom Center", "Bottom Right"};
                
                // Mouse events
                final int zoneIndex = index;
                goalZones[index].setOnMouseEntered(e -> {
                    if (selectedZone == -1) {
                        goalZones[zoneIndex].setOpacity(0.3);
                        goalZones[zoneIndex].setFill(Color.rgb(255, 255, 0, 0.2));
                        messageLabel.setText("Aim: " + zoneNames[zoneIndex] + "\nClick SUBMIT or type SPACE to confirm.");
                    }
                });
                
                goalZones[index].setOnMouseExited(e -> {
                    if (selectedZone == -1 || selectedZone != zoneIndex) {
                        goalZones[zoneIndex].setOpacity(0);
                        goalZones[zoneIndex].setFill(Color.TRANSPARENT);
                    }
                    if (selectedZone == -1) {
                        messageLabel.setText("Click on a goal zone to shoot!");
                    }
                });
                
                goalZones[index].setOnMouseClicked(e -> {
                    if (selectedZone == -1) {
                        controller.onZoneSelected(zoneIndex);
                    }
                });
                
                gamePane.getChildren().add(goalZones[index]);
            }
        }        
    }
    
    // XÓA toàn bộ phương thức updateGoalkeeperArms()

    // THAY THẾ toàn bộ phương thức createGoalkeeper()
    private void createGoalkeeper() {
        try {
            // Thay "/assets/..." bằng đường dẫn chính xác
            imgKeeperIdle = new Image(getClass().getResourceAsStream("../Assets/word-image-3.png")); // Trạng thái đứng yên

            // Gán 6 ảnh save vào mảng
            // Lưu ý: Tên file phải khớp chính xác với 6 zone
            imgKeeperSaves[0] = new Image(getClass().getResourceAsStream("../Assets/word-image-6.png")); // Top Left
            imgKeeperSaves[1] = new Image(getClass().getResourceAsStream("../Assets/word-image-8.png")); // Top Center
            imgKeeperSaves[2] = new Image(getClass().getResourceAsStream("../Assets/word-image-7.png")); // Top Right
            imgKeeperSaves[3] = new Image(getClass().getResourceAsStream("../Assets/word-image-5.png")); // Bottom Left
            imgKeeperSaves[4] = new Image(getClass().getResourceAsStream("../Assets/word-image-3.png")); // THAY THẾ BẰNG ẢNH BOTTOM CENTER
            imgKeeperSaves[5] = new Image(getClass().getResourceAsStream("../Assets/word-image-4.png")); // Bottom Right

        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh thủ môn!");
            e.printStackTrace();
        }

        // Khởi tạo ImageView với ảnh đứng yên
        goalkeeperView = new ImageView(imgKeeperIdle);

        // Điều chỉnh kích thước (ví dụ: rộng 80, cao 100)
        goalkeeperView.setFitWidth(57);
        goalkeeperView.setFitHeight(87);
        goalkeeperView.setPreserveRatio(true);

        // Đặt vị trí đứng yên ban đầu
        // (Tọa độ X, Y này giống với Ellipse cũ của bạn)
        goalkeeperView.setLayoutX(450 - (goalkeeperView.getFitWidth() / 2)); // Giữa gôn
        goalkeeperView.setLayoutY(250 - goalkeeperView.getFitHeight()); // Gần vạch vôi

        gamePane.getChildren().add(goalkeeperView);
    }
    
//    private void updateGoalkeeperArms() {
//        double bodyX = goalkeeperBody.getCenterX();
//        double bodyY = goalkeeperBody.getCenterY();
//        leftArm.setStartX(bodyX);
//        leftArm.setStartY(bodyY);
//        leftArm.setEndX(bodyX - 20); // Tay dang ra
//        leftArm.setEndY(bodyY - 10);
//        rightArm.setStartX(bodyX);
//        rightArm.setStartY(bodyY);
//        rightArm.setEndX(bodyX + 20); // Tay dang ra
//        rightArm.setEndY(bodyY - 10);
//    }
    
    private void createBall() {
        try {
            // Thay "/assets/ball.png" bằng đường dẫn chính xác
            Image ballImage = new Image(getClass().getResourceAsStream("../Assets/word-image-1.png"));
            ballView = new ImageView(ballImage);

            ballView.setFitWidth(25); // Kích thước bóng (điều chỉnh nếu cần)
            ballView.setFitHeight(25);

            // Đặt bóng tại chấm penalty (giữ nguyên tọa độ X, Y từ code cũ)
            // (450, 350) là tâm, nên (Tâm - bán kính) là tọa độ layout
            ballView.setLayoutX(450 - 12);
            ballView.setLayoutY(350 - 12);

            gamePane.getChildren().add(ballView);

        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh quả bóng!");
            // Vẽ hình tròn thay thế nếu lỗi
            ballView = new ImageView(new Circle(12, Color.WHITE).snapshot(null, null));
            ballView.setLayoutX(450 - 12);
            ballView.setLayoutY(350 - 12);
            gamePane.getChildren().add(ballView);
        }

        // XÓA code vẽ Circle và RadialGradient cũ
    }
    
    private VBox createScoreBoard() {
        String nameStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;";
        String scoreStyle = "-fx-text-fill: #35024F; -fx-font-size: 20px; -fx-font-weight: bold;";

        // Gán style
        timerLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
        playerNameLabel.setStyle(nameStyle);
        opponentNameLabel.setStyle(nameStyle);
        myScore.setStyle(scoreStyle);
        opponentScore.setStyle(scoreStyle);

        // Tạo label dấu gạch giữa tỉ số
        Label dash = new Label("-");
        dash.setStyle(scoreStyle);

        // Box tỉ số xanh
        HBox scoreBox = new HBox(5, myScore, dash, opponentScore);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(4, 12, 4, 12));
        scoreBox.setStyle("-fx-background-color: #0CFE76; -fx-background-radius: 4;"); // xanh lá

        // HBox chứa tên 2 đội và tỉ số
        HBox teamsBox = new HBox(10, playerNameLabel, scoreBox, opponentNameLabel);
        teamsBox.setAlignment(Pos.CENTER);
        teamsBox.setStyle("-fx-background-color: #35024F; -fx-background-radius: 6; -fx-padding: 6 10 6 10;");

        // Đặt timer bên dưới
        HBox timerBox = new HBox(timerLabel);
        timerBox.setAlignment(Pos.CENTER);
        timerBox.setPadding(new Insets(4, 0, 0, 0));
        timerBox.setStyle("-fx-background-color: white; -fx-background-radius: 4;");

        // Gộp tất cả lại thành VBox
        VBox scoreboard = new VBox(3, teamsBox, timerBox);
        scoreboard.setAlignment(Pos.CENTER);
        scoreboard.setPadding(new Insets(6));
        scoreboard.setStyle("-fx-background-color: null;");

        return scoreboard;
    }
    
    private HBox createBottomUI() {
        HBox bottomPanel = new HBox(30);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(20));
        bottomPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        bottomPanel.setPrefHeight(100);

        confirmButton = new Button("SUBMIT!");
        confirmButton.setPrefSize(120, 40);
        confirmButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        confirmButton.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        confirmButton.setOnAction(e -> {
            if (inputEnabled && controller != null) {
                controller.onConfirmChoice();
            }
        });
        confirmButton.setDisable(true);
        
        messageLabel = new Label("Click on a goal zone to shoot!");
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.setTextFill(Color.YELLOW);
        messageLabel.setMinWidth(250);
               
        bottomPanel.getChildren().addAll(confirmButton, messageLabel);
        
        return bottomPanel;
    }
    
    private void selectZone(int index) {
        selectedZone = index;
        
        // Highlight selected zone
        for (int i = 0; i < 6; i++) {
            if (i == index) {
                goalZones[i].setOpacity(0.5);
                goalZones[i].setFill(Color.rgb(0, 255, 0, 0.3));
                goalZones[i].setStroke(Color.LIME);
            } else {
                goalZones[i].setOpacity(0);
                goalZones[i].setFill(Color.TRANSPARENT);
                goalZones[i].setStroke(Color.YELLOW);
            }
        }
        
        messageLabel.setText("Target selected! Press SPACE or click SHOOT!");
        messageLabel.setTextFill(Color.LIME);
    }
    
    public void highlightSelectedZone(int index) {
        if (index < 0 || index >= 6) {
            return;
        }
        selectedZone = index; // Lưu lại zone đang được chọn
        for (int i = 0; i < 6; i++) {
            if (i == index) {
                goalZones[i].setFill(Color.rgb(0, 255, 0, 0.4)); // Màu xanh lá cây đậm hơn
                goalZones[i].setStroke(Color.LIME);
                goalZones[i].setOpacity(0.8);
                goalZones[i].getStrokeDashArray().clear(); // Nét liền
            } else {
                resetZoneStyle(i); // Reset các ô khác
            }
        }
        confirmButton.setDisable(false); // Cho phép nhấn Confirm
    }
    
    private void resetZoneStyle(int index) {
        if (index < 0 || index >= 6) {
            return;
        }
        goalZones[index].setFill(Color.TRANSPARENT);
        goalZones[index].setStroke(Color.YELLOW);
        goalZones[index].setOpacity(0.4);
        goalZones[index].getStrokeDashArray().setAll(5d, 5d); // Nét đứt
    }

    public void updateTimer(int seconds) {
        Platform.runLater(() -> {
            if (seconds >= 0) {
                timerLabel.setText("00:" + String.format("%02d", seconds));
            } else {
                timerLabel.setText("Time out"); // Hết giờ hoặc không đếm
            }
        });
    }

    public void updateName(String name) {
        this.playerName = name;
        Platform.runLater(() -> this.playerNameLabel.setText(name));
    }
    public void updateOpponentName(String name) {
        this.opponentName = name;
        Platform.runLater(() -> this.opponentNameLabel.setText(name));
    }
    
    public void playShootAnimation(int shooterZone, int keeperZone, boolean isGoal, Runnable onFinish) {
        Platform.runLater(() -> {
            // --- CẬP NHẬT CHO ballView ---
            // 1. Lấy tâm của zone
            Point targetPoint = getZoneCenter(shooterZone);
            // 2. Tính tọa độ layout mới (tâm ô - nửa kích thước ảnh)
            double targetX = targetPoint.x - (ballView.getFitWidth() / 2);
            double targetY = targetPoint.y - (ballView.getFitHeight() / 2);

            // 3. Lấy tọa độ layout gốc
            double originX = ballView.getLayoutX();
            double originY = ballView.getLayoutY();

            // 4. Tạo TranslateTransition để di chuyển ballView
            TranslateTransition ballAnimation = new TranslateTransition(Duration.millis(500), ballView);
            ballAnimation.setFromX(0); // Luôn reset về 0
            ballAnimation.setFromY(0);
            ballAnimation.setToX(targetX - originX); // Di chuyển đến vị trí mới
            ballAnimation.setToY(targetY - originY);
            // --- KẾT THÚC CẬP NHẬT ---

            // Animation thủ môn (chỉ chạy nếu không phải là bàn thắng)
            if (!isGoal) {
                // Sửa cách gọi: truyền vào zone index
                animateGoalkeeper(keeperZone); 
            }

            ballAnimation.setOnFinished(e -> {
                // Có thể thêm hiệu ứng (lưới rung, bóng bật ra...)
                if (onFinish != null) {
                    // Delay một chút trước khi gọi onFinish để người dùng kịp nhìn kết quả
                    PauseTransition pause = new PauseTransition(Duration.millis(500));
                    pause.setOnFinished(event -> onFinish.run());
                    pause.play();
                }
            });
            ballAnimation.play();
        });
    }
    
    public void playGoalkeeperAnimation(int shooterZone, int keeperZone, boolean isGoal, Runnable onFinish) {
        Platform.runLater(() -> {
            // Thủ môn sẽ bay đến ô keeperZone đã chọn
            animateGoalkeeper(keeperZone); // <-- Sửa cách gọi

            // --- CẬP NHẬT CHO ballView ---
            Point ballTarget = getZoneCenter(shooterZone);
            double targetX = ballTarget.x - (ballView.getFitWidth() / 2);
            double targetY = ballTarget.y - (ballView.getFitHeight() / 2);

            double originX = ballView.getLayoutX();
            double originY = ballView.getLayoutY();

            TranslateTransition ballAnimation = new TranslateTransition(Duration.millis(500), ballView);
            ballAnimation.setFromX(0);
            ballAnimation.setFromY(0);
            ballAnimation.setToX(targetX - originX);
            ballAnimation.setToY(targetY - originY);
            // --- KẾT THÚC CẬP NHẬT ---

            ballAnimation.setOnFinished(e -> {
                // Delay chút rồi gọi onFinish
                PauseTransition pause = new PauseTransition(Duration.millis(500));
                pause.setOnFinished(event -> onFinish.run());
                pause.play();
            });
            ballAnimation.play();

        });
    }
    
    private void animateGoalkeeper(int keeperZone) {
        // 1. Lấy ảnh save tương ứng từ mảng
        Image saveImage = imgKeeperSaves[keeperZone];
        if (saveImage == null) {
            System.err.println("Không tìm thấy ảnh save cho zone " + keeperZone);
            saveImage = imgKeeperIdle; // Dùng tạm ảnh idle nếu bị lỗi
        }

        // 2. Đặt ảnh bay lượn ngay lập tức
        goalkeeperView.setImage(saveImage);

        // 3. Di chuyển ImageView đến tâm của ô
        Point target = getZoneCenter(keeperZone);
        
        // Tính vị trí layout X, Y mới (tâm ô - nửa kích thước ảnh)
        double targetX = target.x - (goalkeeperView.getFitWidth() / 2);
        double targetY = target.y - (goalkeeperView.getFitHeight() / 2);

        // Lấy vị trí layout gốc (vị trí đứng yên ban đầu)
        double originX = 450 - (goalkeeperView.getFitWidth() / 2);
        double originY = 250 - goalkeeperView.getFitHeight();

        // 4. Tạo animation di chuyển (Translate)
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), goalkeeperView);
        tt.setFromX(0); // Luôn bắt đầu từ 0 (vị trí gốc)
        tt.setFromY(0);
        tt.setToX(targetX - originX); // Dịch chuyển tương đối đến vị trí mới
        tt.setToY(targetY - originY);
        tt.setInterpolator(Interpolator.EASE_OUT); // Di chuyển mượt
        tt.play();
    }
    
    public void resetField() {
        // Reset bóng
        if (ballView != null) {
            ballView.setTranslateX(0);
            ballView.setTranslateY(0);
        }

        // Reset thủ môn
        if (goalkeeperView != null) {
            goalkeeperView.setImage(imgKeeperIdle);
            goalkeeperView.setTranslateX(0);
      
        goalkeeperView.setTranslateY(0);
        }

        // Reset trạng thái
        selectedZone = -1;
        confirmButton.setDisable(true);
        inputEnabled = false;
        for (int i = 0; i < 6; i++) {
            resetZoneStyle(i);
            goalZones[i].setMouseTransparent(true);
        }
    }
    
    public void setupControls(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (!inputEnabled) {
                System.out.println("input locked!!!");
                return;
            }
            switch (e.getCode()) {
                case SPACE:
                    if (controller != null) {
                        controller.onConfirmChoice();
                    }
                    break;
                case ESCAPE:
                    Platform.exit();
                    break;
                case DIGIT4: case NUMPAD4:
                    controller.onZoneSelected(3);
                    break;
                case DIGIT5: case NUMPAD5:
                    controller.onZoneSelected(4);
                    break;
                case DIGIT6: case NUMPAD6:
                    controller.onZoneSelected(5);
                    break;
                case DIGIT1: case NUMPAD1:
                    controller.onZoneSelected(0);
                    break;
                case DIGIT2: case NUMPAD2:
                    controller.onZoneSelected(1);
                    break;
                case DIGIT3: case NUMPAD3:
                    controller.onZoneSelected(2);
                    break;

                default:
                    break;
            };
        });
    }
    
    public void enableChoosingZone() {
        inputEnabled = true;
        selectedZone = -1; // Reset lựa chọn
        confirmButton.setDisable(true); // Chưa chọn nên chưa confirm được
         // Reset style các zone (xóa highlight cũ nếu có)
        for (int i = 0; i < 6; i++) {
            resetZoneStyle(i);
            goalZones[i].setMouseTransparent(false);
            System.out.println("enableChoosingZone: zone " + i + " mouseTransparent=" + goalZones[i].isMouseTransparent());
        }
        Platform.runLater(() -> gamePane.requestFocus());
    }

    public void disableInput() {
        inputEnabled = false;
        confirmButton.setDisable(true);
         // Làm mờ các zone hoặc vô hiệu hóa hover/click
         for (Rectangle zone : goalZones) {
             zone.setFill(Color.TRANSPARENT);
             zone.setOpacity(0.2); // Mờ đi
             zone.setMouseTransparent(true); // Không nhận event chuột
         }
    }
    
    public int getSelectedZone(){
        return selectedZone;
    }
    
    public void updateMessage(String message) {
        Platform.runLater(() -> messageLabel.setText(message));
    }
    
    public void updateScore(int myScore, int opponentScore) {
        String m = String.valueOf(myScore);
        Platform.runLater(() -> {
            this.myScore.setText(String.valueOf(myScore));
            this.opponentScore.setText(String.valueOf(opponentScore));
        });
    }
    
    private static class Point {
         int x, y;
         Point(int x, int y) { this.x = x; this.y = y; }
     }
    
    private Point getZoneCenter(int zoneIndex) {
         if (zoneIndex < 0 || zoneIndex >= 6) {
             // Trả về vị trí giữa gôn nếu index không hợp lệ
             return new Point((int)(SCENE_WIDTH / 2), (int)(SCENE_HEIGHT * 0.1 + SCENE_HEIGHT * 0.1));
         }
         double zoneWidth = goalZones[0].getWidth();
         double zoneHeight = goalZones[0].getHeight();
         double zoneX = goalZones[zoneIndex].getX();
         double zoneY = goalZones[zoneIndex].getY();
         return new Point((int)(zoneX + zoneWidth / 2), (int)(zoneY + zoneHeight / 2));
     }
    
    public void showMatchEndMessage(String message, Runnable afterDialogClosed) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Match Over");
            alert.setHeaderText(null);
            alert.setContentText(message);
            // Lấy Stage hiện tại để làm owner
            Stage ownerStage = (Stage) gamePane.getScene().getWindow();
            if (ownerStage != null) {
                alert.initOwner(ownerStage);
            }
            alert.showAndWait(); // Chờ người dùng đóng dialog

            // Thực thi callback sau khi dialog đóng
            if (afterDialogClosed != null) {
                afterDialogClosed.run();
            }
        });
    }
    
    public void showErrorMessage(String message, Runnable afterDialogClosed) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            // Lấy Stage hiện tại để làm owner
            Stage ownerStage = (Stage) gamePane.getScene().getWindow();
            if (ownerStage != null) {
                alert.initOwner(ownerStage);
            }
            alert.showAndWait();

            // Thực thi callback sau khi dialog đóng
            if (afterDialogClosed != null) {
                afterDialogClosed.run();
            }
        });
    }
    public void showErrorMessage(String message) {
        showErrorMessage(message, null); // Gọi hàm mới với callback null
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}