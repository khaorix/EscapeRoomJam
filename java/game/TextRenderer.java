package game;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public class TextRenderer {
	private Texture fontTexture;
	private int charWidth = 32;
	private int charHeight = 32;
	private int columns;
	private int rows;
	
	private Map<Character, int[]> charMap;

	public TextRenderer(String fontPath, int imageWidth, int imageHeight) {
		fontTexture = new Texture(fontPath);
		columns = imageWidth / charWidth;
		rows = imageHeight / charHeight;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		buildCharMap();
	}

	private void buildCharMap() {
		 charMap = new HashMap<>();

	        // Row 0: A to P (16 characters)
	        for (int i = 0; i < 16; i++) {
	            charMap.put((char) ('A' + i), new int[]{0, i});
	        }

	        // Row 1: Q to Z (10 characters) + 6 empty spaces
	        for (int i = 0; i < 10; i++) {
	            charMap.put((char) ('Q' + i), new int[]{1, i});
	        }

	        // Row 2: a to p (16 characters)
	        for (int i = 0; i < 16; i++) {
	            charMap.put((char) ('a' + i), new int[]{2, i});
	        }

	        // Row 3: q to z (10 characters) + ' + 5 empty spaces
	        for (int i = 0; i < 10; i++) {
	            charMap.put((char) ('q' + i), new int[]{3, i});
	        }
	        charMap.put('\'', new int[] {3, 10});
	        charMap.put(' ', new int[] {3, 11});

	        // Row 4: Numbers and punctuation
	        charMap.put('0', new int[]{4, 0});
	        charMap.put('1', new int[]{4, 1});
	        charMap.put('2', new int[]{4, 2});
	        charMap.put('3', new int[]{4, 3});
	        charMap.put('4', new int[]{4, 4});
	        charMap.put('5', new int[]{4, 5});
	        charMap.put('6', new int[]{4, 6});
	        charMap.put('7', new int[]{4, 7});
	        charMap.put('8', new int[]{4, 8});
	        charMap.put('9', new int[]{4, 9});
	        charMap.put('.', new int[]{4, 10});
	        charMap.put(',', new int[]{4, 11});
	        charMap.put('"', new int[]{4, 12});
	        charMap.put(':', new int[]{4, 13});
	        charMap.put('!', new int[]{4, 14});
	        charMap.put('?', new int[]{4, 15});
	        	    }

	public void drawText(String text, float x, float y, float maxWidth) {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float cursorX = x;
		float cursorY = y;

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		for (char c : text.toCharArray()) {
			if (cursorX + charWidth > maxWidth) {
				cursorX = x;
				cursorY += charHeight;
			}
			
			if (c < 32 || c > 126) continue;
			drawCharacter(c, cursorX, cursorY);
			cursorX += charWidth; // Move to the next character position
		}
		//System.out.println();
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void drawCharacter(char c, float x, float y) {
        if (!charMap.containsKey(c)) {
            return; // Skip undefined characters
        }

        int[] pos = charMap.get(c);
        int row = pos[0];
        int col = pos[1];

        float u = (float) col / columns;
        float v = (float) row / rows;
        float u2 = u + (1.0f / columns);
        float v2 = v + (1.0f / rows);
        //System.out.print("Character : " + c + " , Row : " + row + ", Col: " + col);
        fontTexture.bind();
        renderQuad(x, y, charWidth, charHeight, u, v, u2, v2);
    }

	private void renderQuad(float x, float y, float width, float height, float u, float v, float u2, float v2) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(u, v);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(u2, v);
		GL11.glVertex2f(x + width, y);
		GL11.glTexCoord2f(u2, v2);
		GL11.glVertex2f(x + width, y + height);
		GL11.glTexCoord2f(u, v2);
		GL11.glVertex2f(x, y + height);
		GL11.glEnd();
	}

	public void cleanup() {
		fontTexture.cleanup();
	}
}