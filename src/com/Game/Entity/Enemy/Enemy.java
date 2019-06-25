package com.Game.Entity.Enemy;

import com.Game.Main.Main;
import com.Game.Main.MethodHandler;
import com.Game.World.World;
import com.Util.Math.Vector2;
import com.Util.Other.Render;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy {

    public int id = 0;
    public Vector2 position;
    public Vector2 spawnPosition;

    public boolean enabled = true;

    public float respawnTimer = 0;

    public float timer = 0;

    public float maxHealth = 0;
    public float health = 0;

    public BufferedImage image;

    public Enemy(int x, int y) {
        position = new Vector2(x, y);
        spawnPosition = position.clone();

        MethodHandler.enemies.add(this);
    }

    public void updateEnemy() {
        if (!enabled) {
            timer += 1 / Main.fps;

            if (timer > respawnTimer) {
                enabled = true;
                health = maxHealth;
                position = spawnPosition;
                timer = 0;
            }

            return;
        }

        renderEnemy();

        if (health <= 0) {
            enabled = false;
        } else {
            AI();
        }
    }

    public void renderEnemy() {
        Vector2 deltaPosition = position.subtractClone(World.curWorld.offset);

        if (Render.onScreen(position, image)) {
            Render.drawImage(image, deltaPosition.subtract(Render.getDimensions(image).scale(0.5f)));

            // Draw Health Bar
            Render.setColor(Color.RED);
            Render.drawRectangle(deltaPosition.addClone(new Vector2(0, -8)),
                    new Vector2((float) image.getWidth() * (health / maxHealth), 8));
        }
    }

    public void damage(float amount) {
        health -= amount;
    }

    public static BufferedImage getImage(String name) {
        return Main.main.getImageFromRoot("Entities/Enemies/" + name);
    }

    public void AI() {

    }
}
