package dev.brodino.webhooker;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modifiable;

import java.util.Map;
import java.util.HashMap;

@Config(name = "webhooker", wrapperName = "Config")
@Modifiable
public class ConfigHelper {
    private String image;
    private String username;
    private HashMap<String, String> channelList;
    private boolean mentionEveryone;
    private int embedColor;

    public ConfigHelper() {
        this.image = "https://example.com/default-avatar.png";
        this.username = "Webhooker";
        this.channelList = new HashMap<>();
        this.channelList.put("example", "https://discord.com/api/webhooks/your-webhook-url");
        this.mentionEveryone = false;
        this.embedColor = 5814783; // Discord blurple color
    }
    
    public String getImage() {
        return image;
    }
    
    public String getUsername() {
        return username;
    }
    
    public Map<String, String> getChannelList() {
        return new HashMap<>(channelList);
    }
    
    public boolean shouldMentionEveryone() {
        return mentionEveryone;
    }
    
    public int getEmbedColor() {
        return embedColor;
    }
    
    public String getWebhookUrl(String channel) {
        return channelList.get(channel);
    }
    
    public boolean hasChannel(String channel) {
        return channelList.containsKey(channel);
    }
}