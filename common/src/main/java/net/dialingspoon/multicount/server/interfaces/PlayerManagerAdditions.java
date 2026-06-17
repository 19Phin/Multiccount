package net.dialingspoon.multicount.server.interfaces;

import java.util.UUID;

public interface PlayerManagerAdditions {
    void setAccount(UUID playerUuid, int current, int to);
}
