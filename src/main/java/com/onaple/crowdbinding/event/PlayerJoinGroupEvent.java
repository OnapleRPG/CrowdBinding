package com.onaple.crowdbinding.event;

import com.onaple.crowdbinding.Group;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PlayerJoinGroupEvent extends AbstractEvent implements TargetGroupEvent, TargetPlayerEvent, Cancellable {

    private final Cause cause;
    private final Player player;
    private final Group group;
    private boolean cancelled = false;

    public PlayerJoinGroupEvent(Cause cause, Player player, Group group) {
        this.cause = cause;
        this.player = player;
        this.group = group;
    }

    @Override
    public Group getTargetGroup() {
        return this.group;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Player getTargetEntity() {
        return this.player;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }
}
