package penaltyclient.controller;

import javafx.scene.Scene; // Cần import Scene
import javafx.stage.Stage;
import penaltyclient.view.MatchView; // Import view

public class MatchController {
    
    private Stage primaryStage;
    private MatchView matchView; // Giao diện trận đấu
    private String myUsername;
    private String opponentName;
    private String myRole; // "SHOOTER" hoặc "GOALKEEPER"
    private boolean isMyTurn = false;
    
    // (private SocketService socketService;)


    public MatchController(Stage stage, String myUsername, String opponentName, String myRole) {
        this.primaryStage = stage;
        this.myUsername = myUsername;
        this.opponentName = opponentName;
        this.myRole = myRole;
        // (this.socketService = SocketService.getInstance();)
    }
    

    public void showMatchView() {
        // 1. Tự tạo View của mình
        this.matchView = new MatchView();
        
        // 2. (Gán các sự kiện cho các nút trong matchView)
        // ví dụ:
        // for(Button zoneButton : matchView.getZoneButtons()) {
        //    zoneButton.setOnAction(e -> onZoneSelected(zoneButton.getUserData()));
        // }
        // matchView.getConfirmButton().setOnAction(e -> onConfirmChoice());

        // 3. Tạo Scene từ View
        Scene matchScene = new Scene(matchView.getRoot(), 1000, 700); // Kích thước ví dụ
        
        // 4. Thiết lập Scene cho Stage
        primaryStage.setScene(matchScene);
        primaryStage.setTitle("Đang thi đấu với " + opponentName);
        
        // 5. Gọi hàm nội bộ để thiết lập UI ban đầu
        handleMatchStart();
    }
    
    private void handleMatchStart() {
        System.out.println("MatchController: Trận đấu bắt đầu. Vai trò: " + myRole);
        
        // Cập nhật các nhãn tên trên giao diện MatchView
        // (Giả sử MatchView có các phương thức này)
        // matchView.setPlayerNames(myUsername, opponentName);
        // matchView.setScore(0, 0);
        // matchView.showStatus("Trận đấu bắt đầu! Chờ lượt đầu tiên...");
        // matchView.disableChoosingZone(); // Khóa các nút chọn
    }
    
    
    
    // (Các hàm Giai đoạn 3: handleTurnStart, onConfirmChoice, handleTurnResult...)
}