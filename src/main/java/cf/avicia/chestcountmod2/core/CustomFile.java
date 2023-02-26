package cf.avicia.chestcountmod2.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CustomFile extends File {
    public CustomFile(String child) {
        this(MinecraftClient.getInstance().runDirectory, child);
    }

    public CustomFile(File parent, String child) {
        super(parent, child);

        if (!super.exists()) {
            System.out.println("Creating file " + super.getAbsolutePath() + " as it did not exist");
            try {
                File file = new File(child);
                boolean success = file.getParentFile().mkdirs();
                success = success && super.createNewFile();
                if (!success) {
                    System.out.println("Didn't create file");
                }
                this.writeJson("{}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JsonObject readJson() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(this), StandardCharsets.UTF_8); JsonReader jsonReader = new JsonReader(reader)) {
            jsonReader.setLenient(true);
            return new Gson().fromJson(jsonReader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            this.writeJson("{}");
            return new Gson().fromJson("{}", JsonObject.class);
        }
    }

    public void writeJson(JsonObject jsonObject) {
        this.writeJson(jsonObject.toString());
    }

    public void writeJson(String text) {
        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(this), StandardCharsets.UTF_8)) {
            fileWriter.write(text, 0, text.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
