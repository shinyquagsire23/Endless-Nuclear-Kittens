package org.minegaming.zzl.engine;

import org.newdawn.slick.Graphics;

public interface IGameObject
{
    void Update(int delta);
    void Draw(Graphics g);
    void init();
}
