package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.exceptions.PlayerAlreadyInAGroupException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;
import java.util.UUID;

public class InviteCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player sender = (Player) src;
        Optional<Player> recipient = args.<Player>getOne("player");
        if (!recipient.isPresent()) {
            sender.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "Player not found."));
            return CommandResult.empty();
        }

        if (sender.equals(recipient.get())) {
            sender.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, "You cannot invite yourself into a group."));
            return CommandResult.empty();
        }

        try {
            UUID groupUuid = CrowdBinding.getGroupManager().createInvitation(sender, recipient.get());
            sendInvitationIntoChat(sender, recipient.get(), groupUuid);
        } catch (PlayerAlreadyInAGroupException e) {
            sender.sendMessage(Text.of(TextColors.DARK_AQUA, TextStyles.ITALIC, e.getMessage()));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }

    private void sendInvitationIntoChat(Player inviter, Player invited, UUID groupId) {
        Text acceptClickableText = Text.builder("[Accept]")
                .color(TextColors.GREEN)
                .onClick(TextActions.runCommand("/group accept " + groupId.toString()))
                .onHover(TextActions.showText(Text.of("Accept this invitation")))
                .build();
        Text denyClickableText = Text.builder("[Deny]")
                .color(TextColors.RED)
                .onClick(TextActions.runCommand("/group deny " + groupId.toString()))
                .onHover(TextActions.showText(Text.of("Deny this invitation")))
                .build();
        Text invitationText = Text.builder().append(inviter.getDisplayNameData().displayName().get())
                .append(Text.of(" invites you to join his group: "))
                .append(acceptClickableText)
                .append(Text.of(" "))
                .append(denyClickableText)
                .color(TextColors.DARK_AQUA).style(TextStyles.ITALIC)
                .build();
        invited.sendMessage(invitationText);
        inviter.sendMessage(
                Text.builder("You invited ")
                        .append(invited.getDisplayNameData().displayName().get())
                        .append(Text.of(" to your group."))
                        .color(TextColors.DARK_AQUA).style(TextStyles.ITALIC)
                        .build()
        );
    }
}
