package com.example.clickycooker;

import com.almasb.fxgl.entity.Entity;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Upgradeable implements Stage {
    private final String name;
    private int cost;
    private final int speed;
    private final String imagePath;
    private int count;

    private Text costLabel;

    public Upgradeable(GameManager gm, String name, int cost, int speed, String imagePath) {
        this.name = name;
        this.cost = cost;
        this.speed = speed;
        this.imagePath = imagePath;
        this.count = 0;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getSpeed() {
        return speed;
    }

    private void createClone() {
        double x = GameManager.COOKIE_X;
        double y = GameManager.COOKIE_Y;
        double s = GameManager.COOKIE_SIZE;

        double is = 25;
        double ix = x-s/2+is;
        double iy = y-s/2+is;

        ImageView iv = new ImageView(imagePath);
        iv.setX(ix);
        iv.setY(iy);
        iv.setFitHeight(is);
        iv.setFitWidth(is);

        Entity entity = draw(iv);

        Translate translate = new Translate();

        Rotate rotation = new Rotate();
        rotation.setPivotX(x);
        rotation.setPivotY(y);
        iv.getTransforms().addAll(rotation, translate);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0), new KeyValue(translate.xProperty(), 0)),
                new KeyFrame(Duration.seconds(30), new KeyValue(rotation.angleProperty(), 180), new KeyValue(translate.xProperty(), 0)),
                new KeyFrame(Duration.seconds(60), new KeyValue(rotation.angleProperty(), 360), new KeyValue(translate.xProperty(), 0))
        );
        timeline.setCycleCount(-1);
        timeline.play();
    }

    public Rectangle createButton(int i) {
        ImageView iv = new ImageView();
        iv.setImage(new Image(imagePath));
        iv.setFitHeight(50);
        iv.setFitWidth(50);
        iv.setX(550);
        iv.setY(100 * i);
        draw(iv);

        Rectangle button = new Rectangle(150, 50);
        button.setFill(Color.GRAY);
        button.setX(600);
        button.setY(100 * i);
        draw(button);

        costLabel = new Text(name + ": " + cost);
        costLabel.setFill(Color.BLACK);
        costLabel.setStyle("-fx-font: 15 arial");
        costLabel.setX(600);
        costLabel.setY(100 * i + 25);
        costLabel.setMouseTransparent(true);
        draw(costLabel);

        return button;
    }

    public void updateCost() {
        cost *= 1.2;
        costLabel.setText(name + ": " + cost);
    }

    public void spawn() {
        count++;
        createClone();
    }

}
