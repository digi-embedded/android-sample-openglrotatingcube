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

package com.digi.android.rotatingcube.opengl.common;

final class Vector3 {
	private double x,y,z;

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Vector3(double ix, double iy) {
		x = ix;
		y = iy;
		z = (double) 0;
	}

	public void set(double ix, double iy) {
		x = ix;
		y = iy;
		z = (double) 0;
	}

	public double magnitude() {
		return Math.sqrt(x*x+y*y+z*z);
	}

	public void normalise() {
		double mag = magnitude();
		x /= mag;
		y /= mag;
		z /= mag;
	}
}