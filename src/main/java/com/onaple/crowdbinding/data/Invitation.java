package com.onaple.crowdbinding.data;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Date;
import java.util.UUID;

/**
 * Reprensents a pending invitation to a group.
 */
public class Invitation {
    private UUID uuid;
    private UUID groupId;
    private Player inviter, invited;
    private Date inviteDate;

    public Invitation(Player inviter, Player invited) {
        this.inviteDate = new Date(System.currentTimeMillis());
        this.uuid = UUID.randomUUID();
        this.groupId = null;
        this.inviter = inviter;
        this.invited = invited;
    }

    public Invitation(UUID groupId, Player inviter, Player invited) {
        this.inviteDate = new Date(System.currentTimeMillis());
        this.uuid = UUID.randomUUID();
        this.groupId = groupId;
        this.inviter = inviter;
        this.invited = invited;
    }

    public UUID getUuid() {
        return uuid;
    }
    public UUID getGroupId() {
        return groupId;
    }
    public Player getInviter() {
        return inviter;
    }
    public Player getInvited() {
        return invited;
    }
    public Date getInviteDate() {
        return inviteDate;
    }
}
