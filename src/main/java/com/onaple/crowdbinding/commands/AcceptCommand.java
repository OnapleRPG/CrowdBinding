package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.exceptions.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.UUID;

public class AcceptCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        String invitation = args.<String>getOne("invitation").orElse("");
        if (invitation.isEmpty()) {
            src.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "Unrecognized invitation code."));
            return CommandResult.empty();
        }

        UUID inviteUuid;
        try {
            inviteUuid = UUID.fromString(invitation);
        } catch (IllegalArgumentException e) {
            src.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "Unrecognized invitation code."));
            return CommandResult.empty();
        }

        Player inviter;
        try {
            inviter = CrowdBinding.getGroupManager().acceptInvitation(source, inviteUuid);
        } catch (UnknownGroupException | UnknownInvitationException | SenderLeftGroupException | ExpiredInvitationException | SenderJoinedAnotherGroupException e) {
            src.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, e.getMessage()));
            return CommandResult.empty();
        }

        inviter.sendMessage(
                Text.builder(source.getDisplayNameData().displayName().get().toPlain())
                        .append(Text.of(" accepted your invitation."))
                        .color(TextColors.DARK_AQUA).style(TextStyles.ITALIC)
                        .build()
        );
        source.sendMessage(
                Text.builder("You accepted ")
                        .append(inviter.getDisplayNameData().displayName().get())
                        .append(Text.of("'s invitation."))
                        .color(TextColors.DARK_AQUA).style(TextStyles.ITALIC)
                        .build()
        );

        return CommandResult.success();
    }
}
