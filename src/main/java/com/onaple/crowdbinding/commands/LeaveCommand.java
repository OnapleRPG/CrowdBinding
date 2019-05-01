package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.data.Group;
import com.onaple.crowdbinding.events.PlayerLeaveGroupEvent;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;

public class LeaveCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }
        Player source = (Player) src;

        Optional<Group> groupOptional = CrowdBinding.getGroupManager().getPlayerGroup(source);
        if (!groupOptional.isPresent()) {
            source.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You do not belong to a group."));
            return CommandResult.empty();
        }
        Group group = groupOptional.get();

        EventContext context = EventContext.builder().build();
        Cause cause = Cause.builder().append(source).build(context);
        boolean success = CrowdBinding.getEventManager().post(new PlayerLeaveGroupEvent(cause, source, group));
        return success ? CommandResult.success() : CommandResult.empty();
    }
}
