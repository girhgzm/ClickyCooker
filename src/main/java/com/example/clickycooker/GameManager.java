package com.example.clickycooker;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.TimerTask;


public class GameManager implements Stage {
    private static final String UPGRADES_DATA_PATH = "src/main/resources/assets/data/upgradeables.json";
    private static final String TEXTURE_PATH = "assets/textures/";

    public static final double COOKIE_X = 200.0;
    public static final double COOKIE_Y = 250.0;
    public static final double COOKIE_SIZE = 300.0;
    private static int cookies;
    private static int clickSpeed;
    private static int autoClickSpeed;
    private static Text cookiesLabel;
    private static Text autoClickSpeedLabel;
    private static final HashMap<String, Upgrade> upgrades = new HashMap<>();

    private static Achievement achievement;
    private static Data data;

    public GameManager() throws IOException, ParseException {
        data = new Data();

        HashMap<String, Integer> playerData = data.load();

        cookies = playerData.get("cookies");
        clickSpeed = 1;
        achievement = new Achievement();

        Achievement.setLastAchievement(playerData.get("milestone"));

        playerData.remove("cookies");
        playerData.remove("milestone");

        createShop();
        createCookie();

        loadUpgrades();

        //Set count and autoClickSpeed
        int r = 0;
        for (String name : playerData.keySet()) {
            int count = playerData.get(name);

            Upgrade upgrade = upgrades.get(name);

            for (int n=0; n<count; n++) {
                upgrade.spawn(r);
                r++;
            }

            int cost = (int) (upgrade.getCost()*Math.pow(1.2, count));
            upgrade.setCost(cost);

            autoClickSpeed += upgrade.calculateAutoClickSpeed();
        }

        createLabels();

        //Game loop
        FXGL.getGameTimer().runAtInterval (new TimerTask() {
            @Override
            public void run() {
                changeCookies(autoClickSpeed);
                try {
                    saveData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, Duration.millis(1000));
    }

    private void saveData() throws IOException {
        HashMap<String, Integer> playerData = new HashMap<>();

        playerData.put("cookies", cookies);
        playerData.put("milestone", Achievement.getLastAchievement());

        for (String name : upgrades.keySet()) {
            Upgrade upgrade = upgrades.get(name);
            int count = upgrade.getCount();

            playerData.put(name, count);
        }

        data.save(playerData);
    }

    public void changeCookies(int n) {
        cookies += n;
        cookiesLabel.setText("Cookies: " + cookies);

        if (achievement.haveAchievement(cookies)) {
            try {
                achievement.obtainAchievement(cookies);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void changeAutoClickSpeed(int speed) {
        autoClickSpeed += speed;
        autoClickSpeedLabel.setText("speed: " + autoClickSpeed + "/s");
    }

    private void loadUpgrades() throws IOException, ParseException {
        JSONObject jsonObject = readJson(UPGRADES_DATA_PATH);
        JSONArray jsonArray = (JSONArray) jsonObject.get("upgradeables");

        int i = 0;
        for (Object o : jsonArray) {
            JSONObject obj = (JSONObject) o;
            String name = (String) obj.get("name");
            int cost = ((Long) obj.get("cost")).intValue();
            int speed = ((Long) obj.get("speed")).intValue();
            String imagePath = TEXTURE_PATH + obj.get("imagePath");

            Upgrade upgrade = new Upgrade(name, cost, speed, imagePath);
            GameManager.upgrades.put(name, upgrade);

            Rectangle button = upgrade.createButton(i);
            button.setOnMouseClicked(e -> buy(upgrade));

            i++;
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
            createCookieClone(mouseX, mouseY);
        });
    }

    private void createLabels() {
        String cookiesText = "Cookies: " + cookies;
        cookiesLabel = new Text(cookiesText);
        cookiesLabel.setX(COOKIE_X);
        cookiesLabel.setY(50);
        cookiesLabel.setFill(Color.BLACK);
        cookiesLabel.setStyle("-fx-font: 24 arial");
        draw(cookiesLabel);

        String autoClickSpeedText = "speed: " + autoClickSpeed + "/sec";
        autoClickSpeedLabel = new Text(autoClickSpeedText);
        autoClickSpeedLabel.setX(COOKIE_X);
        autoClickSpeedLabel.setY(70);
        autoClickSpeedLabel.setFill(Color.BLACK);
        autoClickSpeedLabel.setStyle("-fx-font: 16 arial");
        draw(autoClickSpeedLabel);

    }

    private void createCookieClone(double x, double y) {
        Random random = new Random();
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
        double waveShiftTime = 10000;
        double waveSize = 100;

        Rectangle rect = new Rectangle(550, 200);
        rect.setX(0);
        rect.setY(300);
        rect.setOpacity(.5);
        rect.setFill(Color.BROWN);
        draw(rect);

        for (int i=0; i<4; i++) {
            Circle wave = new Circle(waveSize);
            wave.setCenterX(i*150);
            wave.setCenterY(250);
            wave.setFill(Color.WHITE);
            draw(wave);

            Translate translate = new Translate();
            wave.getTransforms().add(translate);

            double timeToMidPoint = (550-i*150)/(waveShiftTime/2);
            double distanceToStart = i*150+waveSize/2;
            double timeToEnd = distanceToStart/(waveShiftTime/2);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(translate.xProperty(), 0)),
                    new KeyFrame(Duration.millis(timeToMidPoint), new KeyValue(translate.xProperty(), 550)),
                    new KeyFrame(Duration.millis(timeToMidPoint+.001), new KeyValue(translate.xProperty(), -distanceToStart)),
                    new KeyFrame(Duration.millis(timeToEnd), new KeyValue(translate.xProperty(), 0))
            );

            timeline.setCycleCount(-1);
            timeline.play();
        }
    }

    private void createShop() {
        Rectangle rect = new Rectangle(250, 500);
        rect.setX(550);
        rect.setY(0);
        rect.setFill(Color.BLUE);
        draw(rect);
    }

    public void buy(Upgrade upgrade) {
        int cost = upgrade.getCost();
        int speed = upgrade.getSpeed();
        boolean isAffordable = cookies >= cost;

        if (isAffordable) {
            changeCookies(-cost);
            changeAutoClickSpeed(speed);

            upgrade.updateCost();
            upgrade.spawn();
        }
    }
}
