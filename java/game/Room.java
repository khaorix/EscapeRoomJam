package game;

public class Room {
	private char[][] layout;
	private int width;
	private int height;
	
	public Room(String[] roomLayout) {
		this.height = roomLayout.length;
		this.width = roomLayout[0].length();
		this.layout = new char[height][width];
		
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

    public char getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return layout[y][x];
        }
        return ' '; // Default to empty space if out of bounds
    }
}
