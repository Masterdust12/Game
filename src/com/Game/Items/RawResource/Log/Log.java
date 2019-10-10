package com.Game.Items.RawResource.Log;

import com.Game.Items.Item;
import com.Game.Items.ItemList;
import com.Game.Items.ItemStack;

public class Log extends Item {
    protected ItemList bow;
    protected int arrowShaft = 15;

    public Log(int id, String imageName, String name, String examineText, int maxStack, int worth) {
        super(id, imageName, name, examineText, maxStack, worth);

        setOptions();
    }

    public void setOptions() {
        options.add("Craft Bow");
        options.add("Craft Arrow Shafts");
    }

    public void ClickIdentities(int index) {
        replaceInventory(index, new ItemStack((bow == null) ? ItemList.bow : bow, 1));
    }

    public void OnRightClick(int index, int option) {
        switch (option) {
            case 1:
                // Craft Arrows
                replaceInventory(index, new ItemStack(ItemList.arrowShaft, arrowShaft));
                break;
        }
    }
}