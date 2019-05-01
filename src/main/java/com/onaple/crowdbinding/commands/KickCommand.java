package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.data.Group;
import com.onaple.crowdbinding.events.PlayerLeaveGroupEvent;
import com.onaple.crowdbinding.exceptions.PlayerAlreadyInAGroupException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;
import java.util.UUID;

public class KickCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        Optional<Player> kicked = args.<Player>getOne("player");
        if (!kicked.isPresent()) {
            source.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "Player not found."));
            return CommandResult.empty();
        }

        Optional<Group> groupOptional = CrowdBinding.getGroupManager().getPlayerGroup(source);
        if (!groupOptional.isPresent()) {
            source.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You are not part of a group."));
            return CommandResult.empty();
        }

        if (!groupOptional.get().getLeader().equals(source)) {
            source.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You must be the group leader to kick someone from the group."));
            return CommandResult.empty();
        }

        if (source.equals(kicked.get())) {
            source.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You cannot kick yourself from the group."));
            return CommandResult.empty();
        }

        if (!groupOptional.get().getPlayers().contains(kicked.get())) {
            source.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, kicked.get().getName(), " is not part of your group."));
            return CommandResult.empty();
        }

        CrowdBinding.getGroupManager().kickPlayerFromGroup(groupOptional.get(), kicked.get());

        EventContext context = EventContext.builder().build();
        Cause cause = Cause.builder().append(source).build(context);
        CrowdBinding.getEventManager().post(new PlayerLeaveGroupEvent(cause, source, groupOptional.get()));

        source.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You kicked ", kicked.get().getName(), " from the group."));
        kicked.get().sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You were kicked from the group."));
        return CommandResult.success();
    }
}
