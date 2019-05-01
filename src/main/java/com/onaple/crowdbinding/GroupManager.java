package com.onaple.crowdbinding;

import com.onaple.crowdbinding.data.Group;
import com.onaple.crowdbinding.data.Invitation;
import com.onaple.crowdbinding.exceptions.*;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class GroupManager {
    public GroupManager() {
    }

    private final List<Group> groups = new ArrayList<>();
    private final List<Invitation> invitations = new ArrayList<>();

    public void createInvitation(Player inviter, Player invited) throws PlayerAlreadyInAGroupException {
        // Look for inviter's group
        UUID groupId = null;
        for(Group g : groups) {
            for(Player p : g.getPlayers()) {
                if (p.getUniqueId().equals(inviter.getUniqueId())) {
                    groupId = g.getUuid();
                }
                if (p.getUniqueId().equals(invited.getUniqueId())) {
                    throw new PlayerAlreadyInAGroupException("The player you tried to invite is already in a group.");
                }
            }
        }
        // Create invitation
        Invitation invitation = new Invitation(groupId, inviter, invited);
        invitations.add(invitation);
        // Send invitation into chat
        sendInvitationIntoChat(invited, inviter, invitation.getUuid());
    }

    private void sendInvitationIntoChat(Player invited, Player inviter, UUID groupId) {
        Text acceptClickableText = Text.builder("[Accept]")
                .color(TextColors.GREEN)
                .onClick(TextActions.runCommand("/group accept " + groupId.toString()))
                .onHover(TextActions.showText(Text.of("Accept this invitation")))
                .build();
        Text denyClickableText = Text.builder("[Deny]")
                .color(TextColors.RED)
                .onClick(TextActions.runCommand("/group deny " + groupId.toString()))
                .onHover(TextActions.showText(Text.of("Deny this invitation")))
                .build();
        Text invitationText = Text.builder().append(inviter.getDisplayNameData().displayName().get())
                .append(Text.of(" invites you to join his group: "))
                .append(acceptClickableText)
                .append(Text.of(" "))
                .append(denyClickableText)
                .build();
        invited.sendMessage(invitationText);
        inviter.sendMessage(
                Text.builder("You invited ").append(invited.getDisplayNameData().displayName().get())
                        .append(Text.of(" to your group."))
                        .build()
        );
    }

    public void denyInvitation(Player invited, UUID invitationId) throws UnknownInvitationException,
            ExpiredInvitationException {
        // Find matching invitation
        Optional<Invitation> invitation = invitations.stream().filter(i -> i.getUuid().equals(invitationId)).findAny();
        if (!invitation.isPresent()) {
            throw new UnknownInvitationException("The invitation no longer exists.");
        }
        // Checking that invitation is still valid
        if (System.currentTimeMillis() - invitation.get().getInviteDate().getTime() > 2*60*1000) {
            throw new ExpiredInvitationException("The invitation is no longer valid.");
        }
        // Send messages
        invitation.get().getInviter().sendMessage(
                Text.builder(invited.getDisplayNameData().displayName().get().toPlain())
                        .append(Text.of(" denied your invitation."))
                        .build()
        );
        invited.sendMessage(
                Text.builder("You denied ")
                        .append(invitation.get().getInviter().getDisplayNameData().displayName().get())
                        .append(Text.of("'s invitation."))
                        .build()
        );
        // Remove invitation
        invitations.remove(invitation.get());
    }

    public void acceptInvitation(Player invited, UUID invitationId) throws UnknownGroupException,
            UnknownInvitationException, SenderLeftGroupException, SenderJoinedAnotherGroupException,
            ExpiredInvitationException {
        // Find matching invitation
        Optional<Invitation> invitation = invitations.stream().filter(i -> i.getUuid().equals(invitationId)).findAny();
        if (!invitation.isPresent()) {
            throw new UnknownInvitationException("Cannot accept non existing invitation.");
        }
        // Checking that invitation is still valid
        if (System.currentTimeMillis() - invitation.get().getInviteDate().getTime() > 2*60*1000) {
            throw new ExpiredInvitationException("The invitation is no longer valid.");
        }
        if (invitation.get().getGroupId() != null) {
            // Try to join existing group
            Optional<Group> groupOptional = groups.stream().filter(g -> g.getUuid() == invitation.get().getGroupId()).findAny();
            if (!groupOptional.isPresent()) {
                invitations.remove(invitation.get());
                throw new UnknownGroupException("Cannot accept invitation to non existing group.");
            }
            if (groupOptional.get().getPlayers().stream().noneMatch(p -> p.getUniqueId().equals(invitation.get().getInviter().getUniqueId()))) {
                invitations.remove(invitation.get());
                throw new SenderLeftGroupException("Sender left the group you were invited to.");
            }
            Group group = groupOptional.get();
            group.addPlayer(invited);
            groups.set(groups.indexOf(groupOptional.get()), group);
        } else {
            // Try to join new group
            Optional<Group> existingGroupOptional = groups.stream().filter(g -> g.getPlayers().stream().anyMatch(p -> p.getUniqueId().equals(invitation.get().getInviter().getUniqueId()))).findAny();
            if (!existingGroupOptional.isPresent()) {
                Group newGroup = new Group(invitation.get().getInviter());
                newGroup.addPlayer(invited);
                groups.add(newGroup);
            } else {
                Group existingGroup = existingGroupOptional.get();
                if (existingGroup.getLeader().equals(invitation.get().getInviter())) {
                    existingGroup.addPlayer(invited);
                    groups.set(groups.indexOf(existingGroupOptional.get()), existingGroup);
                } else {
                    invitations.remove(invitation.get());
                    throw new SenderJoinedAnotherGroupException("Sender joined another group.");
                }
            }
        }
        // Send messages
        invitation.get().getInviter().sendMessage(
                Text.builder(invited.getDisplayNameData().displayName().get().toPlain())
                        .append(Text.of(" accepted your invitation."))
                        .build()
        );
        invited.sendMessage(
                Text.builder("You accepted ")
                        .append(invitation.get().getInviter().getDisplayNameData().displayName().get())
                        .append(Text.of("'s invitation."))
                        .build()
        );
        // Remove invitation
        invitations.remove(invitation.get());
    }

    public boolean leaveGroup(Player player) {
        Optional<Group> groupOptional = groups.stream().filter(g -> g.getPlayers().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()))).findAny();
        if (!groupOptional.isPresent()) {
            player.sendMessage(Text.of("You currently do not belong to a group."));
            return false;
        }
        Group group = groupOptional.get();
        group.removePlayer(player);
        if (group.getPlayers().size() <= 1) {
            groups.remove(groups.indexOf(groupOptional.get()));
        } else {
            group.setFirstLeader();
            groups.set(groups.indexOf(groupOptional.get()), group);
        }
        player.sendMessage(Text.of("You left your group."));
        return true;
    }

    public Optional<Group> getPlayerGroup(Player player) {
        return groups.stream().filter(g -> g.getPlayers().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()))).findAny();
    }

    public void promotePlayerWithinGroup(Player requester, Group group, Player playerToPromote) throws InsufficientGroupPermissionException, PlayerNotInGroupException {
        if (!group.getLeader().getUniqueId().equals(requester.getUniqueId())) {
            throw new InsufficientGroupPermissionException("You must be the leader of your group to promote someone else.");
        }

        if (group.getPlayers().stream().noneMatch(p -> p.equals(playerToPromote))) {
            throw new PlayerNotInGroupException("The player you tried to promote is not part of your group.");
        }

        group.setLeader(playerToPromote);
    }

    public Optional<Group> getGroup(UUID groupId) {
        return groups.stream().filter(group -> group.getUuid().equals(groupId)).findAny();
    }


}
