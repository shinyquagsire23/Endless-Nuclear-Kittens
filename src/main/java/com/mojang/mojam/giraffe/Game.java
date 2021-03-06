package com.mojang.mojam.giraffe;

import com.mojang.mojam.giraffe.entity.Mattis;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.util.ResourceLoader;
import org.zzl.minegaming.engine.IGameObject;
import org.zzl.minegaming.engine.MultiControls;
import org.zzl.minegaming.engine.ScreenManager;
import org.zzl.minegaming.engine.XBOXButtons;
import org.zzl.minegaming.screens.ScreenEnd;
import org.zzl.minegaming.screens.ScreenGame;
import org.zzl.minegaming.screens.ScreenMultiplayerGame;
import org.zzl.minegaming.screens.ScreenOptions;
import org.zzl.minegaming.screens.ScreenPaused;
import org.zzl.minegaming.screens.ScreenTitle;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;

public class Game extends BasicGame {
    public static Font FONT;
    public static Font FONT_MENU;
    public static Font FONT_LARGE;
    public static Font[] FONT_SCORES = new Font[5];
    public static boolean fullscreen = true;
    public static boolean wideFullHybrid = false;
    public static boolean roundedCorners = true; //Do we want to round off the corners in hybrid mode?
    public static Vector2f SCREENSIZE = new Vector2f(1024, 768);
    public static Vector2f REALSCREENSIZE = new Vector2f(1024, 768);

    public static final int SOUND_BLASTER = 0;
    public static final int SOUND_EXPLOSION = 1;
    public static final int SOUND_FLAMETHROWER = 2;
    public static final int SOUND_ROCKETLAUNCH = 3;
    public static final int SOUND_SHOTGUN = 4;
    public static final int SOUND_TELEPORT = 5;
    public static final int SOUND_CHAIN_EXPLOSION = 6;
    public static final int SOUND_BULLET_HIT = 7;
    public static final int SOUND_PICKUP = 8;
    public static final int BUTTON_HOVER = 9;
    public static AppGameContainer appContainer;
    private static Sound[] sounds = new Sound[10];
    
    private static Image starfield;
    private static Image[] stars = new Image[3];
    
    private static Image screen;

    private static Mattis[] mattis;
    public static int numPlayers = 1;
    private static World world;
    
    public static Color[] playerColors = new Color[] {Color.white,Color.red,Color.blue,Color.green,Color.yellow,Color.orange,Color.cyan,Color.lightGray,Color.magenta,Color.pink,Color.darkGray,Color.black};
    public static Vector2f[] cursorPos = new Vector2f[12];

    public static Input input;
    public static Image cursor;

    public static boolean hasStarted = false;
    public static boolean gameRunning = false;
    
    public static int rumbleTimer = 0;

    public static final Game INSTANCE = new Game();

    private Game() {
        super("Endless Nuclear Kittens <3");
    }

    public static World getWorld() {
        return world;
    }
    
    public static Mattis[] getMattis()
    {
    	return mattis;
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        input = gc.getInput();
        input.initControllers();
    	numPlayers = MultiControls.numPlayers();
        reset();

        
        for(int i = 0; i < MultiControls.numPlayers(); i++)
        {
        	try
        	{
        		cursorPos[i] = new Vector2f(0,0);
        		for(int j = 0; j < 7; j++)
        		{
        			MultiControls.getController(i).setDeadZone(j, 0.2f);
        			MultiControls.getController(i).setDeadZone(j, 0.2f);
        		}
        	}
        	catch(Exception e){}
        }
        
        System.out.println(input.getControllerCount() + " controllers found!");
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

        starfield = Util.loadImage("stars_01.png");
        stars[0] = Util.loadImage("stars_02.png", Color.white);
        stars[1] = Util.loadImage("stars_03.png", Color.white);
        stars[2] = Util.loadImage("stars_04.png", Color.white);
        
        screen = Util.loadImage("screen.png");

        sounds[SOUND_BLASTER] = new Sound("blaster.wav");
        sounds[SOUND_EXPLOSION] = new Sound("explosion.wav");
        sounds[SOUND_FLAMETHROWER] = new Sound("flamethrower.wav");
        sounds[SOUND_ROCKETLAUNCH] = new Sound("rocketlaunch.wav");
        sounds[SOUND_SHOTGUN] = new Sound("shotgun.wav");
        sounds[SOUND_TELEPORT] = new Sound("teleport.wav");
        sounds[SOUND_CHAIN_EXPLOSION] = new Sound("chain_explosion.wav");
        sounds[SOUND_BULLET_HIT] = new Sound("bullet_hit.wav");
        sounds[SOUND_PICKUP] = new Sound("item_pickup.wav");
        sounds[BUTTON_HOVER] = new Sound("button_hover.wav");
        
        for(IGameObject g : ScreenManager.getScreens())
        	g.init();
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
    	ScreenManager.Update(delta);
        if (!gc.hasFocus() || delta > 1000) {
        	gc.setMouseGrabbed(false);
            return; //If the game isn't focus or the last frame took longer than 1 second to render, skip this one
        }
        
		rumbleTimer -= delta;
		if(rumbleTimer <= 0)
		{
			MultiControls.unrumble();
		}

        if (input.isKeyPressed(Input.KEY_END)) {
            System.exit(0);
        }
        if(input.isKeyPressed(Input.KEY_F3))
        	gc.setMouseGrabbed(!gc.isMouseGrabbed());
        if(ScreenManager.currentScreen instanceof ScreenGame || ScreenManager.currentScreen instanceof ScreenMultiplayerGame)
        {
        	for(int i = 0; i < numPlayers; i++)
            {
        		if(MultiControls.getMode() == MultiControls.P2X_CONTROLLER && i == 0)
        		{
        			cursorPos[i].x = input.getAbsoluteMouseX();
        			cursorPos[i].y = input.getAbsoluteMouseY();
        			continue;
        		}
            	
        		if(MultiControls.getAxisValue(i,XBOXButtons.AX_RIGHT_STICK_X) != 0)
        			cursorPos[i].x = mattis[i].getX() * 2 - world.getCameraPosition().x * 2 + MultiControls.getAxisValue(i,XBOXButtons.AX_RIGHT_STICK_X) * SCREENSIZE.x / 4;
        		else
        			cursorPos[i].x = mattis[i].getX() * 2 - world.getCameraPosition().x * 2;
        		

        		if(MultiControls.getAxisValue(i,XBOXButtons.AX_RIGHT_STICK_Y) != 0)
        			cursorPos[i].y = mattis[i].getY() * 2 - world.getCameraPosition().y * 2 + MultiControls.getAxisValue(i,XBOXButtons.AX_RIGHT_STICK_Y) * SCREENSIZE.x / 4;
        		else
        			cursorPos[i].y = mattis[i].getY() * 2 - world.getCameraPosition().y * 2;
            }
        }
        else
        {
        	for(int i = 0; i < MultiControls.numPlayers(); i++)
        	{
        		if(MultiControls.getMode() == MultiControls.P2X_CONTROLLER && i == 0)
        		{
        			cursorPos[i].x = input.getAbsoluteMouseX();
        			cursorPos[i].y = input.getAbsoluteMouseY();
        			continue;
        		}
        		cursorPos[i].x += MultiControls.getAxisValue(i,XBOXButtons.AX_RIGHT_STICK_X) * 20;
        		cursorPos[i].y += MultiControls.getAxisValue(i,XBOXButtons.AX_RIGHT_STICK_Y) * 20;
        		cursorPos[i].x += MultiControls.getAxisValue(i,XBOXButtons.AX_LEFT_STICK_X) * 20;
        		cursorPos[i].y += MultiControls.getAxisValue(i,XBOXButtons.AX_LEFT_STICK_Y) * 20;

        		if(cursorPos[i].x > SCREENSIZE.x)
        			cursorPos[i].x = SCREENSIZE.x;

        		if(cursorPos[i].y > SCREENSIZE.y)
        			cursorPos[i].y = SCREENSIZE.y;

        		if(cursorPos[i].x < 0)
        			cursorPos[i].x = 0;

        		if(cursorPos[i].y < 0)
        			cursorPos[i].y = 0;
        	}
        }
    }

    public static void reset()
    {
        if(fullscreen && wideFullHybrid) //Use fullscreen mode, but have game emulate a fullscreen monitor on a widescreen resolution
        {
        	SCREENSIZE = new Vector2f(1024 * appContainer.getScreenHeight() / 768, appContainer.getScreenHeight() - 64);
        	REALSCREENSIZE = new Vector2f(appContainer.getScreenWidth(), appContainer.getScreenHeight());
        }
        else if(fullscreen) //True fullscreen, edge to edge up and down
        {
        	SCREENSIZE = new Vector2f(appContainer.getScreenWidth(), appContainer.getScreenHeight());
        	REALSCREENSIZE = new Vector2f(appContainer.getScreenWidth(), appContainer.getScreenHeight());
        }
        
    	mattis = new Mattis[numPlayers];
    	for(int i = 0; i < numPlayers; i++)
    	{
    		mattis[i] = new Mattis(300, 250, SCREENSIZE,i);
    	}
        world = new World(SCREENSIZE, mattis);
    	ScreenTitle.reset();
    	ScreenGame.reset();
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
    	g.pushTransform();
        g.translate((REALSCREENSIZE.x - SCREENSIZE.x) / 2, 0);
        ScreenManager.Draw(g);
        g.scale(2, 2);
        g.setColor(Color.white);
        //g.drawImage(Game.cursor, Game.input.getAbsoluteMouseX() / 2.0f - Game.cursor.getWidth() / 2, Game.input.getAbsoluteMouseY() / 2.0f - Game.cursor.getHeight() / 2);
        for(int i = 0; i < MultiControls.numPlayers(); i++)
        {
        	g.setColor(playerColors[i]);
        	g.drawImage(Game.cursor, cursorPos[i].x / 2.0f - Game.cursor.getWidth() / 2, cursorPos[i].y / 2.0f - Game.cursor.getHeight() / 2,playerColors[i]);
        	if(ScreenManager.currentScreen instanceof ScreenGame)
        		break;
        	g.scale(1 / 2f, 1 / 2f);
        	drawStringCentered(g,i+1+"",cursorPos[i].x - 0f,cursorPos[i].y - 10f);
            g.scale(2, 2);
        }
        g.scale(1 / 2f, 1 / 2f);
        g.popTransform();
        
        //Cover everything with black bars to give it the fullscreen effect
        g.setColor(Color.black);
        g.fillRect(0, 0, (REALSCREENSIZE.x - SCREENSIZE.x) / 2, REALSCREENSIZE.y);
        g.fillRect(REALSCREENSIZE.x - (REALSCREENSIZE.x - SCREENSIZE.x) / 2, 0, (REALSCREENSIZE.x - SCREENSIZE.x) / 2, REALSCREENSIZE.y);
        g.texture(new Rectangle((REALSCREENSIZE.x - SCREENSIZE.x) / 2,0,SCREENSIZE.x,REALSCREENSIZE.y), screen,true);
        //g.fillRect(0, 0, REALSCREENSIZE.x, 32);
        //g.fillRect(0, REALSCREENSIZE.y - 32, REALSCREENSIZE.x, 32);
        g.setColor(Color.white);
    }

    public static void drawWorld(Graphics g)
    {
		g.setFont(Game.FONT);
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
    }
    
    public static void drawGrid(Graphics g)
    {
    	g.setFont(Game.FONT);
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
    	world.drawGrid(g, true);
    }
    
    public static BigInteger getScore()
    {
    	return world.getScore();
    }
    
    public static BigInteger getFrags()
    {
    	return world.getFrags();
    }
    
    public static BigInteger getBossFrags()
    {
    	return world.getBossFrags();
    }
    
    public static void drawStringCentered(Graphics g, String string, float x, float y) {
        float width = FONT_MENU.getWidth(string);
        g.drawString(string, (int) (x - width / 2), y);
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(INSTANCE);
        appContainer = app;
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
       
        if(fullscreen && wideFullHybrid) //Use fullscreen mode, but have game emulate a fullscreen monitor on a widescreen resolution
        {
        	SCREENSIZE = new Vector2f(1024 * app.getScreenHeight() / 768, app.getScreenHeight());
        	REALSCREENSIZE = new Vector2f(app.getScreenWidth(), app.getScreenHeight());
        }
        else if(fullscreen) //True fullscreen, edge to edge up and down
        {
        	SCREENSIZE = new Vector2f(app.getScreenWidth(), app.getScreenHeight());
        	REALSCREENSIZE = new Vector2f(app.getScreenWidth(), app.getScreenHeight());
        }
        
        ScreenManager.AddScreen("title", new ScreenTitle());
        ScreenManager.AddScreen("game", new ScreenGame());
        ScreenManager.AddScreen("multiplayergame", new ScreenMultiplayerGame());
        ScreenManager.AddScreen("pause", new ScreenPaused());
        ScreenManager.AddScreen("end", new ScreenEnd());
        ScreenManager.AddScreen("options", new ScreenOptions());
        ScreenManager.ChangeState("title");
        if(fullscreen)
        	app.setDisplayMode((int) app.getScreenWidth(), (int) REALSCREENSIZE.y, fullscreen);
        else
        	app.setDisplayMode((int) SCREENSIZE.x, (int) SCREENSIZE.y, fullscreen);
        app.start();
    }
}