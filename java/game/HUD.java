package game;

import org.lwjgl.opengl.GL11;

public class HUD {
	private Inventory inventory;
	private TextRenderer textRenderer;
	private static final int TILESIZE = 32;

	public HUD(Inventory inventory) {
		this.inventory = inventory;
		this.textRenderer = new TextRenderer("src/main/resources/font/fontgrid white.png", 512, 160);
	}

	public void render(float screenWidth, float screenHeight) {
		float invX = screenWidth - 100; // Inventory on the right
		float invY = 50; // Offset from top

		// Draw "Inventory" label
		textRenderer.drawText("Items", invX - 50, invY - 40, 100);

		int slotSize = 36; // Adjust for padding
		int index = 0;

		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 2; col++) {
				float x = invX + col * slotSize;
				float y = invY + row * slotSize;

				// Draw the slot background
				GL11.glColor3f(0.2f, 0.2f, 0.2f); // Dark background color
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(x, y);
				GL11.glVertex2f(x + TILESIZE, y);
				GL11.glVertex2f(x + TILESIZE, y + TILESIZE);
				GL11.glVertex2f(x, y + TILESIZE);
				GL11.glEnd();

				// Render the item inside if there is one
				if (index < inventory.getItems().size()) {
					 GL11.glEnable(GL11.GL_BLEND);
					    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					    
					    GL11.glEnable(GL11.GL_TEXTURE_2D); // Ensure textures are enabled
					    GL11.glColor3f(1f, 1f, 1f); // Reset color before drawing each item
					    
	                Item item = inventory.getItems().get(index);
	                if (item != null) {
	                	item.renderItem(x, y);	                	
	                }
	                GL11.glDisable(GL11.GL_TEXTURE_2D); // Disable textures after rendering
					GL11.glDisable(GL11.GL_BLEND); // Disable after rendering
				}
				index++;
			}
		}
		GL11.glColor3f(1f, 1f, 1f); // Reset color to normal
	}
	
	public Inventory getInventory() {
		return inventory;
	}
}