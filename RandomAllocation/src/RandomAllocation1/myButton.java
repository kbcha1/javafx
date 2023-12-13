package RandomAllocation1;

import javafx.scene.control.Button;
import javafx.scene.text.Font;


public class myButton extends Button {
    public myButton(String text){
        super(text);
        setStyle(
                "-fx-background-color: linear-gradient(to right,#03a9f4,#f441a5,#ffeb3b,#03a9f4); -fx-text-fill: #ffffff; -fx-border-color: #ffffff; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 15; -fx-border-width: 2;"
                        +" -fx-background-size: 400%;"
                );
        setMinWidth(100);
    }
    public myButton(){
        setStyle(
                "-fx-background-color: linear-gradient(to right,#03a9f4,#f441a5,#ffeb3b,#03a9f4); -fx-text-fill: #ffffff; -fx-border-color: #ffffff; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 15; -fx-border-width: 2;"
                +" -fx-background-size: 400%;"
        );
        setMinWidth(100);
    }
}
