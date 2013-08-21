package com.mojang.mojam.giraffe;

import org.minegaming.zzl.engine.IGameObject;
import org.minegaming.zzl.engine.ScreenManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import com.mojang.mojam.giraffe.entity.Mattis;

public class ScreenTitle implements IGameObject
{
    private Image title;
    
    private static int year;
    private static String randomWord;
    
    public void init()
    {
    	title = Util.loadImage("title_endlessnuclearkittens.png");
    }
    
	@Override
	public void Update(int delta)
	{
		if (Game.input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
		{
            Game.reset();
            Game.gameRunning = true;
            Game.hasStarted = true;
            ScreenManager.ChangeState("game");
        }
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

    }

	@Override
	public void Draw(Graphics g)
	{
        g.scale(2, 2);
		Game.drawWorld(g);
        g.scale(1 / 2f, 1 / 2f);
        g.setFont(Game.FONT_MENU);
        g.drawString("SCORE: " + Game.getScore(), 10, 10);
        boolean active = true;
        if (!Game.gameRunning || !active) 
        {
            g.setColor(new Color(0.0f, 0.1f, 0.2f, 0.6f));
            g.fillRect(0, 0, Game.SCREENSIZE.x, Game.SCREENSIZE.y);
            g.scale(2f, 2f);
            g.drawImage(title, Game.SCREENSIZE.x / 4 - title.getWidth() / 2, -10 + (Game.hasStarted && active ? -20 : 0));
            g.scale(1 / 2f, 1 / 2f);
            g.setColor(Color.white);
            g.setFont(Game.FONT_MENU);
            if (!active) 
            {
            	Game.drawStringCentered(g, "-- paused --", Game.SCREENSIZE.x / 2, 600);
            	Game.drawStringCentered(g, "(the kittens are waiting)", Game.SCREENSIZE.x / 2, 630);
            } 
            else 
            {
                float y = 580;
                float jump = 30;
                Game.drawStringCentered(g, "In the year " + year + ", kittens are", Game.SCREENSIZE.x / 2, y);
                y += jump;
                Game.drawStringCentered(g, randomWord + ".", Game.SCREENSIZE.x / 2, y);
                y += jump * 1.8f;
                Game.drawStringCentered(g, "Therefore, they must die.", Game.SCREENSIZE.x / 2, y);
                y += jump;
                Game.drawStringCentered(g, "[click to begin]", Game.SCREENSIZE.x / 2, y);
                y += jump;
            }
        }
	}

}
