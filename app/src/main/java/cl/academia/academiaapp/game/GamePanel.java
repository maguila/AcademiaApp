package cl.academia.academiaapp.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by miguel on 26-09-16.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread thread;
    private RectPlayer player;
    private Point playerPoint;
    private ObstacleManager obstacleManager;

    public GamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        player = new RectPlayer(new Rect(300,300,200,200), Color.RED);
        playerPoint = new Point();

        obstacleManager = new ObstacleManager(200, 350, 75, Color.BLACK);

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry){
            try {
                thread.setRunning(false);
                thread.join();
            }catch (Exception e){ e.printStackTrace();}
            retry = false;
        }

        System.out.println("surfaceDestroyed ....");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //System.out.println("onTouchEvent ....");
         switch (event.getAction()){
             case MotionEvent.ACTION_DOWN:
             case MotionEvent.ACTION_MOVE:
                 playerPoint.set((int)event.getX(), (int)event.getY());

         }

        return true;
        //return super.onTouchEvent(event);
    }


    public void update(){
        player.update(playerPoint);
        obstacleManager.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);
        player.draw(canvas);
        obstacleManager.draw(canvas);
    }
}
