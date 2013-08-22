package org.zzl.minegaming.screens;


import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.zzl.minegaming.engine.Button;
import org.zzl.minegaming.engine.ButtonAction;
import org.zzl.minegaming.engine.ButtonActionNull;
import org.zzl.minegaming.engine.IGameObject;
import org.zzl.minegaming.engine.ScreenManager;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Util;

public class ScreenTitle implements IGameObject
{
	public static Image title;

	private static int year;
	private static String randomWord;
	public static boolean active = true;
	private static float tX;
	private static float x;
	private static float mx;
	
	private int titleMin = -512;
	private int wordsMin = -256;
	private static int scrollSpeed = 16;
	private static boolean begin = false;
	private static boolean widescreen = false;
	private static boolean showMenu = false;
	
	private List<Button> buttons = new ArrayList<Button>();

	public void init()
	{
		title = Util.loadImage("title_endlessnuclearkittens.png");
		tX = Game.SCREENSIZE.x / 4 - title.getWidth() / 2;
		x = Game.SCREENSIZE.x / 2;
		mx =  x + Game.SCREENSIZE.x - Game.SCREENSIZE.x / 4;
		
		if(Game.SCREENSIZE.x >= 1600)
		{
			titleMin = -32;
			wordsMin = 512 - 64;
			scrollSpeed = 8;
			widescreen = true;
		}
		int y = 110;
		int jump = 128;
		buttons.add(new Button((widescreen ? mx : x + Game.SCREENSIZE.x - Game.SCREENSIZE.x / 4),y,"Singleplayer", new ButtonAction() {
			public void hover()
			{
				Game.playSound(Game.BUTTON_HOVER);
			}
			
			public void click()
			{
				Game.playSound(Game.SOUND_PICKUP);
				startGame();
			}
			
			public void unhover(){}
		}));
		y += jump;
		buttons.add(new Button((widescreen ? mx : x + Game.SCREENSIZE.x - Game.SCREENSIZE.x / 4),y,"Multiplayer", new ButtonActionNull()));
		y += jump;
		buttons.add(new Button((widescreen ? mx : x + Game.SCREENSIZE.x - Game.SCREENSIZE.x / 4),y,"Options", new ButtonActionNull()));
		y += jump;
		buttons.add(new Button((widescreen ? mx : x + Game.SCREENSIZE.x - Game.SCREENSIZE.x / 4),y,"Exit", new ButtonAction(){
			public void hover()
			{
				Game.playSound(Game.BUTTON_HOVER);
			}
			
			public void click()
			{
				Game.playSound(Game.SOUND_SHOTGUN);
				System.exit(0);
			}
			
			public void unhover(){}
		}));
		y += jump;
	}

	@Override
	public void Update(int delta)
	{
		if (Game.input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
		{
			for(Button b : buttons)
			{
				b.sendClick();
			}
				begin = true;
		}
        if (Game.input.isKeyPressed(Input.KEY_ESCAPE)) {
            System.exit(0);
        }
		
		if(begin)
		{
			if(tX > titleMin)
				tX -= scrollSpeed;
			if(x > wordsMin)
				x -= scrollSpeed * 2;
			if(tX <= 0 && x <= 512)
			{
				showMenu = true;
			}
			else
			{
				mx = Game.SCREENSIZE.x + 512;
			}
			
			if(showMenu && mx >  Game.SCREENSIZE.x + 64 - Game.SCREENSIZE.x / 4)
			{
				mx -= scrollSpeed * 4;
			}
		}
		
		for(Button b : buttons)
		{
			b.setX((widescreen ? mx : x + Game.SCREENSIZE.x - Game.SCREENSIZE.x / 4));
			b.Update(delta);
		}
	}
	
	private void startGame()
	{
		Game.reset();
		Game.gameRunning = true;
		Game.hasStarted = true;
		ScreenManager.ChangeState("game");
	}

	public static void reset() {
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

		try
		{
			tX = Game.SCREENSIZE.x / 4 - title.getWidth() / 2;
			x = Game.SCREENSIZE.x / 2;
			mx =  x + Game.SCREENSIZE.x - Game.SCREENSIZE.x / 4;
		}
		catch(Exception e){}
		scrollSpeed = 16;
		showMenu = false;
	}

	@Override
	public void Draw(Graphics g)
	{
		g.scale(2, 2);
		Game.drawGrid(g);
		g.scale(1 / 2f, 1 / 2f);
		g.setFont(Game.FONT_MENU);
		g.drawString("SCORE: " + Game.getScore(), 10, 10);
		if (!Game.gameRunning || !active) 
		{
			g.setColor(new Color(0.0f, 0.1f, 0.2f, 0.6f));
			g.fillRect(0, 0, Game.SCREENSIZE.x, Game.SCREENSIZE.y);
			g.scale(2f, 2f);
			g.drawImage(title, tX, -10 + (Game.hasStarted && active ? -20 : 0));
			g.scale(1 / 2f, 1 / 2f);
			g.setColor(Color.white);
			g.setFont(Game.FONT_MENU);

			float y = 580;
			float jump = 30;
			Game.drawStringCentered(g, "In the year " + year + ", kittens are", x, y);
			y += jump;
			Game.drawStringCentered(g, randomWord + ".", x, y);
			y += jump * 1.8f;
			Game.drawStringCentered(g, "Therefore, they must die.", x, y);
			y += jump;
			Game.drawStringCentered(g, (showMenu ? "[click a button]" : "[click to begin]"), x, y);
			y += jump;
			
			for(Button b : buttons)
				b.Draw(g);
		}
	}

}
