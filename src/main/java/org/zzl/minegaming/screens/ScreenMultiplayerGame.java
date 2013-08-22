package org.zzl.minegaming.screens;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Graphics;
import org.zzl.minegaming.engine.IGameObject;
import org.zzl.minegaming.engine.MultiControls;
import org.zzl.minegaming.engine.ScreenManager;
import org.zzl.minegaming.engine.XBOXButtons;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Spawner;

public class ScreenMultiplayerGame implements IGameObject
{
    private static Spawner spawner;
    
	@Override
	public void Update(int delta)
	{
		if(Game.numPlayers == 1 || spawner == null)
		{
			Game.numPlayers = MultiControls.numPlayers();
			Game.reset();
			reset();
		}
		if(Game.getWorld().update(delta))
			ScreenManager.ChangeState("end");
        spawner.update(delta);
        for(int i = 0; i < Game.numPlayers; i++)
        	Game.getMattis()[i].handleInput(Game.input, Game.getWorld());
        
        if(Game.input.isKeyPressed(Keyboard.KEY_ESCAPE) || MultiControls.isButtonDown(XBOXButtons.BTN_Y))
        {
        		ScreenTitle.active = !ScreenTitle.active;
        		ScreenManager.ChangeState("pause");
        }
	}

	@Override
	public void Draw(Graphics g)
	{
		g.scale(2, 2);
		Game.drawWorld(g);
		g.scale(1/2f, 1/2f);
		g.setFont(Game.FONT_MENU);
        g.drawString("SCORE: " + Game.getScore(), 10, 10);
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	public static void reset()
	{
        spawner = new Spawner(Game.getWorld(), Game.getMattis());
	}
}
