package org.zzl.minegaming.engine;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;

import com.mojang.mojam.giraffe.Game;

public class MultiControls
{
	public static final int P1X_CONTROLLER = 0; //Player 1 uses controller one and the rest of the players follow
	public static final int P2X_CONTROLLER = 1; //Player 1 uses the mouse and keyboard, player 2 uses controller 1 and rest follow
	private static int currentMode = 1;
	
	public static boolean isLeftMouseMenu()
	{
		boolean mouse = Game.input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		for(int i = 0; i < Controllers.getControllerCount(); i++)
		{
			if(Controllers.getController(i).isButtonPressed(XBOXButtons.BTN_A) || Controllers.getController(i).isButtonPressed(XBOXButtons.BTN_B))
				return true;
		}
		return mouse;
	}
	
	public static boolean isLeftMouse()
	{
		boolean mouse = Game.input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		for(int i = 0; i < Controllers.getControllerCount(); i++)
		{
			if(Controllers.getController(i).getAxisValue(XBOXButtons.AX_RIGHT_TRIGGER) > -1)
				return true;
		}
		return mouse;
	}
	
	public static boolean isRightMouse()
	{
		boolean mouse = Game.input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON);
		for(int i = 0; i < Controllers.getControllerCount(); i++)
		{
			if(Controllers.getController(i).getAxisValue(XBOXButtons.AX_LEFT_TRIGGER) > -1)
				return true;
		}
		return mouse;
	}
	
	public static boolean isButtonDown(int button)
	{
		for(int i = 0; i < Controllers.getControllerCount(); i++)
		{
			if(Controllers.getController(i).isButtonPressed(button))
				return true;
		}
		return false;
	}
	
	public static Controller getController(int player)
	{
		try
		{
			if(currentMode == P1X_CONTROLLER)
				return Controllers.getController(player);
			else if (currentMode == P2X_CONTROLLER && player > 0)
				return Controllers.getController(player-1);
			else
				return Controllers.getController(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return Controllers.getController(0);
		}
	}
	
	public static void setControllerOption(int option)
	{
		currentMode = option;
	}

	public static int numPlayers()
	{
		if(currentMode == P1X_CONTROLLER)
			return Controllers.getControllerCount();
		else if(currentMode == P2X_CONTROLLER)
			return Controllers.getControllerCount() + 1;
		return 0;
	}

	public static float getAxisValue(int i, int b)
	{
		if(currentMode == P1X_CONTROLLER)
		{
			return getController(i).getAxisValue(b);
		}
		else if(currentMode == P2X_CONTROLLER)
		{
			if(i == 0 && (b == XBOXButtons.AX_LEFT_STICK_X || b == XBOXButtons.AX_RIGHT_STICK_X || b == XBOXButtons.AX_LEFT_STICK_Y || b == XBOXButtons.AX_RIGHT_STICK_Y))
				return 0;
			else
				return getController(i).getAxisValue(b);
		}
		return 0.0f;
	}
	
	public static int getMode()
	{
		return currentMode;
	}
}
