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
    private Player leader;
    private Collection<Player> players = new HashSet<>();
    private UUID uuid = UUID.randomUUID();
    private GroupMessageChannel messageChannel;

    public Group(Player player) {
        leader = player;
        this.players.add(player);
        messageChannel = new GroupMessageChannel();
        messageChannel.addMember(player);
    }

    public Player getLeader() {
        return leader;
    }
    public Collection<Player> getPlayers() {
        return players;
    }
    public UUID getUuid() {
        return uuid;
    }
    public GroupMessageChannel getMessageChannel() {
        return messageChannel;
    }

    public void addPlayer(Player player) {
        players.add(player);
        messageChannel.addMember(player);
    }
    public void removePlayer(Player player) {
        players.remove(player);
        messageChannel.removeMember(player);
    }

    public void setFirstLeader() {
        if (players.size() > 0) {
            leader = players.iterator().next();
        }
    }
    public void setLeader(Player player) {
        leader = player;
    }
}
