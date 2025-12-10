package net.dialingspoon.multicount.server.config;

import net.dialingspoon.multicount.Multicount;

import java.util.Map;

public class ModConfigs {

    public int accountNum = 3;
    public boolean command  = false;
    public Map<String, String> configsList;


    public ModConfigs(){
        configsList = ConfigWriter.readFromFile();
        // Get default max accounts from file
        if (configsList.containsKey("default_accounts")) {
            try {
                accountNum = Integer.parseInt(configsList.get("default_accounts"));
            } catch (NumberFormatException e) {
                Multicount.LOGGER.error("\"" + configsList.get("default_accounts") + "\" is not a valid number, defaulting");
            }
        }

        // Set whether the command can be used
        if (configsList.containsKey("command")) {
            try {
                command = Boolean.parseBoolean(configsList.get("command"));
            } catch (NumberFormatException e) {
                Multicount.LOGGER.error("\"" + configsList.get("command") + "\" is not valid, defaulting");
            }
        }

        // Paste to file
        configsList.put("default_accounts", String.valueOf(accountNum));
        configsList.put("command", String.valueOf(command));
        ConfigWriter.writeToFile(configsList);
    }
    public void setMaxAccountNum(Map<String, String> map) {
        configsList = ConfigWriter.readFromFile();
        configsList.putAll(map);
        ConfigWriter.writeToFile(configsList);
    }
    public void setValue(String key, String value) {
        configsList = ConfigWriter.readFromFile();
        configsList.put(key, value);
        ConfigWriter.writeToFile(configsList);
    }
}
