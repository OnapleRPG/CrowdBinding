package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.GroupManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.util.UUID;

public class AcceptCommand implements CommandExecutor {
    @Inject
    private GroupManager groupManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        UUID inviteUuid = UUID.fromString(args.<String>getOne("invitation").get());
        this.groupManager.processInvitationAccepted(inviteUuid);

        return CommandResult.success();
    }
}
