/*
 * Copyright (c) 2014-2021, Digi International Inc. <support@digi.com>
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

final class Vector3 {
	private double x,y,z;

	double getX() {
		return x;
	}

	double getY() {
		return y;
	}

	double getZ() {
		return z;
	}

	Vector3(double ix, double iy) {
		x = ix;
		y = iy;
		z = 0;
	}

	void set(double ix, double iy) {
		x = ix;
		y = iy;
		z = 0;
	}

	double magnitude() {
		return Math.sqrt(x*x + y*y + z*z);
	}

	void normalise() {
		double mag = magnitude();
		x /= mag;
		y /= mag;
		z /= mag;
	}
}