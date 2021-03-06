package mainCity.market.interfaces;

import java.util.Map;
import mainCity.market.MarketMenu;


public interface MarketCashier{
	public abstract void setGreeter(Greeter g);
	public abstract void deductCash(double sub);
	public abstract void setMenu(MarketMenu m);
	
	public abstract void msgComputeBill(Map<String, Integer> inventory, Customer c, Employee e);
	public abstract void msgComputeBill(Map<String, Integer> inventory, String name, Employee e);
	public abstract void msgHereIsPayment(double amount, Customer cust);
	public abstract void msgPleaseRecalculateBill(Customer cust);
	public abstract void msgPleaseRecalculateChange(Customer cust, double amount);
	public abstract void msgChangeVerified(Customer cust);
	public abstract void msgHereIsMoneyIOwe(Customer cust, double amount);
	
	
}