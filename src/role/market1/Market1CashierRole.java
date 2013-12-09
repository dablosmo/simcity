package role.market1;

import java.util.*;

import role.Role;
import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.WorkerRole;
import mainCity.market1.Market1Menu;
import mainCity.market1.Market1Menu.Item;
import mainCity.market1.interfaces.*;



public class Market1CashierRole extends Role implements MarketCashier, WorkerRole {	
	private String name;
	Greeter greeter;
	private double cash = 0;
	Timer timer = new Timer();
	private Market1Menu marketMenu = new Market1Menu();
	
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());	//from waiters
	public enum BillState {computing, waitingForPayment, recomputingBill, calculatingChange, oweMoney, paid};
	private List<Employee> employees = Collections.synchronizedList(new ArrayList<Employee>());
	
	private boolean onDuty;
	
	//constructor
	public Market1CashierRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		onDuty = true;
	}
	public void addWaiter(Employee w){	//hack
		employees.add(w);
	}
	public void setGreeter(Greeter g){
		greeter = g;
	}
	public String getName() {
		return name;
	}
	public List<Bill> getBills(){
		return bills;
	}
	public double getCash(){
		return cash;
	}
	public void deductCash(double sub){
		cash -= sub;
		cash = Math.round(cash*100.0)/100.0;
	}
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.MARKET_CASHIER, this.getName(), s);
	}
	
	
	
	// Messages
	
	//business
	public void msgComputeBill(Map<String, Integer> inventory, String name, Employee e){
		log("Received msgComputeBill for " + name);		
		bills.add(new Bill(inventory, name, BillState.computing, e));
		stateChanged();
	}

	//customer
	public void msgComputeBill(Map<String, Integer> inventory, Customer c, Employee e){
		bills.add(new Bill(inventory, c, BillState.computing, e));
		log(e.getName() + ", received msgComputeBill for " + c.getName());
		stateChanged();
	}
	public void msgHereIsPayment(double amount, Customer cust){
		log("Received msgHereIsPayment: got $" + amount);
		Bill b = null;
		synchronized(bills){
			for (Bill thisB : bills){	
				if (thisB.c.equals(cust)){
					b = thisB;
					break;
				}
			}
		}
		b.amountPaid = amount;
		b.s = BillState.calculatingChange;
		stateChanged();
	}
	public void msgPleaseRecalculateBill(Customer cust){
		log("Received msgPleaseRecalculateBill from " + cust.getName());
		Bill b = null;
		synchronized(bills){
			for (Bill thisB : bills){	
				if (thisB.c.equals(cust)){
					b = thisB;
					break;
				}
			}
		}
		b.s = BillState.recomputingBill;
		stateChanged();
	}
	public void msgChangeVerified(Customer cust){
		log("Received msgChangeVerified from " + cust.getName());
		Bill b = null;
		synchronized(bills){
			for (Bill thisB : bills){	
				if (thisB.c.equals(cust)){
					b = thisB;
					break;
				}
			}
		}
		//NOW we can add the money they finally, for sure paid and are not taking back
		cash += b.amountMarketGets;
		cash = Math.round(cash*100.0)/100.0;
		bills.remove(b);
	}
	public void msgHereIsMoneyIOwe(Customer cust, double amount){
		log("Received msgHereIsMoneyIOwe from " + cust.getName() + ": $" + amount);
		cash += amount;
	}
	
	public void msgGoOffDuty(double amount){
		addToCash(amount);
		onDuty = false;
		stateChanged();
	}

	
	
	 // Scheduler.  Determine what action is called for, and do it.
	 
	public boolean pickAndExecuteAnAction() {
		
		//Customer checks
		synchronized(bills){
			for (Bill b: bills) {
				if (b.s == BillState.calculatingChange){
					CalculateChange(b);
					return true;
				}
			}
		}
		synchronized(bills){
			for (Bill b: bills) {
				if (b.s == BillState.computing){
					ComputeBill(b);
					return true;
				}
			}
		}
		synchronized(bills){
			for (Bill b: bills) {
				if (b.s == BillState.recomputingBill){
					RecomputeBill(b);
					return true;
				}
			}
		}
		
		if(!bills.isEmpty() && !onDuty){
			setInactive();
			onDuty = true;
		}


		return false;
	}
	

	
	
	// Actions
	
	public void ComputeBill(final Bill b){
		log("Computing bill");
		double dollars = 0;
		for (Map.Entry<String, Integer> entry : b.itemsBought.entrySet()){
			//dollars += marketMenu.getPrice(entry.getKey()) * entry.getValue();
			for (Item i : marketMenu.menuItems){
				if (i.getItem().equalsIgnoreCase(entry.getKey()))
					dollars += i.getPrice() * entry.getValue();
			}
		}
		b.amountCharged = Math.round(dollars * 100.0)/100.0;
		
		if (b.c == null){
			b.e.msgHereIsBill(b.restaurantName, b.amountCharged);
			bills.remove(b);		//employee hands it off to deliveryMan, it's handled there
		}
		else {
			b.e.msgHereIsBill(b.c, b.amountCharged);
			b.s = BillState.waitingForPayment;
		}

	}
	
	public void CalculateChange(Bill b){
		log("Calculating change");
		double dollars = 0;
		//check to make sure payment is large enough
		if (b.amountPaid >= b.amountCharged){
			dollars = Math.round((b.amountPaid - b.amountCharged)*100.0)/100.0;
			b.c.msgHereIsYourChange(dollars, b.amountCharged);
			
			b.amountMarketGets = Math.round(b.amountCharged *100.0)/100.0;
			b.s = BillState.paid;
		}
		else {		//if they didn't pay enough
			b.amountOwed = Math.round((b.amountCharged - b.amountPaid)*100.0)/100.0;		//mostly for testing purposes
			b.amountMarketGets = b.amountPaid;
			
			b.c.msgNotEnoughCash(b.amountOwed);
			b.s = BillState.oweMoney;
		}
	}

	public void RecomputeBill(final Bill b){
		log("Recomputing bill");
		double dollars = 0;
		for (Map.Entry<String, Integer> entry : b.itemsBought.entrySet()){
			//dollars += marketMenu.getPrice(entry.getKey()) * entry.getValue();
			for (Item i : marketMenu.menuItems){
				if (i.getItem().equalsIgnoreCase(entry.getKey()))
					dollars += i.getPrice() * entry.getValue();
			}
		}
		b.newAmountCharged = Math.round(dollars * 100.0)/100.0;
		
		if (b.newAmountCharged < b.amountCharged){	//if they over-billed the first time, fix the bill to be correct
			b.amountCharged = b.newAmountCharged;
			
			/*if (b.c == null)
				b.e.msgHereIsBill(b.restaurantName, b.amountCharged);
			else*/ if (b.c != null)
				b.c.msgHereIsBill(b.amountCharged);
			
		} else if (b.newAmountCharged > b.amountCharged) {//else if they under-billed the first time, send them the first bill charge (they messed up, so they'll lose that money)
			/*if (b.c == null)
				b.e.msgHereIsBill(b.restaurantName, b.amountCharged);
			else*/
				b.c.msgHereIsBill(b.amountCharged);
			
		} else if (b.newAmountCharged == b.amountCharged){		//if it's the same, send them a final non-negotiable bill charge
			/*if (b.c == null)
				b.e.msgHereIsFinalBill(b.restaurantName, b.amountCharged);
			else*/ if (b.c != null)
				b.c.msgHereIsFinalBill(b.amountCharged);
		}
		b.s = BillState.waitingForPayment;

	}

	public class Bill {		//for testing purposes only
		Map<String, Integer> itemsBought = new TreeMap<String, Integer>();
		double amountCharged;
		double newAmountCharged; 	//if customer asks cashier to recalculate
		double amountPaid;
		double amountMarketGets;
		double amountOwed;
		Customer c;
		String restaurantName;
		BillState s;
		Employee e;
		
		Bill(Map<String, Integer> inventory, Customer cust, BillState state, Employee em){
			itemsBought = inventory;
			c = cust;
			s = state;
			e = em;
		}
		Bill(Map<String, Integer> inventory, String name, BillState state, Employee em){
			itemsBought = inventory;
			restaurantName = name;
			s = state;
			e = em;
		}
		
		//for testing purposes
		public BillState getState(){
			return s;
		}
		public Employee getEmployee(){
			return e;
		}
		public Customer getCustomer(){
			return c;
		}
		public double getAmountPaid(){
			return amountPaid;
		}
		public String getRestaurant(){
			return restaurantName;
		}
		public double getAmountCharged(){
			return amountCharged;
		}
	}

	
}

