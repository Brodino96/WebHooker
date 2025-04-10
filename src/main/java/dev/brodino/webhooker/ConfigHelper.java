package dev.brodino.webhooker;

import io.wispforest.owo.config.annotation.Config;
import java.util.HashMap;

@Config(name = "webhooker", wrapperName = "Config")
public class ConfigHelper {
    public String image;
    public String username;
    public HashMap<String,String> channelList;

    public ConfigHelper() {
        this.image = "https://img.freepik.com/psd-gratis/single-yellow-potato-closeup-studio-shot_191095-85935.jpg";
        this.username = "Webhooker";
        this.channelList = new HashMap<>();
        this.channelList.put("placeholder", "https://localhost:6969/discord");
    }
}