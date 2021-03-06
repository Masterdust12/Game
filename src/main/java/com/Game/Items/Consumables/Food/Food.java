package com.Game.Items.Consumables.Food;

import com.Game.GUI.Chatbox.ChatBox;
import com.Game.GUI.Inventory.InventoryManager;
import com.Game.Items.Item;
import com.Game.Items.ItemStack;
import com.Game.Main.Main;

public class Food extends Item {

    int healAmount = 0;
    String eatText;

    public Food(int id, String imageName, String name, String examineText, int maxStack, int worth, int healAmount) {
        super(id, imageName, name, examineText, maxStack, worth);
        this.healAmount = healAmount;
        this.eatText = "You eat the " + name + " and it heals some health.";
    }

    public void OnClick(int index) {
        eatFood(index);
    }

    public void eatFood(int index) {
        if (Main.player.health < Main.player.maxHealth) {
            InventoryManager.removeItem(index, 1);
            Main.player.health += healAmount;
            if (Main.player.health > Main.player.maxHealth) {
                Main.player.health = Main.player.maxHealth;
            }
            ChatBox.sendMessage(eatText);
        } else {
            ChatBox.sendMessage("You are already at full health!");
        }
    }

    public void setData(ItemStack stack) {
        stack.options.add("Eat");
    }
}
