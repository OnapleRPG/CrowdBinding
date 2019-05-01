package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.data.Group;
import com.onaple.crowdbinding.exceptions.InsufficientGroupPermissionException;
import com.onaple.crowdbinding.exceptions.PlayerNotInGroupException;
import com.onaple.crowdbinding.exceptions.UnknownInvitationException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

public class PromoteCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        Optional<Group> groupOptional = CrowdBinding.getGroupManager().getPlayerGroup(source);
        if (!groupOptional.isPresent()) {
            src.sendMessage(Text.of("You don't have a group."));
            return CommandResult.empty();
        }

        Optional<String> playerToPromote = args.getOne("player");
        if (!playerToPromote.isPresent()) {
            src.sendMessage(Text.of("You must specify a player to promote."));
            return CommandResult.empty();
        }

        try {
            CrowdBinding.getGroupManager().promotePlayerWithinGroup(source, groupOptional.get(), playerToPromote.get());
        } catch (InsufficientGroupPermissionException | PlayerNotInGroupException e) {
            src.sendMessage(Text.of(e.getMessage()));
            return CommandResult.empty();
        }

        src.sendMessage(Text.of("You promoted ", playerToPromote.get(), " to group leader."));
        return CommandResult.success();
    }
}
