package com.onaple.crowdbinding.service;

import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupService {
    Optional<UUID> getGroupLeader(UUID groupId);

    Optional<UUID> getGroupId(Player player);

    List<UUID> getMembers(UUID groupId);
}
