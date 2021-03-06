package com.Game.Object;

import com.Game.Entity.Player.Player;
import com.Game.GUI.Chatbox.ChatBox;
import com.Game.Main.Main;
import com.Game.Main.MethodHandler;
import com.Game.World.World;
import com.Game.listener.Input;
import com.Util.Math.Vector2;
import com.Util.Other.Render;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GameObject {
    public Vector2 position;
    public BufferedImage image;
    public float maxTimer = 1.5f;
    protected float timer = 0;
    protected float maxDistance;
    protected boolean canInteract = true;
    protected Vector2 scale;

    public GameObject(int x, int y) {
        this.position = new Vector2(x, y);

        MethodHandler.objects.add(this);
    }

    public static UsableGameObject mouseOver() {
        for (GameObject obj : MethodHandler.objects) {
            if (obj.position == null || obj.image == null)
                continue;
            if (Vector2.distance(obj.position.addClone(Render.getImageSize(obj.image).scale(0.5f)), Input.mousePosition.addClone(World.curWorld.offset))
                    < Math.max(obj.image.getWidth(), obj.image.getHeight())
                    && obj instanceof UsableGameObject) {
                return (UsableGameObject) obj;
            }
        }

        return null;
    }

    public void updateObject() {
        if (image == null || !Render.onScreen(position, image))
            return;

        if (scale == null)
            scale = Render.getImageSize(image);

        Render.drawImage(Render.getScaledImage(image, scale),
                position.subtractClone(World.curWorld.offset));

        update();

        float distance = Vector2.distance(position.addClone(Render.getImageSize(image).scale(0.5f)), Main.player.position);

        if (distance <= maxDistance && Input.GetKey(KeyEvent.VK_E) && !ChatBox.typing && canInteract) {
            canInteract = onInteract();
        } else if (!Input.GetKey(KeyEvent.VK_E) && !ChatBox.typing && !canInteract) {
            timer = 0;
            canInteract = true;
            Main.player.changeSprite(Player.idleAnimation);
        }

        if (!Input.GetKey(KeyEvent.VK_E) && distance <= maxDistance && !ChatBox.typing) {
            loseFocus();
            Main.player.changeSprite(Player.idleAnimation);
            timer = 0;
            canInteract = true;
        }
    }

    public void update() {

    }

    public void loseFocus() {

    }

    public static void checkSingleInteract() {
        for (GameObject object : MethodHandler.objects) {
            float distance = Vector2.distance(object.position, Main.player.position);

            if (distance < object.maxDistance && !ChatBox.typing) {
                object.onInteraction();
                return;
            }
        }
    }

    public boolean onInteract() {
        return false;
    }

    public void onInteraction() {

    }

    public static BufferedImage getImage(String name) {
        return Main.main.getImageFromRoot("Object/" + name);
    }

    public void drawProgressBar() {
        Vector2 sPos = position.subtractClone(image.getWidth() * -0.25f, 24).subtract(World.curWorld.offset);
        Vector2 rect = new Vector2(image.getWidth() * (timer / maxTimer) * 0.5f, 8);

        Render.setColor(Color.BLUE);
        Render.drawRectangle(sPos, rect);

        Render.setColor(Color.BLACK);
        Render.drawRectOutline(sPos, rect);
    }

    public void drawPlayerProgressBar() {
        Vector2 sPos = Main.player.position.subtractClone(24, 35).subtract(World.curWorld.offset);
        Vector2 rect = new Vector2(48 * (timer / maxTimer), 8);
        Vector2 compRect = new Vector2(48, 8);

        Render.setColor(Color.LIGHT_GRAY);
        Render.drawRectangle(sPos, compRect);

        Render.setColor(Color.BLACK);
        Render.drawRectOutline(sPos, compRect);

        Render.setColor(Color.BLUE);
        Render.drawRectangle(sPos, rect);
    }

    public void setScale(int x, int y) {
        this.scale = new Vector2(x, y);
    }
}
