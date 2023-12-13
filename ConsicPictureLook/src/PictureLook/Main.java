package PictureLook;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Arrays;

public class Main extends Application {
    //核心属性
    File img;//当前图片
    File imgList[];//图片列表
    int pos = 0;//当前图片在列表的位置
    double scale = 0.1;
    double tFFX, tFFY;

    //核心控件

    Button btOpen = new MyButton();
    Button btLeftSpin = new  MyButton();
    Button btRightSpin = new  MyButton();
    Button btBiggen = new  MyButton();
    Button btshorten = new  MyButton();
    Button btNext = new  MyButton();
    Button btPrev = new  MyButton();
    Button btNext0 = new  MyButton();
    Button btPrev0 = new  MyButton();
    Button btRes = new  MyButton();

    Label lb_page=new Label();

    ImageView iv = new ImageView();


    @Override
    public void start(Stage stage1) throws Exception {
        //核心控件初始样式




        btOpen.setDisable(false);

        btNext.setGraphic(new ImageView("Image/next.png"));
        btPrev.setGraphic(new ImageView("Image/prev.png"));
        btBiggen.setGraphic(new ImageView("Image/biggen.png"));
        btshorten.setGraphic(new ImageView("Image/shorten.png"));
        btPrev0.setGraphic(new ImageView("Image/prev.png"));
        btNext0.setGraphic(new ImageView("Image/next.png"));
        btOpen.setGraphic(new ImageView("Image/open.png"));
        btLeftSpin.setGraphic(new ImageView("Image/leftspin.png"));
        btRightSpin.setGraphic(new ImageView("Image/rightspin.png"));
        btRes.setGraphic(new ImageView("Image/res.png"));


        lb_page.setAlignment(Pos.CENTER);

        iv.setFitHeight(400);
        iv.setPreserveRatio(true);

        lb_page.setFont(Font.font("宋体", 28));


//
        //音乐播放器
        AudioClip ac;
        URL url = this.getClass().getClassLoader().getResource("Music/y2080.mp3");
        ac = new AudioClip(url.toExternalForm());

        //

        //
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")+"/Pictures"));
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("所有文件", "*.*"),
                new FileChooser.ExtensionFilter("JPG图片", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG图片", "*.png")
        );

        BorderPane bp = new BorderPane();

        FlowPane fp=new FlowPane(btOpen, btLeftSpin,btRightSpin, btBiggen, btshorten,btPrev,btNext,lb_page);

        ScrollPane sp = new ScrollPane();
        sp.setContent(iv);


        bp.setTop(fp);

        bp.setLeft(btPrev0);

        bp.setRight(btNext0);

        bp.setCenter(sp);






        //设置监听事件

        btRes.setOnAction(e->{
            
        });



        //鼠标移动
        bp.setOnMouseMoved(event -> {
            if (event.getX() >= btPrev0.getLayoutX()&& event.getX() <= (btPrev0.getLayoutX() + btPrev0.getWidth()) &&
                    event.getY() >= btPrev0.getLayoutY() && event.getY() <= (btPrev0.getLayoutY() + btPrev0.getHeight())) {
                btPrev0.setVisible(true);
            } else if(event.getX() >= btNext0.getLayoutX()&& event.getX() <= (btNext0.getLayoutX() + btNext0.getWidth()) &&
                    event.getY() >= btNext0.getLayoutY() && event.getY() <= (btNext0.getLayoutY() + btNext0.getHeight())){
                btNext0.setVisible(true);
            }else{
                btPrev0.setVisible(false);
                btNext0.setVisible(false);
            }
        });

        //拖拽

        iv.setOnMouseDragged(e -> {
            iv.setTranslateX(e.getSceneX() - tFFX);
            iv.setTranslateY(e.getSceneY() - tFFY);
        });

        iv.setOnMousePressed(e -> {
            tFFX = e.getSceneX() - iv.getX();
            tFFY = e.getSceneY() - iv.getY();
        });

        //打开图片
        btOpen.setOnAction(e -> {
            img = fc.showOpenDialog(stage1);
            if (img != null) {
                System.out.println("成功打开图片" + img.getPath());
                refreshImageList();
                refreshImage();
                setOnOff();
                setPage();
            }
        });

        //旋转图片
        btLeftSpin.setOnAction(e -> {
            iv.setRotate(iv.getRotate()-90);
        });
        btRightSpin.setOnAction(e -> {
            iv.setRotate(iv.getRotate()+90);
        });

        //放大图片
        btBiggen.setOnAction(e -> {
            iv.setScaleX(iv.getScaleX() * (1+scale));
            iv.setScaleY(iv.getScaleY() * (1+scale));
        });

        //缩小图片
        btshorten.setOnAction(e -> {
            if (iv.getFitHeight() > 50) {
                iv.setScaleX(iv.getScaleX() * (1-scale));
                iv.setScaleY(iv.getScaleY() * (1-scale));
            }
        });

        //下一张
        class Next implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent event) {
                pos++;
                if(pos== imgList.length-1){
                    ac.play();
                    System.out.println("最后一张");
                }
                else if(pos== imgList.length){
                    pos=0;
                    ac.play();
                    System.out.println("第一张");
                }
                img = imgList[pos];
                refreshImage();
                setOnOff();
                setPage();
            }
        }
        btNext.setOnAction(new Next());
        btNext0.setOnAction(new Next());


        //前一张
        class Prev implements EventHandler<ActionEvent> {

            @Override
            public void handle(ActionEvent event) {
                pos--;
                if(pos==0){
                    ac.play();
                    System.out.println("第一张");
                }
                else if(pos==-1){
                    pos= imgList.length-1;
                    ac.play();
                    System.out.println("最后一张");
                }

                img = imgList[pos];
                refreshImage();
                setOnOff();
                setPage();
            }
        }
        btPrev.setOnAction(new Prev());
        btPrev0.setOnAction(new Prev());


        //容器组织


        Scene s1 = new Scene(bp, 800, 500);
        stage1.setScene(s1);
        stage1.setTitle("照片查看器");
        stage1.getIcons().add(new Image("Image/dark.jpg"));
        stage1.show();

    }


    private void setPage() {
        lb_page.setText((pos+1)+"/"+ imgList.length);
    }


    //

    //

    private void setOnOff() {
        if (imgList.length != 1) {
            btPrev.setDisable(false);
            btPrev0.setDisable(false);
            btNext0.setDisable(false);
            btNext.setDisable(false);
        }
        btLeftSpin.setDisable(false);
        btRightSpin.setDisable(false);
        btBiggen.setDisable(false);
        btshorten.setDisable(false);
    }


    protected void refreshImage() {
        iv.setImage(new Image("file:" + img.getPath()));
        //text
        System.out.println("更新图片成功,当前图片位置" + pos + " 路径:" + img.getPath());

    }

    protected void refreshImageList() {
        File folder = img.getParentFile();
        imgList = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(".jpg") || name.endsWith(".png");
            }
        });

        pos = Arrays.binarySearch(imgList, img);

        System.out.println("更新文件夹成功,当前图片位置" + pos + " 路径:" + img.getPath());
    }


    public static void main(String[] args) {
        Application.launch(args);
    }

}
