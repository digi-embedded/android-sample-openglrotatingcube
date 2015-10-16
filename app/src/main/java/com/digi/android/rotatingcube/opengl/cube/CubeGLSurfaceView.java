/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.android.rotatingcube.opengl.cube;

import com.digi.android.rotatingcube.opengl.common.DragControl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/*
 * Custom GL view by extending GLSurfaceView so as
 * to override event handlers such as onKeyUp(), onTouchEvent().
 */
public class CubeGLSurfaceView extends GLSurfaceView {
	
	// Custom GL Renderer.
	private final CubeGLRenderer renderer;

	/**
	 * Class constructor. Allocate and set the renderer.
	 * 
	 *  @param context Application context.
	 */
	public CubeGLSurfaceView(Context context) {
		super(context);
		renderer = new CubeGLRenderer(context);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setRenderer(renderer);
		// Request focus, otherwise key/button won't react
		this.requestFocus();  
		this.setFocusableInTouchMode(true);
	}
	
	public CubeGLSurfaceView(Context context, AttributeSet attributes){
		super(context,attributes);
		renderer = new CubeGLRenderer(context);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setRenderer(renderer);
		// Request focus, otherwise key/button won't react.
		this.requestFocus();
		this.setFocusableInTouchMode(true);
	}


	/**
	 * Sets the drag control with the given one.
	 * 
	 * @param dragControl New drag control.
	 */
	public void setDragControl(DragControl dragControl){
		this.renderer.setDragControl(dragControl);
	}
}