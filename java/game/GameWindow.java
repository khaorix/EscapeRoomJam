package game;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import constants.*;

public class GameWindow {
	private long window;
	private int windowWidth = 800;
	private int windowHeight = 600;

	int playerX = 2;
	int playerY = 1;
	// x = width
	// y = height
	private String title = "Escape Room";

	private static int TILESIZE = 64;
	private static int textDisplayStart = 25;
	private static int textDisplayHeight = 500;
	private Map<Character, Integer> tileTextures;

	private Map<Integer, Boolean> pressedKeys = new HashMap<>();

	private Room currentRoom;
	private Inventory inventory;
	
	private HUD hud;
	
	private TextRenderer textRenderer;
	private String displayedText = "";
	private boolean textVisible = false;

	// TECHNICAL
	public void run() {
		init();
		loop();
		cleanup();
	}

	private void init() {

		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW");
		}

		window = GLFW.glfwCreateWindow(windowWidth, windowHeight, title, 0, 0);
		if (window == 0) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		GLFW.glfwSwapInterval(1);
		GLFW.glfwShowWindow(window);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, windowWidth, windowHeight, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		tileTextures = new HashMap<>();
		tileTextures.put(TileConstants.WALL, TextureLoader.loadTexture("src/main/resources/textures/wall.png"));
		tileTextures.put(TileConstants.FLOOR, TextureLoader.loadTexture("src/main/resources/textures/floor.png"));
		tileTextures.put(TileConstants.TABLE, TextureLoader.loadTexture("src/main/resources/textures/table.png"));
		tileTextures.put(TileConstants.BED, TextureLoader.loadTexture("src/main/resources/textures/bed.png"));
		tileTextures.put(TileConstants.CHAIR, TextureLoader.loadTexture("src/main/resources/textures/chair.png"));
		tileTextures.put(TileConstants.WARDROBE, TextureLoader.loadTexture("src/main/resources/textures/wardrobe.png"));
		tileTextures.put(TileConstants.DOOR, TextureLoader.loadTexture("src/main/resources/textures/door.png"));
		tileTextures.put(TileConstants.PLAYER, TextureLoader.loadTexture("src/main/resources/textures/player.png"));

		textRenderer = new TextRenderer("src/main/resources/font/fontgrid white.png", 512, 160);
		
		currentRoom = new Room(RoomsConstants.TEST_ROOM);
		inventory = new Inventory();
		hud = new HUD(inventory);
	}

	// INPUT
	private void checkKey(int key, int dx, int dy) {
		if (GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS) {
			if (!pressedKeys.getOrDefault(key, false)) {
				movePlayer(dx, dy);
				pressedKeys.put(key, true);
			}
		} else {
			pressedKeys.put(key, false); // Reset when key is released
		}
	}

	private void handleInput() {
		checkKey(GLFW.GLFW_KEY_W, 0, -1);
		checkKey(GLFW.GLFW_KEY_UP, 0, -1);
		checkKey(GLFW.GLFW_KEY_Z, 0, -1);

		checkKey(GLFW.GLFW_KEY_S, 0, 1);
		checkKey(GLFW.GLFW_KEY_DOWN, 0, 1);

		checkKey(GLFW.GLFW_KEY_A, -1, 0);
		checkKey(GLFW.GLFW_KEY_LEFT, -1, 0);
		checkKey(GLFW.GLFW_KEY_Q, -1, 0);

		checkKey(GLFW.GLFW_KEY_D, 1, 0);
		checkKey(GLFW.GLFW_KEY_RIGHT, 1, 0);

		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS
				|| GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS) {
			interact();
		}

		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
			hideText();
		}
	}

	// PLAYER MOVEMENT
	private void movePlayer(int dx, int dy) {
		int newX = playerX + dx;
		int newY = playerY + dy;

		// Check if the new position is inside the map bounds
		if (newX >= 0 && newX < currentRoom.getWidth() && newY >= 0 && newY < currentRoom.getHeight()) {

			// Check if the tile is walkable (e.g., not a wall)
			char tile = currentRoom.getTile(newX, newY);
			if (tile == TileConstants.FLOOR || tile == TileConstants.DOOR) {
				playerX = newX;
				playerY = newY;
			}
		}
	}

	//INTERACTION
	private void interact() {
	    int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Up, Down, Left, Right

	    for (int[] dir : directions) {
	        int checkX = playerX + dir[0];
	        int checkY = playerY + dir[1];

	        if (checkX >= 0 && checkX < currentRoom.getWidth() && checkY >= 0 && checkY < currentRoom.getHeight()) {
	            char tile = currentRoom.getTile(checkX, checkY);
	            
	            Item pickedUp = currentRoom.getItemFromCoords(checkX, checkY);
	            if (pickedUp!= null) {
	            	pickup(pickedUp);
	            	return;
	            }
	            if (tile == TileConstants.TABLE) {
	                showText("It's an old wooden table. Looks fragile.");
	                return;
	            } else if (tile == TileConstants.BED) {
	                showText("This isn't my bed, yet I woke up in it...");
	                return;
	            } else if (tile == TileConstants.WARDROBE) {
	                showText("The wardrobe creaks as I touch it.");
	                return;
	            }
	        }
	    }
	    showText("Nothing interesting here.");
	}
	
	private void pickup(Item pickedUp) {
		if (hud.getInventory().isFull()){
			showText("I can't pick it up, my pockets are full...");
		} else {
			hud.getInventory().addItem(pickedUp);
			showText(pickedUp.getPickupMessage());
		}
		
	}

	// TEXT
	private void showText(String message) {
		displayedText = message;
		textVisible = true;
	}

	private void hideText() {
		textVisible = false;
	}

	// RENDERING
	private void loop() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Clear the screen
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			handleInput();
			renderRoom();

			// Poll events and swap buffers
			GLFW.glfwPollEvents();
			GLFW.glfwSwapBuffers(window);
		}
	}

	private void renderRoom() {
		// x = width
		// y = height
		char tile;

		for (int y = 0; y < currentRoom.getHeight(); y++) {
			for (int x = 0; x < currentRoom.getWidth(); x++) {
				if (x == playerX && y == playerY) {
					tile = TileConstants.PLAYER;
				} else {
					tile = currentRoom.getTile(x, y);
				}
				renderTile(tile, x, y);
			}
		}

		hud.render(windowWidth, windowHeight);
		
		if (textVisible) {
			textRenderer.drawText(displayedText, textDisplayStart, textDisplayHeight, windowWidth);
		}
	}


	private void renderTile(char tile, int x, int y) {
		// x = width
		// y = height
		int screenX = x * TILESIZE;
		int screenY = y * TILESIZE;

		Integer textureID = tileTextures.get(tile);

		if (textureID != null) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		}

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

	// EXIT
	private void cleanup() {
		textRenderer.cleanup();
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}

	// MAIN
	public static void main(String[] args) {
		new GameWindow().run();
	}
}
