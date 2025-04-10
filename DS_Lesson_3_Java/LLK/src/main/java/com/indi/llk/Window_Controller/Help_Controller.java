package com.indi.llk.Window_Controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Help_Controller {
    @FXML
    private TextFlow helpTextFlow;

    @FXML
    public void initialize() {
        Text title = new Text("基本模式\n");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Text intro = new Text("基本模式是欢乐连连看游戏的基本模式，包含游戏的基本功能：\n");
        intro.setStyle("-fx-font-size: 14;");

        Text features = new Text("开始游戏、暂停游戏、提示、重拍、计时\n");
        features.setStyle("-fx-font-size: 14; -fx-fill: #3498db;");

        Text subtitle1 = new Text("\n1. 开始游戏\n");
        subtitle1.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Text content1 = new Text("当第一次进入游戏或者完成一局游戏后，点击'开始游戏'可以生成游戏地图，进行连连看游戏。在游戏地图中用鼠标左键点击任意位置的两张图片，选中图片后，会在选择的图片四周显示红色的矩形框。并判断能否消子。\n");
        content1.setStyle("-fx-font-size: 14;");

        Text subtitle2 = new Text("\n2. 暂停游戏\n");
        subtitle2.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Text content2 = new Text("当游戏开始时，点击'暂停游戏'可以暂停游戏，再次点击'暂停游戏'可以继续游戏。\n");
        content2.setStyle("-fx-font-size: 14;");

        Text subtitle3 = new Text("\n3. 提示\n");
        subtitle3.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Text content3 = new Text("当游戏开始时，点击'提示'可以提示一对可以消子的图片。\n");
        content3.setStyle("-fx-font-size: 14;");

        Text subtitle4 = new Text("\n4. 重排\n");
        subtitle4.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Text content4 = new Text("游戏开始后，点击'重排'按钮，将游戏地图中剩余的位置的图片重新排列。\n");
        content4.setStyle("-fx-font-size: 14;");

        Text subtitle5 = new Text("\n5. 计时\n");
        subtitle5.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Text content5 = new Text("游戏开始后，计时器开始计时，计时器显示在游戏地图的右上角。\n");
        content5.setStyle("-fx-font-size: 14;");

        helpTextFlow.getChildren().addAll(title, intro, features,
                subtitle1, content1,
                subtitle2, content2,
                subtitle3, content3,
                subtitle4, content4,
                subtitle5, content5);
    }
}