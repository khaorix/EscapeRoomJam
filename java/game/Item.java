package game;

import org.lwjgl.opengl.GL11;

public class Item {
	private String name;
	private String pickupMessage;
	private String description;
	private String useMessage;
	private Texture texture;
	private static int TILESIZE = 32;
	
	public Item(String name, String texturePath, String pickupMessage, String description, String useMessage) {
		this.name = name;
		this.pickupMessage = pickupMessage;
		this.description = description;
		this.useMessage = useMessage;
		this.texture = new Texture(texturePath);
	}
	
    public String getName() {
        return name;
    }

    public Texture getTexture() {
        return texture;
    }

    public void renderItem(float screenX, float screenY) {
    	 GL11.glEnable(GL11.GL_TEXTURE_2D); // Ensure textures are enabled
    	    texture.bind(); // Bind the item's texture

    	    GL11.glColor3f(1f, 1f, 1f); // Reset color to full brightness (important!)

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

    	    GL11.glDisable(GL11.GL_TEXTURE_2D); // Disable textures after rendering (optional)
    	}

	public String getPickupMessage() {
		return pickupMessage;
	}

	public void setPickupMessage(String pickupMessage) {
		this.pickupMessage = pickupMessage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public String getUseMessage() {
		return useMessage;
	}

	public void setUseMessage(String useMessage) {
		this.useMessage = useMessage;
	}
}
