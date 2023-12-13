package snake;

import java.util.*;

public class Snake extends Thread{

    class Map{

        public static final char MARKWALL = '■';
        public static final char MARKFOOD = '☼';
        public static final char MARKHEAD = '@';
        public static final char MARKBODY = 'o';

        class Node{

            private int x;
            private int y;
            private char mark;
            private String type;

            public Node(int x, int y, String type) {
                super();
                this.x = x;
                this.y = y;
                this.setType(type);
            }

            private void setType(String type) {
                this.type = type;
                if(type.equals("wall")){
                    mark = MARKWALL;
                } else if (type.equals("food")) {
                    mark = MARKFOOD;
                } else if (type.equals("head")) {
                    mark = MARKHEAD;
                } else if (type.equals("body")) {
                    mark = MARKBODY;
                }
            }

            @Override
            public String toString(){
                return mark + "";
            }

        }

        private Node[][] map;
        private int width;
        private int height;

        public Map(int width, int height) {
            this.width = width;
            this.height = height;

            map = new Node[width][height];
            this.initWall();
        }

        private void initWall() {

            for (int x=1;x<=width;x++){

                map[x-1][0] = new Node(x,1,"wall");//
                map[x-1][height-1] = new Node(x,height,"wall");

            }

            for (int y=2;y<=height-1;y++){
                map[0][y-1] = new Node(1,y,"wall");
                map[width-1][y-1] = new Node(width,y,"wall");
            }

        }

        public void print(){
            //原则，空白地方不创建Node
            String grid = "";
            for (int i=0;i<height;i++){
                for (int j=0;j<width;j++){
                    if (map[i][j]==null){
                        grid+=" ";
                    }else {
                        grid += map[i][j];
                    }
                }
                grid+="\n";
            }
            System.out.println(grid);
        }

        public Node addFood(int x,int y){
            map[x-1][y-1] = new Node(x,y,"food");
            return map[x-1][y-1];
        }

        public Node addHead(int x,int y){
            map[x-1][y-1] = new Node(x,y,"head");
            return map[x-1][y-1];
        }

        public Node addBody(int x,int y){
            map[x-1][y-1] = new Node(x,y,"body");
            return map[x-1][y-1];
        }

        public void ChangeNode(Node n, String type) {
            n.setType(type);
        }

        public void removeNode(Node n) {
            map[n.x-1][n.y-1] = null;
        }

        public Node getNode(int x, int y) {
            return map[x-1][y-1];
        }

    }//Map类结束

    public Map map;
    int mapw;
    int maph;
    int numFood;
    boolean gameover ;

    LinkedList<Map.Node> snake = new LinkedList<>();
    List<Map.Node> foods = new ArrayList<>(0);

    char user = '\u0000';
    int nextx,nexty;


    public Snake(int mapw,int maph,int numFood){
        super();
        this.maph= maph;
        this.mapw = mapw;
        this.numFood = numFood;
    }

    public void init(){

        map = new Map(mapw,maph);
        initSnake();
        addFood(numFood);
    }

    private void addFood(int numFood) {

        int count = 0;
        while(count<numFood){
            int x= 1+new Random().nextInt(mapw);
            int y= 1+new Random().nextInt(maph);

            if (map.getNode(x,y)==null){
                foods.add(map.addFood(x,y));
                count++;
            }
        }
    }


    private void initSnake() {
        snake.add(map.addHead(mapw/2,maph/2));
        snake.add(map.addBody(mapw/2-1,maph/2));
        snake.add(map.addBody(mapw/2-2,maph/2));
        snake.add(map.addBody(mapw/2-3,maph/2));
    }

    private boolean reversed(){

        return nextx == snake.get(1).x && nexty == snake.get(1).y;

    }

    public void moveUp(){

        nextx = snake.getFirst().x;
        nexty = snake.getFirst().y-1;

        if (reversed()){
            moveDown();
        }
        else {
            checkNext();
        }

    }

    public void moveDown(){
        nextx = snake.getFirst().x;
        nexty = snake.getFirst().y+1;

        if (reversed()){
            moveUp();
        }
        else {
            checkNext();
        }

    }
    public void moveRight(){
        nextx = snake.getFirst().x+1;
        nexty = snake.getFirst().y;

        if (reversed()){
            moveLeft();
        }
        else {
            checkNext();
        }

    }
    public void moveLeft(){
        nextx = snake.getFirst().x-1;
        nexty = snake.getFirst().y;

        if (reversed()){
            moveRight();
        }
        else {
            checkNext();
        }

    }

    private void checkNext() {
        Map.Node next = map.getNode(nextx,nexty);
        if (next == null){

            snake.addFirst(map.addHead(nextx,nexty));
            map.ChangeNode(snake.get(1),"body");
            map.removeNode(snake.getLast());
            snake.removeLast();

        }else if (next.type=="food"){

            map.ChangeNode(next,"head");
            map.ChangeNode(snake.getFirst(),"body");
            snake.addFirst(next);
            foods.remove(next);

            addFood(1);

        } else if (next.type == "wall"||next.type=="body") {
            System.out.println("游戏结束");
            gameover = true;
        }
    }

    @Override
    public void run(){
        while (!gameover){
            switch (user){
                case 'd': moveDown();break;//右
                case 'a': moveUp();break;//左
                case 's': moveLeft();break;//往下
                case 'w': moveRight();break;//上
            }

            map.print();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startGame(){
        this.init();
        this.start();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()){

            String s = sc.nextLine().trim().toLowerCase();
            if (s.length()>0){
                if ("wasd".indexOf(s.charAt(0)) > -1){
                    user = s.charAt(0);
                }
            }
        }

    }

}
