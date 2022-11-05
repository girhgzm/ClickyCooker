package com.example.clickycooker;

import com.almasb.fxgl.entity.Entity;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GemRain extends Event {
    private static final String GEM_IMAGE_PATH = "assets/textures/gem.png";
    private static int value = 10;

    @Override
    void start(GameManager gm) {
        AtomicBoolean isRemoved = new AtomicBoolean(false);
        Random random = new Random();

        ImageView image = new ImageView(GEM_IMAGE_PATH);
        image.setFitWidth(50);
        image.setFitHeight(50);

        Entity gem = draw(image);
        gem.setX(random.nextDouble(500));
        gem.setY(-50);

        Translate translate = new Translate();
        image.getTransforms().add(translate);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(translate.yProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(translate.yProperty(), 550))
        );

        timeline.play();
        timeline.setCycleCount(1);

        timeline.setOnFinished(e -> {
            if (isRemoved.get()) return;
            isRemoved.set(true);

            gem.removeFromWorld();
        });

        image.setOnMouseClicked(e -> {
            if (isRemoved.get()) return;
            isRemoved.set(true);

            gm.changeCookies(value);
            value *= 1.5;

            gem.removeFromWorld();
        });
    }
}
