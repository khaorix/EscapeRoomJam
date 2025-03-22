package game;

import java.awt.Point;
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

	private boolean interactPressed = false;

	private Item ironKey;
	private Item goldKey;

	private boolean ironRoomSolved = false;
	private boolean goldKeyFound = false;
	private boolean finalRoomSolved = false;

	private enum GameState {
		PLAYING, ENDING
	};

	private GameState gameState = GameState.PLAYING;

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
		tileTextures.put(TileConstants.IRON_DOOR, TextureLoader.loadTexture("src/main/resources/textures/door.png"));
		tileTextures.put(TileConstants.PLAYER, TextureLoader.loadTexture("src/main/resources/textures/player.png"));
		tileTextures.put(TileConstants.OPEN_IRON_DOOR,
				TextureLoader.loadTexture("src/main/resources/textures/openDoor.png"));
		tileTextures.put(TileConstants.OPEN_IRON_DOOR_2,
				TextureLoader.loadTexture("src/main/resources/textures/openDoor.png"));
		tileTextures.put(TileConstants.LEVER_OFF,
				TextureLoader.loadTexture("src/main/resources/textures/leverOff.png"));
		tileTextures.put(TileConstants.LEVER_ON, TextureLoader.loadTexture("src/main/resources/textures/leverOn.png"));
		tileTextures.put(TileConstants.LEVER_DOOR,
				TextureLoader.loadTexture("src/main/resources/textures/leverDoor.png"));
		tileTextures.put(TileConstants.OPEN_LEVER_DOOR,
				TextureLoader.loadTexture("src/main/resources/textures/openDoor.png"));
		tileTextures.put(TileConstants.OPEN_LEVER_DOOR_2,
				TextureLoader.loadTexture("src/main/resources/textures/openDoor.png"));
		tileTextures.put(TileConstants.GOLD_DOOR,
				TextureLoader.loadTexture("src/main/resources/textures/goldDoor.png"));
		tileTextures.put(TileConstants.OPEN_GOLD_DOOR,
				TextureLoader.loadTexture("src/main/resources/textures/openDoor.png"));

		textRenderer = new TextRenderer("src/main/resources/font/fontgrid white.png", 512, 160);

		currentRoom = new Room(RoomsConstants.TEST_ROOM);
		ironKey = ItemConstants.IRON_KEY;
		goldKey = ItemConstants.GOLD_KEY;
		currentRoom.addItem(5, 1, ironKey);
		inventory = new Inventory();
		hud = new HUD(inventory);
	}

	// INPUT
	private void checkKey(int key, int dx, int dy) {
		if (GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS) {
			if (!pressedKeys.getOrDefault(key, false)) {
				movePlayer(dx, dy);
				showText("");
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

		boolean keyDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS
				|| GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS;

		if (keyDown && !interactPressed) {
			interactPressed = true; // Prevent repeated calls
			interact(); // Call the function once
		}

		if (!keyDown) {
			interactPressed = false; // Reset when key is released
		}

		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
			hideText();
		}
		if (gameState == GameState.ENDING) {
			if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
				GLFW.glfwSetWindowShouldClose(window, true); // Close the game
			}
			return; // Prevent normal game input
		}

	}

	// PLAYER MOVEMENT
	private void movePlayer(int dx, int dy) {
		if (gameState == GameState.ENDING) {
			return;
		}

		int newX = playerX + dx;
		int newY = playerY + dy;

		// Check if the new position is inside the map bounds
		if (newX >= 0 && newX < currentRoom.getWidth() && newY >= 0 && newY < currentRoom.getHeight()) {

			// Check if the tile is walkable (e.g., not a wall)
			char tile = currentRoom.getTile(newX, newY);
			if (tile == TileConstants.FLOOR) {
				playerX = newX;
				playerY = newY;
			}

			// Handle map transitions
			if (tile == TileConstants.OPEN_IRON_DOOR) {
				loadIronRoom();
				return;
			}
			if (tile == TileConstants.OPEN_IRON_DOOR_2) {
				loadTestRoom();
				return;
			}
			if (tile == TileConstants.OPEN_LEVER_DOOR) {
				loadFinalRoom();
				return;
			}
			if (tile == TileConstants.OPEN_LEVER_DOOR_2) {
				loadIronRoom();
				return;
			}
			if (tile == TileConstants.OPEN_GOLD_DOOR) {
				win();
				return;
			}
		}
	}

	private void loadTestRoom() {
		currentRoom = new Room(RoomsConstants.TEST_ROOM_2);
		playerX = 5;
		playerY = 3;
	}

	private void loadIronRoom() {
		if (ironRoomSolved) {
			if (currentRoom.getTile(5, 1) == TileConstants.WARDROBE) {
				currentRoom = new Room(RoomsConstants.IRON_ROOM_2);
				playerX = 1;
				playerY = 3;
			} else {
				currentRoom = new Room(RoomsConstants.IRON_ROOM_2);
				playerX = 3;
				playerY = 5;
			}
		} else {
			currentRoom = new Room(RoomsConstants.IRON_ROOM);
			playerX = 1;
			playerY = 3;
		}
		if (!goldKeyFound) {
			currentRoom.addItem(1, 4, goldKey);
		}
	}

	private void loadFinalRoom() {
		if (!finalRoomSolved) {
			currentRoom = new Room(RoomsConstants.FINAL_ROOM);
			playerX = 3;
			playerY = 1;
		} else {
			currentRoom = new Room(RoomsConstants.FINAL_ROOM_2);
			playerX = 3;
			playerY = 1;
		}
	}

	private void win() {
		gameState = GameState.ENDING;
	}

	// INTERACTION
	private void interact() {
		int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } }; // Up, Down, Left, Right

		for (int[] dir : directions) {
			int checkX = playerX + dir[0];
			int checkY = playerY + dir[1];

			if (checkX >= 0 && checkX < currentRoom.getWidth() && checkY >= 0 && checkY < currentRoom.getHeight()) {
				char tile = currentRoom.getTile(checkX, checkY);
				Item pickedUp = currentRoom.getItemFromCoords(checkX, checkY);
				if (pickedUp != null) {
					pickup(pickedUp, checkX, checkY);
					return;
				}
				switch (tile) {
				case TileConstants.TABLE:
					showText("It's an old wooden table. Looks fragile.");
					return;
				case TileConstants.BED:
					showText("This isn't my bed, yet I woke up in it...");
					return;
				case TileConstants.WARDROBE:
					showText("The wardrobe creaks as I touch it.");
					return;
				case TileConstants.IRON_DOOR:
					ironDoorInteraction(checkX, checkY);
					return;
				case TileConstants.LEVER_OFF:
					LeverInteraction(checkX, checkY);
					return;
				case TileConstants.LEVER_DOOR:
					leverDoorInteraction(checkX, checkY);
					return;
				case TileConstants.GOLD_DOOR:
					goldDoorInteraction(checkX, checkY);
					return;
				default:
					showText("Nothing interesting here.");
				}
			}
		}
	}

	private void ironDoorInteraction(int x, int y) {
		if (hud.getInventory().getItems().contains(ironKey)) {
			showText("I can open this door!");
			openDoor(x, y);
		} else {
			showText("The door's bolted with an iron latch.");
		}
	}

	private void leverDoorInteraction(int x, int y) {
		if (currentRoom.getTile(1, 0) == TileConstants.LEVER_ON
				&& currentRoom.getTile(5, 0) == TileConstants.LEVER_ON) {
			showText("I can open this door!");
			ironRoomSolved = true;
			openDoor(x, y);
		} else {
			showText("The door doesn't budge. No lock, either...");
		}
	}

	private void LeverInteraction(int x, int y) {
		if (currentRoom.getTile(x, y) == TileConstants.LEVER_OFF) {
			showText("I can pull this down...");
			currentRoom.setTile(x, y, TileConstants.LEVER_ON);
		}
	}

	private void goldDoorInteraction(int x, int y) {
		if (hud.getInventory().getItems().contains(goldKey)) {
			showText("I can open this door!");

			openDoor(x, y);
		} else {
			showText("This golden lock is the last obstacle to freedom!");
		}
	}

	private void openDoor(int x, int y) {
		char tile = currentRoom.getTile(x, y);
		if (tile == TileConstants.IRON_DOOR) {
			currentRoom.setTile(x, y, TileConstants.OPEN_IRON_DOOR);
			hud.getInventory().removeItem(ironKey);
		} else {
			if (tile == TileConstants.LEVER_DOOR) {
				currentRoom.setTile(x, y, TileConstants.OPEN_LEVER_DOOR);
			} else {
				if (tile == TileConstants.GOLD_DOOR) {
					currentRoom.setTile(x, y, TileConstants.OPEN_GOLD_DOOR);
					hud.getInventory().removeItem(goldKey);
					finalRoomSolved = true;
				}
			}
		}
	}

	private void pickup(Item pickedUp, int x, int y) {
		if (hud.getInventory().isFull()) {
			showText("I can't pick it up, my pockets are full...");
		} else {
			hud.getInventory().addItem(pickedUp);
			showText(pickedUp.getPickupMessage());
			Point point = new Point(x, y);
			currentRoom.removeItem(point);
			if (pickedUp.getName().equals("Gold Key")) {
				goldKeyFound = true;
			}
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
		if (gameState == GameState.ENDING) {
			textRenderer.drawText("I can see the blue sky and the green meadows.I am free...", windowWidth / 2 - 100,
					windowHeight / 2, windowWidth);
			return;
		}
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
