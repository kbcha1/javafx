package ReciteWords1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends javafx.application.Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        main_Scene();
    }

    public static void main_Scene(){
        try {
            FXMLLoader fxml = new FXMLLoader();
            fxml.setLocation(Main.class.getResource("/Fxml/myFxml.fxml")); //获取布局文件fxml的内容
            Scene sc = new Scene(fxml.load());

            Controller controller =fxml.getController();


            controller.keyBord(sc);

            primaryStage.setTitle("冇道背单词");
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