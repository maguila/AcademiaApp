package cl.academia.academiaapp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGlActivity extends AppCompatActivity{


    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new ClearGLSurfaceView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }


    class ClearGLSurfaceView extends GLSurfaceView {
        public ClearGLSurfaceView(Context context) {
            super(context);
            mRenderer = new ClearRenderer();
            setRenderer(mRenderer);
        }

        public boolean onTouchEvent(final MotionEvent event) {
            queueEvent(new Runnable(){
                public void run() {
                    mRenderer.setColor(event.getX() / getWidth(),
                            event.getY() / getHeight(), 1.0f);
                }});
            return true;
        }

        ClearRenderer mRenderer;
    }


    class ClearRenderer implements GLSurfaceView.Renderer {
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Do nothing special.
        }

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            gl.glViewport(0, 0, w, h);
        }

        public void onDrawFrame(GL10 gl) {
            gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        }

        public void setColor(float r, float g, float b) {
            mRed = r;
            mGreen = g;
            mBlue = b;
        }

        private float mRed;
        private float mGreen;
        private float mBlue;
    }

}
