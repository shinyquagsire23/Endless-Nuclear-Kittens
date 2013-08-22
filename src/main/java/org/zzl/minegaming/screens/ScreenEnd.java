package org.zzl.minegaming.screens;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.zzl.minegaming.engine.IGameObject;
import org.zzl.minegaming.engine.ScreenManager;

import com.mojang.mojam.giraffe.Game;

public class ScreenEnd implements IGameObject
{
	@Override
	public void Update(int delta)
	{
		if (Game.input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
		{
            Game.gameRunning = false;
            Game.hasStarted = false;
            Game.getWorld().clearEntities();
            ScreenManager.ChangeState("title");
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
		
        g.setColor(new Color(0.0f, 0.1f, 0.2f, 0.6f));
        g.fillRect(0, 0, Game.SCREENSIZE.x, Game.SCREENSIZE.y);
        g.scale(2f, 2f);
        g.drawImage(ScreenTitle.title, Game.SCREENSIZE.x / 4 - ScreenTitle.title.getWidth() / 2, -10);
        g.scale(1 / 2f, 1 / 2f);
        g.setColor(Color.white);
        g.setFont(Game.FONT_MENU);
        
		float y = 580;
        float jump = 30;
        Game.drawStringCentered(g, "OH NO! TOO MANY KITTENS!", Game.SCREENSIZE.x / 2, y);
        y += jump;
        Game.drawStringCentered(g, "SCORE: " + Game.getScore(), Game.SCREENSIZE.x / 2, y);
        y += jump * 1.8f;
        Game.drawStringCentered(g, "[click to return to title]", Game.SCREENSIZE.x / 2, y);
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}

}
