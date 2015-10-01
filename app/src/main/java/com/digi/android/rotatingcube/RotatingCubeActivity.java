package com.digi.android.rotatingcube;

import com.digi.android.rotatingcube.opengl.common.DragControl;
import com.digi.android.rotatingcube.opengl.cube.CubeGLSurfaceView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This application demonstrates the usage of the OpenGL implementation in
 * Android to render a basic cube with textures.
 *
 * <p>These are the application features:</p>
 * <ul>
 * <li>OpenGL shape rendering with textures.</li>
 * <li>Rendering over a transparent background.</li>
 * <li>Multi axis rotation using touch screen.</li>
 * <li>Zoom emulation using touch screen.</li>
 * <li>Friction simulation using settings menu.</li>
 * </ul>
 */
public class RotatingCubeActivity extends Activity {
		// Menu entries
		private static final int MENU_ID_BACK = 0;
		private static final int MENU_ID_FRICTION = 1;

		// Friction levels
		private static final int FRICTION_NONE = 0;
		private static final int FRICTION_LOW = 1;
		private static final int FRICTION_MEDIUM = 2;
		private static final int FRICTION_HARD = 3;
		
		
		/** Touch screen event handler */
		private DragControl dragControl;
		
		/** Friction values */
		private String[] frictions = new String[]{"None", "Low", "Medium", "Hard"};
		
		/** Selected friction level */
		private int selectedFriction = FRICTION_NONE;

		/*
		 * (non-Javadoc)
		 * @see android.app.Activity#onCreate(android.os.Bundle)
		 */
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			this.setContentView(R.layout.cube);
			
			LinearLayout ly = (LinearLayout)this.findViewById(R.id.layout);
			CubeGLSurfaceView glView = new CubeGLSurfaceView(this);
			ly.addView(glView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			//CubeGLSurfaceView glView = (CubeGLSurfaceView)findViewById(R.id.cube);
			this.dragControl = new DragControl(this);
			glView.setOnTouchListener(dragControl);
			((CubeGLSurfaceView) glView).setDragControl(dragControl);
			//glView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT | LayoutParams.FLAG_BLUR_BEHIND);
			glView.setZOrderOnTop(true);

			ImageView logoDigi = (ImageView)this.findViewById(R.id.logo_digi);
			ImageView logoAndroid = (ImageView)this.findViewById(R.id.logo_android);
			TextView overlayText = (TextView)this.findViewById(R.id.overlay_text);
			
			logoDigi.bringToFront();
			logoAndroid.bringToFront();
			overlayText.bringToFront();
		}

		/*
		 * (non-Javadoc)
		 * @see android.app.Activity#onPause()
		 */
		protected void onPause() {
			super.onPause();
			//glView.onPause();
		}

		/*
		 * (non-Javadoc)
		 * @see android.app.Activity#onResume()
		 */
		protected void onResume() {
			super.onResume();
			//glView.onResume();
		}

		/*
		 * (non-Javadoc)
		 * @see android.app.Activity#onDestroy()
		 */
		protected void onDestroy(){
			super.onDestroy();
		}

		/*
		 * (non-Javadoc)
		 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
		 */
		public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(Menu.NONE, MENU_ID_BACK, 2, R.string.menu_back).setIcon(android.R.drawable.ic_menu_revert);
			menu.add(Menu.NONE, MENU_ID_FRICTION, 2, R.string.menu_friction).setIcon(android.R.drawable.ic_menu_sort_by_size);
			return super.onCreateOptionsMenu(menu);
		}

		/*
		 * (non-Javadoc)
		 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
		 */
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case MENU_ID_BACK:
				this.finish();
				break;
			case MENU_ID_FRICTION:
				this.handleDifficultiesPressed();
				break;
			}
			return super.onOptionsItemSelected(item);
		}

		/**
		 * Handles what happens when difficulties button is pressed.
		 */
		private void handleDifficultiesPressed(){
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("Select friction");
			ab.setSingleChoiceItems(this.frictions, this.selectedFriction, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					selectedFriction = item;
					dragControl.setFD(retrieveFDFromSelection(selectedFriction));
					dialog.dismiss();
				}
			});
			ab.show();
		}

		/**
		 * Retrieves the real friction value depending on selected friction level.
		 * 
		 * @param selection Selected friction level.
		 * @return Real friction value.
		 */
		private float retrieveFDFromSelection(int selection){
			switch(selection) {
			case FRICTION_NONE: return 1;
			case FRICTION_LOW: return 0.98f;
			case FRICTION_MEDIUM: return 0.9f;
			case FRICTION_HARD: return 0;
			default: return 1;
			}
		}

}