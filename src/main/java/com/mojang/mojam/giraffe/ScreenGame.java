package com.mojang.mojam.giraffe;

import org.lwjgl.input.Keyboard;
import org.minegaming.zzl.engine.IGameObject;
import org.minegaming.zzl.engine.ScreenManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ScreenGame implements IGameObject
{
    private static Spawner spawner;
    private boolean active = true;
    
	@Override
	public void Update(int delta)
	{
		if(Game.getWorld().update(delta))
			ScreenManager.ChangeState("end");
        spawner.update(delta);
        Game.getMattis().handleInput(Game.input, Game.getWorld());
        
        if(Game.input.isKeyPressed(Keyboard.KEY_ESCAPE))
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
