package com.Game.Object.SkillingAreas;

import com.Game.GUI.Chatbox.ChatBox;
import com.Game.GUI.Inventory.InventoryManager;
import com.Game.GUI.Skills.Skills;
import com.Game.Main.Main;
import com.Game.Object.GameObject;
import com.Util.Math.DeltaMath;

public class MiningRock extends GameObject {

    private int rocks;
    private RockType rockType;
    private float rpTimer;

    public MiningRock(int x, int y, RockType rockType) {
        super(x, y);

        this.rockType = rockType;
        this.maxDistance = 48;
        this.rocks = (int) DeltaMath.range(rockType.minRocks, rockType.maxRocks);
        this.maxTimer = getTime();

        image = getImage(rockType.imageName);
        setScale(128, 128);
    }

    public float getTime() {
        if (rockType == null) {
            return -1;
        }
        return DeltaMath.range(rockType.minTime, rockType.maxTime);
    }

    public void update() {
        if (rocks == 0) {
            rpTimer -= Main.dTime();

            if (rpTimer <= 0) {
                image = getImage(rockType.imageName);
                rocks = (int) DeltaMath.range(rockType.minRocks, rockType.maxRocks);
            }
        }
    }

    public boolean onInteract() {
        if (rocks == 0) {
            return false;
        }

        if (Skills.getLevel(Skills.MINING) < rockType.level) {
            ChatBox.sendMessage("You do not have the requirement of " + rockType.level + " to mine this rock.");
            return false;
        }

        if (InventoryManager.isFull()) {
            ChatBox.sendMessage("You do not have any inventory space to complete this action!");
            return false;
        }

        drawPlayerProgressBar();

        timer += Main.dTime();

        if (timer > maxTimer) {
            timer = 0;
            rocks--;
            maxTimer = getTime();
            rockType.drops.determineOutput().forEach(InventoryManager::addItem);
            Skills.addExperience(Skills.MINING, rockType.xp);

            if (rocks == 0) {
                rpTimer = DeltaMath.range(rockType.minTime, rockType.maxTime);
                image = getImage("empty_rock.png");
            }
        }

        return true;
    }

    public void loseFocus() {
        timer = 0;
        maxTimer = getTime();
    }
}
