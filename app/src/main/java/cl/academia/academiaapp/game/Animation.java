package cl.academia.academiaapp.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by miguel on 27-09-16.
 */

public class Animation {

    private Bitmap[] frames;
    private int frameIndex;
    private float frameTime;
    private long lastFrame;

    private boolean isPlaying = false;

    public Animation(Bitmap[] frames, float animTime){
        this.frames = frames;
        this.frameIndex = 0;
        this.frameTime = animTime/frames.length;
        this.lastFrame = System.currentTimeMillis();
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void play(){
        isPlaying = true;
        frameIndex = 0;
        lastFrame = System.currentTimeMillis();
    }

    public void stop(){
        isPlaying = false;
    }

    public void draw(Canvas canvas, Rect destination){
        if(!isPlaying)
            return;

        scaleRect(destination);

        canvas.drawBitmap(frames[frameIndex], null, destination, new Paint());
    }

    private void scaleRect(Rect rect){
        float whRatio = (float) (frames[frameIndex].getWidth())/frames[frameIndex].getHeight();

        if(rect.width() > rect.height())
            rect.left = rect.right - (int)(rect.height() * whRatio);
        else
            rect.top = rect.bottom - (int)(rect.width() * (1/whRatio));

    }

    public void update(){

        if(!isPlaying)
            return;

        if(System.currentTimeMillis() - lastFrame > frameTime*1000){
            frameIndex++;
            frameIndex = frameIndex >= frames.length ? 0 : frameIndex;
            lastFrame = System.currentTimeMillis();
        }
    }
}
