package net.dialingspoon.multicount.config;

import com.mojang.datafixers.util.Pair;
import net.dialingspoon.multicount.Multicount;

public class ModConfigs {
    private static SimpleConfig CONFIG;

    public static int accountnum;

    public static void registerConfigs() {
        ModConfigProvider configs = new ModConfigProvider();
        configs.addKeyValuePair(new Pair<>("accounts", 3));

        CONFIG = SimpleConfig.of(Multicount.MOD_ID + "config").provider(configs).request();

        accountnum = CONFIG.getOrDefault("accounts", 3);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}
