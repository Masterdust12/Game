package com.Game.Entity.Player;

import com.Game.Entity.Enemy.Enemy;
import com.Game.GUI.Chatbox.ChatBox;
import com.Game.GUI.GUI;
import com.Game.GUI.Inventory.AccessoriesManager;
import com.Game.GUI.RightClick;
import com.Game.Items.ItemStack;
import com.Game.Main.Main;
import com.Game.Main.MethodHandler;
import com.Game.Object.GameObject;
import com.Game.Projectile.Fist;
import com.Game.World.World;
import com.Game.listener.Input;
import com.Util.Math.DeltaMath;
import com.Util.Math.Vector2;
import com.Util.Other.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Player {
    public Vector2 position;
    public int subWorld = 0;
    public float speed;
    public Color playerColor;
    public boolean canMove = true;
    public float speedMod = 0f;
    public float dashMultiplier = 0f;
    public float dashTimer = 0f;
    public Vector2 scale;
    private Vector2 curSpeed = Vector2.zero();
    public float dx = 0, dy = 0, dMod = 0, shootTimer = 0;
    public String name = null;
    private final float maxSlide = 8f;

    public float maxHealth = 100f;
    public float health = 100f;

    public float healthRegen = 1f;

    public static final SpriteSheet playerIdle = new SpriteSheet("Player/player_idle_1.png", 32, 32);
    public static final SpriteSheet playerRun = new SpriteSheet("Player/player_run.png", 32, 32);
    public static final SpriteSheet playerChop  = new SpriteSheet("Player/player_chop.png", 32, 32);

    public static final AnimatedSprite idleAnimation = new AnimatedSprite(2, playerIdle, 2);
    public static final AnimatedSprite runAnimation = new AnimatedSprite(6, playerRun, 4);
    public static final AnimatedSprite chopAnimation = new AnimatedSprite(8, playerChop, 8);

    public AnimatedSprite current = idleAnimation;
    private boolean leftFacing = true;

    public Player(Vector2 position, float speed, Color playerColor, float dash) {
        this.position = position;
        this.speed = speed;
        this.playerColor = playerColor;
        this.dashMultiplier = dash;
        this.scale = new Vector2(80, 80);

        init();
    }

    public void init() {

    }

    public void update() {
        shootTimer -= Main.dTime();
        dashTimer -= Main.dTime();

        current.update();

        if (health < maxHealth)
            health += Main.dTime() * healthRegen;

        if (!Settings.paused())
            movement();

        handleOffset();
    }

    public void damage(float amount) {
        float dmg = amount - amount * damageReduction();
        health -= dmg;
    }

    public float damageReduction() {
        float armor = AccessoriesManager.armor;
        return 0.01f * (100 - (100 / ((armor / 1000) + 1)) + DeltaMath.range(-7.5f, 7.5f));
    }

    public void movement() {
        if (speedMod > 0.1) {
            speedMod *= Math.pow(0.93, Main.dTime() * 37.5);
        } else if (speedMod < 0.1) {
            speedMod = 0;
        }

        dx = 0;
        dy = 0;
        curSpeed = Vector2.zero();

        if (canMove && !ChatBox.typing && Main.fps > 0) {

            if (Input.GetKey(KeyEvent.VK_A)) {
                curSpeed.x -= 1;
            }

            if (Input.GetKey(KeyEvent.VK_D)) {
                curSpeed.x += 1;
            }

            if (Input.GetKey(KeyEvent.VK_W)) {
                curSpeed.y -= 1;
            }

            if (Input.GetKey(KeyEvent.VK_S)) {
                curSpeed.y += 1;
            }

            if (Input.GetKeyDown(KeyEvent.VK_E)) {
                GameObject.checkSingleInteract();
            }

            if (Input.GetKeyDown(KeyEvent.VK_SHIFT) && dashTimer <= 0) {
                speedMod = dashMultiplier;
                dashTimer = Settings.dashTimer;
            }

            if (Input.GetKey(KeyEvent.VK_SPACE) && shootTimer <= 0) {
                ItemStack accessory = AccessoriesManager.getSlot(AccessoriesManager.WEAPON_SLOT);
                if (accessory.getID() == 0)
                    new Fist(position, Input.mousePosition.addClone(World.curWorld.offset));
                else
                    accessory.getItem().useWeapon(position, Input.mousePosition.addClone(World.curWorld.offset));

                SoundHandler.playSound("default_shoot.wav");
            }
        }

        Vector2 move = curSpeed.scaleClone((float) Main.dTime() * speed * (1 + speedMod));
        Vector2 movement = handleCollision(move);

        if (!movement.equalTo(Vector2.zero())) {
            position.add(movement);
            GUI.disableShop();
            changeSprite(runAnimation);
            sendMovementPacket();

            if (movement.x > 0)
                leftFacing = false;
            else if (movement.x < 0)
                leftFacing = true;
        } else if (current == runAnimation){
            changeSprite(idleAnimation);
        }
    }

    public void changeSprite(AnimatedSprite spriteSheet) {
        if (!current.equivalent(spriteSheet)) {
            current = spriteSheet;
            current.reset();
            sendMovementPacket();
        }
    }

    public void tpToPos(int x, int y, int subWorld) {
        World.changeWorld(subWorld);
        position.x = x;
        position.y = y;
        sendMovementPacket();
    }

    public void sendMovementPacket() {
        cancelCurrent();
        Main.sendPacket("15" + Main.player.name + ":" + (int) position.x + ":" + (int) position.y + ":" + subWorld + ":" + animationInformation() + " " + leftFacing);
    }

    private String animationInformation() {
        int spriteSheet = 0;

        if (current == idleAnimation)
            spriteSheet = 0;
        else if (current == runAnimation)
            spriteSheet = 1;
        else if (current == chopAnimation)
            spriteSheet = 2;

        int frame = current.getFrame();

        return spriteSheet + " " + frame;
    }

    public static BufferedImage getAnimation(String information) {
        String[] parts = information.split(" ");
        AnimatedSprite sheet = null;

        switch (parts[0]) {
            case "0": // Idle Animation
                sheet = idleAnimation;
                break;
            case "1":
                sheet = runAnimation;
                break;
            case "2":
                sheet = chopAnimation;
                break;
        }

        BufferedImage image = sheet.getFrame(Integer.parseInt(parts[1]));

        if (Objects.equals(parts[2], "true"))
            image = Render.mirrorImageHorizontally(image);

        return image;
    }

    private void cancelCurrent() {
        RightClick.object.loseFocus();
    }

    public Vector2[] getPoints(Vector2 offset) {
        Vector2 pos = position.addClone(offset);

        return new Vector2[]{
                pos.addClone(new Vector2(scale.x, scale.y).scale(0.5f)),
                pos.addClone(new Vector2(-scale.x, -scale.y).scale(0.5f)),
                pos.addClone(new Vector2(-scale.x, scale.y).scale(0.5f)),
                pos.addClone(new Vector2(scale.x, -scale.y).scale(0.5f))
        };
    }

    public Vector2 handleCollision(Vector2 curSpeed) {
        Vector2 speed = curSpeed.clone();
        Vector2[] points = getPoints(curSpeed);

        if (!CollisionHandler.isFree(points)) {
            Vector2[] xPoints = getPoints(new Vector2(speed.x, 0));
            Vector2[] yPoints = getPoints(new Vector2(0, speed.y));

            if (curSpeed.x != 0 && !CollisionHandler.isFree(xPoints))
                speed.x = 0;

            if (curSpeed.y != 0 && !CollisionHandler.isFree(yPoints))
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

    public void drawBar(Vector2 startPos, Color color, float current, float max) {
        Render.setColor(Color.LIGHT_GRAY);
        Render.drawRectangle(startPos,
                new Vector2(GUI.intBoxSize * 4, 16));

        Render.setColor(color);
        Render.drawRectangle(startPos,
                new Vector2(GUI.intBoxSize * 4 * (current / max), 16));

        Render.setColor(Color.BLACK);
        Render.drawRectOutline(startPos,
                new Vector2(GUI.intBoxSize * 4, 16));
    }

    public void renderStats() {
        drawBar(GUI.GuiPos.subtractClone(new Vector2(0, 36)), Color.RED, health, maxHealth);
        drawBar(GUI.GuiPos.subtractClone(new Vector2(0, 18)), Color.CYAN.darker(), Settings.dashTimer - Math.max(0, dashTimer), Settings.dashTimer);

        if (health <= 0) {
            ChatBox.sendMessage("Oh no. You are dead!");

            health = maxHealth;
            position = Settings.playerSpawn.clone();
            World.changeWorld(0);
            resetAggro();
            sendMovementPacket();
        }
    }

    public void resetAggro() {
        for (Enemy enemy : MethodHandler.enemies) {
            enemy.loseTarget();
        }
    }

    public void render() {
        Render.drawImage((!leftFacing) ? getImage() : Render.mirrorImageHorizontally(getImage()) /* Image is mirrored horizontally if the player is moving to the left.*/,
                position.x - scale.x / 2 - World.curWorld.offset.x,
                position.y - scale.y / 2 - World.curWorld.offset.y);

        renderStats();
    }

    public BufferedImage getImage() {
        return current.getImage(scale);
    }
}