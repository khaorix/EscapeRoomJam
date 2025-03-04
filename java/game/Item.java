package game;

import org.lwjgl.opengl.GL11;

public class Item {
	private String name;
	private Texture texture;
	private static int TILESIZE = 32;
	
	public Item(String name, String texturePath) {
		this.name = name;
		this.texture = new Texture(texturePath);
	}
	
    public String getName() {
        return name;
    }

    public Texture getTexture() {
        return texture;
    }

    public void renderItem(float screenX, float screenY) {
        texture.bind();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(screenX, screenY);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(screenX + TILESIZE, screenY);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(screenX + TILESIZE, screenY + TILESIZE);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(screenX, screenY + TILESIZE);
        GL11.glEnd();
    }
}
