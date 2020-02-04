package com.Game.GUI.GUIWindow;

import com.Game.GUI.GUI;
import com.Game.Items.Item;
import com.Game.Items.ItemStack;
import com.Util.Math.Vector2;
import com.Util.Other.Render;
import com.Util.Other.Settings;

import java.awt.image.BufferedImage;

/**
 * Represents a single item render on a GUI. Add one of these to a GUIWindow
 * to render an ItemStack;
 */
public class GUIItemSlot extends GUIElement {
    protected ItemStack stack;
    protected Vector2 position;
    protected Vector2 renderSize;
    protected boolean bordered = true;
    protected boolean renderItem = true;
    protected boolean draggable = false;

    public GUIItemSlot(ItemStack stack, Vector2 position, Vector2 renderSize) {
        this.stack = stack;
        this.position = position;
        this.renderSize = renderSize;
    }

    public GUIItemSlot(Item item, Vector2 position, Vector2 renderSize) {
        this.stack = new ItemStack(item, -1);
        this.position = position;
        this.renderSize = renderSize;
    }

    public GUIItemSlot(ItemStack item, Vector2 position) {
        this.stack = item;
        this.position = position;
        this.renderSize = Render.getImageSize(stack.getImage());
    }

    public GUIItemSlot(ItemStack item, float x, float y) {
        this.stack = item;
        this.position = new Vector2(x, y);
        this.renderSize = GUI.invSize;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
    }

    public void setEnabled(boolean enabled) {
        this.renderItem = enabled;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getID() {
        return stack.getID();
    }

    public BufferedImage getImage() {
        return stack.getImage();
    }

    public int getAmount() {
        return stack.getAmount();
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public void setAmount(int amount) {
        stack.amount = amount;
    }

    public void addAmount(int amount) {
        setAmount(amount + getAmount());
    }

    public void render(GUIWindow window) {
        Vector2 pos = window.offset(position);

        if (bordered) {
            Render.drawBorderedRect(pos, renderSize);
        }

        if (renderItem)
            Render.drawImage(Render.getScaledImage(stack.getImage(), renderSize), pos);

        if (renderItem && stack.getAmount() > 1) {
            String text = "" + stack.getAmount();
            Render.drawText(text,
                    pos.addClone(new Vector2(GUI.IntBoxSize - Settings.sWidth(text) - 4, GUI.IntBoxSize - 4)));
        }
    }

    public void update(GUIWindow window) {

    }
}