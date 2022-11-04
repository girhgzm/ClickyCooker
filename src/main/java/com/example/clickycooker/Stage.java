package com.example.clickycooker;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;


public interface Stage {
    default Entity draw(Node node) {
        return FXGL.entityBuilder().at(0,0).view(node).buildAndAttach();
    }
    default Entity draw(Node node, double x, double y) {
        return FXGL.entityBuilder().at(x,y).view(node).buildAndAttach();
    }

    default void removeNode(Node node) {
        FXGL.removeUINode(node);
    }
}
