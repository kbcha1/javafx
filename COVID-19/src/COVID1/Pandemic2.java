package COVID1;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Pandemic2 extends javafx.application.Application{

    private static final int PADDING = 10;//组件距离
    private static final int RADIUS = 5;//半径
    private static final int DAYSTEPS = 50;//帧数
    private static final int infect_dist = 15;//传播距离

    private static final Color COLOR0 = Color.BLACK;//正常
    private static final Color COLOR1 = Color.GREEN;//感染不传播
    private static final Color COLOR2 = Color.RED;//感染且传播
    private static final Color COLOR3 = Color.DEEPSKYBLUE;//痊愈
    private static final Color COLOR4 = Color.GRAY;//死亡

    private int num0;
    private int num1;
    private int num2;
    private int num3;
    private int num4;

    private int flag;//是否隔离

    private int qianfu;//潜伏期
    private int speed;//速度
    private double PROB_DEATH ;//死亡率
    private int SPREAD_DAYS ;//病发期
    private double PROB_PROACH ;//接触传染率
    private int seedNum;//初始发病人数
    private double PROB_HERTH;//治愈率

    private double mapWidth;
    private double mapHeight;

    //初始值
    int speed_ = 1;
    int num0_ = 100;
    int qianfu_ = 14;
    int SPREAD_DAYS_ = 14;
    double PROB_PROACH_ = .5;
    int seedNum_ = 1;
    double PROB_HERTH_=1.0;//治愈率为1
    int flag_ = 0;
    double PROB_DEATH_ =  0.7 ;

    int day = 0;

    String temp = "是";

    private ArrayList<Person> pool = new ArrayList<>();

    private Button btStart = new Button("开始");
    private Button btPause = new Button("暂停");
    private Button btSet = new Button("设置");
    private Button btSpeed = new Button("加速");
    private Button btExprot = new Button("报告");


    Label lb_day = new Label("第0天");
    Text textN0 = new Text("正常100");
    Text textN1 = new Text("感染0");
    Text textN2 = new Text("病发0");
    Text textN3 = new Text("康复0");
    Text textN4 = new Text("死亡0");
    Label lb_speed = new Label("X1 ");
    int[] dayAdd = new int[1000];//记录每日新增

    XYChart.Series<Number,Number> series1 = new XYChart.Series<>();//累积存活
    XYChart.Series<Number,Number> series2 = new XYChart.Series<>();//累积感染
    XYChart.Series<Number,Number> series3 = new XYChart.Series<>();//每日新增

    class Person extends Circle{

        private int status;
        private double dx,dy;
        private int step;//总共移动过的次数
        private int day_infected;//感染天数

        public Person() {
            this(0);
        }

        public Person(int status) {
            super(RADIUS*2+Math.random()*(mapWidth -4*RADIUS),
                    RADIUS*2+Math.random()*(.8*mapHeight -4*RADIUS),
                    RADIUS);
            this.status = status;
            this.setId(num0+num1+num2+num3+num4+" ");

            switch(status) {
                case 1: num1++; break;
                case 2: num2++; break;
                case 3: num3++; break;
                case 4: num4++; break;
                default: num0++;
            }

            update();
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
            update();
        }

        private void update() {
            switch(this.status) {
                case 1: this.setStroke(COLOR0);this.setFill(COLOR1);break;
                case 2: this.setStroke(COLOR2);this.setFill(null);break;
                case 3: this.setStroke(COLOR0);this.setFill(COLOR3);break;
                case 4: this.setStroke(COLOR0);this.setFill(COLOR4);break;
                default: this.setStroke(COLOR0);this.setFill(null);
            }
        }
        //为什么在类中创建一个对象，如果对象和类属于相同类型，则可以访问其私有域

        void randomwalk() {
            //停止条件
            if(this.status>=3) {
                return ;
            }else if(btStart.getText() == "开始") {
                //用户点击停止按钮
                this.setVisible(false);
                return;
            }else if(btPause.getText() == "继续") {
                //用户点击了暂停
                return;
            }

            //状态2的特殊处理
            if(this.status == 2) {
                pool.forEach(p->{
                    //传染
                    if(p.status==0 && this.dist(p)<=infect_dist && (PROB_PROACH <= Math.random())) {
                        p.setStatus(1);
                        num1++;num0--;
                        dayAdd[1+(step / DAYSTEPS)]++;
                    }
                    //突出
                    if(this.getRadius() > 1.6*RADIUS)
                        this.setRadius(.7*RADIUS);
                    else
                        this.setRadius(this.getRadius()+1);
                });

            }

            //所有状态共同动作
            if(step%DAYSTEPS == 0) {

                double dayx,dayy;

                dayx = this.getCenterX() + .3* mapWidth - .6* mapWidth *Math.random();
                dayy = this.getCenterY() + .3* mapHeight - .6* mapHeight *Math.random();

                if(dayx < RADIUS*2)
                    dayx = RADIUS*2;
                else if(dayx > mapWidth -RADIUS*2)
                    dayx = mapWidth -RADIUS*2;

                if (flag == 1&&this.status == 2){
                    dayy = 0.9*mapHeight;
                }
                else if(dayy < RADIUS*2){
                    dayy = RADIUS*2;
                }
                else if(dayy > .8*mapHeight -RADIUS*2)
                {
                        dayy = .8*mapHeight -RADIUS*2;
                }

                    dx = (dayx - this.getCenterX()) / DAYSTEPS;
                    dy = (dayy - this.getCenterY()) / DAYSTEPS;

                    if(this.status == 1) {
                        this.day_infected++;
                        if(this.day_infected >= qianfu) {
                            this.setStatus(2);
                            num1--;num2++;
                        }
                    }else if(this.status == 2) {
                        this.day_infected++;

                        if(this.day_infected >= qianfu + SPREAD_DAYS) {

                            if(Math.random() <PROB_DEATH) {
                                this.setStatus(4);
                                num2--;num4++;
                            }
                            else {
                                this.setStatus(3);
                                num2--;num3++;
                            }
                        }

                }
                lb_day.setText("第"+(step/DAYSTEPS+1)+"天");

                textN0.setText("正常"+num0);
                textN1.setText("感染"+num1);
                textN2.setText("病发"+num2);
                textN3.setText("康复"+num3);
                textN4.setText("死亡"+num4);

                series1.getData().add(new XYChart.Data((step / DAYSTEPS)+1,num0+num1+num2+num3));//存活
                series2.getData().add(new XYChart.Data((step / DAYSTEPS)+1,num1+num2+num3+num4));//
                series3.getData().add(new XYChart.Data((step / DAYSTEPS)+1,dayAdd[1+(step / DAYSTEPS)]));//每日新增
                System.out.println(dayAdd[1+(step/DAYSTEPS)]);

            }//新的一天

            day = step / DAYSTEPS;
            //行走，方向是dx,dy
            FadeTransition ft = new FadeTransition();
            ft.setFromValue(1);
            ft.setToValue(1);
            ft.setDuration(Duration.millis(1000/DAYSTEPS/speed));
            ft.setNode(this);
            ft.play();
            ft.setOnFinished(e->{
                this.setCenterX(this.getCenterX() + dx);
                this.setCenterY(this.getCenterY() + dy);
                this.step++;
                randomwalk();
            });
        }

        private double dist(Person p) {
            return Math.sqrt(Math.pow(this.getCenterX()-p.getCenterX(),2)
                    + Math.pow(this.getCenterY()-p.getCenterY(), 2))
                    - 2*RADIUS;
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //样式
        btExprot.setDisable(true);
        btSpeed.setDisable(true);
        btPause.setDisable(true);

        btSet.setStyle("-fx-font-size: 20");
        btPause.setStyle("-fx-font-size: 20");
        btStart.setStyle("-fx-font-size: 20");
        btSpeed.setStyle("-fx-font-size: 20");
        btExprot.setStyle("-fx-font-size: 20");
        lb_speed.setStyle("-fx-font-size: 20");
        lb_day.setStyle("-fx-font-size: 20");

        lb_day.setPrefSize(100,50);
        lb_speed.setPrefSize(50,50);
        lb_speed.setAlignment(Pos.CENTER);
        lb_day.setAlignment(Pos.CENTER);

        btStart.setGraphic(new ImageView("start.png"));


        for(Text t: new Text[] {textN0,textN1,textN2,textN3,textN4}) {
            t.setFont(Font.font(20));
            t.setTextAlignment(TextAlignment.CENTER);
        }

        textN0.setFill(COLOR0);
        textN1.setFill(COLOR1);
        textN2.setFill(COLOR2);
        textN3.setFill(COLOR3);
        textN4.setFill(COLOR4);


        //图
        Pane map = new Pane();

        //曲线
        NumberAxis axisX = new NumberAxis();
        axisX.setLabel("天数");
        axisX.setTickUnit(1);
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("人数");
        LineChart<Number,Number> liveLine = new LineChart<>(axisX, axisY);
        series1.setName("存活人数");
        series2.setName("累积感染");
        series3.setName("每日新增");
        liveLine.getData().addAll(series1,series3,series2);
        liveLine.setCreateSymbols(false);

        //主布局
        GridPane gp = new GridPane();
        gp.add(btSet,0,0);
        gp.add(btStart,1,0);
        gp.add(btPause,2,0);
        gp.add(btSpeed,3,0);
        gp.add(lb_speed,4,0);
        gp.add(btExprot,5,0);
        gp.add(lb_day,6,0,2,1);
        gp.add(textN0,8,0);
        gp.add(textN1,9,0);
        gp.add(textN2,10,0);
        gp.add(textN3,11,0);
        gp.add(textN4,12,0);
        gp.add(map,0,1,7,12);
        gp.add(liveLine,8,1,5,10);

        gp.setAlignment(Pos.TOP_CENTER);
//        gp.setPrefSize(600,600);
        gp.setHgap(PADDING);
        gp.setVgap(2*PADDING);

        //场景布局
        Scene s1 = new Scene(gp,1300,600);
        primaryStage.setScene(s1);
        primaryStage.show();

        //设置布局
        Button btSure = new Button("确定");

        ToggleGroup tg = new ToggleGroup();

        RadioButton rbY = new RadioButton("住院");
        rbY.setToggleGroup(tg);

        RadioButton rbN = new RadioButton("放任");
        rbN.setToggleGroup(tg);
        rbN.setSelected(true);

        Text t0 = new Text("管控措施");
        Text t1 = new Text("致死率");
        Text t2 = new Text("治愈率");
        Text t3 = new Text("接触感染率");
        Text t4 = new Text("正常人数");
        Text t5 = new Text("初始感染人数");
        Text t6 = new Text("病发期");
        Text t7 = new Text("潜伏期");

        TextField tf1 = new TextField(""+PROB_DEATH_);//致死率
        TextField tf2 = new TextField(""+PROB_HERTH_);//治愈率
        TextField tf3 = new TextField(""+PROB_PROACH_);//接触感染率
        TextField tf4 = new TextField(""+ num0_);//正常人数
        TextField tf5 = new TextField(""+seedNum_);//初始感染人数
        TextField tf6 = new TextField(""+SPREAD_DAYS_);//病发期
        TextField tf7 = new TextField(""+qianfu_);//潜伏期

        GridPane gp1 = new GridPane();

        gp1.add(t0,0,0,2,1);
        gp1.add(rbY,0,1);
        gp1.add(rbN,1,1);
        gp1.add(t1,0,2);
        gp1.add(tf1,1,2);
        gp1.add(t2,0,3);
        gp1.add(tf2,1,3);
        gp1.add(t3,0,4);
        gp1.add(tf3,1,4);
        gp1.add(t4,0,5);
        gp1.add(tf4,1,5);
        gp1.add(t5,0,6);
        gp1.add(tf5,1,6);
        gp1.add(t6,0,7);
        gp1.add(tf6,1,7);
        gp1.add(t7,0,8);
        gp1.add(tf7,1,8);
        gp1.add(btSure,0,9,2,1);

        gp1.setAlignment(Pos.TOP_CENTER);
        gp1.setHgap(PADDING);
        gp1.setVgap(PADDING);


        for(Text t: new Text[] {t0,t1,t2,t3,t4,t5,t6,t7}) {
            t.setFont(Font.font(20));
        }

        rbY.setStyle("-fx-font-size: 20");
        rbN.setStyle("-fx-font-size: 20");
        btSure.setStyle("-fx-font-size: 20");



        Scene setScene = new Scene(gp1,400,500);

        Stage setStage = new Stage();
        setStage.setScene(setScene);
        setStage.setTitle("参数设置");

        //导出布局
        TextArea ta = new TextArea();

        ta.setStyle("-fx-font-size: 20");

        Scene exScene = new Scene(ta,300,500);

        Stage exStage = new Stage();
        exStage.setScene(exScene);
        exStage.setTitle("报告");



        //监听事件

        //参数设置
        btSet.setOnAction(e->{
            setStage.show();
        });

        btSure.setOnAction(e->{

            RadioButton tmpRB = (RadioButton) tg.getSelectedToggle();
            if (tmpRB.getText().equals("住院")){
                flag_ = 1;
            }else {
                flag_ = 0;
            }

            PROB_DEATH_ = Double.parseDouble(tf1.getText());//死亡率
            PROB_HERTH_ = Double.parseDouble(tf2.getText());
            PROB_PROACH_ = Double.parseDouble(tf3.getText());
            num0_ = Integer.parseInt(tf4.getText());//正常
            seedNum_ = Integer.parseInt(tf5.getText());
            SPREAD_DAYS_ = Integer.parseInt(tf6.getText());
            qianfu_ = Integer.parseInt(tf7.getText());
            PROB_DEATH_ =  Math.min(PROB_DEATH_,1-flag_*PROB_HERTH_);//死亡率-更新

            setStage.close();
        });

        //加速
        btSpeed.setOnAction(e->{
            if (speed < 10) {
                speed++;
                lb_speed.setText("X"+speed);
            } else{
                speed = 1;
                lb_speed.setText("X"+speed);
            }
        });

        //开始
        btStart.setOnAction(e->{

            for(Person p : pool){
                if (p.status ==1||p.status ==2){
                    temp = "否";
                }
            }

            //设置
            mapWidth = map.getWidth();
            mapHeight = map.getHeight();
            pool.clear();
            map.getChildren().clear();

            //判断
            if(btStart.getText() == "开始") {

                //此时结束，但保留部分数据
                Arrays.setAll(dayAdd, i -> 0);
                series1.getData().clear();
                series2.getData().clear();
                series3.getData().clear();

                //数据赋值，get\set方法
                speed = speed_;
                qianfu = qianfu_;
                PROB_DEATH = PROB_DEATH_;
                SPREAD_DAYS = SPREAD_DAYS_;//病发期
                PROB_PROACH = PROB_PROACH_;
                PROB_HERTH = PROB_HERTH_;
                flag = flag_;
                seedNum = seedNum_;
                num0 = 0;
                day = 0;
                num1 = num2 = num3 = num4 = 0;


                //控件属性
                lb_speed.setText("X"+speed_);
                lb_day.setText("第0天");
                textN0.setText("正常"+num0_);
                textN1.setText("感染"+num1);
                textN2.setText("病发"+num2);
                textN3.setText("康复"+num3);
                textN4.setText("死亡"+num4);

                btStart.setText("结束");

                //激活暂停键，关闭报告，关闭设置
                btSet.setDisable(true);
                btExprot.setDisable(true);
                btPause.setDisable(false);
                btPause.setText("暂停");
                btSpeed.setDisable(false);

                for(int i=0; i < seedNum;i++) {
                    pool.add(new Person(2));
                }

                for(int i=0; i < num0_;i++) {
                    pool.add(new Person());
                }

                map.getChildren().addAll(pool);

                pool.forEach(p -> p.randomwalk());

            }else {
                //此时结束,开启设置按钮、开启报告，关闭暂停，关闭加速
                btPause.setDisable(true);
                btSpeed.setDisable(true);
                btSet.setDisable(false);
                btExprot.setDisable(false);

                btStart.setText("开始");
            }
        });

        //暂停
        btPause.setOnAction(e->{
            if(btPause.getText()=="暂停") {
                btPause.setText("继续");
            }else {
                btPause.setText("暂停");
                pool.forEach(p->p.randomwalk());
            }
        });

        //展示结果
        btExprot.setOnAction(e->{

            exStage.toFront();
            exStage.show();
            String result = "本次实验总人数共计："+ (num0_+seedNum_) +"人\n"+
                    "历经天数："+day+"\n"+
                    "总共存活："+(num0_+seedNum_-num4)+"人\n"+
                    "累积感染："+(num1+num2+num3+num4)+"人\n"+
                    "累积死亡："+num4+"人\n"+
                    "是否达到群体免疫："+temp
                    ;
            ta.textProperty().set(new String(""));

            //添加一个自动打字线程
            Thread thread = new Thread(()->{

                for (int i = 0; i < result.length(); i++) {
                    try {
                        char tmp = result.charAt(i);
                        Thread.sleep((long) (Math.random()*50+100));
//                        Platform.runLater(()->{
                            ta.textProperty().set(ta.getText() + tmp );
//                        });

                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }


            });
            thread.start();
        });
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}
