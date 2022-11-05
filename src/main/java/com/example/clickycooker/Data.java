package com.example.clickycooker;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Data implements Stage {
    private static final String FILE_PATH = "src/main/resources/assets/data/playerData.json";

    public HashMap<String, Integer> load() throws IOException, ParseException {
        HashMap<String, Integer> data = new HashMap<>();

        JSONObject jsonObject = readJson(FILE_PATH);

        int cookies = ((Long) jsonObject.get("cookies")).intValue();
        int milestone = ((Long) jsonObject.get("milestone")).intValue();

        data.put("cookies", cookies);
        data.put("milestone", milestone);

        JSONArray arr = (JSONArray) jsonObject.get("upgrades");
        for (Object o : arr) {
            JSONObject upgrade = (JSONObject) o;

            String name = (String) upgrade.get("name");
            int count = ((Long) upgrade.get("count")).intValue();

            data.put(name, count);
        }

        return data;
    }

    public void save(HashMap<String, Integer> data) throws IOException {
        FileWriter file = new FileWriter(FILE_PATH);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        int cookies = data.get("cookies");
        int milestone = data.get("milestone");

        jsonObject.put("cookies", cookies);
        jsonObject.put("milestone", milestone);
        jsonObject.put("upgrades", jsonArray);

        data.remove("cookies");
        data.remove("milestone");

        for (String name : data.keySet()) {
            JSONObject upgrade = new JSONObject();
            upgrade.put("name", name);
            upgrade.put("count", data.get(name));
            jsonArray.add(upgrade);
        }

        file.write(jsonObject.toJSONString());
        file.close();
    }
}
