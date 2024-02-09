package net.dialingspoon.multicount.util;

import net.dialingspoon.multicount.Multicount;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class SingleplayerAccountHandler {

    public int account = 1;
    public String uuid;
    public Map<String,String> lanUuids = new HashMap<>();

    public NbtCompound getAccount(File worldFile) {
        // Copy account advancements to main
        File advancementsFile = new File(Paths.get(worldFile.getAbsolutePath(), WorldSavePath.ADVANCEMENTS.getRelativePath()).toFile(), uuid + ".json");
        get(advancementsFile);

        File statsFile = new File(Paths.get(worldFile.getAbsolutePath(), WorldSavePath.STATS.getRelativePath()).toFile(), uuid + ".json");
        get(statsFile);

        // Return new player data or delete main
        File mainPlayerDat = new File(Paths.get(worldFile.getAbsolutePath(), WorldSavePath.PLAYERDATA.getRelativePath()).toFile(), uuid + ".dat");
        File playerDat = new File(Paths.get(worldFile.getAbsolutePath(), WorldSavePath.PLAYERDATA.getRelativePath()).toFile(), uuid + ".dat" + account);

        NbtCompound playerData = null;
        try {
            if (playerDat.exists()) {
                playerData = NbtIo.readCompressed(playerDat);
            } else if (mainPlayerDat.exists()) {
                mainPlayerDat.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return playerData;
    }

    public void saveAccount(MinecraftServer server) {
        File playerDataFile = new File(server.getSavePath(WorldSavePath.PLAYERDATA).toFile(), uuid+ ".dat");
        save(playerDataFile);

        File advancementsFile = new File(server.getSavePath(WorldSavePath.ADVANCEMENTS).toFile(), uuid + ".json");
        save(advancementsFile);

        File statsFile = new File(server.getSavePath(WorldSavePath.STATS).toFile(), uuid + ".json");
        save(statsFile);
    }


    private void get(File file){
        try {
            File newAccountFile = new File(file.getPath() + account);

            // Check if new account file exists
            if (newAccountFile.exists()) {
                // Copy contents from new account file to the original file
                Files.copy(newAccountFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                // If new account file doesn't exist, make the original file also not exist
                Files.deleteIfExists(file.toPath());
            }
        } catch (IOException e) {
            Multicount.LOGGER.error("Couldn't switch player data in {" + file.getPath() + "}", e);
        }
    }
    private void save(File file){
        try {
            File oldAccountFile = new File(file.getPath() + account);

            // Copy the current file to the old account file
            Files.copy(file.toPath(), oldAccountFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Multicount.LOGGER.error("Couldn't switch player data in {" + file.getPath() + "}", e);
        }
    }

}