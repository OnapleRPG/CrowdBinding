package com.onaple.crowdbinding;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Plugin(id = "crowdbinding", name = "CrowdBinding", version = "0.1")
@Singleton
/**
 * CrowdBinding main class, a plugin that allows players to create and manage groups of players.
 */
public class CrowdBinding implements GroupManager {
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
    private Game game;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameInitializationEvent gameInitializationEvent) {

    }

    @Listener
    public void onClientDisconnect(ClientConnectionEvent.Disconnect clientDisconnectEvent) {

    }

    Group createAndRegisterGroup(UUID... players) {
        Group group = new Group(players);
        for (UUID playerUuid : players) {
            this.groups.put(playerUuid, group);
        }

        return group;
    }

    void processPlayerLeaveGroup(UUID player) {
        Optional<Group> potentialGroup = getGroupFromPlayerUuid(player);

        if (!potentialGroup.isPresent()) {
            logger.warn("Player {} left a group but had no group.", player);
            return;
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
    }

    void processInvitationAccepted(PendingInvitation invitation) {
        Preconditions.checkArgument(pendingInvitationsMap.containsKey(invitation.getUuid()), "The accepted invitation was not found");
        Group invitersGroup = getGroupFromPlayerUuid(invitation.getInviter())
                .orElse(createAndRegisterGroup(invitation.getInviter()));

        // The invited leaves his previous group if she/he had one
        getGroupFromPlayerUuid(invitation.getInvited()).ifPresent(group -> processPlayerLeaveGroup(invitation.getInvited()));
        processGroupJoin(invitation.getInvited(), invitersGroup);
        pendingInvitationsMap.remove(invitation.getUuid(), invitation);
    }

    void processGroupJoin(UUID joiningPlayer, Group joinedGroup) {
        Preconditions.checkArgument(!getGroupFromPlayerUuid(joiningPlayer).isPresent());
        groups.put(joiningPlayer, joinedGroup);
        joinedGroup.getPlayers().add(joiningPlayer);
    }

    @Override
    public Optional<Group> getGroupFromPlayerUuid(UUID uuid) {
        return Optional.ofNullable(groups.get(uuid));
    }
}