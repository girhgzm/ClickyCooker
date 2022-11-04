package com.example.clickycooker;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameManager implements Stage {
    private static final String UPGRADEABLES_DATA_PATH = "src/main/resources/assets/data/upgradeables.json";
    private static final String TEXTURE_PATH = "assets/textures/";

    public static final double COOKIE_X = 200.0;
    public static final double COOKIE_Y = 250.0;
    public static final double COOKIE_SIZE = 300.0;
    private static int cookies;
    private static int clickSpeed;
    private static int autoClickSpeed;
    private static Text cookiesLabel;
    private final ArrayList<Upgradeable> upgradeables;

    public GameManager() throws IOException, ParseException {
        cookies = 0;
        clickSpeed = 1;
        autoClickSpeed = 0;
        this.upgradeables = new ArrayList<>();

        loadUpgradeables();
        createMilk();
        createCookie();
        createShop();
        createCookiesLabel();

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                changeCookies(autoClickSpeed);
            }
        }, 1000, 1000);
    }

    public void changeCookies(int cookies) {
        GameManager.cookies += cookies;
        updateCookiesLabel();
    }

    private void loadUpgradeables() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(UPGRADEABLES_DATA_PATH));
        JSONArray jsonArray = (JSONArray) jsonObject.get("upgradeables");

        for (Object o : jsonArray) {
            JSONObject obj = (JSONObject) o;
            String name = (String) obj.get("name");
            int cost = ((Long) obj.get("cost")).intValue();
            int speed = ((Long) obj.get("speed")).intValue();
            String imagePath = TEXTURE_PATH + obj.get("imagePath");
            System.out.println(imagePath);

            upgradeables.add(new Upgradeable(this, name, cost, speed, imagePath));
        }
    }

    private void createCookie() {
        ImageView iv = new ImageView();
        iv.setImage(new Image(TEXTURE_PATH + "cookie.png"));
        iv.setFitHeight(COOKIE_SIZE);
        iv.setFitWidth(COOKIE_SIZE);
        draw(iv, COOKIE_X - COOKIE_SIZE / 2, COOKIE_Y - COOKIE_SIZE / 2);

        iv.setOnMouseClicked(e -> {
            double mouseX = e.getSceneX();
            double mouseY = e.getSceneY();

            Scale scale = new Scale();
            iv.getTransforms().add(scale);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(scale.xProperty(), 1), new KeyValue(scale.yProperty(), 1)),
                    new KeyFrame(Duration.millis(100), new KeyValue(scale.xProperty(), 1.05), new KeyValue(scale.yProperty(), 1.05)),
                    new KeyFrame(Duration.millis(200), new KeyValue(scale.xProperty(), 1), new KeyValue(scale.yProperty(), 1))
            );

            timeline.setCycleCount(1);
            timeline.play();


            changeCookies(clickSpeed);
            updateCookiesLabel();
            createCookieClone(mouseX, mouseY);
        });
    }

    private void createCookiesLabel() {
        cookiesLabel = new Text("Cookies: 0");
        cookiesLabel.setX(50);
        cookiesLabel.setY(50);
        cookiesLabel.setFill(Color.BLACK);
        cookiesLabel.setStyle("-fx-font: 24 arial");
        draw(cookiesLabel);
    }

    private void createCookieClone(double x, double y) {
        Random random = new Random();
        int n = random.nextInt(-100, 100);
        int p = random.nextBoolean() ? 1 : -1;

        ImageView iv = new ImageView();
        iv.setImage(new Image(TEXTURE_PATH + "cookie.png"));
        iv.setFitHeight(30);
        iv.setFitWidth(30);
        iv.setX(x);
        iv.setY(y);
        iv.setOpacity(.8);
        draw(iv);

        Translate translate = new Translate();
        iv.getTransforms().add(translate);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(translate.xProperty(), 0), new KeyValue(translate.yProperty(), 0)),
                new KeyFrame(Duration.millis(50), new KeyValue(translate.xProperty(), p*10), new KeyValue(translate.yProperty(), -28)),
                new KeyFrame(Duration.millis(100), new KeyValue(translate.xProperty(), p*15), new KeyValue(translate.yProperty(), -30)),
                new KeyFrame(Duration.millis(150), new KeyValue(translate.xProperty(), p*20), new KeyValue(translate.yProperty(), -28)),
                new KeyFrame(Duration.millis(200), new KeyValue(translate.xProperty(), p*30), new KeyValue(translate.yProperty(), 0))
        );

        timeline.play();
        timeline.setOnFinished(e -> {
            iv.setOpacity(0);
            removeNode(iv);
        });

    }

    private void createMilk() {
        double waveShiftTime = 2000;
        double waveSize = 100;

        Rectangle rect = new Rectangle(550, 200);
        rect.setX(0);
        rect.setY(300);
        rect.setOpacity(.5);
        rect.setFill(Paint.valueOf("BROWN"));
        draw(rect);

        for (int i=0; i<4; i++) {
            Circle wave = new Circle(waveSize);
            wave.setCenterX(i*150);
            wave.setCenterY(250);
            wave.setFill(Paint.valueOf("WHITE"));
            draw(wave);

            Translate translate = new Translate();
            wave.getTransforms().add(translate);

            double timeToEnd = (550-i*150)/(waveShiftTime/2);
            double distanceToStart = i*150+waveSize/2;
            double timeToStart = distanceToStart/(waveShiftTime/2);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(translate.xProperty(), 0)),
                    new KeyFrame(Duration.millis(timeToEnd), new KeyValue(translate.xProperty(), 550)),
                    new KeyFrame(Duration.millis(timeToEnd+.001), new KeyValue(translate.xProperty(), -distanceToStart)),
                    new KeyFrame(Duration.millis(timeToStart), new KeyValue(translate.xProperty(), 0))
            );

            timeline.setCycleCount(-1);
            timeline.play();
        }
    }

    private void createShop() {
        Rectangle rect = new Rectangle(250, 500);
        rect.setX(550);
        rect.setY(0);
        rect.setFill(Paint.valueOf("BLUE"));
        draw(rect);

        for (int i = 0; i < upgradeables.size(); i++) {
            Upgradeable upgradeable = upgradeables.get(i);
            System.out.println(upgradeable.getName());
            Rectangle button = upgradeable.createButton(i);

            button.setOnMouseClicked(e -> {
                buy(upgradeable);
            });
        }
    }

    private void updateCookiesLabel() {
        cookiesLabel.setText("Cookies: " + cookies);
    }

    public void buy(Upgradeable upgradeable) {
        int cost = upgradeable.getCost();
        int speed = upgradeable.getSpeed();
        boolean isAffordable = cookies >= cost;

        if (isAffordable) {
            cookies -= cost;
            updateCookiesLabel();

            autoClickSpeed += speed;
            upgradeable.updateCost();
            upgradeable.spawn();
        }
    }
}
