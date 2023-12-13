package RandomAllocation1;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;


public class Run extends Application{


   public static class  Candidate{

        SimpleIntegerProperty no;
        SimpleStringProperty name;
        SimpleIntegerProperty score;
        SimpleIntegerProperty groupNo;

        public  Candidate(int no,String name,int score,int groupNo) {
            this.no = new SimpleIntegerProperty(no);
            this.name = new SimpleStringProperty(name);
            this.score = new SimpleIntegerProperty(score);
            this.groupNo = new SimpleIntegerProperty(groupNo);
        }


        public int getNo() {
            return no.get();
        }

        public String getName() {
            return name.get();
        }

        public int getScore() {
            return score.get();
        }

        public int getGroupNo() {
            return groupNo.get();
        }

       public void setNo(int no) {
           this.no.set(no);
       }

       public void setName(String name) {
           this.name.set(name);
       }

       public void setScore(int score) {
           this.score.set(score);
       }

       public void setGroupNo(int groupNo) {
           this.groupNo.set(groupNo);
       }

       @Override
        public String toString() {
            return "Candidate{" +
                    "no=" + no +
                    ", name=" + name +
                    ", score=" + score +
                    ", groupNo=" + groupNo +
                    '}';
        }

   }

    int No=0;//编号
    int GNo=5;//组数

    //分组前
    ArrayList<Candidate>  list0 =new ArrayList<>();
    //分组后
    ArrayList<Candidate> list1 =new ArrayList<>();

    ObservableList<Candidate> list = FXCollections.observableArrayList();//数据源

    TableView<Candidate> tv =new TableView<>(list);

    TableColumn<Candidate,Number> col_no = new TableColumn<>("No");
    TableColumn<Candidate,String> col_name = new TableColumn<>("Name");
    TableColumn<Candidate,Number> col_score = new TableColumn<>("Score");
    TableColumn<Candidate,Number> col_groupNo = new TableColumn<>("GroupNo");

    Button btLoad =new myButton("导入名单");
    Button btPaste =new myButton("粘贴名单");
    Button btGroup =new myButton("种子分组");
    Button btRand = new myButton("随机分组");
    Button btCopy =new myButton("复制名单");
    Button btClear =new myButton("清空名单");
    Button btRecover =new myButton("复原");
    Button btAdd = new myButton("+");
    Button btDel = new myButton("-");

    Label lbNum = new Label(""+GNo);

    @Override
    public void start(Stage stage1) throws Exception {



        VBox vb =new VBox(btLoad,btPaste,btGroup,btRand,btCopy,btClear,btRecover,btAdd,lbNum,btDel);
        vb.setAlignment(Pos.TOP_CENTER);

        vb.setSpacing(10);

        tv.setEditable(true);
        col_no.setVisible(false);
        col_name.setVisible(false);
        col_groupNo.setVisible(false);
        col_score.setVisible(false);
        lbNum.setVisible(false);
        btAdd.setDisable(true);
        btClear.setDisable(true);
        btCopy.setDisable(true);
        btRand.setDisable(true);
        btRecover.setDisable(true);
        btPaste.setDisable(true);
        btGroup.setDisable(true);
        btPaste.setDisable(true);
        btDel.setDisable(true);


        col_no.setCellValueFactory(new PropertyValueFactory<Candidate,Number>("No"));
        col_name.setCellValueFactory(new PropertyValueFactory<Candidate,String>("Name"));
        col_score.setCellValueFactory(new PropertyValueFactory<Candidate,Number>("Score"));
        col_groupNo.setCellValueFactory(new PropertyValueFactory<Candidate,Number>("GroupNo"));

        tv.getColumns().addAll(col_no,col_name,col_score,col_groupNo);


        BorderPane bp=new BorderPane();
        bp.setCenter(tv);
        bp.setRight(vb);

        //复制提示界面
        Label lbHint = new Label();
        lbHint.setAlignment(Pos.CENTER);
        lbHint.setStyle("-fx-font-size: 15;");

        Scene s2 = new Scene(lbHint,100,50);
        Stage stage2 = new Stage();
        stage2.setScene(s2);

        //粘贴提示界面
        TextArea ta = new TextArea("粘贴格式：\"名字,得分\"");
        ta.setMinWidth(200);
        ta.setMinHeight(250);
        Button btEnter = new Button("确认");
        Button btCancel = new Button("取消");

        HBox hb3_1=new HBox(btEnter,btCancel);
        hb3_1.setAlignment(Pos.BOTTOM_CENTER);
        VBox xb3_1=new VBox(ta,hb3_1);

        Scene sc3 = new Scene(xb3_1,200,300);
        Stage stage3 =new Stage();
        stage3.setScene(sc3);

//        css
        col_score.setMinWidth(100);
        col_no.setMinWidth(100);
        col_groupNo.setMinWidth(120);
        col_name.setMinWidth(100);

        lbNum.setStyle("-fx-font-size: 15;"+
                "-fx-text-fill: #ff0000;");








        //主界面设计
        Scene s1=new Scene(bp,500,500);
//        bp.setId("root");
//        URL cssurl = this.getClass().getClassLoader().getResource("css/mycss.css");
//        s1.getStylesheets().add(cssurl.toExternalForm());

        stage1.setTitle("分组小程序");
        stage1.getIcons().add(new Image("Image/dark.png"));
        stage1.setScene(s1);
        stage1.show();





        //导入名单
        btLoad.setOnAction(e->{
            FileChooser fc =new FileChooser();
            fc.setInitialDirectory(new File(System.getProperty("user.home")+"/Documents"));
            File f =fc.showOpenDialog(stage1);
            openCon();

            try(Scanner s=new Scanner(f,"UTF-8")) {
                list.clear();
                No=0;//编号清零
                list0.clear();//导入文件后清零
                list1.clear();//导入文件后清零

                while (s.hasNextLine()){
                    String line=s.nextLine();
                    load.accept(line);
                }

            } catch (FileNotFoundException ex) {

                System.out.println("加载失败！");
                throw new RuntimeException(ex);

            }
        });

        //复制名单
        btCopy.setOnAction(e->{

            //处理list1
            if (!list1.isEmpty()) {
                String str1 = "";
                String str2 = "";

                for (int i = 1; i <= GNo; i++) {
                    str1 = "第" + (i) + "组: ";
                    for (Candidate cc : list1) {
                        if (cc != null) {
                            if (cc.getGroupNo() == i)
                                str1 = str1 + cc.getName() + " ";
                        }
                    }
                    str1 = str1 + "\n";
                    str2 += str1;
                }
                Clipboard cb = Clipboard.getSystemClipboard();
                ClipboardContent clC = new ClipboardContent();
                clC.putString(str2);
                cb.setContent(clC);

                lbHint.setText("复制成功！");
            } else {

                lbHint.setText("还未分组！");

            }
            stage2.show();
        });

        //粘贴名单
        btPaste.setOnAction(e->{
            //显示粘贴板
            stage3.show();
        });

        //确认粘贴
        btEnter.setOnAction(e->{
            try {
                openCon();
                String txt = ta.getText();
                list.clear();
                list0.clear();
                list1.clear();
                No=0;
                for (String str : txt.split("\n")){
                    load.accept(str);
                }
                refreshTable(list0);

            }catch (Exception exception){
                System.out.println("粘贴失败");
            }
            stage3.close();

        });

        //增加分组数
        btAdd.setOnAction(e->{
            int Num = Integer.parseInt(lbNum.getText());
            if (Num<list0.size()){
                GNo=Num+1;
            }
            lbNum.setText(""+GNo);
        });

        //减少分组数
        btDel.setOnAction(e->{
            int Num = Integer.parseInt(lbNum.getText());
            if (Num>1){
                GNo=Num-1;
                lbNum.setText(""+GNo);
            }
        });

        //取消
        btCancel.setOnAction(e->{
            stage3.close();
        });

        //开始分组
        btGroup.setOnAction(e->{

            if(!list0.isEmpty()){
                list1.clear();
                list1.addAll(list0);
                int num=list1.size();
                //先排序
                list1.sort(new Comparator<Candidate>() {
                    @Override
                    public int compare(Candidate p1, Candidate p2) {
                        return Integer.compare(p1.getScore(), p2.getScore());
                    }
                });

                //交错排名
                for (int i = 0 ; i < (num+1)/2 ; i+=2){
                    Collections.swap(list1,i,num-1-i);
                }


                //开始编组号
                for (int i = 1,resid=num,cm,j = 0,k=0;i <= GNo ;i++){
                    cm=resid/(GNo-i+1);//计算下一组人数
                    k=j+cm;
                    for(;j <k ; j++){
                        list1.get(j).setGroupNo(i);
                    }
                    resid-=cm;
                }
                //更新列表数据
                refreshTable(list1);

            }
        });

        //随机分组
        btRand.setOnAction(e->{
            if(!list0.isEmpty()){
                list1.clear();
                list1.addAll(list0);

                Collections.shuffle(list1);
                }

                int num=list1.size();

                for (int i = 1,resid=num,cm,j = 0,k=0;i <= GNo ;i++){
                    cm=resid/(GNo-i+1);//计算下一组人数
                    k=j+cm;
                    for(;j <k ; j++){
                        list1.get(j).setGroupNo(i);
                    }
                    resid-=cm;
                }

                //更新列表数据
                refreshTable(list1);
        });

        btClear.setOnAction(e->{
            list.clear();
        });

        //复原
        btRecover.setOnAction(e->{
            for (Candidate cc : list0){
                cc.setGroupNo(0);
            }
            refreshTable(list0);
        });

    }

    private void openCon() {

        lbNum.setVisible(true);
        tv.setEditable(false);
        col_no.setVisible(true);
        col_name.setVisible(true);
        col_groupNo.setVisible(true);
        col_score.setVisible(true);
        btAdd.setDisable(false);
        btClear.setDisable(false);
        btCopy.setDisable(false);
        btRand.setDisable(false);
        btRecover.setDisable(false);
        btPaste.setDisable(false);
        btGroup.setDisable(false);
        btPaste.setDisable(false);
        btDel.setDisable(false);
    }

    private void refreshTable(ArrayList<Candidate> ls) {
        list.clear();
        list.addAll(ls);
    }


    Consumer<String> load = line->{

        String[] tmp = line.split(",");
        Candidate c=new Candidate(No+1,tmp[0],Integer.parseInt(tmp[1]),0);
        list0.add(c);//未分组
        list.add(c);//未分组
        No++;
    };

    public static void main(String[] args) {
        Application.launch();
    }
}
