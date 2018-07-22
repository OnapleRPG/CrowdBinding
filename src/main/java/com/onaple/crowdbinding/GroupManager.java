package com.onaple.crowdbinding;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

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

    @Inject
    private EventManager eventManager;

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

    void processInvitationAccepted(UUID invitationUuid) {
        PendingInvitation invitation = pendingInvitationsMap.get(invitationUuid);
        Preconditions.checkNotNull(invitation,"The accepted invitation was not found");
        Group invitersGroup = getGroupFromPlayerUuid(invitation.getInviter())
                .orElse(createAndRegisterGroup(invitation.getInviter()));

        // The invited leaves his previous group if she/he had one
        getGroupFromPlayerUuid(invitation.getInvited()).ifPresent(group -> processPlayerLeaveGroup(invitation.getInvited()));
        processPlayerJoinGroup(invitation.getInvited(), invitersGroup);
        pendingInvitationsMap.remove(invitation.getUuid(), invitation);
    }

    public void processInvitationDenied(UUID invitationUuid) {
        // TODO
    }

    void processInvitationSent(Player inviter, Player invited) {
        // TODO
        PendingInvitation pendingInvitation = new PendingInvitation(inviter.getUniqueId(), invited.getUniqueId());
        registerInvitation(pendingInvitation);
        Text acceptClickableText = Text.builder("[Accept]")
                .color(TextColors.GREEN)
                .onClick(TextActions.runCommand("group accept " + pendingInvitation.getUuid().toString()))
                .onHover(TextActions.showText(Text.of("Accept this invitation")))
                .build();

        Text denyClickableText = Text.builder("[Deny]")
                .color(TextColors.RED)
                .onClick(TextActions.runCommand("group deny " + pendingInvitation.getUuid().toString()))
                .onHover(TextActions.showText(Text.of("Deny this invitation")))
                .build();

        Text invitationText = Text.builder().append(inviter.getDisplayNameData().displayName().get())
                .append(Text.of(" invites you to join his group: "))
                .append(acceptClickableText)
                .append(Text.of(" "))
                .append(denyClickableText)
                .build();
    }

    private void registerInvitation(PendingInvitation pendingInvitation) {
        this.pendingInvitationsMap.put(pendingInvitation.getUuid(), pendingInvitation);
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
