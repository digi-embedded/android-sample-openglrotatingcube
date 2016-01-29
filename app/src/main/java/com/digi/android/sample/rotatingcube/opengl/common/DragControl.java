/**
 * Copyright (c) 2014-2016, Digi International Inc. <support@digi.com>
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

package com.digi.android.sample.rotatingcube.opengl.common;

import com.digi.android.sample.rotatingcube.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public final class DragControl implements OnTouchListener {

	private static final int DRAG_START = 0;
	private static final int DRAG_END = 1;
	
	// Touch modes.
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static final int ZOOM_LONG = 3;

	private static final double FLING_REDUCTION = 3000;
	private static final double DRAG_SLOWING = 90;
	// Between 0 and 1 -> 0 to stop, 1 to spin always.
	private double flingDamping = 1;

	private final float[] dragX = new float[2];
	private final float[] dragY = new float[2];
	
	private final PointF longZoom = new PointF();
	
	// Old distance for the pinch-zoom.
	private float oldDist = 1f;
	// The current object scale.
	private float scale;
	// The maximum object scale.
	private final float maxScale;
	// The minimum object scale.
	private final float minScale;
	// The standard object scale.
	private final float standardScale;
	// Current touch mode.
	private int mode = NONE;
	// Base rotation, before drag.
	private final Quaternion rotation = new Quaternion(new Vector3(0,1));
	// The amount of rotation to add as part of drag.
	private final Quaternion dragRotation = new Quaternion(new Vector3(0,1));
	// Equal to rotation*dragRotation.
	private final Quaternion intermediateRotation = new Quaternion(new Vector3(0,1));
	// The current axis about which the object is being rotated
	private Vector3 spinAxis = new Vector3(0,0);

	/** Flinging
	* When you flick the screen with your finger it will keep spinning.
	* How fast it is spinning on its own. */
	private double flingSpeed = 0;
	
	// The axis about which we are being flung, if any.
	private final Vector3 flingAxis = new Vector3(0, 0);
	
	/** Fling rotation we most recent added to rotation.
	* Only here to save creating new objects too often. */
	private final Quaternion flingRotation = new Quaternion(new Vector3(0, 1));
	
	// The gesture detector used to detect special touch events.
	private final GestureDetector gestureDetector;
	
	
	public DragControl(final Context context) {
		this(context, -5f, -10f, -3f);
	}
	
	private DragControl(final Context context, float standardScale, float minScale, float maxScale) {
		this.maxScale = maxScale;
		this.minScale = minScale;
		this.standardScale = standardScale;
		this.scale = this.standardScale;

		this.gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
			@Override
			public boolean onDown(MotionEvent motionEvent) {
				// Do nothing here.
				return false;
			}

			@Override
			public void onShowPress(MotionEvent motionEvent) {
				// Do nothing here.
			}

			@Override
			public boolean onSingleTapUp(MotionEvent motionEvent) {
				// Do nothing here.
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
				// Do nothing here.
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				mode = ZOOM_LONG;
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				View toastRoot = inflater.inflate(R.layout.zoom_toast, null);
				Toast toast = new Toast(context);
				toast.setView(toastRoot);
				toast.setGravity(Gravity.START | Gravity.TOP,
						(int)(e.getX() - toastRoot.getWidth() - 130), (int)(e.getY() - toastRoot.getHeight() - 80));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				longZoom.set(e.getX(), e.getY());
			}

			@Override
			public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
				flingAxis.set(-velocityY, -velocityX);
				flingSpeed = flingAxis.magnitude()/FLING_REDUCTION;
				flingAxis.normalise();
				return true;
			}
		});

		this.gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
			@Override
			public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
				// Do nothing here.
				return false;
			}

			@Override
			public boolean onDoubleTap(MotionEvent motionEvent) {
				resetScale();
				return true;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent motionEvent) {
				// Do nothing here.
				return false;
			}
		});
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		int action = event.getAction();
		// Important use mask to distinguish pointer events (multi-touch).
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				dragX[DRAG_START] = dragX[DRAG_END] = event.getX();
				dragY[DRAG_START] = dragY[DRAG_END] = event.getY();
				flingSpeed = 0;
				mode = DRAG;
				return true;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				if (oldDist > 20f)
					mode = ZOOM;
				return true;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG){
					dragX[DRAG_END] = event.getX();
					dragY[DRAG_END] = event.getY();
				} else if (mode == ZOOM){
					float newDist = spacing(event);
					if (newDist > 20f){
						float tempScale = scale * (oldDist / newDist);
						if (tempScale < maxScale && tempScale > minScale)
							scale = tempScale;
						oldDist = newDist;
					}
				}
				else if (mode == ZOOM_LONG){
					float newDist = spacingZoom(event);
					if (newDist > 3f) {
						if (isZoomIn(event)) {
							if (scale < maxScale)
								scale += 0.1;
						} else {
							if (scale > minScale)
							scale -= 0.1;
						}
					}
					longZoom.set(event.getX(), event.getY());
				}
				return true;
			case MotionEvent.ACTION_UP:
				float rotateX = dragX[DRAG_END] - dragX[DRAG_START];
				float rotateY = dragY[DRAG_END] - dragY[DRAG_START];
				if (rotateX != 0 || rotateY != 0) {
					spinAxis = new Vector3(-rotateY, -rotateX);
					double mag = spinAxis.magnitude();
					spinAxis.normalise();
					intermediateRotation.set(spinAxis, mag/DRAG_SLOWING);
					rotation.mulThis(intermediateRotation);
				}
				dragX[DRAG_END] = dragX[DRAG_START] = 0;
				dragY[DRAG_END] = dragY[DRAG_START] = 0;
				mode = NONE;
				return true;
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				return true;
		}
		return false;
	}

	/**
	 * FIXME do the actual updating in a separate method that
	 * Is time-dependent.
	 */
	public Quaternion currentRotation() {
		float rotateX = dragX[DRAG_END] - dragX[DRAG_START];
		float rotateY = dragY[DRAG_END] - dragY[DRAG_START];

		if (mode == DRAG && (rotateX != 0 || rotateY != 0)) {
			spinAxis.set(-rotateY, -rotateX);
			double mag = spinAxis.magnitude();
			spinAxis.normalise();

			intermediateRotation.set(spinAxis, mag/DRAG_SLOWING);
			dragRotation.set(rotation);
			dragRotation.mulThis(intermediateRotation);

			return dragRotation;
		} else {
			if (flingSpeed > 0) {
				flingSpeed *= flingDamping;
				flingRotation.set(flingAxis, flingSpeed);
				rotation.mulThis(flingRotation);
			}
			return rotation;
		}
	}
	
	/**
	 * Retrieves current object scale.
	 * 
	 * @return The object scale.
	 */
	public float getCurrentScale(){
		return scale;
	}
	
	/**
	 * Sets the new fling damping value with the given one.
	 * 
	 * @param flingDamping New fling damping value.
	 */
	public void setFD(double flingDamping){
		this.flingDamping = flingDamping;
		if (this.flingDamping > 1)
			this.flingDamping = 1;
		if (this.flingDamping < 0)
			this.flingDamping = 0;
	}
	
	/**
	 * Retrieves absolute distance between pinch zoom points.
	 * 
	 * @param event Pinch zoom event.
	 * @return Absolute distance.
	 */
	private float spacing(MotionEvent event){
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	/**
	 * Retrieves absolute distance between long zoom points.
	 * 
	 * @param event Last touch event.
	 * @return Absolute distance.
	 */
	private float spacingZoom(MotionEvent event){
		float x = event.getX() - longZoom.x;
		float y = event.getY() - longZoom.y;
		return FloatMath.sqrt(x * x + y * y);
	}
	
	/**
	 * Checks whether we are doing zoom in or zoom out.
	 * 
	 * @param event The event originated by touch.
	 * @return True if we are zooming in, false otherwise.
	 */
	private boolean isZoomIn(MotionEvent event){
		return event.getY() < longZoom.y;
	}
	
	/**
	 * Resets the object scale.
	 */
	private void resetScale() {
		scale = standardScale;
	}
}