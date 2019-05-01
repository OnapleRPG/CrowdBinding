package com.onaple.crowdbinding.data;

import com.onaple.crowdbinding.GroupMessageChannel;
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
    private GroupMessageChannel messageChannel;

    public Group(UUID player) {
        leader = player;
        this.players.add(player);
        messageChannel = new GroupMessageChannel();
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
    public GroupMessageChannel getMessageChannel() {
        return messageChannel;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        messageChannel.addMember(player);
    }
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        messageChannel.removeMember(player);
    }
}
