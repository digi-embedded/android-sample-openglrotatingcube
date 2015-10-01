package com.digi.android.rotatingcube.opengl.cube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.digi.android.rotatingcube.opengl.common.DragControl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

/**
 *  OpenGL Custom renderer used with GLSurfaceView 
 */
public class CubeGLRenderer implements GLSurfaceView.Renderer{

	/** Touch screen event handler */
	private DragControl dragControl;

	/** OpenGL object in form of a rendered cube */
	private PhotoCube photocube;

	/**
	 * Class constructor.
	 * 
	 * @param context Application context.
	 */
	public CubeGLRenderer(Context context) {
		photocube = new PhotoCube(context);
	}

	/**
	 * Sets the drag control with the given one.
	 * 
	 * @param dragControl New drag control.
	 */
	public void setDragControl(DragControl dragControl){
		this.dragControl = dragControl;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set color's clear-value to black
		gl.glClearColor(0f, 0f, 0f, 0f);
		// Set depth's clear-value to farthest
		gl.glClearDepthf(1.0f);
		// Enables depth-buffer for hidden surface removal
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_BLEND);
		// The type of depth testing to do
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Nice perspective view
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		// Enable smooth shading of color
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Disable dithering for better performance
		//gl.glDisable(GL10.GL_DITHER);
		// Selects blending method
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// Allows alpha channels or transperancy
		gl.glEnable(GL10.GL_ALPHA_TEST);
		// Sets aplha function
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
		// Enable texture
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// Load cube textures
		photocube.loadTexture(gl); 
	}

	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
        /*
         * Set our projection matrix. This doesn't have to be done
         * each time we draw, but usually a new projection needs to
         * be set when the viewport is resized.
         */
		gl.glViewport(0, 0, width, height);
		// if (height == 0) height = 1;   // To prevent divide by zero
		float aspect = (float)width / height;
		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);
		// Setup perspective projection, with aspect ratio matches viewport
		// Select projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset projection matrix
		gl.glLoadIdentity();
		// Use perspective projection
		GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);
		// Select model-view matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset
		gl.glLoadIdentity();
	}

	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {
		// Clear color and depth buffers using clear-value set earlier
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Reset the model-view matrix		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity(); 
		// Translate into the screen depending on scale
		gl.glTranslatef(0.0f, 0.0f, dragControl.getCurrentScale());   
		// Rotate depending on touch rotation
		gl.glMultMatrixf(dragControl.currentRotation().toMatrix(), 0);
		// Draw the cube
		photocube.draw(gl); 
	}
}