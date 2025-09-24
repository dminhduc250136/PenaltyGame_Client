package penaltyclient.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MatchView1 extends Application {

    private Label timerLabel;
    private int timeLeft = 10;
    private Timeline countdown;

    private Circle[] scoreP1 = new Circle[5];
    private Circle[] scoreP2 = new Circle[5];

    private Button[][] choiceButtons = new Button[2][3];

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // ===== Timer at top =====
        timerLabel = new Label("10");
        timerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        HBox timerBox = new HBox(timerLabel);
        timerBox.setAlignment(Pos.CENTER);

        // ===== Score panel at top-left =====
        VBox scorePanel = new VBox(10);
        scorePanel.setAlignment(Pos.TOP_LEFT);

        HBox p1Row = new HBox(5);
        Label p1Label = new Label("Player 1");
        p1Row.getChildren().add(p1Label);
        for (int i = 0; i < 5; i++) {
            scoreP1[i] = createScoreDot();
            p1Row.getChildren().add(scoreP1[i]);
        }

        HBox p2Row = new HBox(5);
        Label p2Label = new Label("Player 2");
        p2Row.getChildren().add(p2Label);
        for (int i = 0; i < 5; i++) {
            scoreP2[i] = createScoreDot();
            p2Row.getChildren().add(scoreP2[i]);
        }

        scorePanel.getChildren().addAll(p1Row, p2Row);

        // Top wrapper
        BorderPane topPanel = new BorderPane();
        topPanel.setCenter(timerBox);
        topPanel.setLeft(scorePanel);
        root.setTop(topPanel);

        // ===== Center panel: 6 buttons grid =====
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                Button btn = new Button((r * 3 + c + 1) + "");
                btn.setPrefSize(80, 80);
                choiceButtons[r][c] = btn;
                grid.add(btn, c, r);
            }
        }

        root.setCenter(grid);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Penalty Game");
        stage.show();

        startCountdown();

        // Demo test: đổi màu kết quả
        Timeline demo = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> setScore(1, 0, true)),
                new KeyFrame(Duration.seconds(4), e -> setScore(2, 0, false))
        );
        demo.play();
    }

    private Circle createScoreDot() {
        Circle circle = new Circle(8);
        circle.setFill(Color.GRAY);
        return circle;
    }

    // ===== Countdown logic =====
    public void startCountdown() {
        timeLeft = 10;
        timerLabel.setText("10");
        if (countdown != null) countdown.stop();

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 0) {
                countdown.stop();
                // TODO: random chọn nếu chưa click
            }
        }));
        countdown.setCycleCount(10);
        countdown.play();
    }

    public void stopCountdown() {
        if (countdown != null) countdown.stop();
    }

    // ===== Score update =====
    public void setScore(int player, int turn, boolean goal) {
        Circle dot = (player == 1) ? scoreP1[turn] : scoreP2[turn];
        dot.setFill(goal ? Color.GREEN : Color.RED);
    }

    public Button[][] getChoiceButtons() {
        return choiceButtons;
    }
    
        // Hiển thị vai trò hiện tại
    public void updateRole(String text) {
        timerLabel.setText(text); // tạm reuse chỗ timer cho demo
    }

    // Cập nhật đồng hồ từ server
    public void updateTimer(int t) {
        timerLabel.setText(String.valueOf(t));
    }

    // Khóa các nút khi đã chọn
    public void disableButtons() {
        for (Button[] row : choiceButtons)
            for (Button b : row) b.setDisable(true);
    }

    // Reset trạng thái nút để chơi lượt mới
    public void resetButtons() {
        for (Button[] row : choiceButtons)
            for (Button b : row) b.setDisable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}
