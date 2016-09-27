package cl.academia.academiaapp.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by miguel on 27-09-16.
 */

public interface Scene {
    public void update();
    public void draw(Canvas canvas);
    public void terminate();
    public void recieveTouch(MotionEvent event);
}
