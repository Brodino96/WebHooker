package dev.brodino.webhooker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Path configPath;
    private Config.Type data;

    public Config() {
        Path dataDirectory = Path.of("config");

        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            this.configPath = dataDirectory.resolve(Webhooker.MOD_ID + ".json");
            this.load();
        } catch (IOException e) {
            Webhooker.LOGGER.error("Failed to load " + Webhooker.MOD_ID + ".json");
        }
    }

    private void load() throws IOException {
        if (!Files.exists(this.configPath)) {
            this.data = this.getDefaults();
            this.save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(this.configPath)) {
            this.data = GSON.fromJson(reader, Config.Type.class);
            if (data == null) {
                this.data = this.getDefaults();
                this.save();
            }
        }
    }

    public boolean reload() {
        try {
            this.load();
            return true;
        } catch (IOException e) {
            Webhooker.LOGGER.error("Failed  to reload config", e);
            return false;
        }
    }

    private void save() throws IOException {
        try (Writer writer = Files.newBufferedWriter(this.configPath)) {
            GSON.toJson(this.data, writer);
        }
    }

    private Config.Type getDefaults() {
        return new Config.Type();
    }

    private static class Type {
        public String image = "https://img.freepik.com/psd-gratis/single-yellow-potato-closeup-studio-shot_191095-85935.jpg";
        public String username = "WebHooker";

        public HashMap<String, String> channelList = new HashMap<>() {{
            put("example", "https://discord.com/api/webhooks/your-webhook-url");
        }};

        public int embedColor = 5814783; // Discord blurple

        public HashMap<String, String> taggableList = new HashMap<>() {{
            put("everyone", "@everyone");
            put("user", "11111111111");
        }};

        public String[] defaultTags = new String[]{ "everyone" };
    }

    public String getImage() {
        return this.data.image;
    }

    public String getUsername() {
        return this.data.username;
    }

    public HashMap<String, String> getChannelList() {
        return this.data.channelList;
    }

    public int getEmbedColor() {
        return this.data.embedColor;
    }

    public HashMap<String, String> getTaggableList() {
        return this.data.taggableList;
    }

    public String[] getDefaultTags() {
        return this.data.defaultTags;
    }
}