package org.minegaming.zzl.engine;

import java.util.*;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class ScreenManager
{
	public static Map<String, IGameObject> screenStore = new HashMap<String, IGameObject>();
	public static IGameObject currentScreen = null;
	public static Vector2f ScreenSize;
	public static boolean HandleSounds = true;
	public static boolean anaglyphMode = true;

	public static void Update(int delta)
	{
		if (currentScreen == null)
		{
			return;
		}
		currentScreen.Update(delta);
	}

	public static void Draw(Graphics g)
	{

		if (currentScreen == null)
		{
			return;
		}
		currentScreen.Draw(g);
	}

	public static void AddScreen(String stateId, IGameObject screen)
	{
		//System.Diagnostics.Debug.Assert(Exists(stateId));
		screenStore.put(stateId,screen);
	}

	public static void ChangeState(String stateId)
	{
		//System.Diagnostics.Debug.Assert(Exists(stateId));
		currentScreen = (IGameObject) screenStore.get(stateId);
	}

	public static Boolean Exists(String stateId)
	{
		return (screenStore.containsKey(stateId));
	}

	public static IGameObject[] getScreens()
	{
		IGameObject[] array = new IGameObject[screenStore.size()];
		int i = 0;
		for(IGameObject g : screenStore.values())
		{
			array[i] = g;
			i++;
		}
		return array;
	}
}
