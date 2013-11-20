package mainCity.restaurants.EllenRestaurant.interfaces;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.interfaces.*;


public interface Cook extends MainCook {
	
	public abstract void msgHereIsOrder(String choice, int table, EllenWaiterRole w);
	public abstract void msgHereIsInventory(String choice, int addAmount, EllenMarketRole m);
	public abstract void msgCantFulfill(String choice, int amountStillNeeded);
	public abstract void pickingUpFood(int table);
	
}