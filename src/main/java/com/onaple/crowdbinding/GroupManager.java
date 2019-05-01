package com.onaple.crowdbinding;

import com.onaple.crowdbinding.data.Group;
import com.onaple.crowdbinding.data.Invitation;
import com.onaple.crowdbinding.exceptions.*;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class GroupManager {
    public GroupManager() {
    }

    private final List<Group> groups = new ArrayList<>();
    private final List<Invitation> invitations = new ArrayList<>();

    public UUID createInvitation(Player inviter, Player invited) throws PlayerAlreadyInAGroupException {
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
        return invitation.getUuid();
    }

    public Player denyInvitation(Player invited, UUID invitationId) throws UnknownInvitationException,
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
        // Remove invitation
        invitations.remove(invitation.get());
        // Returns sender so he can be notified
        return invitation.get().getInviter();
    }

    public Player acceptInvitation(Player invited, UUID invitationId) throws UnknownGroupException,
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
        // Leave group if already in group
        if (getPlayerGroup(invited).isPresent()) {
            try {
                leaveGroup(invited);
            } catch (PlayerNotInGroupException e) {
                CrowdBinding.getLogger().warn("A group was found but could not be left.");
            }
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
        // Remove invitation
        invitations.remove(invitation.get());
        // Returns sender so he can be notified
        return invitation.get().getInviter();
    }

    public Collection<Player> leaveGroup(Player player) throws PlayerNotInGroupException {
        Optional<Group> groupOptional = groups.stream().filter(g -> g.getPlayers().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()))).findAny();
        if (!groupOptional.isPresent()) {
            throw new PlayerNotInGroupException("You currently do not belong to a group.");
        }
        Group group = groupOptional.get();
        group.removePlayer(player);
        if (group.getPlayers().size() <= 1) {
            groups.remove(groups.indexOf(groupOptional.get()));
        } else {
            group.setFirstLeader();
            groups.set(groups.indexOf(groupOptional.get()), group);
        }
        return group.getPlayers();
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
