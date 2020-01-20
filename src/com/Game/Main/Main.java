package com.Game.Main;

import com.Game.Entity.Player.Player;
import com.Game.GUI.GUI;
import com.Game.GUI.Inventory.InventoryManager;
import com.Game.Networking.Client;
import com.Game.Networking.Login;
import com.Game.listener.Input;
import com.Util.Math.Vector2;
import com.Util.Other.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Main extends Canvas {

    private boolean running = false;
    private boolean startup = false;

    public static double fps = 0;
    public static Main main;
    public static JFrame frame;
    public static Dimension screenSize;
    public static Graphics graphics;
    public static final String connectionCode = "69";
    public static final String messageCode = "13";
    public static boolean isConnected = false;

    public static final String ipAddress = "localhost";//"hacksugar.asuscomm.com";
    public static Player player;
    public static MenuHandler menu;
    public static Client client;

    public static MethodHandler methods;

    private static long updateLength;

    public static void main(String[] args) {
        main = new Main();
        frame = new JFrame("Game");
        Input input = new Input();
        Vector2 res = Settings.curResolution();

        frame.add(main);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int) res.x, (int) res.y);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

        main.running = true;

        Input.init();

        main.init();
        main.updateFrame();
        main.addKeyListener(input);
        main.addMouseListener(input);
        main.addMouseMotionListener(input);
        main.setFocusTraversalKeysEnabled(false);
        main.run();
    }

    public static boolean serverConnect(String username, String password, int loginCode) {
        client = new Client(ipAddress, 3112);
        return client.connect(username, password, loginCode);
    }

    public static void logout() {
        isConnected = false;
        InventoryManager.useIndex = -1;
        Login.resetLogin();
    }

    public void init() {
        Main.screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        player = new Player(Settings.playerSpawn, 2500f, Color.GREEN, 2.75f);
        menu = new MenuHandler();

        Settings.npcFont = getFont("npc-text.ttf", 24, Font.BOLD);
        Settings.skillPopupFont = getFont("skill-text.ttf", 16, Font.PLAIN);
        Settings.questFont = getFont("quest-text.ttf", 16, Font.PLAIN);

        methods = new MethodHandler();

        initMethods();
    }

    public static double dTime() {
        double timeStep = 0.0000000017 * updateLength;
        if (timeStep <= 0 || timeStep >= 0.25) {
            timeStep = 0.045;
        }
        return timeStep;
    }

    public void initMethods() {
        methods.player = player;
        methods.settings = menu;

        GUI.init();
        Login.init();
    }

    // Used for when frame settings are changed from the settings screen and need to be refreshed.
    public void updateFrame() {
        if (Settings.fullScreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            frame.setExtendedState(0);
        }

        frame.setSize((int) Settings.curResolution().x, (int) Settings.curResolution().y);
        frame.setLocationRelativeTo(null);

        Settings.itemFont = new Font("Arial", Font.PLAIN, (int) Settings.curResolution().x / 75);

        GUI.init();
    }

    // Calculate fps and run other functions
    public void run() {

        // NOTE: just don't touch any of this, I got this off of StackOverFlow
        double lastFpsTime = 0;
        double lfps = 0;
        long lastLoopTime = System.nanoTime();

        while (running) {
            long optimalTime = 1000000000 / Settings.fpsCap;
            long now = System.nanoTime();
            updateLength = now - lastLoopTime;

            lastLoopTime = now;

            double delta = updateLength / ((double)optimalTime);

            lastFpsTime += updateLength;
            lfps++;

            if (lastFpsTime >= 1000000000) {
                lastFpsTime = 0;
                fps = lfps;
                lfps = 0;
            }

            if (delta < 2) {
                // Creates buffer graphics to pass to methods.
                BufferStrategy bs = getBufferStrategy();

                if (bs == null) {
                    createBufferStrategy(3);
                    continue;
                }

                Graphics g = bs.getDrawGraphics();

                Main.graphics = g;

                Input.update();

                if (isConnected && Main.dTime() != 0) {
                    update();
                    render();
                } else {
                    handleMenu();
                }

                bs.show();
                g.dispose();
            }

            long sleepTime = (lastLoopTime - System.nanoTime() + optimalTime) / 1000000;

            if (sleepTime < 0)
                continue;

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Calls every tick - use for game logic and other similar non-graphical devices
    public void update() {
        methods.update();
    }

    // Calls every tick - use for drawing graphics and other things similar
    public void render() {
        methods.render();
    }

    public void handleMenu() {
        Login.update();
        Login.render();
    }

    public static BufferedImage getImage(String root) {
        return Main.main.getImageFromRoot(root);
    }

    public BufferedImage getImageFromRoot(Object root) {
        BufferedImage image = null;
        InputStream stream = Main.class.getResourceAsStream("/res/images/" + root);

        if (stream == null) {
            System.err.println("Unrecognized file: /res/images/" + root.toString());
            return null;
        }

        try {
            image = ImageIO.read(stream);
        } catch (IOException e) {
            System.err.println("ERROR, IMAGE NOT FOUND: " + root);
        } catch (IllegalArgumentException e) {
            System.err.println("ILLEGAL! " + root);
        }

        return image;
    }

    public static Font getFont(String root, float size, int style) {
        InputStream is = Main.class.getResourceAsStream("/res/fonts/" + root);

        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);

            return font.deriveFont(style, size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void sendPacket(String message) {
        client.send(message);
    }

}