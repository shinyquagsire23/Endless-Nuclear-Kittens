package org.zzl.minegaming.screens;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.zzl.minegaming.engine.Button;
import org.zzl.minegaming.engine.ButtonAction;
import org.zzl.minegaming.engine.IGameObject;
import org.zzl.minegaming.engine.ScreenManager;

import com.mojang.mojam.giraffe.Game;

public class ScreenOptions implements IGameObject
{
	Button back = new Button(Game.SCREENSIZE.x, Game.SCREENSIZE.y - 80, 512, 64, "Back to TitleScreen", new ButtonAction() {

		@Override
		public void hover()
		{
			Game.playSound(Game.BUTTON_HOVER);
		}

		@Override
		public void unhover() {}

		@Override
		public void click()
		{
			Game.playSound(Game.SOUND_SHOTGUN);
			Game.reset();
			ScreenManager.ChangeState("title");
		}
		
	});
	@Override
	public void Update(int delta)
	{
		if (Game.input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
		{
			back.sendClick();
		}
		if(back.getX() > Game.SCREENSIZE.x / 2)
		back.setX(back.getX() - 16);
		back.Update(delta);
	}

	@Override
	public void Draw(Graphics g)
	{
		g.scale(2, 2);
		Game.drawGrid(g);
		g.scale(1 / 2f, 1 / 2f);
		g.setFont(Game.FONT_MENU);
		g.drawString("SCORE: " + Game.getScore(), 10, 10);
		g.setColor(new Color(0.0f, 0.1f, 0.2f, 0.6f));
		g.fillRect(0, 0, Game.SCREENSIZE.x, Game.SCREENSIZE.y);
		
		back.Draw(g);
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

}
