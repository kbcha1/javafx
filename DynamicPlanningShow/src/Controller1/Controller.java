package Controller1;

import DyanmicPlanningShow1.DynamicPrograming;
import DyanmicPlanningShow1.Run;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private MenuItem mi_Struct;

    @FXML
    private MenuItem mi_Simple;

    @FXML
    private ScrollPane sp_Result;


    @FXML
    void NewStruct(ActionEvent event) {

        try {
            Stage stageNew = new Stage();
            FXMLLoader fxmlNew = new FXMLLoader();
            fxmlNew.setLocation(Run.class.getResource("/Fxml/NewF.fxml"));
            Scene scNew = new Scene(fxmlNew.load());
            stageNew.setTitle("一维节点问题设置");
            stageNew.setScene(scNew);
            stageNew.show();

        }catch (Exception exception){
            exception.printStackTrace();
        }






    }

    @FXML
    void simple(ActionEvent event) {
        DynamicPrograming.main();
    }


}
