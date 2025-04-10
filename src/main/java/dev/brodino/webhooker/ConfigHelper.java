package dev.brodino.webhooker;

import io.wispforest.owo.config.annotation.Config;
import java.util.HashMap;

@Config(name = "webhooker", wrapperName = "Config")
public class ConfigHelper {
    public String image;
    public String username;
    public HashMap<String,String> channelList;
    public boolean mentionEveryone;
    public int embedColor;

    public ConfigHelper() {
        this.image = "https://img.freepik.com/psd-gratis/single-yellow-potato-closeup-studio-shot_191095-85935.jpg";
        this.username = "WebHooker";
        this.channelList = new HashMap<>();
        this.channelList.put("example", "https://discord.com/api/webhooks/your-webhook-url");
        this.mentionEveryone = true;
        this.embedColor = 5814783; // Discord blurple color
    }
}