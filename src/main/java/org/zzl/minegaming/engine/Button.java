package org.zzl.minegaming.engine;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import com.mojang.mojam.giraffe.Game;

public class Button implements IGameObject
{
	private float x;
	private float y;
	private float width;
	private float height;
	private String text;
	private boolean hover = false;
	private ButtonAction action;
	
	
	public Button(float xpos, float ypos, float w, float h, String s, ButtonAction a)
	{
		x = xpos;
		y = ypos;
		width = w;
		height = h;
		text = s;
		action = a;
	}
	
	public Button(float xpos, float ypos, String s, ButtonAction a)
	{
		this(xpos,ypos,256+128,64,s,a);
	}
	
	public Button(float xpos, float ypos, float width, float height, String s)
	{
		this(xpos,ypos,width,height,s,new ButtonActionNull());
	}
	
	public Button(float xpos, float ypos, String s)
	{
		this(xpos,ypos,s,new ButtonActionNull());
	}
	
	public void setX(float f)
	{
		this.x = f;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public void Draw(Graphics g)
	{
		g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.6f));
		g.fillRect(x - (width - width / 2), y, width, 64);
		if(!hover)
			g.setColor(new Color(0.0f, 0.1f, 0.2f, 0.6f));
		else
			g.setColor(new Color(1.0f, 0.2f, 0.3f, 0.8f));
		g.fillRect(x - (width - width / 2 - 12), y + 10, width - 20, 44);
		g.setColor(Color.white);
		Game.drawStringCentered(g, text, x, y + 20);
	}

	@Override
	public void Update(int delta)
	{
		if(Game.input.getAbsoluteMouseX() >= this.x - (width - width / 4) && Game.input.getAbsoluteMouseX() < this.x + width - 16)
		{
			if(Game.input.getAbsoluteMouseY() >= this.y - 16 && Game.input.getAbsoluteMouseY() < this.y + 64 + 16)
			{
				if(hover == false)
					action.hover();
				hover = true;
			}
			else
			{
				if(hover == true)
					action.unhover();
				hover = false;
			}
		}
		else
		{
			if(hover == true)
				action.unhover();
			hover = false;
		}
		
		System.out.println(hover + " " + Game.input.isMousePressed(Input.MOUSE_LEFT_BUTTON));
		if(hover && Game.input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
			action.click();
				
	}
	
	public void sendClick()
	{
		if(hover)
			action.click();
	}

	@Override
	public void init(){}
}
