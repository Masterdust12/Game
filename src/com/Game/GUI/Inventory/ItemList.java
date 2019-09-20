package com.Game.GUI.Inventory;

import com.Game.GUI.Inventory.Item;
import com.Game.GUI.Inventory.Items.Ammo.ArrowItem;
import com.Game.GUI.Inventory.Items.Consumables.Food.BlueFishFood;
import com.Game.GUI.Inventory.Items.Consumables.Food.ClownFishFood;
import com.Game.GUI.Inventory.Items.RawResource.Log;
import com.Game.GUI.Inventory.Items.RawResource.MapleLog;
import com.Game.GUI.Inventory.Items.Weapon.Bow;
import com.Game.GUI.Inventory.Items.Weapon.CrystalBow;

public enum ItemList {
    empty(new Item(0, "/", "/", "/", 0)),
    wood(new Log(1, "wood.png", "Log", "The remnants of a tree.", 1)),
    bow(new Bow(2, "bow.png", "Bow","Get ready for the power of my bow!", 1)),
    arrow(new ArrowItem(3, "arrow.png", "Arrow", "Some sharp arrows!", 100000)),
    crystalBow(new CrystalBow(4, "crystalBow.png", "Crystal Bow", "This is really gonna hurt.", 1)),
    clownfish(new ClownFishFood(5, "clownfish.png", "Clownfish", "This looks quite tasty.", 1)),
    bluefish(new BlueFishFood(6, "bluefish.png", "Bluefish", "This looks very tasty.", 1)),
    mapleLog(new MapleLog(7, "maplewood.png", "Maple Log", "A sticky log, sounds useful to me.", 1));

    public Item item;

    ItemList(Item item) {
        this.item = item;
    }

    public int maxStack() {
        return item.maxStack;
    }

    public int getID() {
        return item.id;
    }
}
