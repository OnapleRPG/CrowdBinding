package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.data.Group;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

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


        Inventory inventory = Inventory.builder()
                .of(InventoryArchetypes.HOPPER)
                .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of("Member of the group")))
                .build(CrowdBinding.getInstance());

        if (!groupOptional.isPresent()) {
            source.sendMessage(Text.of("You have no group."));
            return CommandResult.success();
        }

        Group group = groupOptional.get();
        Text.Builder builder = Text.builder("Your group's members are: ");

        builder.append(
                Text.of(
                        group.getPlayers().stream()
                                .map(Player::getDisplayNameData)
                                .map(DisplayNameData::displayName)
                                .map(Value::get)
                                .map(Text::toPlain)
                                .collect(Collectors.joining(", "))
                )
        );


        group.getPlayers().stream().forEach(player ->
        {
            ItemStack head = ItemStack.builder()
                    .itemType(ItemTypes.SKULL)
                    .build();


            head.offer(Keys.SKULL_TYPE, SkullTypes.PLAYER);
            head.offer(Keys.REPRESENTED_PLAYER, source.getProfile());
            head.offer(Keys.DISPLAY_NAME, Text.of(source.getName()));

            inventory.offer(head);
        });
        source.openInventory(inventory);

        source.sendMessage(builder.build());

        return CommandResult.success();
    }
}
