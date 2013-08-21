package com.mojang.mojam.giraffe;

import org.minegaming.zzl.engine.IGameObject;
import org.minegaming.zzl.engine.ScreenManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ScreenGame implements IGameObject
{
    private static Spawner spawner;
    
	@Override
	public void Update(int delta)
	{
		if (Game.input.isMousePressed(Input.MOUSE_LEFT_BUTTON));
		if(!Game.getWorld().update(delta));
			//ScreenManager.ChangeState("end");
        spawner.update(delta);
        Game.getMattis().handleInput(Game.input, Game.getWorld());
	}

	@Override
	public void Draw(Graphics g)
	{
		g.scale(2, 2);
		Game.drawWorld(g);
		g.scale(1/2f, 1/2f);
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
