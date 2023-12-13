package ReciteWords1;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class Controller {

    ArrayList<Word> wordBook = new ArrayList<>();//单词本
    ArrayList<Word> wordRecord= new ArrayList<>();//学习记录

    Queue<Word> wordQueue = new LinkedList<>();//每组


    int Icount = 5;
    int IgroupNum = 2;
    int NO = 0;//所有单词序号
    int no = 0;//当前单词序号
    int groupNum = 2;//组数
    int yetGroup = 0; // 已经学习的组数
    int count = 5;//每组多少个
    int yetStudyInGourp = 0 ;//每组已经学习多少个了
    double progress =0.0;
    int end = count;

    @FXML
    private MenuItem bt_set;

    @FXML
    private Label lb_ySIG;

    @FXML
    private Button bt_blur;

    @FXML
    private Button bt_know;

    @FXML
    private Button bt_nknow;

    @FXML
    private Label lb_Word;

    @FXML
    private Label lb_China;

    @FXML
    private ProgressBar pb;

    @FXML
    private Button bt_nerver;

    @FXML
    private MenuItem bt_load;

    @FXML
    private Label lb_cont;

    @FXML
    private Label lb_group;

    public void initialize(){

        //导入单词
        bt_load.setOnAction(e->{

            FileChooser fc =new FileChooser();
            fc.setInitialDirectory(new File(System.getProperty("user.home")+"/Documents"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("单词文件", "*.dct"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("所有文件", "*.*"));
            File f =fc.showOpenDialog(lb_China.getScene().getWindow());

            try{

                wordBook.clear();
                wordRecord.clear();
                wordQueue.clear();
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;
                while ((line = reader.readLine()) != null) {
                    load.accept(line);
                }
            } catch (IOException ex) {
                System.out.println("加载失败！");
                throw new RuntimeException(ex);
            }

            //初始化操作

             no = 0;//当前单词序号
             groupNum = IgroupNum;//组数
             yetGroup = 0; // 已经学习的组数
             count = Icount;//每组多少个
             yetStudyInGourp = 0 ;//每组已经学习多少个了
             progress =0.0;
             end = count;


            //此处开始背单词了
            bt_nerver.setVisible(true);
            lb_group.setVisible(true);
            bt_blur.setVisible(true);
            bt_know.setVisible(true);
            lb_ySIG.setVisible(true);
            bt_nknow.setVisible(true);
            lb_cont.setVisible(true);

            pb.setVisible(true);
            lb_China.setText("");
            lb_ySIG.setText(yetStudyInGourp+"/"+count);//进度显示
            lb_group.setText(yetGroup+"/"+groupNum);
            setWordQueue();//添加初始单词组
            next();//展示下一个单词

        });

        //认识
        bt_know.setOnAction(e->know());

        //

        //模糊
        bt_blur.setOnAction(e->blur());



        //不认识
        bt_nknow.setOnAction(e->nknow());


        //设置不再出现
        bt_nerver.setOnAction(e->never());


        bt_set.setOnAction(e->{
            Stage stage = new Stage();

            GridPane gp = new GridPane();
            Label lb_count = new Label("每组学习个数");
            Label lb_group = new Label("学习组数");
            TextField tf_count = new TextField(""+count);
            TextField tf_group = new TextField(""+groupNum);
            Button bt_enter = new Button("确定");


            tf_group.setMaxSize(100,50);
            tf_count.setMaxSize(100,50);
            lb_count.setMinSize(200,50);
            lb_count.setTextAlignment(TextAlignment.CENTER);
            lb_count.setTextAlignment(TextAlignment.CENTER);
            lb_group.setMinSize(200,50);

            gp.add(lb_count , 0 , 0);
            gp.add(lb_group,0,1);
            gp.add(tf_count,1 ,0);
            gp.add(tf_group,1 ,1);
            gp.add(bt_enter,1 , 2,2,2);



            Scene scene =new Scene(gp,400,150);

            stage.setScene(scene);
            stage.show();

            bt_enter.setOnAction(event->{
                IgroupNum = Integer.parseInt(tf_group.getText());
                Icount = Integer.parseInt(tf_count.getText());
                stage.close();
            });

        });

    }

    public void keyBord(Scene scene){
        scene.setOnKeyPressed(e->{
            KeyCode kc = e.getCode();
            if (kc == KeyCode.A){
                know();
            } else if (kc==KeyCode.S) {
                blur();
            } else if (kc == KeyCode.D) {
                nknow();
            }
        });

    }

    public void know(){
        //标记一次认识
        int cnt = wordQueue.peek().getCount();

        if (cnt == 1){
            //设置进度
            yetStudyInGourp++;
            System.out.println(yetStudyInGourp);
            progress = (0.0+yetStudyInGourp)/count;
            pb.setProgress(progress);
            lb_ySIG.setText(yetStudyInGourp+"/"+count);
            lb_group.setText(yetGroup+"/"+groupNum);
        }
        wordQueue.peek().setCount(cnt + 1);
       //放入队尾
       wordQueue.add(wordQueue.poll());
        //下一个
        next();
    }

    public void blur(){

        String blur = bt_blur.getText();
        if (blur.equals("模糊")){
            //先显示中文
            lb_China.setText(wordQueue.peek().getChinese());
            //放入队尾
            wordQueue.add(wordQueue.poll());
            bt_blur.setText("继续");
        }
        else {
            lb_China.setText("");
            next();
            bt_blur.setText("模糊");
        }

    }

    public void nknow(){
        String nknow = bt_nknow.getText();
        if (nknow.equals("不认识") ){
            //先显示中文
            lb_China.setText(wordQueue.peek().getChinese());
            //获取词频
            int cntt = wordQueue.peek().getCount();
            if (cntt >1){
                cntt-=1;
                wordQueue.peek().setCount(cntt);
            }
            //放入队尾
            wordQueue.add(wordQueue.poll());
            bt_nknow.setText("继续");
        }
        else {
            lb_China.setText("");
            next();
            bt_nknow.setText("不认识");
        }
    }

    public void never(){
        lb_China.setText("");
        yetStudyInGourp++;
        wordQueue.poll();
        lb_ySIG.setText(yetStudyInGourp+"/"+count);
        lb_group.setText(yetGroup+"/"+groupNum);
        pb.setProgress(yetStudyInGourp*1.0/count);
        next();
    }

    public void next(){
        //检查此队列是否都认识超过两次
        if (check()){//都学习完成，开始下一组

            no+=count;
            end+=count;
            yetGroup++;
            yetStudyInGourp=0;//学习数目清零
            pb.setProgress(0);//进度清零
            lb_ySIG.setText(yetStudyInGourp+"/"+count);
            lb_group.setText(yetGroup+"/"+groupNum);
            setWordQueue();
            //设置完后,继续next
            if (!wordQueue.isEmpty()){
                lb_Word.setText(wordQueue.peek().getWord());
                lb_cont.setText("连续答对次数"+" "+wordQueue.peek().getCount());
            }
        }else {//否则,循环本组单词

            while (wordQueue.peek().getCount()==2){//如果熟悉
                //设置进度
                wordQueue.poll();
            }

            lb_Word.setText(wordQueue.peek().getWord());
            lb_cont.setText("连续答对次数"+" "+wordQueue.peek().getCount());
        }

    }

    public void setWordQueue(){
        wordQueue.clear();//清空
        if (yetGroup < groupNum){
            wordQueue.addAll(wordRecord.subList(no,end));
        }
        else {//关闭背单词功能
            yetGroup = 0;
            bt_nerver.setVisible(false);
            bt_blur.setVisible(false);
            bt_know.setVisible(false);
            bt_nknow.setVisible(false);
            lb_ySIG.setVisible(false);
            lb_group.setVisible(false);
            lb_Word.setText("");
            lb_cont.setText("");
            lb_group.setText("");
            lb_cont.setVisible(false);
            lb_China.setText("今日学习完成!");
            pb.setVisible(false);
            NO = 0;//所有单词序号
        }
    }

    public boolean check(){

        if (wordQueue.isEmpty()){
            return true;
        }

        for ( Word w : wordQueue){
            if (w.getCount() < 2){
                return false;
            }
        }
        return true;
    }

    Consumer<String> load = line->{

        String[] tmp = line.split("::");
        Word w = new Word(tmp[0],tmp[1],NO);
        NO++;
        wordBook.add(w);
        wordRecord.add(w);
    };

}
