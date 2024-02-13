package net.dialingspoon.multicount.server.util;

import net.dialingspoon.multicount.Multicount;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

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
        // Get data directories
        File playerDataDirectory = server.getSavePath(WorldSavePath.PLAYERDATA).toFile();
        File advancementsDirectory = server.getSavePath(WorldSavePath.ADVANCEMENTS).toFile();
        File statsDirectory = server.getSavePath(WorldSavePath.STATS).toFile();

        // Check if no .dat0 files exist in the player data directory
        File[] dat0Files = playerDataDirectory.listFiles((dir, name) -> name.endsWith(".dat0"));
        boolean oldFiles = dat0Files != null && dat0Files.length > 0;

        // Check if multicount.dat does not exist in the data directory
        boolean multicountDatNotExists = !new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "data/multicount.dat").exists();

        // Update files to new format
        if (oldFiles && multicountDatNotExists) {
            Multicount.LOGGER.info("Updating data format");
            try {
                renameFilesInDirectory(playerDataDirectory);
                renameFilesInDirectory(advancementsDirectory);
                renameFilesInDirectory(statsDirectory);
                processDatFiles(playerDataDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void processDatFiles(File playerDataDirectory) throws IOException {
        // Add current account number to persistent state
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
        // Up files by 1
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                // Sort files based on the [number] part in descending order
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
            NbtCompound compoundTag = NbtIo.readCompressed(datFile.toPath(), NbtSizeTracker.ofUnlimitedBytes());
            // Check if the "account" tag exists
            if (compoundTag.contains("account")) {
                return compoundTag.getInt("account");
            } else {
                Multicount.LOGGER.info("Warning: 'account' tag not found in NBT data");
                return 1; // Default value
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1; // Default value or handle the error case
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
            // The file name doesn't match the expected pattern
            return originalName;
        }
    }
}
