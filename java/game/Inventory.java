package game;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
	private static final int ROWS = 2;
	private static final int COLUMNS = 4;
	private static final int MAX_SLOTS = COLUMNS * ROWS;
	private List<Item> items;
	
	public Inventory() {
		items = new ArrayList<>();
	}
	
	public boolean addItem(Item item) {
		if (items.size() < MAX_SLOTS) {
			items.add(item);
			return true;
		}
		return false;
	}
	
	public void removeItem(Item item) {
		items.remove(item);
	}
	
	public List<Item> getItems(){
		return items;
	}
	
	public boolean isFull() {
		return items.size() >= MAX_SLOTS;
	}
}
