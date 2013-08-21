package com.mojang.mojam.giraffe;

import com.mojang.mojam.giraffe.entity.Mattis;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class Game extends BasicGame {
    public static Font FONT;
    public static Font FONT_MENU;
    public static Font FONT_LARGE;
    public static Font[] FONT_SCORES = new Font[5];
    public static boolean fullscreen = false;
    public static Vector2f SCREENSIZE = new Vector2f(1024, 768);

    public static final int SOUND_BLASTER = 0;
    public static final int SOUND_EXPLOSION = 1;
    public static final int SOUND_FLAMETHROWER = 2;
    public static final int SOUND_ROCKETLAUNCH = 3;
    public static final int SOUND_SHOTGUN = 4;
    public static final int SOUND_TELEPORT = 5;
    public static final int SOUND_CHAIN_EXPLOSION = 6;
    public static final int SOUND_BULLET_HIT = 7;
    public static final int SOUND_PICKUP = 8;
    private static Sound[] sounds = new Sound[9];

    private Mattis mattis;
    private World world;

    private Spawner spawner;
    private Input input;
    private Image cursor;

    private boolean hasStarted = false;
    private boolean gameRunning = false;

    public static final Game INSTANCE = new Game();

    private Image title;
    private Image starfield;
    private Image[] stars = new Image[3];

    private int year;
    private String randomWord;

    private Game() {
        super("Endless Nuclear Kittens <3");
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        reset();
        input = gc.getInput();
        gc.setTargetFrameRate(60);
        gc.setMouseGrabbed(true);
        gc.setShowFPS(false);
        gc.setAlwaysRender(true);
        cursor = new Image("crosshair.png", Color.white);
        cursor.setFilter(Image.FILTER_NEAREST);

        // load font from a .ttf file
        java.awt.Font ttfFont = null;
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream("prstartk.ttf");
            ttfFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // [EB]: Doing this in static gives me an exception
        FONT = new TrueTypeFont(ttfFont.deriveFont(java.awt.Font.BOLD, 16), true);
        for (int i = 0; i < FONT_SCORES.length; i++) {
            FONT_SCORES[i] = new TrueTypeFont(ttfFont.deriveFont(java.awt.Font.BOLD, 8 + i * 6), true);
        }
        FONT_MENU = new TrueTypeFont(ttfFont.deriveFont(java.awt.Font.BOLD, 24), true);
        FONT_LARGE = new TrueTypeFont(ttfFont.deriveFont(java.awt.Font.BOLD, 32), true);

        title = Util.loadImage("title_endlessnuclearkittens.png");
        starfield = Util.loadImage("stars_01.png");
        stars[0] = Util.loadImage("stars_02.png", Color.white);
        stars[1] = Util.loadImage("stars_03.png", Color.white);
        stars[2] = Util.loadImage("stars_04.png", Color.white);

        sounds[SOUND_BLASTER] = new Sound("blaster.wav");
        sounds[SOUND_EXPLOSION] = new Sound("explosion.wav");
        sounds[SOUND_FLAMETHROWER] = new Sound("flamethrower.wav");
        sounds[SOUND_ROCKETLAUNCH] = new Sound("rocketlaunch.wav");
        sounds[SOUND_SHOTGUN] = new Sound("shotgun.wav");
        sounds[SOUND_TELEPORT] = new Sound("teleport.wav");
        sounds[SOUND_CHAIN_EXPLOSION] = new Sound("chain_explosion.wav");
        sounds[SOUND_BULLET_HIT] = new Sound("bullet_hit.wav");
        sounds[SOUND_PICKUP] = new Sound("item_pickup.wav");
    }

    private static ByteBuffer loadIcon(URL url) throws IOException, NullPointerException {
        InputStream fileInputStream = url.openStream();
        try {
            PNGDecoder decoder = new PNGDecoder(fileInputStream);
            ByteBuffer bb = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
            decoder.decode(bb, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            bb.flip();
            return bb;
        } finally {
            fileInputStream.close();
        }
    }

    public static void playSound(int sound) {
        playSound(sound, 1.0f, 0.5f);
    }

    public static void playSound(int sound, float pitch, float volume) {
        sounds[sound].play(pitch, volume);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        if (!gc.hasFocus() || delta > 1000) {
            return; //If the game isn't focus or the last frame took longer than 1 second to render, skip this one
        }
        if (gameRunning) {
            gameRunning = !world.update(delta);
            spawner.update(delta);
            mattis.handleInput(input, world);
            if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) ; //DUMMY MOUSEPRESS CALL - HACK!
        } else {
            if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                reset();
                gameRunning = true;
                hasStarted = true;
            }
        }
        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            System.exit(0);
        }
    }

    private void reset() {
        year = 2300 + (int) (Math.random() * 300);
        double rnd = Math.random();
        if (rnd < 0.2) {
            randomWord = "the new earth overlords";
        } else if (rnd < 0.4) {
            randomWord = "still cute and cuddly";
        } else if (rnd < 0.6) {
            randomWord = "the spawn of the devil";
        } else if (rnd < 0.7) {
            randomWord = "an evil little bunch";
        } else if (rnd < 0.8) {
            randomWord = "the most beautiful things";
        } else if (rnd < 0.9) {
            randomWord = "collectors of souls";
        } else {
            randomWord = "made of love";
        }
        mattis = new Mattis(300, 250, SCREENSIZE);
        world = new World(SCREENSIZE, mattis);
        spawner = new Spawner(world, mattis);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setFont(FONT);
        g.scale(2, 2);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 3; y++) {
                g.drawImage(starfield, x * 256, y * 256);
            }
        }
        for (int x = -2; x < 8; x++) {
            for (int y = -2; y < 7; y++) {
                float offsetX = -world.getCameraPosition().x / 20.0f;
                float offsetY = -world.getCameraPosition().y / 20.0f;
                g.drawImage(stars[(11 * (19 * Math.abs(x) + 17 * Math.abs(y))) % 3], x * 256 + offsetX, y * 256 + offsetY);
            }
        }
        world.draw(g);
        g.scale(1 / 2f, 1 / 2f);
        g.setFont(FONT_MENU);
        g.drawString("SCORE: " + world.getScore(), 10, 10);
        boolean active = gc.hasFocus();
        if (!gameRunning || !active) {
            g.setColor(new Color(0.0f, 0.1f, 0.2f, 0.6f));
            g.fillRect(0, 0, SCREENSIZE.x, SCREENSIZE.y);
            g.scale(2f, 2f);
            g.drawImage(title, SCREENSIZE.x / 4 - title.getWidth() / 2, -10 + (hasStarted && active ? -20 : 0));
            g.scale(1 / 2f, 1 / 2f);
            g.setColor(Color.white);
            g.setFont(FONT_MENU);
            if (!active) {
                drawStringCentered(g, "-- paused --", SCREENSIZE.x / 2, 600);
                drawStringCentered(g, "(the kittens are waiting)", SCREENSIZE.x / 2, 630);
            } else {
                float y = 580;
                float jump = 30;
                if (hasStarted) {
                    y -= 75;
                    drawStringCentered(g, "OH NO! TOO MANY KITTENS!", SCREENSIZE.x / 2, y);
                    y += jump;
                    drawStringCentered(g, "SCORE: " + world.getScore(), SCREENSIZE.x / 2, y);
                    y += jump * 1.8f;
                }
                drawStringCentered(g, "In the year " + year + ", kittens are", SCREENSIZE.x / 2, y);
                y += jump;
                drawStringCentered(g, randomWord + ".", SCREENSIZE.x / 2, y);
                y += jump * 1.8f;
                drawStringCentered(g, "Therefore, they must die.", SCREENSIZE.x / 2, y);
                y += jump;
                drawStringCentered(g, "[click to begin]", SCREENSIZE.x / 2, y);
                y += jump;
            }
        }
        g.scale(2, 2);
        g.setColor(Color.white);
        g.drawImage(cursor, input.getAbsoluteMouseX() / 2.0f - cursor.getWidth() / 2, input.getAbsoluteMouseY() / 2.0f - cursor.getHeight() / 2);
        g.scale(1 / 2f, 1 / 2f);
    }

    private void drawStringCentered(Graphics g, String string, float x, float y) {
        float width = FONT_MENU.getWidth(string);
        g.drawString(string, (int) (x - width / 2), y);
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(INSTANCE);
        for(String s : args)
        {
        	if(s.equalsIgnoreCase("-f") || s.equalsIgnoreCase("--fullscreen"))
        		fullscreen = true;
        }

       try {
            Display.setIcon(new ByteBuffer[]{
                    loadIcon(Game.class.getClassLoader().getResource("128.png")),
                    loadIcon(Game.class.getClassLoader().getResource("64.png")),
                    loadIcon(Game.class.getClassLoader().getResource("32.png")),
                    loadIcon(Game.class.getClassLoader().getResource("16.png")),
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        if(fullscreen)
        	SCREENSIZE = new Vector2f(app.getScreenWidth(), app.getScreenHeight());
        app.setDisplayMode((int) SCREENSIZE.x, (int) SCREENSIZE.y, fullscreen);
        app.start();
    }
}