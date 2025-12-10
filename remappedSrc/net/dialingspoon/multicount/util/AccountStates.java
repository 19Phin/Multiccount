package net.dialingspoon.multicount.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountStates extends PersistentState {
    private final ConcurrentHashMap<UUID, Integer> uuidToIntMap = new ConcurrentHashMap<>();

    private Map<String, Integer> toStringIntMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<UUID, Integer> e : uuidToIntMap.entrySet()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        return map;
    }

    private void fromStringIntMap(Map<String, Integer> map) {
        uuidToIntMap.clear();
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            UUID id = UUID.fromString(e.getKey());
            uuidToIntMap.put(id, e.getValue());
        }
    }

    public static final Codec<AccountStates> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            Codec.unboundedMap(Codec.STRING, Codec.INT)
                                    .fieldOf("AccountStates")
                                    .forGetter(AccountStates::toStringIntMap)
                    )
                    .apply(instance, map -> {
                        AccountStates state = new AccountStates();
                        state.fromStringIntMap(map);
                        return state;
                    })
    );


    // Getters and setters
    public int getValue(UUID uuid) {
        return uuidToIntMap.getOrDefault(uuid, 1);
    }

    public void setValue(UUID uuid, int value) {
        uuidToIntMap.put(uuid, value);
    }
    @Override
    public boolean isDirty() {
        return true;
    }
}