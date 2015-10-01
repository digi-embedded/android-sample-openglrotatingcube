package com.digi.android.rotatingcube.opengl.common;

import com.digi.android.rotatingcube.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.Display;
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
	
	// Touch regions
	private static final int ZOOM_IN = 0;
	private static final int ZOOM_OUT = 1;
	private static final int SPIN_IN = 2;
	private static final int SPIN_OUT = 3;
	
	// Touch modes
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static final int ZOOM_LONG = 3;

	private static final double FLING_REDUCTION = 3000;
	private static final double DRAG_SLOWING = 90;
	
	private double flingDamping = 1; // Between 0 and 1 -> 0 to stop, 1 to spin always

	private float[] dragX = new float[2];
	private float[] dragY = new float[2];
	
	private PointF longZoom = new PointF();
	
	/** Old distance for the pinch-zoom */
	private float oldDist = 1f;
	
	/** The current object scale */
	private float scale;
	
	/** The maximum object scale */
	private float maxScale;
	
	/** The minimum object scale */
	private float minScale;
	
	/** The standard object scale*/
	private float standardScale;
	
	/** Size of the screen width */
	private int screenWidth;
	
	/** Size of the screen height */
	private int screenHeight;
	
	/** Size of the touch square side used to detect touch event on specific regions of the screen */
	private int touchSquareSide;
	
	/** The application context */
	private Context context;
	
	/** Current touch mode */
	private int mode = NONE;

	/** Base rotation, before drag */
	private Quaternion rotation = new Quaternion(new Vector3(0,1,0), 0);

	/** The amount of rotation to add as part of drag */
	private Quaternion dragRotation = new Quaternion(new Vector3(0,1,0), 0);

	/** Equal to rotation*dragRotation. */
	private Quaternion intermediateRotation = new Quaternion(new Vector3(0,1,0), 0);

	/** The current axis about which the object is being rotated */
	private Vector3 spinAxis = new Vector3(0,0,0);

	/** Flinging
	* When you flick the screen with your finger it will keep spinning.
	* How fast it is spinning on its own */
	private double flingSpeed = 0;
	
	/** The axis about which we are being flung, if any */
	private Vector3 flingAxis = new Vector3(0, 0, 0);
	
	/** Fling rotation we most recent added to rotation.
	* Only here to save creating new objects too often. */
	private Quaternion flingRotation = new Quaternion(new Vector3(0, 1, 0), 0);
	
	/** The gesture detector used to detect special touch events */
	private GestureDetector gestureDetector;	
	
	
	public DragControl(final Context context) {
		this(context, -5f, -10f, -3f);
	}
	
	public DragControl(final Context context, float standardScale, float minScale, float maxScale) {
		this.context = context;
		Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
		this.screenWidth = display.getWidth();
		this.screenHeight = display.getHeight();
		this.touchSquareSide = screenHeight / 5;
		this.maxScale = maxScale;
		this.minScale = minScale;
		this.standardScale = standardScale;
		this.scale = this.standardScale;
		this.gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.GestureDetector.SimpleOnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
			 */
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				flingAxis.set(-velocityY, -velocityX, 0);
				flingSpeed = flingAxis.magnitude()/FLING_REDUCTION;
				flingAxis.normalise();
				return true;
			}
			
			/*
			 * (non-Javadoc)
			 * @see android.view.GestureDetector.SimpleOnGestureListener#onDoubleTap(android.view.MotionEvent)
			 */
			public boolean onDoubleTap(MotionEvent e) {
				resetScale();
				return true;
			}
			
			/*
			 * (non-Javadoc)
			 * @see android.view.GestureDetector.SimpleOnGestureListener#onSingleTapConfirmed(android.view.MotionEvent)
			 */
			public boolean onSingleTapConfirmed(MotionEvent e) {
				// Do nothing here.
				return true;
			}
			
			/*
			 * (non-Javadoc)
			 * @see android.view.GestureDetector.SimpleOnGestureListener#onLongPress(android.view.MotionEvent)
			 */
			public void onLongPress(MotionEvent e) {
				super.onLongPress(e);
				mode = ZOOM_LONG;
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            View toastRoot = inflater.inflate(R.layout.zoom_toast, null);
	            Toast toast = new Toast(context);
	            toast.setView(toastRoot);
	            toast.setGravity(Gravity.LEFT | Gravity.TOP,
	            		(int)(e.getX() - toastRoot.getWidth() - 130), (int)(e.getY() - toastRoot.getHeight() - 80));
	            toast.setDuration(Toast.LENGTH_SHORT);
	            toast.show();
	            longZoom.set(e.getX(), e.getY());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	public boolean onTouch(View view, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		int action = event.getAction();
		// Important use mask to distinguish pointer events (multi-touch)
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			switch (getTouchArea((int)event.getX(), (int)event.getY())){
			case ZOOM_IN:
				if (scale < maxScale)
					scale += 0.5f;
				break;
			case ZOOM_OUT:
				if (scale > minScale)
					scale -= 0.5f;
				break;
			case SPIN_IN:
				if((flingDamping >= 0) && (flingDamping < 0.9))
					setFD(getFD() + 0.015);
				else if((flingDamping>=0.9)&&(flingDamping<1))
					setFD(getFD() + 0.02);
				break;
			case SPIN_OUT:
				if ((flingDamping > 0) && (flingDamping < 0.9))
					setFD(getFD() - 0.015);
				else if((flingDamping >= 0.9) && (flingDamping <= 1))
					setFD(getFD() - 0.02);
				break;
			default:
				dragX[DRAG_START] = dragX[DRAG_END] = event.getX();
				dragY[DRAG_START] = dragY[DRAG_END] = event.getY();
				flingSpeed = 0;
				mode = DRAG; 
			}
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
				spinAxis = new Vector3(-rotateY, -rotateX, 0);
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
	 * is time-dependent.
	 */
	public Quaternion currentRotation() {
		float rotateX = dragX[DRAG_END] - dragX[DRAG_START];
		float rotateY = dragY[DRAG_END] - dragY[DRAG_START];

		if (mode == DRAG && (rotateX != 0 || rotateY != 0)) {
			spinAxis.set(-rotateY, -rotateX, 0);
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
	 * Retrieves current fling damping value.
	 * 
	 * @return Current fling damping value.
	 */
	public double getFD(){
		return this.flingDamping;
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
	 * Retrieves the special region of the screen where touch event occurred.
	 * 
	 * @param x Touch X.
	 * @param y Touch Y.
	 * @return Screen region.
	 */
	private int getTouchArea(int x, int y){
		int mode;
		if (x <= touchSquareSide) {
			if (y <= touchSquareSide)
				mode = ZOOM_IN;
			else if (y <= 2*touchSquareSide)
				mode = ZOOM_OUT;
		} else if (x > screenWidth - touchSquareSide) {
			if (y <= touchSquareSide)
				mode = SPIN_IN;
			else if (y <= 2*touchSquareSide)
				mode = SPIN_OUT;
		}
		// Reset mode, we don't want to check special regions
		mode = -1;
		return mode;
	}
	
	/**
	 * Resets the object scale.
	 */
	private void resetScale() {
		scale = standardScale;
	}
}