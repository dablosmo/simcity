package mainCity.test;

import mainCity.PersonAgent;
import mainCity.PersonAgent.*;
import junit.framework.*;
import role.davidRestaurant.*;
import role.jeffersonRestaurant.*;
import role.marcusRestaurant.*;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.enaRestaurant.*;

//TODO test with every type of agent, test priority queue, possible gui?, customers

public class PersonTest extends TestCase {
	PersonAgent person;
	MockPersonGui gui;
	
	public void setUp() throws Exception{
		super.setUp();		
		person = new PersonAgent("PersonTest");
		gui = new MockPersonGui(person.getName());
		person.setGui(gui);
	}

	public void testOneMarcusNormalWaiterRoleWork() {
		person.updateOccupation("marcusWaiter", -1, -1);
		MarcusWaiterRole waiter = null;
		assertNull("MarcusNormalWaiterRole should be null. It isn't", waiter);
		
		waiter = new MarcusNormalWaiterRole(person, person.getName());
		assertEquals("MarcusNormalWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusNormalWaiterRole should be active. It isn't", waiter.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		waiter.setInactive();
		
		assertFalse("Person's MarcusNormalWaiterRole should be inactive. It isn't", waiter.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusNormalWaiter
	
	public void testTwoMarcusSharedWaiterRoleWork() {
		person.updateOccupation("marcusSharedWaiter", -1, -1);
		MarcusWaiterRole waiter = null;
		assertNull("MarcusSharedWaiterRole should be null. It isn't", waiter);
		
		waiter = new MarcusSharedWaiterRole(person, person.getName());
		assertEquals("MarcusSharedWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusSharedWaiterRole should be active. It isn't", waiter.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		waiter.setInactive();
		
		assertFalse("Person's MarcusSharedWaiterRole should be inactive. It isn't", waiter.isActive());
		assertEquals("Person's state should be Shared. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusSharedWaiter
	
	public void testThreeMarcusCashierRoleWork() {
		person.updateOccupation("marcusCashier", -1, -1);
		MarcusCashierRole cashier = null;
		assertNull("MarcusCashierRole should be null. It isn't", cashier);
		
		cashier = new MarcusCashierRole(person, person.getName());
		assertEquals("MarcusCashierRole should have current person as holder. It's not", cashier.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cashier);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusCashierRole should be active. It isn't", cashier.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cashier.setInactive();
		
		assertFalse("Person's MarcusCashierRole should be inactive. It isn't", cashier.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusCashier
	
	public void testFourMarcusCookRoleWork() {
		person.updateOccupation("marcusCook", -1, -1);
		MarcusCookRole cook = null;
		assertNull("MarcusCookRole should be null. It isn't", cook);
		
		cook = new MarcusCookRole(person, person.getName());
		assertEquals("MarcusCookRole should have current person as holder. It's not", cook.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cook);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusCookRole should be active. It isn't", cook.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cook.setInactive();
		
		assertFalse("Person's MarcusCashierRole should be inactive. It isn't", cook.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusCook
	
	public void testFiveMarcusHostWork() {
		person.updateOccupation("marcusHost", -1, -1);
		MarcusHostRole host = null;
		assertNull("MarcusHostRole should be null. It isn't", host);
		
		host = new MarcusHostRole(person, person.getName());
		assertEquals("MarcusHostRole should have current person as holder. It's not", host.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, host);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusHostRole should be active. It isn't", host.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		host.setInactive();
		
		assertFalse("Person's MarcusCashierRole should be inactive. It isn't", host.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusHost
	
	public void testSixDavidWaiterRoleWork() {
		person.updateOccupation("davidWaiter", -1, -1);
		DavidWaiterRole waiter = null;
		assertNull("DavidNormalWaiterRole should be null. It isn't", waiter);
		
		waiter = new DavidWaiterRole(person.getName(), person);
		assertEquals("DavidNormalWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's DavidNormalWaiterRole should be active. It isn't", waiter.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		waiter.setInactive();
		
		assertFalse("Person's DavidNormalWaiterRole should be inactive. It isn't", waiter.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End davidWaiter
	
	public void testSevenDavidCashierRoleWork() {
		person.updateOccupation("davidCashier", -1, -1);
		DavidCashierRole cashier = null;
		assertNull("DavidCashierRole should be null. It isn't", cashier);
		
		cashier = new DavidCashierRole(person.getName(), person);
		assertEquals("DavidCashierRole should have current person as holder. It's not", cashier.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cashier);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's DavidCashierRole should be active. It isn't", cashier.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cashier.setInactive();
		
		assertFalse("Person's DavidCashierRole should be inactive. It isn't", cashier.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End DavidCashier
	
	public void testEightDavidCookRoleWork() {
		person.updateOccupation("davidCook", -1, -1);
		DavidCookRole cook = null;
		assertNull("DavidCookRole should be null. It isn't", cook);
		
		cook = new DavidCookRole(person.getName(), person);
		assertEquals("DavidCookRole should have current person as holder. It's not", cook.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cook);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's DavidCookRole should be active. It isn't", cook.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cook.setInactive();
		
		assertFalse("Person's DavidCashierRole should be inactive. It isn't", cook.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End DavidCook
	
	public void testNineDavidHostWork() {
		person.updateOccupation("davidHost", -1, -1);
		DavidHostRole host = null;
		assertNull("DavidHostRole should be null. It isn't", host);
		
		host = new DavidHostRole(person.getName(), person);
		assertEquals("DavidHostRole should have current person as holder. It's not", host.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, host);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's DavidHostRole should be active. It isn't", host.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		host.setInactive();
		
		assertFalse("Person's DavidCashierRole should be inactive. It isn't", host.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End DavidHost
	
	public void testTenJeffersonWaiterRoleWork() {
		person.updateOccupation("jeffersonWaiter", -1, -1);
		JeffersonWaiterRole waiter = null;
		assertNull("JeffersonNormalWaiterRole should be null. It isn't", waiter);
		
		waiter = new JeffersonWaiterRole(person, person.getName());
		assertEquals("JeffersonNormalWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's JeffersonNormalWaiterRole should be active. It isn't", waiter.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		waiter.setInactive();
		
		assertFalse("Person's JeffersonNormalWaiterRole should be inactive. It isn't", waiter.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End JeffersonWaiter
	
	public void testElevenJeffersonCashierRoleWork() {
		person.updateOccupation("jeffersonCashier", -1, -1);
		JeffersonCashierRole cashier = null;
		assertNull("JeffersonCashierRole should be null. It isn't", cashier);
		
		cashier = new JeffersonCashierRole(person, person.getName());
		assertEquals("JeffersonCashierRole should have current person as holder. It's not", cashier.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cashier);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's JeffersonCashierRole should be active. It isn't", cashier.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cashier.setInactive();
		
		assertFalse("Person's JeffersonCashierRole should be inactive. It isn't", cashier.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End JeffersonCashier
	
	public void testTwelveJeffersonCookRoleWork() {
		person.updateOccupation("jeffersonCook", -1, -1);
		JeffersonCookRole cook = null;
		assertNull("JeffersonCookRole should be null. It isn't", cook);
		
		cook = new JeffersonCookRole(person, person.getName());
		assertEquals("JeffersonCookRole should have current person as holder. It's not", cook.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cook);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's JeffersonCookRole should be active. It isn't", cook.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cook.setInactive();
		
		assertFalse("Person's JeffersonCashierRole should be inactive. It isn't", cook.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End JeffersonCook
	
	public void testThirteenJeffersonHostWork() {
		person.updateOccupation("JeffersonHost", -1, -1);
		JeffersonHostRole host = null;
		assertNull("JeffersonHostRole should be null. It isn't", host);
		
		host = new JeffersonHostRole(person, person.getName());
		assertEquals("JeffersonHostRole should have current person as holder. It's not", host.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, host);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's JeffersonHostRole should be active. It isn't", host.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		host.setInactive();
		
		assertFalse("Person's JeffersonCashierRole should be inactive. It isn't", host.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End JeffersonHost
	
	public void testFourteenEllenWaiterRoleWork() {
		person.updateOccupation("ellenWaiter", -1, -1);
		EllenWaiterRole waiter = null;
		assertNull("EllenNormalWaiterRole should be null. It isn't", waiter);
		
		waiter = new EllenNormalWaiterRole(person, person.getName());
		assertEquals("EllenNormalWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's EllenNormalWaiterRole should be active. It isn't", waiter.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		waiter.setInactive();
		
		assertFalse("Person's EllenNormalWaiterRole should be inactive. It isn't", waiter.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End EllenWaiter
	
	public void testFifteenEllenCashierRoleWork() {
		person.updateOccupation("ellenCashier", -1, -1);
		EllenCashierRole cashier = null;
		assertNull("EllenCashierRole should be null. It isn't", cashier);
		
		cashier = new EllenCashierRole(person, person.getName());
		assertEquals("EllenCashierRole should have current person as holder. It's not", cashier.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cashier);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's EllenCashierRole should be active. It isn't", cashier.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cashier.setInactive();
		
		assertFalse("Person's EllenCashierRole should be inactive. It isn't", cashier.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End EllenCashier
	
	public void testSixteenEllenCookRoleWork() {
		person.updateOccupation("ellenCook", -1, -1);
		EllenCookRole cook = null;
		assertNull("EllenCookRole should be null. It isn't", cook);
		
		cook = new EllenCookRole(person, person.getName());
		assertEquals("EllenCookRole should have current person as holder. It's not", cook.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cook);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's EllenCookRole should be active. It isn't", cook.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cook.setInactive();
		
		assertFalse("Person's EllenCashierRole should be inactive. It isn't", cook.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End EllenCook
	
	public void testSeventeenEllenHostWork() {
		person.updateOccupation("ellenHost", -1, -1);
		EllenHostRole host = null;
		assertNull("EllenHostRole should be null. It isn't", host);
		
		host = new EllenHostRole(person, person.getName());
		assertEquals("EllenHostRole should have current person as holder. It's not", host.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, host);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's EllenHostRole should be active. It isn't", host.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		host.setInactive();
		
		assertFalse("Person's EllenCashierRole should be inactive. It isn't", host.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End EllenHost
	
	public void testEighteenEnaWaiterRoleWork() {
		person.updateOccupation("EnaWaiter", -1, -1);
		EnaWaiterRole waiter = null;
		assertNull("EnaNormalWaiterRole should be null. It isn't", waiter);
		
		waiter = new EnaWaiterRole(person, person.getName());
		assertEquals("EnaNormalWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's EnaNormalWaiterRole should be active. It isn't", waiter.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		waiter.setInactive();
		
		assertFalse("Person's EnaNormalWaiterRole should be inactive. It isn't", waiter.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End EnaWaiter
	
	public void testNinteenEnaCashierRoleWork() {
		person.updateOccupation("enaCashier", -1, -1);
		EnaCashierRole cashier = null;
		assertNull("EnaCashierRole should be null. It isn't", cashier);
		
		cashier = new EnaCashierRole(person, person.getName());
		assertEquals("EnaCashierRole should have current person as holder. It's not", cashier.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cashier);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's EnaCashierRole should be active. It isn't", cashier.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cashier.setInactive();
		
		assertFalse("Person's EnaCashierRole should be inactive. It isn't", cashier.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End EnaCashier
	
	public void testTwentyEnaCookRoleWork() {
		person.updateOccupation("EnaCook", -1, -1);
		EnaCookRole cook = null;
		assertNull("EnaCookRole should be null. It isn't", cook);
		
		cook = new EnaCookRole(person, person.getName());
		assertEquals("EnaCookRole should have current person as holder. It's not", cook.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cook);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's EnaCookRole should be active. It isn't", cook.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cook.setInactive();
		
		assertFalse("Person's EnaCashierRole should be inactive. It isn't", cook.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End EnaCook
	
	public void testTwentyOneEnaHostWork() {
		person.updateOccupation("EnaHost", -1, -1);
		EnaHostRole host = null;
		assertNull("EnaHostRole should be null. It isn't", host);
		
		host = new EnaHostRole(person, person.getName());
		assertEquals("EnaHostRole should have current person as holder. It's not", host.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, host);
		assertEquals("PersonAgent should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's EnaHostRole should be active. It isn't", host.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		host.setInactive();
		
		assertFalse("Person's EnaCashierRole should be inactive. It isn't", host.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End EnaHost
	
	public void testTwentyThreeDavidCustomer() {
		DavidCustomerRole customer = null;
		assertNull("DavidCustomerRole should be null. It isn't", customer);
		
		customer = new DavidCustomerRole(person.getName(), person);
		assertEquals("DavidCustomerRole should have current person as holder. It's not", customer.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		assertEquals("PersonAgent should have no roles in its list of roles. It doesn't", person.getRoles().size(), 0);
		person.addRole(ActionType.restaurant, customer);
		assertEquals("PersonAgent should have now have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToRestaurant();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());	
		assertEquals("Person's current action should be go to restaurant.", person.getCurrentAction().type, ActionType.restaurant);
		
		//Arriving at restaurant
		person.setEvent(PersonEvent.arrivedAtRestaurant);
		person.setDestination(CityLocation.restaurant_david);

		customer.setActive();
		assertFalse("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());

		//Is David
		assertTrue("Person's DavidCustomerRole should be active. It isn't", customer.isActive());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Leaving
		customer.setInactive();
		
		assertFalse("Person's DavidCustomerRole should be inactive. It isn't", customer.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	

	}//End DavidCustomer

	public void testTwentyFourJeffersonCustomer() {
		JeffersonCustomerRole customer = null;
		assertNull("JeffersonCustomerRole should be null. It isn't", customer);
		
		customer = new JeffersonCustomerRole(person, person.getName());
		assertEquals("JeffersonCustomerRole should have current person as holder. It's not", customer.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		assertEquals("PersonAgent should have no roles in its list of roles. It doesn't", person.getRoles().size(), 0);
		person.addRole(ActionType.restaurant, customer);
		assertEquals("PersonAgent should have now have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToRestaurant();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());	
		assertEquals("Person's current action should be go to restaurant.", person.getCurrentAction().type, ActionType.restaurant);
		
		//Arriving at restaurant
		person.setEvent(PersonEvent.arrivedAtRestaurant);
		person.setDestination(CityLocation.restaurant_jefferson);

		customer.setActive();
		assertFalse("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());

		//Is Jefferson
		assertTrue("Person's JeffersonCustomerRole should be active. It isn't", customer.isActive());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Leaving
		customer.setInactive();
		
		assertFalse("Person's JeffersonCustomerRole should be inactive. It isn't", customer.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	

	}//End JeffersonCustomer

	public void testTwentyFiveEnaCustomer() {
		EnaCustomerRole customer = null;
		assertNull("EnaCustomerRole should be null. It isn't", customer);
		
		customer = new EnaCustomerRole(person, person.getName());
		assertEquals("EnaCustomerRole should have current person as holder. It's not", customer.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		assertEquals("PersonAgent should have no roles in its list of roles. It doesn't", person.getRoles().size(), 0);
		person.addRole(ActionType.restaurant, customer);
		assertEquals("PersonAgent should have now have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToRestaurant();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());	
		assertEquals("Person's current action should be go to restaurant.", person.getCurrentAction().type, ActionType.restaurant);
		
		//Arriving at restaurant
		person.setEvent(PersonEvent.arrivedAtRestaurant);
		person.setDestination(CityLocation.restaurant_ena);

		customer.setActive();
		assertFalse("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());

		//Is Ena
		assertTrue("Person's EnaCustomerRole should be active. It isn't", customer.isActive());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Leaving
		customer.setInactive();
		
		assertFalse("Person's EnaCustomerRole should be inactive. It isn't", customer.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	

	}//End EnaCustomer
	
	public void testTwentyFiveEllenCustomer() {
		EllenCustomerRole customer = null;
		assertNull("EllenCustomerRole should be null. It isn't", customer);
		
		customer = new EllenCustomerRole(person, person.getName());
		assertEquals("EllenCustomerRole should have current person as holder. It's not", customer.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		assertEquals("PersonAgent should have no roles in its list of roles. It doesn't", person.getRoles().size(), 0);
		person.addRole(ActionType.restaurant, customer);
		assertEquals("PersonAgent should have now have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToRestaurant();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());	
		assertEquals("Person's current action should be go to restaurant.", person.getCurrentAction().type, ActionType.restaurant);
		
		//Arriving at restaurant
		person.setEvent(PersonEvent.arrivedAtRestaurant);
		person.setDestination(CityLocation.restaurant_ellen);

		customer.setActive();
		assertFalse("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());

		//Is Ellen
		assertTrue("Person's EllenCustomerRole should be active. It isn't", customer.isActive());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Leaving
		customer.setInactive();
		
		assertFalse("Person's EllenCustomerRole should be inactive. It isn't", customer.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	

	}//End EllenCustomer
}