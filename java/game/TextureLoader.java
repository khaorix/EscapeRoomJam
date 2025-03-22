package game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class TextureLoader {
	private static Map<String, Integer> textures = new HashMap<>();
	
	public static int loadTexture(String resourcePath) {
	    try (InputStream is = TextureLoader.class.getResourceAsStream(resourcePath)) {
	        if (is == null) {
	            throw new IOException("Texture file not found: " + resourcePath);
	        }

	        // Load image from stream instead of file
	        BufferedImage image = ImageIO.read(is);
	        int width = image.getWidth();
	        int height = image.getHeight();

	        int[] pixels = new int[width * height];
	        image.getRGB(0, 0, width, height, pixels, 0, width);

	        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
	        for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                int pixel = pixels[y * width + x];
	                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
	                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
	                buffer.put((byte) (pixel & 0xFF));         // Blue
	                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
	            }
	        }
	        buffer.flip();

	        // Generate OpenGL texture
	        int textureID = GL11.glGenTextures();
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

	        return textureID;
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to load texture: " + resourcePath, e);
	    }
	}
	
}
