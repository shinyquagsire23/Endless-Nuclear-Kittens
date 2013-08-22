package org.zzl.minegaming.screens;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.zzl.minegaming.engine.Button;
import org.zzl.minegaming.engine.ButtonAction;
import org.zzl.minegaming.engine.IGameObject;
import org.zzl.minegaming.engine.ScreenManager;

import com.mojang.mojam.giraffe.Game;

public class ScreenPaused implements IGameObject
{

	Button resume = new Button(Game.SCREENSIZE.x / 2,600f, 540, 64, "Resume Game", new ButtonAction() {
		
		public void hover()
		{
			Game.playSound(Game.BUTTON_HOVER);
		}
		
		public void click()
		{
			ScreenManager.ChangeState("game");
		}
		
		public void unhover() {}
	});

	Button backToTitle = new Button(Game.SCREENSIZE.x / 2,700f, 540, 64, "Back to Title Screen", new ButtonAction() {
		
		public void hover()
		{
			Game.playSound(Game.BUTTON_HOVER);
		}
		
		public void click()
		{
			Game.playSound(Game.SOUND_SHOTGUN);
			Game.reset();
			ScreenManager.ChangeState("title");
		}
		
		public void unhover() {}
	});
	
	Button quitGame = new Button(Game.SCREENSIZE.x / 2,800f, "Quit Game", new ButtonAction() {
		
		public void hover()
		{
			Game.playSound(Game.BUTTON_HOVER);
		}
		
		public void click()
		{
			Game.playSound(Game.SOUND_SHOTGUN);
			System.exit(0);
		}
		
		public void unhover() {}
	});

	@Override
	public void Update(int delta)
	{
		if(Game.input.isKeyPressed(Keyboard.KEY_ESCAPE))
		{
			ScreenTitle.active = !ScreenTitle.active;
			ScreenManager.ChangeState("game");
		}
		if (Game.input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
		{
			backToTitle.sendClick();
			quitGame.sendClick();
			resume.sendClick();
		}
		backToTitle.Update(delta);
		quitGame.Update(delta);
		resume.Update(delta);
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
		Game.drawStringCentered(g, "-- paused --", Game.SCREENSIZE.x / 2, 500);
		Game.drawStringCentered(g, "(the kittens are waiting)", Game.SCREENSIZE.x / 2, 530);
		backToTitle.Draw(g);
		quitGame.Draw(g);
		resume.Draw(g);
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

}
