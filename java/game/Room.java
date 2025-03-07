package game;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class Room {
	private char[][] layout;
	private int width;
	private int height;
	private Map<Point,Item> items;
	
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
    	Point point = new Point(x, y);
    	items.put(point, item);
    }
    
    public Item getItemFromCoords(int x, int y) {
    	System.out.println("Checking for items in " +x + "/" + y);
    	Point point = new Point(x, y);
    	if (items.containsKey(point)) {
    		System.out.println("Item found!");
    		return items.get(point);
    	} else {
    		return null;
    	}
    	
    }
    
    public void removeItem(Point point) {
    	items.remove(point);
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
