package net.dialingspoon.multicount.server.config;

import net.dialingspoon.multicount.Multicount;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigWriter {

    private static final String FILE_PATH = "config/"+ Multicount.MOD_ID +".properties";

    public static void writeToFile(Map<String, String> map) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            if (map.containsKey("command")) {
                writer.write("command=" + map.get("command"));
                writer.newLine();
            }
            if (map.containsKey("default_accounts")) {
                writer.write("default_accounts=" + map.get("default_accounts"));
                writer.newLine();
            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!entry.getKey().equals("command") && !entry.getKey().equals("default_accounts")) {
                    writer.write(entry.getKey() + "=" + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> readFromFile() {
        Map<String, String> keyValueMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    keyValueMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyValueMap;
    }
}