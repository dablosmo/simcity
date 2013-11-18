package mainCity.restaurants.marcusRestaurant;

import agent.Agent;
import mainCity.restaurants.marcusRestaurant.interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class MarcusHostRole extends Agent {
	static final int NTABLES = 4;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	private List<Customer> waitingCustomers = Collections.synchronizedList(new ArrayList<Customer>());
	private List<MyWaiter> waitersList = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public Collection<MarcusTable> tables;
	boolean newCustomer;

	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	
	private String name;
	private int customerCount;
	//private Semaphore atTable = new Semaphore(0,true);

	//public WaiterGui waiterGui = null;

	public MarcusHostRole(String name) {
		super();

		this.name = name;
		customerCount = 0;
		newCustomer = false;
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<MarcusTable>(NTABLES));
		
		synchronized(tables) {
			for (int ix = 1; ix <= NTABLES; ix++) {
				tables.add(new MarcusTable(ix));//how you add to a collections
			}
		}
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	
	public void addWaiter(Waiter w) {
		waitersList.add(new MyWaiter(w));
		stateChanged();
	}
	// Messages	
	public void msgIWantToEat(Customer cust) {
		print(cust + " wants to eat");
		waitingCustomers.add(cust);
		newCustomer = true;
		stateChanged();
	}

	public void msgIWillWait(Customer c) {
		print(c + " decided to wait");
		waitingCustomers.add(c);
		stateChanged();
	}
	
	public void msgTableIsClear(MarcusTable t) {
		print(t + " is now clear");
		t.setUnoccupied();
		customerCount--;
		stateChanged();
	}

	public void msgWantToGoOnBreak(Waiter w) {
		print(w + " just requested to go on break");
		synchronized(waitersList) {
			for(MyWaiter waiter : waitersList) {
				if(waiter.waiter == w) {
					waiter.state = WaiterState.requested;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgBackOnDuty(Waiter w) {
		print(w + " is now back on duty");
		synchronized(waitersList) {
			for(MyWaiter waiter : waitersList) {
				if(waiter.waiter == w) {
					waiter.state = WaiterState.onDuty;
					stateChanged();
					return;
				}
			}
		}
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		if(restaurantFull() && newCustomer && !waitingCustomers.isEmpty()) {//If the restaurant is full, we get the customer in the back of the queue 
			waitingCustomers.get(0).msgWantToWait();
			waitingCustomers.remove(waitingCustomers.get(0));
			newCustomer = false;
			return true;
		}
		
		synchronized(tables) {
			for (MarcusTable table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty() && !waitersList.isEmpty()) {
						seatCustomer(waitingCustomers.get(waitingCustomers.size()-1), table);//the action
						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
				}
			}
		}
		
		synchronized(waitersList) {
			for(MyWaiter w : waitersList) {
				if(w.state == WaiterState.requested) {
					handleRequest(w);
					return true;
				}
			}
		}

		return false;
	}

	// Actions
	private void seatCustomer(Customer customer, MarcusTable table) {
		print("Calling a waiter to take " + customer + " to " + table);		
		customerCount++;
		chooseWaiter().msgSeatAtTable(customer, table);
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
	}
	
	private Waiter chooseWaiter() {
		int holder = (customerCount / waitersList.size());
		
		int counter = 0;
		synchronized(waitersList) {
			for(MyWaiter waiter : waitersList) {
				if(waiter.state == WaiterState.onBreak) {
					++counter;
				}
			}
		}
		
		synchronized(waitersList) {
			for(MyWaiter w : waitersList) {
				if(w.state != WaiterState.onBreak) {
					if(((customerCount < waitersList.size() && w.waiter.getCustomerCount() == 0)) || (waitersList.size() - counter) == 1) {
						return w.waiter;
					}
					else if(w.waiter.getCustomerCount() < holder) {
						return w.waiter;
					}
				}
			}
		}
		
		return waitersList.get(0).waiter; //Backup case, should not be reached though
	}
	
	private void handleRequest(MyWaiter w) {
		print("Handling waiter's break request");
		int counter = 0;
		
		synchronized(waitersList) {
			for(MyWaiter waiter : waitersList) {
				if(waiter.state == WaiterState.onBreak) {
					++counter;
				}
			}
		}

		if(waitersList.size() == 1 || (waitersList.size() - counter) == 1) {//makes sure someone is always on duty
			w.state = WaiterState.onDuty;
			w.waiter.msgBreakReply(false);
			return;
		}
		
		w.state = WaiterState.onBreak;
		w.waiter.msgBreakReply(true);
	}
	
	private boolean restaurantFull() {
		synchronized(tables) {
			for(MarcusTable table : tables) {
				if (!table.isOccupied()) {
					return false;
				}
			}
		}
	
		return true;
	}
	
	public enum WaiterState {onDuty, requested, onBreak};
	
	class MyWaiter {
		Waiter waiter;
		WaiterState state;
		
		MyWaiter(Waiter w) {
			this.waiter = w;
			state = WaiterState.onDuty;
		}
	}
}
