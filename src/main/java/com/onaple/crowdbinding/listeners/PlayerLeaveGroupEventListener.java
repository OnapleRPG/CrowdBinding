package com.onaple.crowdbinding.listeners;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.GroupManager;
import com.onaple.crowdbinding.data.Group;
import com.onaple.crowdbinding.events.PlayerLeaveGroupEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class PlayerLeaveGroupEventListener {
    @Listener
    public void onPlayerLeaveGroupEvent(PlayerLeaveGroupEvent event) {
        GroupManager groupManager = CrowdBinding.getGroupManager();
        Group group = new Group(event.getTargetGroup());
        Player player = event.getTargetEntity();

        group.removePlayer(player);
        groupManager.updateGroup(event.getTargetGroup(), group);

        player.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You left the group."));
        group.getPlayers().forEach(p -> p.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, player.getName(), " left the group.")));

        if (!groupManager.groupInUsed(event.getTargetGroup())) {
            group.getPlayers().forEach(p -> p.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "Your group no longer exists.")));
            groupManager.removeGroup(group);
        } else {
            if (group.getLeader().equals(player)) {
                group.setFirstLeader();
                group.getPlayers().forEach(p -> p.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, group.getLeader().getName(), " is the new group leader.")));
                groupManager.updateGroup(event.getTargetGroup(), group);
            }
        }
    }
}
