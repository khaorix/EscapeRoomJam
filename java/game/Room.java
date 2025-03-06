package game;

import java.util.HashMap;
import java.util.Map;

public class Room {
	private char[][] layout;
	private int width;
	private int height;
	private Map<int[],Item> items;
	
	public Room(String[] roomLayout) {
		this.height = roomLayout.length;
		this.width = roomLayout[0].length();
		this.layout = new char[height][width];
		this.items = new HashMap<>();
		
		for (int y = 0; y < height; y++) {
			layout[y] = roomLayout[y].toCharArray();
		}
	}
	
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public void addItem(int x, int y, Item item) {
    	int[] coords = new int[2];
    	coords[0] = x;
    	coords[1] = y;
    	items.put(coords, item);
    }
    
    public Item getItemFromCoords(int x, int y) {
    	int[] coords = new int[2];
    	coords[0] = x;
    	coords[1] = y;
    	if (items.containsKey(coords)) {
    		return items.get(coords);
    	} else {
    		return null;
    	}
    	
    }
    
    public void removeItem(Item item) {
    	items.remove(item);
    }
    
    public void setTile(int x, int y, char newTile) {
        layout[x][y] = newTile;
    }

    public char getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return layout[y][x];
        }
        return ' '; // Default to empty space if out of bounds
    }
}
