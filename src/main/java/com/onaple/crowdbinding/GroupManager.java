package com.onaple.crowdbinding;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public interface GroupManager {
    Optional<Group> getGroupFromPlayerUuid(UUID uuid);

    default Optional<Group> getGroup(Player player) {
        return getGroupFromPlayerUuid(player.getUniqueId());
    }
}
