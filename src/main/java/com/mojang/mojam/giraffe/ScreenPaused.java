package com.mojang.mojam.giraffe;

import org.lwjgl.input.Keyboard;
import org.minegaming.zzl.engine.IGameObject;
import org.minegaming.zzl.engine.ScreenManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ScreenPaused implements IGameObject
{

	@Override
	public void Update(int delta)
	{
		if(Game.input.isKeyPressed(Keyboard.KEY_ESCAPE))
		{
			ScreenTitle.active = !ScreenTitle.active;
			ScreenManager.ChangeState("game");
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
		g.drawImage(ScreenTitle.title, Game.SCREENSIZE.x / 4 - ScreenTitle.title.getWidth() / 2, -10 + (Game.hasStarted ? -20 : 0));
		g.scale(1 / 2f, 1 / 2f);
		g.setColor(Color.white);
		g.setFont(Game.FONT_MENU);
		Game.drawStringCentered(g, "-- paused --", Game.SCREENSIZE.x / 2, 600);
		Game.drawStringCentered(g, "(the kittens are waiting)", Game.SCREENSIZE.x / 2, 630);
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

}
