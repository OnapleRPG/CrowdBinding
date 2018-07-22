package com.onaple.crowdbinding;

import java.util.Objects;
import java.util.UUID;

/**
 * Reprensents a pending invitation to a group.
 */
public class PendingInvitation {

    /**
     * The unique identifier of this invitation.
     */
    private UUID uuid;
    private UUID inviter, invited;

    public PendingInvitation(UUID inviter, UUID invited) {
        this.uuid = UUID.randomUUID();
        this.inviter = inviter;
        this.invited = invited;
    }

    /**
     * @return the unique identifier of this invitation
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return the player who invited the {@code invited} to his group
     */
    public UUID getInviter() {
        return inviter;
    }

    /**
     * @return the player who was invited to the {@code inviter}'s  group
     */
    public UUID getInvited() {
        return invited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingInvitation that = (PendingInvitation) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
