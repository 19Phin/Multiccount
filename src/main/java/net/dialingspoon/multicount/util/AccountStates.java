package net.dialingspoon.multicount.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountStates extends PersistentState {
    private final ConcurrentHashMap<UUID, Integer> uuidToIntMap = new ConcurrentHashMap<>();

    // Create persistent state multicount.dat
    public AccountStates() {
        super();
    }

    public static AccountStates fromNbt(NbtCompound nbt) {
        AccountStates storage = new AccountStates();
        NbtCompound mapTag = nbt.getCompound("AccountStates");

        for (String key : mapTag.getKeys()) {
            UUID uuid = UUID.fromString(key);
            int value = mapTag.getInt(key);
            storage.uuidToIntMap.put(uuid, value);
        }

        return storage;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound mapTag = new NbtCompound();

        for (Map.Entry<UUID, Integer> entry : uuidToIntMap.entrySet()) {
            mapTag.putInt(entry.getKey().toString(), entry.getValue());
        }

        nbt.put("AccountStates", mapTag);

        return nbt;
    }

    // Getters and setters
    public Integer getValue(UUID uuid) {
        return uuidToIntMap.getOrDefault(uuid, 1);
    }

    public void setValue(UUID uuid, int value) {
        uuidToIntMap.put(uuid, value);
        this.setDirty(true);
    }
}