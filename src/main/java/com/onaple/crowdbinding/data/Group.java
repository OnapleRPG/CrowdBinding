package com.onaple.crowdbinding.data;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * Represents a group of players.
 */
public class Group {
    private UUID leader;
    private Collection<UUID> players = new HashSet<>();
    private UUID uuid = UUID.randomUUID();

    public Group(Player...players) {
        for (Player player : players) {
            this.players.add(player.getUniqueId());
        }
    }

    public Group(UUID...players) {
        for (UUID player : players) {
            this.players.add(player);
        }
    }

    public Group(UUID player) {
        leader = player;
        this.players.add(player);
    }

    public UUID getLeader() {
        return leader;
    }
    public Collection<UUID> getPlayers() {
        return players;
    }
    public UUID getUuid() {
        return uuid;
    }

    public void addPlayer(UUID player) {
        players.add(player);
    }
    public void removePlayer(UUID player) {
        players.remove(player);
    }
}
