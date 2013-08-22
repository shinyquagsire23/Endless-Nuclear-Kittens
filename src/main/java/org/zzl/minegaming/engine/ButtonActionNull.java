package org.zzl.minegaming.engine;

import com.mojang.mojam.giraffe.Game;

public class ButtonActionNull implements ButtonAction
{

	@Override
	public void hover() { Game.playSound(Game.BUTTON_HOVER); }

	@Override
	public void unhover() {}

	@Override
	public void click() {System.out.println("click");}

}
