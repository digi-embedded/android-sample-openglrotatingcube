/*
 * Copyright (c) 2014-2025, Digi International Inc. <support@digi.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.digi.android.sample.rotatingcube.opengl.cube;

import com.digi.android.sample.rotatingcube.opengl.common.DragControl;

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
