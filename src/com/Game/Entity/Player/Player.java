package com.Game.Entity.Player;

import com.Game.GUI.Chatbox.ChatBox;
import com.Game.GUI.GUI;
import com.Game.GUI.Inventory.AccessoriesManager;
import com.Game.GUI.TextBox;
import com.Game.Main.Main;
import com.Game.Projectile.Projectile;
import com.Game.World.World;
import com.Game.listener.Input;
import com.Util.Math.Vector2;
import com.Util.Other.Render;
import com.Util.Other.Settings;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player {
    public Vector2 position;
    public float speed;
    public Color playerColor;
    public boolean canMove = true;
    public float speedMod = 0f;
    public float dashMultiplier = 0f;
    public int scale = 0;
    public BufferedImage image;
    private Vector2 curSpeed = Vector2.zero();
    private float dx = 0;
    private float dy = 0;
    private float dMod = 0;
    private float shootTimer = 0;

    public float maxHealth = 100f;
    public float health = 100f;

    public static ArrayList<Projectile> projectiles;
    public static ArrayList<Projectile> removeProj;

    public Player(Vector2 position, float speed, Color playerColor, float dash) {
        this.position = position;
        this.speed = speed;
        this.playerColor = playerColor;
        this.dashMultiplier = dash;
        this.image = Main.main.getImageFromRoot("player.png");
        this.scale = image.getWidth();

        init();
    }

    public void init() {
        projectiles = new ArrayList();

        removeProj = new ArrayList();
    }

    public Vector2[] getPoints(Vector2 offset) {
        Vector2 pos = position.addClone(offset);
        Vector2[] points = new Vector2[4];
        points[0] = pos.addClone(scale / 2, scale / 2);
        points[1] = pos.addClone(-scale / 2, -scale / 2);
        points[2] = pos.addClone(-scale / 2, scale / 2);
        points[3] = pos.addClone(scale / 2, -scale / 2);

        return points;
    }

    public void update() {
        if (shootTimer > 0)
            shootTimer -= 1 / Main.fps;

        movement();
        handleOffset();
    }

    private void updatePlayerArrays() {
        for (Projectile p : projectiles)
            p.projectileUpdate();

        if (!removeProj.isEmpty()) {
            projectiles.removeAll(removeProj);
            removeProj.clear();
        }

    }

    public void damage(float amount) {
        health -= amount;
    }

    public void movement() {
        if (speedMod > 0.1) {
            speedMod *= 0.93;
        } else if (speedMod < 0.1) {
            speedMod = 0;
        }

        dx = 0;
        dy = 0;
        curSpeed = Vector2.zero();

        if (canMove && !ChatBox.typing && Main.fps > 0) {

            if (Input.GetKey(KeyEvent.VK_A)) {
                dx -= speed;
            }

            if (Input.GetKey(KeyEvent.VK_D)) {
                dx += speed;
            }

            if (Input.GetKey(KeyEvent.VK_W)) {
                dy -= speed;
            }

            if (Input.GetKey(KeyEvent.VK_S)) {
                dy += speed;
            }

            if (Input.GetKeyDown(KeyEvent.VK_SHIFT)) {
                speedMod = speed * dashMultiplier;
            }

            if (Input.GetKey(KeyEvent.VK_SPACE) && shootTimer <= 0) {
                AccessoriesManager.getSlot(AccessoriesManager.WEAPON_SLOT).
                        item.useWeapon(position, Input.mousePosition.addClone(World.curWorld.offset));
                shootTimer = 0.25f;
            }

            dMod = speedMod;

            if (dx != 0 && dy != 0)
                dMod /= Math.sqrt(2);

            curSpeed = new Vector2((float) ((dx + Math.signum(dx) * dMod) / Main.fps), (float) ((dy + Math.signum(dy) * dMod) / Main.fps));
        }

        position.add(handleCollision(curSpeed));
    }

    public Vector2 handleCollision(Vector2 curSpeed) {
        Vector2 speed = curSpeed.clone();
        Vector2[] xPoints = getPoints(new Vector2(curSpeed.x, 0));
        Vector2[] yPoints = getPoints(new Vector2(0, curSpeed.y));

        if (!CollisionHandler.isFree(xPoints)) {
            speed.x = 0;
        }

        if (!CollisionHandler.isFree(yPoints)) {
            speed.y = 0;
        }

        return speed;
    }

    public void handleOffset() {
        // These variables are probably not all necessary but it looks cleaner.
        Vector2 offset = World.curWorld.offset;
        Vector2 size = World.curWorld.size;
        Vector2 res = Settings.curResolution();
        Vector2 middle = res.scaleClone(0.5f);
        Vector2 sens = middle.scaleClone(Settings.cameraSensitivity);
        Vector2 arcsens = middle.scaleClone(1 - Settings.cameraSensitivity);

        float rX = (position.x - World.curWorld.offset.x);
        float rY = (position.y - World.curWorld.offset.y);

        if (rX < middle.x - sens.x) {
            offset.x = position.x - arcsens.x;
        } else if (rX > middle.x + sens.x) {
            offset.x = position.x - sens.x - middle.x;
        }

        if (rY < middle.y - sens.y) {
            offset.y = position.y - arcsens.y;
        } else if (rY > middle.y + sens.y) {
            offset.y = position.y - sens.y - middle.y;
        }

        Vector2 maximum = size.scaleClone(Settings.worldScale).subtractClone(Settings.curResolution());

        if (offset.x < 0) {
            offset.x = 0;
        } else if (offset.x > maximum.x) {
            offset.x = maximum.x;
        }

        if (offset.y < 0) {
            offset.y = 0;
        } else if (offset.y > maximum.y) {
            offset.y = maximum.y;
        }
    }

    public void renderStats() {
        // Draw Health Bar
        Render.setColor(Color.LIGHT_GRAY);
        Render.drawRectangle(GUI.GuiPos.subtractClone(new Vector2(0, 18)),
                new Vector2(GUI.IntBoxSize * 4 * (health / maxHealth), 16));

        Render.setColor(Color.RED);
        Render.drawRectangle(GUI.GuiPos.subtractClone(new Vector2(0, 18)),
                new Vector2(GUI.IntBoxSize * 4 * (health / maxHealth), 16));

        Render.setColor(Color.BLACK);
        Render.drawRectOutline(GUI.GuiPos.subtractClone(new Vector2(0, 18)),
                new Vector2(GUI.IntBoxSize * 4, 16));

        if (health <= 0) {
            TextBox.setText("Oh no! You are dead!");

            health = maxHealth;
        }
    }

    public void render() {
        Render.drawImage(image, position.x - scale / 2 - World.curWorld.offset.x,
                position.y - scale / 2 - World.curWorld.offset.y);

        renderStats();

        updatePlayerArrays();
    }
}
