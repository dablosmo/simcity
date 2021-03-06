package role.jeffersonRestaurant;

import agent.Agent;
//import sun.awt.windows.WWindowPeer;









import java.util.*;
import java.util.concurrent.Semaphore;

import role.Role;
import role.jeffersonRestaurant.JeffersonHostRole;
import role.jeffersonRestaurant.JeffersonHostRole.Table;
import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
//import mainCity.restaurants.jeffersonrestaurant.Menu;
import mainCity.restaurants.jeffersonrestaurant.Menu;
import mainCity.restaurants.jeffersonrestaurant.gui.CookGui;
import mainCity.restaurants.jeffersonrestaurant.gui.WaiterGui;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import mainCity.restaurants.jeffersonrestaurant.interfaces.WaiterGuiInterface;

/**
 * Restaurant Waiter Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public abstract class JeffersonWaiterRole extends Role implements Waiter {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	
	public Collection<Table> tables;
	public CookGui cookgui;
	public List<WaiterCust>CustomerList= Collections.synchronizedList(new ArrayList<WaiterCust>());
	
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;
	private Semaphore atTable = new Semaphore(0,false);
	protected Semaphore atHome =  new Semaphore(0,false);
	protected Semaphore atCook =new Semaphore(0, false);
	protected Semaphore atPlating =new Semaphore(0, false);
	
	private PersonAgent p;
	protected JeffersonCookRole cook;
	private JeffersonHostRole host;
	private JeffersonCashierRole cashier;
	public WaiterGuiInterface waiterGui = null;
	public enum waiterCustState
	{notSeated, seated, readyToOrder,waitingForWaiter, ordered,waitingForOrder,foodReady,eating,requestedCheck,waitingForCheck, 
		paid, waiterHasCheck,recievedCheck,waitingPaymentClear, leaving,cleared, cantOrder};

	public boolean wantToBreak;
	public boolean canBreak;
	public boolean onBreak,onDuty;
	
	
	public waiterCustState atStart() {
		return null;
	}
	private Menu menu;
	

	public JeffersonWaiterRole(PersonAgent p, String name) {
		super(p);
		this.p=p;
		this.name = name;
		this.wantToBreak=false;
		this.canBreak=false;
		this.onBreak=false;
		this.onDuty = true;
		//hack to establish connection  to cookgui
		//cookgui=cook.cookGui;
		}
	
	public void setCook(JeffersonCookRole ck){
		this.cook=ck;
	}
	
	public void setHost(JeffersonHostRole h){
		this.host=h;
	}
	
	public void setCashier(JeffersonCashierRole c){
		this.cashier=c;
	}
	

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public Menu getMenu(){
		return menu;
	}

	public Collection getTables() {
		return tables;
	}
	// Messages

	public void msgButtonAskBreak(){
		
		wantToBreak=true;
		stateChanged();
		Do("want to break");
	}
	
	public void msgYouCanBreak(){
		canBreak=true;
		stateChanged();
		
	}
	
	public void msgSeatAtTable(Customer c, int table) {
		CustomerList.add(new WaiterCust(c, table));
		log("waiter added new customer");
		stateChanged();
	}
	public void msgHereIsMyChoice(Customer cust, String choice){
		synchronized(CustomerList){
			for(WaiterCust w :CustomerList){
				if(w.c==cust){
					w.choice=choice;
					w.state=waiterCustState.ordered;
				}
			}
		}
			//print("waiter recieved choice");
		stateChanged();	
	}

	public void msgLeavingTable(Customer cust) {
		synchronized(CustomerList){
			for(WaiterCust wc:CustomerList){
				if(wc.c==cust){
					wc.state = waiterCustState.leaving;
					//print("acknowledge leaving");
					stateChanged();
				}
			}
		}	
	}

	public void msgGoOffDuty(double amount) {
		addToCash(amount);
		//System.out.println("go off duty called");
		onDuty = false;
		stateChanged();
	}
	
	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();// = true;
		
		stateChanged();
	}
	
	public void msgAtHome(){
		atHome.release();
		stateChanged();
	}
	
	public void msgAtPlating(){
		atPlating.release();
		stateChanged();
	}
	public void msgAtCook(){
		atCook.release();
		stateChanged();
	}



	public void msgFinishedLeavingRestaurant() {
		setInactive();
		onDuty= true;
		
	}
	
	public void msgImReadyToOrder(Customer cust){
		synchronized(CustomerList){
			for(WaiterCust w:CustomerList){
				if(w.c==cust){
					w.state=waiterCustState.readyToOrder;
					stateChanged();
				}
			}
		}
	}
	
	public void msgOrderIsReady(int t){
		synchronized(CustomerList){
			for(WaiterCust w:CustomerList){
				if(w.table==t){
					w.state=waiterCustState.foodReady;
					stateChanged();
				}
			}
		}	
	}
	
	public void msgOutOfChoice(int t){
		synchronized(CustomerList){
			for(WaiterCust w:CustomerList){
				if(w.table==t){
					w.state=waiterCustState.cantOrder;
					stateChanged();
				}
			}
		}
	}
	public void msgWantCheck(Customer c){
		synchronized(CustomerList){
			for(WaiterCust w:CustomerList){
				if(w.c==c){
					w.state=waiterCustState.requestedCheck;
					stateChanged();
				}
			}
		}	
	}
	
	public void msgCheckPrinted(int t){
		synchronized(CustomerList){
			for(WaiterCust w:CustomerList){
				if(w.table==t){
					w.state=waiterCustState.waiterHasCheck;
					stateChanged();
				}
			}
		}
	}
	
	public void msgHereIsPayment(Customer c, double money){
		synchronized(CustomerList){
			for(WaiterCust w:CustomerList){
				if(w.c==c){
					w.state=waiterCustState.paid;
					w.moneyPaid=money;
					stateChanged();
				}
			}
		}
	}

	public void msgPaymentComplete(int t){
		synchronized(CustomerList){
			for(WaiterCust w:CustomerList){
				if(w.table==t){
					w.state=waiterCustState.cleared;
					stateChanged();
				}
			}
		}
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		if(CustomerList.isEmpty()&& canBreak){
			log("going on break");
			canBreak=false;
			onBreak=true;
			return true;
		}

		
		if(wantToBreak==true){
			
			askHostForBreak();
			this.wantToBreak=false;
			return true;
		}
		if(CustomerList.isEmpty() && !onDuty){
			leaveRestaurant();
		}
		
		synchronized(CustomerList){
			for(WaiterCust waitercust:CustomerList){
				if(waitercust.state==waiterCustState.notSeated && waiterGui.atStart()){
					waitercust.state=waiterCustState.seated;
					seatCustomer(waitercust.c, waitercust.table);
					
					return true;
				}


				if(waitercust.state == waiterCustState.readyToOrder){
					waitercust.state = waiterCustState.waitingForWaiter;
					log("going to take order");
					goToTakeOrder(waitercust);
					return true;
				}


				if(waitercust.state==waiterCustState.ordered){
					waitercust.state=waiterCustState.waitingForOrder;
					tellCook(waitercust.table, waitercust.choice);
					
					return true;
				}	

		

				if(waitercust.state==waiterCustState.foodReady){
					waitercust.state=waiterCustState.eating;
					deliverOrder(waitercust);
					return true;
				}	

		

				if(waitercust.state==waiterCustState.leaving){
					clearCustomer(waitercust);
					
					return true;	
				}


				if(waitercust.state==waiterCustState.cleared){
					clearCustomer(waitercust);
					return true;
				}

		

				if(waitercust.state==waiterCustState.cantOrder){
					//System.out.println("Customer can't order");
					tellCustomerOutOfStock(waitercust);
					waitercust.state=waiterCustState.seated;
					return true;
				}

	
				if(waitercust.state==waiterCustState.requestedCheck){
					waitercust.state=waiterCustState.waitingForCheck;
					tellCashier(waitercust);
					return true;
				}


				if(waitercust.state==waiterCustState.waiterHasCheck){
					waitercust.state=waiterCustState.recievedCheck;
					giveCheckToCust(waitercust);
					return true;
				}
		
				if(waitercust.state==waiterCustState.paid){
					waitercust.state=waiterCustState.waitingPaymentClear;
					deliverPayment(waitercust);
					return true;
				}
			}
				
		}
		
		//System.out.println("waiter no action");
		return false;
	}

	// Actions

	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.JEFFERSON_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.JEFFERSON_WAITER, this.getName(), s);
	}
	private void tellCustomerOutOfStock(WaiterCust cust){
		cust.c.msgNotAvailable();
		
	}
	
	private void askHostForBreak(){
		log("Asking host for break");
		host.msgWantToGoOnBreak(this);
	}

	private void seatCustomer(Customer customer, int table) {
		
		log("msged cust to sit");
		Menu m = new Menu();
		customer.msgSitAtTable(table, m,this);
		
		
		//print("finished seating");
		DoSeatCustomer(customer, table);
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(Customer customer, int table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		//print("Seating " + customer + " at " + table);
		
		waiterGui.DoBringToTable(customer,table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		//print ("TEST");
		


		waiterGui.DoLeaveCustomer();
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	
	private void goToTakeOrder(WaiterCust cust){
		//call Gui action to bring waiter to table then call
		
		waiterGui.DoBringToTable(cust.c,cust.table);
				try {
			atTable.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
				cust.c.msgWhatWouldYouLike();
				log("asked cust what he liked");

		//print ("TEST");
	
	}
	
	protected abstract void tellCook(int table, String choice);
	
	/*
	private void tellCook(int table, String choice){
		print("sending order to cook");
		
		
		waiterGui.DoGoToCook();
		
		try {
			atCook.acquire();
		} 
		catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		cook.msghereIsAnOrder(table, choice,this);
		
		waiterGui.DoLeaveCustomer();
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	*/
	private void deliverOrder(WaiterCust cust){
		log("delivering order to cust");
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		waiterGui.DoGetFood();
		try {
			atPlating.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		cook.msgOrderTaken(cust.table);
		log("Delivering food to table" + cust.table);
		waiterGui.DoDeliverOrder(cust.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		cust.c.msgHereIsYourFood();
		waiterGui.DoLeaveCustomer();
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private void tellCashier(WaiterCust w){
		log("waiter asking cashier for check");
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		waiterGui.DoDeliverOrder(w.table);
		try {
			atTable.acquire();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.DoLeaveCustomer();
		try {
			atHome.acquire();
		} 
		catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		cashier.msgCustWantsCheck(w.table, w.choice, this);
	
		
		
	}
		
	
	
	
	private void giveCheckToCust(WaiterCust w){
		log("giving check to customer");
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		waiterGui.DoDeliverOrder(w.table);
		try {
			atTable.acquire();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		w.c.msgHereIsYourCheck();
		
		waiterGui.DoLeaveCustomer();
		try {
			atHome.acquire();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void leaveRestaurant(){
		waiterGui.DoLeaveRestaurant();
		host.msgFinishingShift(this);
	}
	private void deliverPayment(WaiterCust w){
		log("giving money to cashier");
		cashier.msgHereisPayment(w.table, this, w.moneyPaid);
		
	}
	
	private void clearCustomer(WaiterCust w){
		log("cleaning up customer");
		host.msgTableIsFree(w.table);
		CustomerList.remove(w);
	}

	//utilities

	public void setGui(WaiterGuiInterface gui) {
		waiterGui = gui;
	}

	public WaiterGuiInterface getGui() {
		return waiterGui;
	}

	public class Table {
		Customer occupiedBy;
		int tableNumber;
		int locx;
		int locy;
		int number;
	
		public int getTableNumber() {
			return tableNumber;
		}
		public int getLocx() {
			return locx;
		}
		public int getLocy() {
			return locy;
		}
		
		public void setLocx(int locx) {
			this.locx = locx;
		}
		public void setLocy(int locy) {
			this.locy = locy;
		}

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Customer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Customer getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
	

	
	public class WaiterCust{
		public waiterCustState state;
		public Customer c;
		public int table;
		public String choice;
		double moneyPaid;
		
		
		public WaiterCust(Customer c, int table){
			this.c=c;
			this.table=table;
			state = waiterCustState.notSeated;
			
			
		}

	}


	

	
		
}





