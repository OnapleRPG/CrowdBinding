package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.exceptions.PlayerNotInGroupException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Collection;

public class LeaveCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        try {
            Collection<Player> remainingGroupPlayers = CrowdBinding.getGroupManager().leaveGroup(source);
            src.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You left your group."));
            remainingGroupPlayers.forEach(p -> p.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, source.getName(), " left your group.")));
            if (remainingGroupPlayers.size() <= 1) {
                remainingGroupPlayers.forEach(p -> p.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, source.getName(), "The group no longer exists.")));
            }
            return CommandResult.success();
        } catch (PlayerNotInGroupException e) {
            src.sendMessage(Text.of(e.getMessage()));
            return CommandResult.empty();
        }
    }
}
