package com.example.android.rotatingcube.opengl.cube;

import com.example.android.rotatingcube.opengl.common.DragControl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/*
 * Custom GL view by extending GLSurfaceView so as
 * to override event handlers such as onKeyUp(), onTouchEvent()
 */
public class CubeGLSurfaceView extends GLSurfaceView {
	
	/** Custom GL Renderer */
	private CubeGLRenderer renderer;    

	/** Touch screen event handler */
	private DragControl dragControl;
	
	/**
	 * Class constructor. Allocate and set the renderer
	 * 
	 *  @param context Application context.
	 */
	public CubeGLSurfaceView(Context context) {
		super(context);
		renderer = new CubeGLRenderer(context);
		//setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setRenderer(renderer);
		// Request focus, otherwise key/button won't react
		this.requestFocus();  
		this.setFocusableInTouchMode(true);
	}
	
	public CubeGLSurfaceView(Context context, AttributeSet attributes){
		super(context,attributes);
		renderer = new CubeGLRenderer(context);
		//setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setRenderer(renderer);
		// Request focus, otherwise key/button won't react
		this.requestFocus();
		this.setFocusableInTouchMode(true);
	}
	

	/**
	 * Sets the drag control with the given one.
	 * 
	 * @param dragControl New drag control.
	 */
	public void setDragControl(DragControl dragControl){
		this.dragControl = dragControl;
		this.renderer.setDragControl(dragControl);
	}
	
	/**
	 * Retrieves the drag control associated with this view.
	 * 
	 * @return The associated drag control.
	 */
	public DragControl getDragControl(){
		return this.dragControl;
	}
}