package com.mojang.mojam.giraffe;

import org.minegaming.zzl.engine.IGameObject;
import org.minegaming.zzl.engine.ScreenManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ScreenEnd implements IGameObject
{

	private static String randomWord;
	private static int year;

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

	@Override
	public void Draw(Graphics g)
	{
		float y = 505;
        float jump = 30;
        y -= 75;
        Game.drawStringCentered(g, "OH NO! TOO MANY KITTENS!", Game.SCREENSIZE.x / 2, y);
        y += jump;
        Game.drawStringCentered(g, "SCORE: " + Game.getScore(), Game.SCREENSIZE.x / 2, y);
        y += jump * 1.8f;
        Game.drawStringCentered(g, "In the year " + year + ", kittens are", Game.SCREENSIZE.x / 2, y);
        y += jump;
        Game.drawStringCentered(g, randomWord + ".", Game.SCREENSIZE.x / 2, y);
        y += jump * 1.8f;
        Game.drawStringCentered(g, "Therefore, they must die.", Game.SCREENSIZE.x / 2, y);
        y += jump;
        Game.drawStringCentered(g, "[click to begin]", Game.SCREENSIZE.x / 2, y);
        y += jump;
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
	public void init()
	{
		// TODO Auto-generated method stub
		
	}

}
