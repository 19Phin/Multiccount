package net.dialingspoon.multicount.server.config;

import net.dialingspoon.multicount.Multicount;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigWriter {

    private static final String FILE_PATH = "config/"+ Multicount.MOD_ID +".properties";

    public static void writeToFile(Map<String, String> map) {
        File file = new File(FILE_PATH);
        // Check if the file exists, if not, create it
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Write map to file in the form `key=value`
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Write "command" key if present
            if (map.containsKey("command")) {
                writer.write("command=" + map.get("command"));
                writer.newLine();
            }
            // Write "default_accounts" key if present
            if (map.containsKey("default_accounts")) {
                writer.write("default_accounts=" + map.get("default_accounts"));
                writer.newLine();
            }

            // Write remaining keys
            for (Map.Entry<String, String> entry : map.entrySet()) {
                // Skip "command" and "default_accounts" keys, as they are already written
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