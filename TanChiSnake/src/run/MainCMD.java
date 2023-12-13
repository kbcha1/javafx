package run;

import snake.Snake;

public class MainCMD {
    public static void main(String[] args) {

        Snake snake = new Snake(30,30,5);
        snake.startGame();

    }
}
