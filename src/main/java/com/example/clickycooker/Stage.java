package com.example.clickycooker;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.Node;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;


public interface Stage {
    default JSONObject readJson(String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(path));
    }

    default Entity draw(Node node) {
        return FXGL.entityBuilder().at(0,0).view(node).buildAndAttach();
    }
    default Entity draw(Node node, double x, double y) {
        return FXGL.entityBuilder().at(x,y).view(node).buildAndAttach();
    }

    default void addEntity(Entity ... entities) {
        FXGL.getGameWorld().addEntities(entities);
    }
    default void removeNode(Node node) {
        FXGL.removeUINode(node);
    }

}
