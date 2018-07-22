package com.onaple.crowdbinding;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class GroupManager {
    /**
     * A mapping for any player to his group.
     */
    private final Map<UUID, Group> groups = new HashMap<>();

    /**
     * A mapping of all the currently pending invitations to any group. The key {@code UUID} is the unique identifier
     * of the invitation.
     */
    private final Map<UUID, PendingInvitation> pendingInvitationsMap = new HashMap<>();

    @Inject
    private Logger logger;

    Group createAndRegisterGroup(UUID... players) {
        Group group = new Group(players);
        for (UUID playerUuid : players) {
            this.groups.put(playerUuid, group);
        }

        return group;
    }


    /**
     * @return {@code true} if the left process succeed, {@code false} if it failed or has been cancelled
     */
    boolean processPlayerLeaveGroup(UUID player) {
        Optional<Group> potentialGroup = getGroupFromPlayerUuid(player);

        if (!potentialGroup.isPresent()) {
            logger.warn("Player {} left a group but had no group.", player);
            return false;
        }
        Group leftGroup = potentialGroup.get();
        if (!leftGroup.getPlayers().remove(player)) {
            logger.warn("Player {} left a group he was not part of.", player);
        }

        if (!groups.remove(player, leftGroup)) {
            logger.warn("Player {} left an unregistered or wrongly registered group.", player);
        }

        // We do not allow a group of 1 player, so if there were only 2 players in the left group,
        // we destroy it.
        if (leftGroup.getPlayers().size() == 1) {
            UUID orphan = leftGroup.getPlayers().iterator().next();
            leftGroup.getPlayers().clear();
            groups.remove(orphan, leftGroup);
        }
        return true;
    }

    void processInvitationAccepted(PendingInvitation invitation) {
        Preconditions.checkArgument(pendingInvitationsMap.containsKey(invitation.getUuid()), "The accepted invitation was not found");
        Group invitersGroup = getGroupFromPlayerUuid(invitation.getInviter())
                .orElse(createAndRegisterGroup(invitation.getInviter()));

        // The invited leaves his previous group if she/he had one
        getGroupFromPlayerUuid(invitation.getInvited()).ifPresent(group -> processPlayerLeaveGroup(invitation.getInvited()));
        processPlayerJoinGroup(invitation.getInvited(), invitersGroup);
        pendingInvitationsMap.remove(invitation.getUuid(), invitation);
    }

    void processPlayerJoinGroup(UUID joiningPlayer, Group joinedGroup) {
        Preconditions.checkArgument(!getGroupFromPlayerUuid(joiningPlayer).isPresent());
        groups.put(joiningPlayer, joinedGroup);
        joinedGroup.getPlayers().add(joiningPlayer);
    }

    public Optional<Group> getGroupFromPlayerUuid(UUID uuid) {
        return Optional.ofNullable(groups.get(uuid));
    }

    public Optional<Group> getGroup(Player player) {
        return this.getGroupFromPlayerUuid(player.getUniqueId());
    }
}
