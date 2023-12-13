package PictureLook;

import javafx.scene.control.Button;

public class MyButton extends Button {
    public MyButton(String text){
        super(text);
       setStyle("-fx-background-color: transparent;");
       setDisable(true);
    }
    public MyButton(){
        setStyle("-fx-background-color: transparent;");
        setDisable(true);

    }

}
