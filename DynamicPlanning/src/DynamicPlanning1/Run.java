package DynamicPlanning1;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Run extends javafx.application.Application {

    class CNode extends Circle {

        int score;
        int status;
        Text tx_num = new Text();
        Text tx_score = new Text("" + score);
        Line line = new Line();

        public CNode(int number, int status, double centerX, double centerY, double radius, int score, Color blue) {
            super(centerX, centerY, radius);
            this.score = score;
            this.setId("" + number);
            this.setFill(blue);
        }

        public CNode(double centerX, double centerY, double radius) {
            this.setCenterX(centerX);
            this.setCenterY(centerY);
            this.setRadius(radius);
        }


    }

    class WardLine extends Line {

        int startNum;
        int endNum;
        int score;
        int status;
        Text tx_ward = new Text();

        public WardLine(int startNum, int endNum, int score) {
            this.startNum = startNum;
            this.endNum = endNum;
            this.score = score;
            tx_ward.setText("" + score);
            this.setStrokeWidth(3);
            tx_ward.setStyle("-fx-font-size: 20");
            this.status = 0;
        }

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public static int RADIUS = 30;
    public static int width = 400;
    public static int height = 400;

    private int num;//节点总数
    private int N;//状态
    private int T;//时间
    private int prob_type;//问题类型，根据不同的问题有不同的对应方案
    private int source = 1;//起始节点
    private int target = 1;//目标节点
    private int stepLine = 0;//起始路线索引
    private String optType = "max";//目标约束


    Circle thief = new Circle();

    ArrayList<CNode> poolCNode = new ArrayList<>();//存放节点
    ArrayList<WardLine> poolWardLine = new ArrayList<>();//网络图连线

    Pane map = new Pane();

    Spinner<Integer> spine_creat_num = new Spinner<>(1, 30, 1);//一、二维节点数量微调

    TextArea ta_setScore = new TextArea("1");//节点得分区域

    TextArea ta_source = new TextArea("" + source);
    TextArea ta_target = new TextArea("" + target);
    TextArea ta_result = new TextArea() ;

    Stage st_result = new Stage();


    DynamicPrograming dp = new DynamicPrograming(N, T, optType);

    private String string_score;//节点得分以及连通性文本

    ArrayList<Integer> roadCNode = new ArrayList<>();//路线节点
    ArrayList<Path> pathLine = new ArrayList<>();//路线线条

    ArrayList<Integer> score_prob1 = new ArrayList<>();//一维问题节点分数
    ArrayList<List<Integer>> score_prob2 = new ArrayList<>();//二维问题节点分数
    ArrayList<List<Integer>> score_prob3 = new ArrayList<>();//网络问题节点分数

    ArrayList<List<Boolean>> connect_prob1 = new ArrayList<>();//一维问题节点连接性
    ArrayList<List<Boolean>> connect_prob2 = new ArrayList<>();//二维问题节点连接性
    ArrayList<List<Boolean>> connect_prob3 = new ArrayList<>();//网络问题节点连接性

    @Override
    public void start(Stage primaryStage) throws Exception {

        //主界面----------------------------------------------

        map.getChildren().addAll(poolCNode);
        map.setStyle("-fx-border-color: black");

        MenuItem mi_creat = new MenuItem("创建问题");
        MenuItem mi_save = new MenuItem("保存问题");
        MenuItem mi_open = new MenuItem("打开问题");
        MenuItem mi_change= new MenuItem("修改问题");

        Menu mu_prob = new Menu("问题");
        Menu mu_set = new Menu("主题");
        Menu mu_regard = new Menu("关于");

        mu_prob.getItems().addAll(mi_creat,mi_change,mi_save, mi_open);

        MenuBar mb_main = new MenuBar();

        mb_main.getMenus().addAll(mu_prob,mu_set,mu_regard);

        Button bt_main_run = new Button("运行");
        Button bt_main_pro = new Button("上一步");
        Button bt_main_next = new Button("下一步");
        Button bt_main_clear = new Button("清除");

        FlowPane fp_main = new FlowPane();

        fp_main.getChildren().addAll(bt_main_run, bt_main_pro, bt_main_next, bt_main_clear);
        fp_main.setHgap(RADIUS);


        BorderPane bp_main = new BorderPane();
        bp_main.setPadding(new Insets(RADIUS / 2.0));

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents"));
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All types(*.*)", "*.*"),
                new FileChooser.ExtensionFilter("DynamicPrograming File(*.dp)", "*.dp")
        );

        bp_main.setTop(mb_main);
        bp_main.setCenter(map);
        bp_main.setBottom(fp_main);


        Scene sc_main = new Scene(bp_main, 1000, 700);

        primaryStage.setScene(sc_main);
        primaryStage.show();

        //创建问题设置界面----------------------------------------------
        ToggleGroup tgProb = new ToggleGroup();

        RadioButton rb_creat_prob1 = new RadioButton("一维节点选择问题");
        RadioButton rb_creat_prob2 = new RadioButton("二维节点选择问题");
        RadioButton rb_creat_prob3 = new RadioButton("网状节点选择问题");
        rb_creat_prob1.setToggleGroup(tgProb);
        rb_creat_prob2.setToggleGroup(tgProb);
        rb_creat_prob3.setToggleGroup(tgProb);
        rb_creat_prob1.setSelected(true);

        Label lb_creat1 = new Label("问题设置");
        Label lb_creat2 = new Label("目标设置");
        Label lb_creat3 = new Label("节点设置");
        Label lb_creat4 = new Label("规则设置");
        Label lb_creat5 = new Label("起始节点");
        Label lb_creat6 = new Label("目标节点");

        lb_creat5.visibleProperty().bind(rb_creat_prob3.selectedProperty());
        lb_creat6.visibleProperty().bind(rb_creat_prob3.selectedProperty());

        ta_source.visibleProperty().bind(rb_creat_prob3.selectedProperty());
        ta_target.visibleProperty().bind(rb_creat_prob3.selectedProperty());


        ta_source.setMaxSize(25, 25);
        ta_target.setMaxSize(25, 25);

        Button bt_creat_enter = new Button("确定");
        Button bt_creat_setScore = new Button("设置节点分数");

        ToggleGroup tg2 = new ToggleGroup();


        RadioButton rb_creat_max = new RadioButton("总和最大");
        RadioButton rb_creat_min = new RadioButton("总和最小");
        rb_creat_max.setToggleGroup(tg2);
        rb_creat_min.setToggleGroup(tg2);
        rb_creat_max.setSelected(true);

        GridPane gp_creat = new GridPane();

        gp_creat.add(lb_creat1, 0, 0);
        gp_creat.add(rb_creat_prob1, 1, 1);
        gp_creat.add(rb_creat_prob2, 2, 1);
        gp_creat.add(rb_creat_prob3, 3, 1);

        gp_creat.add(lb_creat2, 0, 2);
        gp_creat.add(rb_creat_max, 1, 3);
        gp_creat.add(rb_creat_min, 2, 3);

        gp_creat.add(lb_creat3, 0, 4);
        gp_creat.add(spine_creat_num, 1, 5);

        gp_creat.add(lb_creat4, 0, 6);
        gp_creat.add(bt_creat_setScore, 1, 7);
        gp_creat.add(lb_creat5, 2, 7);
        gp_creat.add(ta_source, 3, 7);
        gp_creat.add(lb_creat6, 4, 7);
        gp_creat.add(ta_target, 5, 7);

        gp_creat.add(bt_creat_enter, 0, 8);

        gp_creat.setHgap(10);
        gp_creat.setVgap(10);

        Scene sc_creat = new Scene(gp_creat, 800, 400);

        Stage st_creat = new Stage();

        st_creat.setScene(sc_creat);

        //设置分数界面----------------------------------------------
        Button bt_setScore_enter = new Button("确定");

        ToggleGroup tg_setScore = new ToggleGroup();

        RadioButton rb_setScore_noReturn = new RadioButton("单连通");
        RadioButton rb_setScore_return = new RadioButton("双连通");

        rb_setScore_return.setToggleGroup(tg_setScore);
        rb_setScore_noReturn.setToggleGroup(tg_setScore);
        rb_setScore_return.setSelected(true);
        rb_setScore_return.visibleProperty().bind(rb_creat_prob3.selectedProperty());
        rb_setScore_noReturn.visibleProperty().bind(rb_creat_prob3.selectedProperty());

        HBox hb_setScore = new HBox(bt_setScore_enter, rb_setScore_return, rb_setScore_noReturn);
        hb_setScore.setAlignment(Pos.CENTER);
        hb_setScore.setSpacing(RADIUS);


        VBox vb_setScore = new VBox(ta_setScore, hb_setScore);

        Scene sc_setScore = new Scene(vb_setScore, 400, 280);

        Stage st_setScore = new Stage();

        st_setScore.setScene(sc_setScore);


        Scene sc_result = new Scene(ta_result,100,100);
        st_result.setScene(sc_result);

        //按钮监听设置----------------------------------------------

        bt_main_run.setOnAction(e -> runTheCompute());

        mi_open.setOnAction(e -> {
            File file = fc.showOpenDialog(map.getScene().getWindow());
        });

        bt_main_clear.setOnAction(e -> clearAll());//清除节点

        mi_creat.setOnAction(e -> st_creat.show());//打开创建问题界面

        mi_change.setOnAction(e->st_creat.show());

        bt_creat_enter.setOnAction(e -> {

            clearAll();//清除之前的信息

            //获取信息
            this.num = spine_creat_num.getValue();//节点数
            this.T = num;//步长
            this.string_score = ta_setScore.getText();//分数设置

            //目标约束
            if (rb_creat_max.isSelected())
                this.optType = "max";
            else
                this.optType = "min";

            //问题类型
            if (rb_creat_prob1.isSelected()) {
                this.prob_type = 1;
                getInfo1();
            }

            //问题二
            else if (rb_creat_prob2.isSelected()) {

                this.prob_type = 2;
                getInfo2();

            } else {//问题三
                this.prob_type = 3;
                getInfo3();
            }

            if (prob_type == 1) {
                compute_prob1();

            } else if (prob_type == 2) {
                compute_prob2();

            } else
                compute_prob3();
            st_creat.close();
            getRoad();
        });//获取问题信息，计算结果，结束创建问题

        bt_creat_setScore.setOnAction(e -> st_setScore.show());//打开设置分数界面

        bt_setScore_enter.setOnAction(e -> {
            st_setScore.close();
        });//关闭设置分数界面

        bt_main_pro.setOnAction(e -> proStep());//上一步

        bt_main_next.setOnAction(e -> nextStep());//下一步

    }

    public void clearAll() {
        this.T = 1;

        this.stepLine=0;
        this.poolCNode.clear();
        this.poolWardLine.clear();
        this.pathLine.clear();
        this.prob_type = 1;
        this.roadCNode.clear();
        this.num = 1;
        this.score_prob1.clear();
        this.score_prob2.clear();
        this.score_prob3.clear();
        this.connect_prob1.clear();
        this.connect_prob2.clear();
        this.connect_prob3.clear();
        this.string_score = "";
        this.target = 1;
        this.source = 1;
        map.getChildren().clear();
    }

    public void getInfo1() {
        T = num;
        N = num;
        String[] temp = string_score.split("\\s");//获取得分

        for (int i = 0; i < N; i++) {
            score_prob1.add(Integer.parseInt(temp[i]));
        }//设置得分

        for (int i = 0; i < N; i++) {              //获取一般情况下的连通性
            List<Boolean> list = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                if (j > i + 1 || j < i - 1)
                    list.add(true);
                else
                    list.add(false);
            }
            connect_prob1.add(list);
        }//设置转移矩阵A

    }

    public void getInfo2() {

        N = num;//状态数量
        T = N;
        //根据状态数设置节点数量
        int k = 0;

        for (int i = 0; i < N; i++) {
            k += i + 1;
        }
        num = k;
        //设置得分
        String[] temp1 = string_score.split("\n");
        for (String temp2 : temp1) {
            List<Integer> list = new ArrayList<>();
            String str[] = temp2.split("\\s");
            int size_str = str.length;
            int i = 0;
            for (; i < size_str; i++) {
                list.add(Integer.parseInt(str[i]));
            }
            for (int j = i; j < N; j++) {
                list.add(0);
            }
            score_prob2.add(list);
        }

        //设置转移矩阵
        for (int i = 0; i < N; i++) {
            List<Boolean> list = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                if (i == j) {
                    list.add(true);
                    if (i < N - 1) {
                        list.add(true);
                    }
                } else {
                    list.add(false);
                }
            }
            connect_prob2.add(list);
        }

    }

    public void getInfo3() {

        N = num;
        T = 1000 * N;

        //获取起点与终点
        source = Integer.parseInt(ta_source.getText());
        target = Integer.parseInt(ta_target.getText());


        //初始化数组
        for (int i = 0; i < N; i++) {

            List<Boolean> list_A = new ArrayList<>();
            List<Integer> list_B = new ArrayList<>();

            for (int j = 0; j < N; j++) {
                list_A.add(false);
                list_B.add(0);
            }

            connect_prob3.add(list_A);
            score_prob3.add(list_B);
        }

        //设置得分
        String[] temp1 = string_score.split("\n");

        //设置得分与转移矩阵
        for (String temp2 : temp1) {

            String str[] = temp2.split("\\s");
            connect_prob3.get(Integer.parseInt(str[0]) - 1).set(Integer.parseInt(str[1]) - 1, true);
            connect_prob3.get(Integer.parseInt(str[1]) - 1).set(Integer.parseInt(str[0]) - 1, true);

            score_prob3.get(Integer.parseInt(str[0]) - 1).set(Integer.parseInt(str[1]) - 1, Integer.parseInt(str[2]));
            score_prob3.get(Integer.parseInt(str[1]) - 1).set(Integer.parseInt(str[0]) - 1, Integer.parseInt(str[2]));

        }

    }

    public void compute_prob1() {

        int[] input = score_prob1.stream().mapToInt(Integer::valueOf).toArray();//获取得分

        boolean A[][] = new boolean[N][N];

        for (int i = 0; i < N; i++) {//按要求输入A
            for (int j = 0; j < N; j++) {
                A[i][j] = connect_prob1.get(i).get(j);
            }
        }

        dp = new DynamicPrograming(N, T, optType);

        dp.setA(A);

        dp.setB(input, "end");

        List<Integer>[] rejectedNodes = new ArrayList[N + 1];

        for (int i = 1; i <= N; i++) {
            //这里恰好与A相同
            rejectedNodes[i] = new ArrayList<Integer>();
            rejectedNodes[i].add(i - 1);
            rejectedNodes[i].add(i);
            rejectedNodes[i].add(i + 1);
        }

        dp.train(new CheckPathTypeRejectNodes(rejectedNodes));

        dp.evaluate(null);

        dp.output();

        addTheCNode1(input);

    }

    public void compute_prob2() {

        int input[][] = new int[N][N];

        boolean A[][] = new boolean[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = connect_prob2.get(i).get(j);
                input[i][j] = score_prob2.get(i).get(j);
            }
        }

        dp = new DynamicPrograming(N, T, optType);

        dp.setA(A);

        dp.setB(input, "step", "end");

        dp.setPi(new int[]{1});

        dp.train(null);

        dp.evaluate(null);

        dp.output();

        addTheCNode2(input);

    }

    public void compute_prob3() {

        int[][] input = new int[N][N];
        boolean A[][] = new boolean[N][N];


        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                input[i][j] = score_prob3.get(i).get(j);
                A[i][j] = connect_prob3.get(i).get(j);
            }
        }

        dp = new DynamicPrograming(N, T, optType);

        dp.setA(A);

        dp.setB(input, "start", "end");

        dp.setPi(new int[]{source});
        dp.setTarget(target);

        dp.train(new CheckPathTypeShortestPath());

        dp.evaluate(new EvaluatorTypeShortestPath());

        dp.output();

        addTheCNode3();

    }

    public void save_date(){

        Properties prop = new Properties();

        prop.setProperty("运算类型",optType);
        prop.setProperty("问题类型",""+prob_type);
//        prop.store(new FileOutputStream(System.getProperty("user.home") + "/Documents"+"\""),"");
    }

    //添加节点到 map
    private void addTheCNode1(int[] input) {

        double boundx = map.getBoundsInLocal().getMinX();
        double boundy = map.getBoundsInLocal().getMinY();

        double spacex = 0.1 * map.getWidth();
        double spacey = 0.1 * map.getHeight();

        double x = boundx;
        double y = boundy + 4 * spacey;

        for (int i = 1; i <= num; i++) {

            if (x > 0.8 * map.getWidth()) {
                x = .0;
                y += spacey;
            }

            x += spacex;

            //设置样式
            CNode cNode = new CNode(i, 0, x, y, RADIUS, input[i - 1], null);
            cNode.setStroke(Color.BLACK);
            cNode.setStrokeWidth(RADIUS / 4);

            map.getChildren().addAll(cNode.tx_num, cNode.tx_score);

            cNode.tx_num.setText("" + i);
            cNode.tx_num.xProperty().bind(cNode.centerXProperty());
            cNode.tx_num.yProperty().bind(cNode.centerYProperty());

            cNode.tx_score.setText("" + cNode.score);
            cNode.tx_score.xProperty().bind(cNode.centerXProperty());
            cNode.tx_score.yProperty().bind(cNode.centerYProperty().add(-1.5 * RADIUS).divide(1));

            cNode.setOnMousePressed(this::onMousePressed);
            cNode.setOnMouseDragged(this::onMouseDragged);
            poolCNode.add(cNode);

        }

        map.getChildren().addAll(poolCNode);

    }

    private void addTheCNode2(int[][] input) {

        double boundy = map.getBoundsInLocal().getMinY();//获取y

        double widthx = map.getWidth();
        double heighty = map.getHeight();

        double x;
        double y;

        //开始排列
        for (int i = 0, k = 1; i < N; i++) {

            x = widthx / 3 - 2 * i * RADIUS;

            for (int j = 0; j < N; j++) {
                if (j <= i) {
                    k++;
                    //设置x,y
                    x += 4 * RADIUS;
                    y = boundy + heighty * .2 + RADIUS * i * 2;

                    //设置样式
                    CNode cNode = new CNode(k-1, 0, x, y, RADIUS, input[i][j], null);
                    cNode.setStroke(Color.BLACK);
                    cNode.setStrokeWidth(RADIUS / 4);
                    map.getChildren().addAll(cNode.tx_num, cNode.tx_score);

                    cNode.tx_num.setText("" + (k-1));
                    cNode.tx_num.xProperty().bind(cNode.centerXProperty());
                    cNode.tx_num.yProperty().bind(cNode.centerYProperty());

                    cNode.tx_score.setText("" + cNode.score);
                    cNode.tx_score.xProperty().bind(cNode.centerXProperty());
                    cNode.tx_score.yProperty().bind(cNode.centerYProperty().add(-1.5 * RADIUS).divide(1));

                    cNode.setOnMousePressed(this::onMousePressed);
                    cNode.setOnMouseDragged(this::onMouseDragged);
                    poolCNode.add(cNode);
                }

            }

        }
        map.getChildren().addAll(poolCNode);

    }

    private void addTheCNode3() {

        double boundy = map.getBoundsInLocal().getMinY();//获取y

        double widthx = map.getWidth();
        double heighty = map.getHeight();

        double x;
        double y;

        for (int i = 0; i < N; i++) {//随机添加节点

            x = RADIUS + (widthx - 4 * RADIUS) * Math.random();
            //设置x,y
            y = boundy + heighty * .2 + RADIUS * i * 3;
            //设置样式
            CNode cNode = new CNode(i + 1, 0, x, y, RADIUS, 0, null);

            cNode.setStroke(Color.BLACK);
            cNode.setStrokeWidth(RADIUS / 4);
            map.getChildren().addAll(cNode.tx_num);

            cNode.tx_score.setVisible(false);

            cNode.tx_num.setText(cNode.getId());
            cNode.tx_num.xProperty().bind(cNode.centerXProperty());
            cNode.tx_num.yProperty().bind(cNode.centerYProperty());


            cNode.setOnMousePressed(this::onMousePressed);
            cNode.setOnMouseDragged(this::onMouseDragged);
            poolCNode.add(cNode);
        }

        map.getChildren().addAll(poolCNode);

        //添加连线
        String[] temp1 = string_score.split("\n");
        //设置得分与转移矩阵
        for (String temp2 : temp1) {
            String str[] = temp2.split("\\s");

            int i = Integer.parseInt(str[0]) - 1;
            int j = Integer.parseInt(str[1]) - 1;

            WardLine wardLine = new WardLine(i, j, Integer.parseInt(str[2]));

            wardLine.startXProperty().bind(poolCNode.get(i).centerXProperty());
            wardLine.startYProperty().bind(poolCNode.get(i).centerYProperty());

            wardLine.endXProperty().bind(poolCNode.get(j).centerXProperty());
            wardLine.endYProperty().bind(poolCNode.get(j).centerYProperty());

            wardLine.tx_ward.xProperty().bind(wardLine.startXProperty().add(wardLine.endXProperty()).divide(2));
            wardLine.tx_ward.yProperty().bind(wardLine.startYProperty().add(wardLine.endYProperty()).divide(2));

            poolWardLine.add(wardLine);
            map.getChildren().add(wardLine.tx_ward);
        }

        map.getChildren().addAll(poolWardLine);
    }

    //获取路径
    public void getRoad() {

        //清理路尽、清理路线
        roadCNode.clear();
        pathLine.clear();
        map.getChildren().remove(thief);

        if (prob_type != 3){
            poolWardLine.clear();
        }

        //获取路径
        for (String str : dp.getPath()[dp.getBestT()][dp.getBestN()].split("/")) {
            if (prob_type == 1 || prob_type == 3)
                roadCNode.add(Integer.parseInt(str) - 1);
            else
                roadCNode.add(Integer.parseInt(str));
        }
        if (prob_type == 2) {
            for (int i = 0; i < N; i++) {
                int j = roadCNode.get(i);
                int sum = 0;
                for (int k = 0; k <= i; k++) {
                    sum += k;
                }
                roadCNode.set(i, sum + j - 1);
            }
        }

        if (prob_type == 1 || prob_type ==2){
            Collections.sort(roadCNode);
        }
        System.out.println(roadCNode);
        //起始化小偷
        thief.setRadius(RADIUS / 2.0);


        //获取动画播放路线路线以及连线
        if (prob_type == 1 ){
            thief.setCenterX(RADIUS);
            thief.setCenterY(RADIUS);
            //问题1添加所有线路
            for(int i =-1;i<N-1;i++){
                Path path = new Path();
                if (i ==-1){
                    path.getElements().add(new MoveTo(thief.getCenterX(),thief.getCenterY()));
                    System.out.println("头到第1个节点");
                }
                else {
                    path.getElements().add(new MoveTo(poolCNode.get(i).getCenterX(),poolCNode.get(i).getCenterY()));
                    System.out.println("第"+(i+1)+"个节点到第"+(i+2)+"个节点");
                }
                path.getElements().add(new LineTo(poolCNode.get(i+1).getCenterX(),poolCNode.get(i+1).getCenterY()));
                pathLine.add(path);
            }

        }
        else if (prob_type == 2){
            thief.setCenterX(poolCNode.get(roadCNode.get(0)).getCenterX());
            thief.setCenterY(poolCNode.get(roadCNode.get(0)).getCenterY());
            //问题二添加关键线路
            for(int i = 0 ;i<roadCNode.size()-1;i++){

                int x = roadCNode.get(i);
                int y = roadCNode.get(i+1);

                Path path = new Path();
                path.getElements().add(new MoveTo(poolCNode.get(x).getCenterX(),poolCNode.get(x).getCenterY()));
                path.getElements().add(new LineTo(poolCNode.get(y).getCenterX(),poolCNode.get(y).getCenterY()));

                WardLine wardLine = new WardLine(x, y,0);
                wardLine.setVisible(false);

                wardLine.startXProperty().bind(poolCNode.get(x).centerXProperty());
                wardLine.startYProperty().bind(poolCNode.get(x).centerYProperty());

                wardLine.endXProperty().bind(poolCNode.get(y).centerXProperty());
                wardLine.endYProperty().bind(poolCNode.get(y).centerYProperty());

                pathLine.add(path);
                poolWardLine.add(wardLine);
            }
            map.getChildren().addAll(poolWardLine);
        }
        else if (prob_type == 3){

            thief.setCenterX(roadCNode.get(this.source - 1));
            thief.setCenterY(roadCNode.get(this.source - 1));

            //问题二添加关键线路
            for(int i =0;i<roadCNode.size()-1;i++){
                int x = roadCNode.get(i);
                int y = roadCNode.get(i+1);
                Path path = new Path();
                path.getElements().add(new MoveTo(poolCNode.get(x).getCenterX(),poolCNode.get(x).getCenterY()));
                path.getElements().add(new LineTo(poolCNode.get(y).getCenterX(),poolCNode.get(y).getCenterY()));
                pathLine.add(path);
            }
        }
        map.getChildren().add(thief);
    }

    //运行结果
    public void runTheCompute() {

        getRoad();

        stepLine = 0;
        //播放动画
        if (prob_type == 1){
            thief.setCenterX(RADIUS);
            thief.setCenterY(RADIUS);
        }else if (prob_type == 2){
            thief.setCenterX(poolCNode.get(roadCNode.get(0)).getCenterX());
            thief.setCenterY(poolCNode.get(roadCNode.get(0)).getCenterY());
        }else {
            thief.setCenterX(roadCNode.get(this.source - 1));
            thief.setCenterY(roadCNode.get(this.source - 1));
        }
        playNextPathTransition(thief,stepLine);

        String st = "最佳路径：";
        for (int i : roadCNode){
                st+=" "+(i+1);
        }
        ta_result.setText(""+st);
        st_result.show();


    }

    //上一步
    public void proStep() {
        stepLine--;
        thief.setCenterY(poolCNode.get(roadCNode.get(stepLine+1)).getCenterY());
        thief.setCenterX(poolCNode.get(roadCNode.get(stepLine+1)).getCenterX());

    }

    //下一步
    public void nextStep() {
                PathTransition pt = new PathTransition();
                pt.setNode(thief);
                pt.setDuration(Duration.seconds(2));
                Path path = pathLine.get(stepLine);
                pt.setPath(path);
                pt.play();
                stepLine++;
                pt.setOnFinished(e->{
                    //每个问题区分对待
                    if (prob_type == 1){
                        //节点变色、抖动
                        if (roadCNode.contains(stepLine-1)){
                            System.out.println("开始判断是否为路径节点"+stepLine);
                            shakeHouse(poolCNode.get(stepLine-1));
                        }
                    }else if (prob_type == 2 ){
                        //节点变色、添加线条
                        poolCNode.get(roadCNode.get(stepLine-1)).setStroke(Color.RED);
                        poolWardLine.get(stepLine-1).setVisible(true);
                        poolWardLine.get(stepLine-1).setStroke(Color.RED);
                    }else
                    {//节点变色，线条也变色
                        poolCNode.get(roadCNode.get(stepLine-1)).setStroke(Color.RED);
                        poolWardLine.forEach(pl->{
                            if (pl.startNum == roadCNode.get(stepLine-1)&&pl.endNum == roadCNode.get(stepLine)){
                                pl.setStroke(Color.RED);
                            }
                        });
                    }
                });

        }



    //播放整个
    public void playNextPathTransition(Circle cNode,int stepLine) {

        System.out.println("这是第"+(stepLine+1)+"个动画开始");
        if (stepLine < pathLine.size()) {
            PathTransition pt = new PathTransition();
            pt.setNode(cNode);
            pt.setDuration(Duration.seconds(2));
            Path path = pathLine.get(stepLine);
            pt.setPath(path);
            pt.play();

            pt.setOnFinished(event -> {
                //每个问题区分对待
                if (prob_type == 1){
                    //节点变色、抖动
                    if (roadCNode.contains(stepLine)){
                        System.out.println("开始判断是否为路径节点"+stepLine);
                        shakeHouse(poolCNode.get(stepLine));
                    }
                }else if (prob_type == 2 ){
                    //节点变色、添加线条
                    poolCNode.get(roadCNode.get(stepLine)).setStroke(Color.RED);
                    poolWardLine.get(stepLine).setVisible(true);
                    poolWardLine.get(stepLine).setStroke(Color.RED);
                }else
                {//节点变色，线条也变色
                    poolCNode.get(roadCNode.get(stepLine)).setStroke(Color.RED);
                    poolWardLine.forEach(pl->{
                        if (pl.startNum == roadCNode.get(stepLine)&&pl.endNum == roadCNode.get(stepLine+1)){
                            pl.setStroke(Color.RED);
                        }
                    });
                }
                playNextPathTransition(cNode,stepLine+1);
            });
        }
        if (prob_type == 2|| prob_type== 3){
            poolCNode.get(roadCNode.get(stepLine)).setStroke(Color.RED);

        }
    }

    public void shakeHouse(CNode cNode){
        System.out.println("到达节点"+cNode.getId());
        Path path = new Path();
        cNode.setStroke(Color.RED);
        double x=cNode.getCenterX();
        double y=cNode.getCenterY();

        path.getElements().add(new MoveTo(x, y + RADIUS));
        path.getElements().add(new LineTo(x-RADIUS, y+RADIUS));
        path.getElements().add(new LineTo(x+RADIUS, y-RADIUS));
        path.getElements().add(new LineTo(x-RADIUS, y+RADIUS));
        path.getElements().add(new LineTo(x+RADIUS, y-RADIUS));
        path.getElements().add(new LineTo(x, y-RADIUS));
        path.getElements().add(new LineTo(x, y));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(1));
        pathTransition.setPath(path);
        pathTransition.setNode(cNode);
        pathTransition.setCycleCount(1);
        pathTransition.play();
    }

    private CNode createDraggableCNode(double centerX, double centerY, double radius) {
        CNode circle = new CNode(centerX, centerY, radius); // 创建蓝色圆形
        circle.setOnMousePressed(this::onMousePressed);
        circle.setOnMouseDragged(this::onMouseDragged);
        return circle;
    }

    private void onMousePressed(MouseEvent event) {
        CNode circle = (CNode) event.getSource();
        circle.setUserData(new double[]{event.getSceneX() - circle.getCenterX(), event.getSceneY() - circle.getCenterY()});
    }

    private void onMouseDragged(MouseEvent event) {
        CNode circle = (CNode) event.getSource();
        double[] offset = (double[]) circle.getUserData();
        double newX = event.getSceneX() - offset[0];
        double newY = event.getSceneY() - offset[1];
        circle.setCenterX(newX);
        circle.setCenterY(newY);
    }


}
