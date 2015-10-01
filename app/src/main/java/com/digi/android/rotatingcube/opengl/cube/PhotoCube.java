package com.digi.android.rotatingcube.opengl.cube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.digi.android.rotatingcube.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
 * A photo cube with 6 pictures (textures) on its 6 faces.
 */
public class PhotoCube {
	private FloatBuffer vertexBuffer;  // Vertex Buffer

	private int numFaces = 6;
	private int[] imageFileIDs = {  // Image file IDs
//			R.drawable.red,
//			R.drawable.red,
//			R.drawable.blue,
//			R.drawable.blue,
//			R.drawable.green,
//			R.drawable.green
			R.drawable.logodigi,
			R.drawable.logodigi,
			R.drawable.android,
			R.drawable.android,
			R.drawable.freescale_logo,
			R.drawable.freescale_logo
	};

	private float[] vertices = {  // Vertices of the 6 faces
			// FRONT
			-1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
			1.0f, -1.0f,  1.0f,  // 1. right-bottom-front
			-1.0f,  1.0f,  1.0f,  // 2. left-top-front
			1.0f,  1.0f,  1.0f,  // 3. right-top-front
			// BACK
			1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
			-1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
			1.0f,  1.0f, -1.0f,  // 7. right-top-back
			-1.0f,  1.0f, -1.0f,  // 5. left-top-back
			// LEFT
			-1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
			-1.0f, -1.0f,  1.0f,  // 0. left-bottom-front 
			-1.0f,  1.0f, -1.0f,  // 5. left-top-back
			-1.0f,  1.0f,  1.0f,  // 2. left-top-front
			// RIGHT
			1.0f, -1.0f,  1.0f,  // 1. right-bottom-front
			1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
			1.0f,  1.0f,  1.0f,  // 3. right-top-front
			1.0f,  1.0f, -1.0f,  // 7. right-top-back
			// TOP
			-1.0f,  1.0f,  1.0f,  // 2. left-top-front
			1.0f,  1.0f,  1.0f,  // 3. right-top-front
			-1.0f,  1.0f, -1.0f,  // 5. left-top-back
			1.0f,  1.0f, -1.0f,  // 7. right-top-back
			// BOTTOM
			-1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
			1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
			-1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
			1.0f, -1.0f,  1.0f   // 1. right-bottom-front
	};

	float textureCoordinates[] = {0.0f, 1.0f,
			1.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f };

	private FloatBuffer mTextureBuffer;

	private boolean mShouldLoadTexture = false;

	private int[] textureIDs = new int[numFaces];
	private Bitmap[] bitmap = new Bitmap[numFaces];
	private Context context;

	// Constructor - Set up the vertex buffer
	public PhotoCube(Context context) {

		this.context = context;

		// Setup vertex-array buffer. Vertices in float. An float has 4 bytes
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder()); // Use native byte order
		vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
		vertexBuffer.put(vertices);         // Copy data into buffer
		vertexBuffer.position(0);           // Rewind

		setTextureCoordinates(textureCoordinates);
	}

	// Render the shape
	public void draw(GL10 gl) {
		if (mShouldLoadTexture) {
			loadTexture(gl);
			mShouldLoadTexture = false;
		}

		gl.glFrontFace(GL10.GL_CCW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);

		// front
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NICEST);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);


		// left

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[1]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NICEST);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);


		// back
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[2]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NICEST);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);


		// right
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[3]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NICEST);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);

		// top

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[4]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NICEST);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);

		// bottom

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[5]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NICEST);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	/**
	 * Set the texture coordinates.
	 *
	 * @param textureCoords
	 */
	protected void setTextureCoordinates(float[] textureCoords) {
		// float is 4 bytes, therefore we multiply the number if vertices with 4 * 6 faces.
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoords.length * 4 * 6);
		byteBuf.order(ByteOrder.nativeOrder());
		mTextureBuffer = byteBuf.asFloatBuffer();
		for (int face = 0; face < numFaces; face++)
			mTextureBuffer.put(textureCoords);
		mTextureBuffer.position(0);
	}


	// Load images into 6 GL textures
	public void loadTexture(GL10 gl) {
		gl.glGenTextures(6, textureIDs, 0); // Generate texture-ID array for 6 IDs

		for (int face = 0; face < numFaces; face++) {
			// Load up, and flip the texture:
			bitmap[face] = BitmapFactory.decodeStream(context.getResources().openRawResource(imageFileIDs[face]));


			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]);

			// Create Nearest Filtered Texture
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_NEAREST);

			// Build Texture from loaded bitmap for the currently-bind texture ID
			final int TILE_WIDTH = 512; 
			final int TILE_HEIGHT = 512; 
			final int TILE_SIZE = TILE_WIDTH * TILE_HEIGHT;

			int[] pixels = new int[TILE_WIDTH];

			short[] rgb_565 = new short[TILE_SIZE];

			// Convert 8888 to 565, and swap red and green channel position:

			int i = 0; 
			for (int y = 0; y < TILE_HEIGHT; y++) { 
				bitmap[face].getPixels(pixels, 0, TILE_WIDTH, 0, y, TILE_WIDTH, 1); 
				for (int x = 0; x < TILE_WIDTH; x++) { 
					int argb = pixels[x];
					int r = 0x1f & (argb >> 19); // Take 5 bits from 23..19 
					int g = 0x3f & (argb >> 10); // Take 6 bits from 15..10 
					int b = 0x1f & (argb >> 3); // Take 5 bits from 7.. 3 
					int rgb = (r << 11) | (g << 5) | b; 
					rgb_565[i] = (short) rgb;
					++i;
				}
			}
			ShortBuffer textureBuffer = ShortBuffer.wrap (rgb_565, 0, TILE_SIZE); 
			gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGB, bitmap[face].getWidth(), bitmap[face].getHeight(), 0, GL10.GL_RGB, GL10.GL_UNSIGNED_SHORT_5_6_5, textureBuffer); 
			//GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[face], 0);
			bitmap[face].recycle();
		}
	}
}
