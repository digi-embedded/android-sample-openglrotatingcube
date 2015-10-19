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
import android.widget.LinearLayout;

/**
 * OpenGL Rotating cube sample application.
 *
 * <p>This example demonstrates the usage of the OpenGL implementation in Android to
 * render a basic rotating cube with textures.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class RotatingCubeActivity extends Activity {
	// Menu entries.
	private static final int MENU_ID_FRICTION = 0;

	// Friction levels.
	private static final int FRICTION_NONE = 0;
	private static final int FRICTION_LOW = 1;
	private static final int FRICTION_MEDIUM = 2;
	private static final int FRICTION_HARD = 3;


	// Touch screen event handler.
	private DragControl dragControl;

	// Friction values.
	private final String[] frictions = new String[]{"None", "Low", "Medium", "Hard"};

	// Selected friction level.
	private int selectedFriction = FRICTION_NONE;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ID_FRICTION, 0, R.string.menu_friction).setIcon(android.R.drawable.ic_menu_sort_by_size);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ID_FRICTION:
				this.handleDifficultiesPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.cube);

		LinearLayout ly = (LinearLayout)this.findViewById(R.id.layout);
		CubeGLSurfaceView glView = new CubeGLSurfaceView(this);
		ly.addView(glView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.dragControl = new DragControl(this);
		glView.setOnTouchListener(dragControl);
		glView.setDragControl(dragControl);
		glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		glView.setZOrderOnTop(true);
	}

	/**
	 * Handles what happens when difficulties button is pressed.
	 */
	private void handleDifficultiesPressed(){
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("Select friction");
		ab.setSingleChoiceItems(this.frictions, this.selectedFriction, new DialogInterface.OnClickListener() {
			@Override
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