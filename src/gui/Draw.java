package gui;

import semantic.Semantic;

import java.awt.*;

/**
 * Created by think on 2016/12/9.
 */
public class Draw extends Panel {
    public Draw(){
        super();
    }

    @Override
    public void paint(Graphics g){
        g.setColor(Color.black);
        g.fillOval((int) Semantic.x,(int)Semantic.y,2,2 );
    }

    @Override
    public void update(Graphics g){
        paint(g);
    }

    @Override
    public void repaint(){
        update(getGraphics());
    }

}
