package cl.academia.academiaapp.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by miguel on 26-09-16.
 */

public class MainThread extends Thread{

    public static final int MAX_FPS = 30;
    public double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    private static Canvas canvas;


    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel){
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis = 1000 / MAX_FPS;
        long waitTime;
        int frameCout = 0;
        long totalTime = 0;
        long targetTime = 1000 / MAX_FPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(canvas!=null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch (Exception e){ e.printStackTrace(); }
                }
            }
            timeMillis = (System.nanoTime() - startTime ) / 1000000;
            waitTime = targetTime - timeMillis;

            try{
                if(waitTime > 0)
                    this.sleep(waitTime);
            }catch (Exception e){ e.printStackTrace(); }

            totalTime += System.nanoTime() - startTime;
            frameCout++;

            if(frameCout == MAX_FPS){
                averageFPS = 1000/((totalTime/frameCout)/1000000);
                System.out.println("totalTime="+totalTime+" frameCout="+frameCout+"  averageFPS = " + averageFPS);
                frameCout = 0;
                totalTime = 0;
            }

        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
