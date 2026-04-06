package net.dialingspoon.multicount.util;

import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.server.util.Updater;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SingleplayerAccountHandler {

    public int account = 1;
    public String uuid;

    public CompoundTag getAccount(File worldFile, String baseUuid) {
        Updater.checkSingleplayerDataFormat(worldFile);

        File advancementsFile = new File(Paths.get(worldFile.getAbsolutePath(), LevelResource.PLAYER_ADVANCEMENTS_DIR.id()).toFile(), baseUuid + ".json");
        get(advancementsFile);
        File statsFile = new File(Paths.get(worldFile.getAbsolutePath(), LevelResource.PLAYER_STATS_DIR.id()).toFile(), baseUuid + ".json");
        get(statsFile);

        File playerDataPath = Paths.get(worldFile.getAbsolutePath(), LevelResource.PLAYER_DATA_DIR.id()).toFile();
        File mainPlayerDat = new File(playerDataPath, baseUuid + ".dat");
        File playerDat = new File(playerDataPath, baseUuid + ".dat" + account);

        return loadAccountData(mainPlayerDat, playerDat);
    }

    private CompoundTag loadAccountData(File mainPlayerDat, File playerDat) {
        CompoundTag playerData = null;
        try {
            if (playerDat.exists()) {
                playerData = NbtIo.readCompressed(playerDat.toPath(), NbtAccounter.unlimitedHeap());
            } else if (mainPlayerDat.exists()) {
                Files.copy(mainPlayerDat.toPath(), new File(mainPlayerDat.getPath() + "_old").toPath(), StandardCopyOption.REPLACE_EXISTING);
                mainPlayerDat.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return playerData;
    }

    public void saveAccount(MinecraftServer server) {
        File playerDataFile = new File(server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile(), uuid+ ".dat");
        save(playerDataFile);

        File advancementsFile = new File(server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile(), uuid + ".json");
        save(advancementsFile);

        File statsFile = new File(server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile(), uuid + ".json");
        save(statsFile);
    }


    private void get(File file){
        try {
            File newAccountFile = new File(file.getPath() + account);

            if (newAccountFile.exists()) {
                Files.copy(newAccountFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.deleteIfExists(file.toPath());
            }
        } catch (IOException e) {
            Multicount.LOGGER.error("Couldn't switch player data in {" + file.getPath() + "}", e);
        }
    }
    private void save(File file){
        try {
            File oldAccountFile = new File(file.getPath() + account);

            Files.copy(file.toPath(), oldAccountFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Multicount.LOGGER.error("Couldn't switch player data in {" + file.getPath() + "}", e);
        }
    }

}