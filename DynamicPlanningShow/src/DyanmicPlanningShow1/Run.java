package DyanmicPlanningShow1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Run extends javafx.application.Application{

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        main_Scene();
    }

    public static void main_Scene(){
        try {
            FXMLLoader fxml = new FXMLLoader();
            fxml.setLocation(Run.class.getResource("/Fxml/MainF.fxml"));
            Scene sc = new Scene(fxml.load());
            primaryStage.setTitle("动态规划可视化");
            primaryStage.setScene(sc);
            primaryStage.show();

        }catch (IOException exception){
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
