package cl.academia.academiaapp.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by miguel on 27-09-16.
 */

public class Obstacle implements GameObject {

    private Rect rectangle;
    private Rect rectangle2;
    private int color;
    private int start;
    private int playerGap;
    private int rectHeight;

    public Obstacle(int rectHeight, int color, int startX, int startY, int playerGap){
        this.color = color;
        rectangle  = new Rect(0,startY, startX, startY + rectHeight );
        rectangle2 = new Rect(startX + playerGap, startY, Constants.SCREEN_WIDTH, startY + rectHeight );
    }

    public void incrementY(float y){
        rectangle.top += y;
        rectangle.bottom += y;
        rectangle2.top += y;
        rectangle2.bottom += y;
    }

    public boolean playerCollide(RectPlayer player){
        return Rect.intersects(rectangle, player.getRectangle()) || Rect.intersects(rectangle2, player.getRectangle());
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(rectangle, paint);
        canvas.drawRect(rectangle2, paint);
    }

    @Override
    public void update() {

    }

    public Rect getRectangle() {
        return rectangle;
    }


}
