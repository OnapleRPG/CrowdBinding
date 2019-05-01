package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.data.Group;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;
import java.util.stream.Collectors;

public class ListCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        Optional<Group> groupOptional = CrowdBinding.getGroupManager().getPlayerGroup(source);

        if (!groupOptional.isPresent()) {
            source.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You have no group."));
            return CommandResult.success();
        }

        Group group = groupOptional.get();
        Text.Builder builder = Text.builder("Your group's members are: ");
        boolean placeComma = false;
        for(Player p : group.getPlayers()) {
            if (placeComma) {
                builder.append(Text.of(", "));
            }
            placeComma = true;
            if (group.getLeader().getName().equals(p.getName())) {
                builder.append(Text.of(TextStyles.BOLD, p.getName(), TextStyles.NONE));
            } else {
                builder.append(Text.of(p.getName()));
            }
        }
        builder.append(Text.of("."));
        builder.color(TextColors.DARK_AQUA).style(TextStyles.ITALIC);

        src.sendMessage(builder.build());

        return CommandResult.success();
    }
}
