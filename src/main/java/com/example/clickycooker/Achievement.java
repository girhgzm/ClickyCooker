package com.example.clickycooker;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Achievement implements Stage {
    private static final String FILE_PATH = "src/main/resources/assets/data/achievements.json";
    private static int lastAchievement = 0;
    private static int[] milestoneList;
    private static JSONObject jsonObject;

    public Achievement() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        jsonObject = (JSONObject) parser.parse(new FileReader(FILE_PATH));

        Object[] set = jsonObject.keySet().toArray();
        milestoneList = Arrays.stream(set)
                .mapToInt(i->Integer.parseInt((String) i))
                .sorted()
                .toArray();
    }

    private int[] getObtainedMilestones(int n) {
        return Arrays.stream(milestoneList).filter(i -> i <= n).toArray();
    }

    public boolean haveAchievement(int cookies) {
        int milestone = 0;
        for (int n : milestoneList) {
            if (cookies >= n) {
                milestone = n;
            }
        }


        return milestone > lastAchievement;
    }

    public void obtainAchievement(int cookies) throws IOException, ParseException {
        int[] milestones = getObtainedMilestones(cookies);
        lastAchievement = milestones[milestones.length-1];

        for (int n : milestones) {
            JSONObject arr = (JSONObject) jsonObject.get(String.valueOf(n));

            if (arr != null) {
                String title = (String) arr.get("title");
                String imagePath = (String) arr.get("imagePath");

                double x = -150;
                double y = 400;
                double w = 150;
                double h = 50;

                Rectangle background = new Rectangle(w, h);
                background.setFill(Color.DARKBLUE);
                background.setX(x);
                background.setY(y);
                draw(background);

                Text au = new Text("Achievement unlocked!");
                au.setX(x + 5);
                au.setY(y + 10);
                au.setFill(Color.WHITE);
                au.setStyle("-fx-font: 8 arial");
                draw(au);

                Text text = new Text("\"" + title + "\"");
                text.setX(x + 5);
                text.setY(y + 30);
                text.setFill(Color.WHITE);
                text.setStyle("-fx-font: 12 arial");
                draw(text);

                ImageView iv = new ImageView("assets/textures/" + imagePath);
                iv.setFitWidth(30);
                iv.setFitHeight(30);
                iv.setX(x + 100);
                iv.setY(y + 10);
                draw(iv);

                Translate bgTranslate = new Translate();
                Translate auTranslate = new Translate();
                Translate textTranslate = new Translate();
                Translate imageTranslate = new Translate();

                background.getTransforms().add(bgTranslate);
                au.getTransforms().add(auTranslate);
                text.getTransforms().add(textTranslate);
                iv.getTransforms().add(imageTranslate);

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(bgTranslate.xProperty(), 0), new KeyValue(auTranslate.xProperty(), 0), new KeyValue(textTranslate.xProperty(), 0), new KeyValue(imageTranslate.xProperty(), 0)),
                        new KeyFrame(Duration.millis(250), new KeyValue(bgTranslate.xProperty(), -x), new KeyValue(auTranslate.xProperty(), -x), new KeyValue(textTranslate.xProperty(), -x), new KeyValue(imageTranslate.xProperty(), -x)),
                        new KeyFrame(Duration.millis(3000), new KeyValue(bgTranslate.xProperty(), -x), new KeyValue(auTranslate.xProperty(), -x), new KeyValue(textTranslate.xProperty(), -x), new KeyValue(imageTranslate.xProperty(), -x)),
                        new KeyFrame(Duration.millis(3250), new KeyValue(bgTranslate.xProperty(), 0), new KeyValue(auTranslate.xProperty(), 0), new KeyValue(textTranslate.xProperty(), 0), new KeyValue(imageTranslate.xProperty(), 0))
                );

                timeline.play();
                timeline.setOnFinished(e -> {
                    removeNode(background);
                    removeNode(au);
                    removeNode(text);
                    removeNode(iv);
                });
            }
        }
    }
}
