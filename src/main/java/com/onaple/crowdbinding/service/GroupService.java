package com.onaple.crowdbinding.service;

import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupService {

    /**
     * Get the leader of the given group
     * @param groupId The Id of the group
     * @return The optional player who is the leader if the group exist
     */
    Optional<Player> getGroupLeader(UUID groupId);

    /**
     * Get the Id of the current group  of the player
     * @param player The player that you want to get the group
     * @return The optional Id of the group if the player have one
     */
    Optional<UUID> getGroupId(Player player);

    /**
     * Get the List of players in the given group
     * @param groupId The Id of the group
     * @return The list of the groups members
     */
    List<Player> getMembers(UUID groupId);

    /**
     * Add a player to a group
     * @param newMember The player you want to add to the group
     * @param Group The Id of the group
     */
    void addPlayer(Player newMember,UUID Group);

    /**
     * Kick a player from his current group
     * @param player The player you want to kick
     */
    void kickPlayer(Player player);


}
