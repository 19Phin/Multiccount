package net.dialingspoon.multicount.server.util;

import net.dialingspoon.multicount.Multicount;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Updater {
    public static void checkAndUpdateDataFormat(MinecraftServer server) {
        File playerDataDirectory = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
        File advancementsDirectory = server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile();
        File statsDirectory = server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();

        File[] dat0Files = playerDataDirectory.listFiles((dir, name) -> name.endsWith(".dat0"));
        boolean oldFiles = dat0Files != null && dat0Files.length > 0;

        boolean multicountDatNotExists = !new File(server.getWorldPath(LevelResource.ROOT).toFile(), "data/multicount.dat").exists();

        if (multicountDatNotExists) {
            try {
                if (oldFiles) {
                    Multicount.LOGGER.info("Updating data format");
                    renameFilesInDirectory(playerDataDirectory);
                    renameFilesInDirectory(advancementsDirectory);
                    renameFilesInDirectory(statsDirectory);
                    processDatFiles(playerDataDirectory);
                } else {
                    MakeMulticountFiles(playerDataDirectory);
                    MakeMulticountFiles(advancementsDirectory);
                    MakeMulticountFiles(statsDirectory);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void checkSingleplayerDataFormat(File directory) {
        File playerDataDirectory = new File(directory, String.valueOf(LevelResource.PLAYER_DATA_DIR));
        File advancementsDirectory = new File(directory, String.valueOf(LevelResource.PLAYER_ADVANCEMENTS_DIR));
        File statsDirectory = new File(directory, String.valueOf(LevelResource.PLAYER_STATS_DIR));
        File dataDirectory = new File(directory, String.valueOf(LevelResource.LEVEL_DATA_FILE));
        File primaryPlayerData = new File(playerDataDirectory, Multicount.accountHandler.uuid + ".dat");

        boolean multicountDatNotExists = !new File(directory, "data/multicount.dat").exists();

        if (multicountDatNotExists) {
            try {
                if (!primaryPlayerData.exists()) {
                    CompoundTag data = NbtIo.readCompressed(dataDirectory.toPath(), NbtAccounter.unlimitedHeap());
                    CompoundTag playerData = data.getCompound("Data").orElse(new CompoundTag()).getCompound("Player").orElse(null);
                    if (playerData != null) {
                        NbtIo.writeCompressed(playerData, primaryPlayerData.toPath());
                    }
                }

                MakeMulticountFiles(playerDataDirectory);
                MakeMulticountFiles(advancementsDirectory);
                MakeMulticountFiles(statsDirectory);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void MakeMulticountFiles(File directory) throws IOException {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String originalName = file.getName();
                    String newName = originalName + "1";
                    File newFile = new File(directory, newName);
                    Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void processDatFiles(File playerDataDirectory) throws IOException {
        File[] datFiles = playerDataDirectory.listFiles((dir, name) -> name.endsWith(".dat"));

        if (datFiles != null) {
            for (File datFile : datFiles) {
                String fileNameWithoutExtension = getFileNameWithoutExtension(datFile.getName());

                int accountValue = readAccountValueFromDat(datFile);
                Multicount.accountStates.setValue(UUID.fromString(fileNameWithoutExtension), accountValue);
            }
        }
    }

    private static void renameFilesInDirectory(File directory) throws IOException {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                Arrays.sort(files, Comparator.comparing(File::getName, Comparator.reverseOrder()));

                for (File file : files) {
                    String originalName = file.getName();
                    String newName = generateNewFileName(originalName);
                    File newFile = new File(directory, newName);
                    Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.deleteIfExists(file.toPath());
                }
            }
        }
    }

    private static String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
    }

    private static int readAccountValueFromDat(File datFile){
        try {
            CompoundTag compoundTag = NbtIo.readCompressed(datFile.toPath(), NbtAccounter.unlimitedHeap());

            if (compoundTag.contains("account")) {
                return compoundTag.getInt("account").get();
            } else {
                Multicount.LOGGER.info("Warning: 'account' tag not found in NBT data");
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    private static String generateNewFileName(String originalName) {
        Pattern pattern = Pattern.compile("^(.+?)\\.(\\w+)(\\d+)$");
        Matcher matcher = pattern.matcher(originalName);
        if (matcher.matches()) {
            String baseName = matcher.group(1);
            String extension = matcher.group(2);
            String number = matcher.group(3);

            int newNumber = Integer.parseInt(number) + 1;

            return baseName + "." + extension + newNumber;
        } else {
            return originalName;
        }
    }
}
