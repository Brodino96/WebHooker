package dev.brodino.webhooker;

import io.wispforest.owo.config.annotation.Config;
import java.util.HashMap;

@Config(name = "webhooker", wrapperName = "Config")
public class ConfigHelper {
    public String image;
    public String username;
    public HashMap<String,String> channelList;
    public int embedColor;
    public HashMap<String,String> taggableList;
    public String[] defaultTags;

    public ConfigHelper() {

        this.image = "https://img.freepik.com/psd-gratis/single-yellow-potato-closeup-studio-shot_191095-85935.jpg";

        this.username = "WebHooker";

        this.channelList = new HashMap<>();
        this.channelList.put("example", "https://discord.com/api/webhooks/your-webhook-url");

        this.embedColor = 5814783; // Discord blurple color

        this.taggableList = new HashMap<>();
        this.taggableList.put("everyone", "@everyone");
        this.taggableList.put("user", "1111111111");

        this.defaultTags = new String[]{"everyone"};
    }
}