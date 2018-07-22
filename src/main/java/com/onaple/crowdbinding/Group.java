package com.onaple.crowdbinding;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

/**
 * Represents a group of players.
 */
public class Group {
    private Collection<UUID> players = new HashSet<>();

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

    /**
     * @return this group players
     */
    public Collection<UUID> getPlayers() {
        return players;
    }
}
