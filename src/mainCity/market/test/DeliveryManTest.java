package mainCity.market.test;

import java.util.Map;
import java.util.TreeMap;

import role.market.MarketDeliveryManRole;
import role.market.MarketDeliveryManRole.AgentState;
import role.market.MarketDeliveryManRole.DeliveryEvent;
import role.market.MarketDeliveryManRole.DeliveryState;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.contactList.ContactList;
import mainCity.interfaces.*;
import mainCity.market.test.mock.*;
import mainCity.restaurants.EllenRestaurant.test.mock.MockCashier;
import mainCity.restaurants.EllenRestaurant.test.mock.MockCook;
import junit.framework.TestCase;


public class DeliveryManTest extends TestCase {
	MarketDeliveryManRole deliveryMan;
	MockDeliveryManGui gui;
	//MockCustomer customer1;
	//MockCustomer customer2;
	MockEmployee employee;
	MockCashier cashier;
	MockCook cook;
	
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent d = new PersonAgent("Delivery man");
        deliveryMan = new MarketDeliveryManRole(d, d.getName());
        d.addRole(ActionType.work, deliveryMan);
        
        //customer1 = new MockCustomer("MockCustomer1"); 
        //customer2 = new MockCustomer("MockCustomer2");  
        gui = new MockDeliveryManGui("MockDeliveryGui");
        deliveryMan.setGui(gui);
        
        employee = new MockEmployee("MockEmployee");
        cashier = new MockCashier("MockCashier");
        cook = new MockCook("MockCook");
        
        
	}
	
		//=============================== NEXT TEST =========================================================================
		public void testOneNormalBusinessScenario(){
			gui.deliveryMan = deliveryMan;
			//deliveryMan.deliveryGui = gui;
			
			
			//preconditions
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
            assertEquals("Delivery man should have 0 available money but does not.", deliveryMan.getAvailableMoney(), 0.0);
            assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            
            //step 1
            deliveryMan.msgHereIsOrderForDelivery("ellenRestaurant", cook, cashier, inventory, 25.99);
            
            //postconditions 1/preconditions 2
            assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + cashier.log.toString(), 0, cashier.log.size());
            assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
                    + cook.log.toString(), 0, cook.log.size());
            assertEquals("Cashier should have 1 bill but does not.", deliveryMan.getBills().size(), 1);
            assertTrue("Delivery man should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.newBill);
            assertTrue("Delivery man should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Delivery man should contain a check with the correct cook. It doesn't.",
            		deliveryMan.bills.get(0).getCook() == cook);
            assertTrue("Delivery man should contain a check with the correct cashier. It doesn't.",
                    deliveryMan.bills.get(0).getCashier() == cashier);
            assertTrue("Delivery man should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            assertTrue("Delivery man should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(0).getAmountCharged() == 25.99);
            
            deliveryMan.bills.get(0).setCashier(cashier);
            deliveryMan.bills.get(0).setCook(cook);
            
            
            //step 2 - we have to manually run through "DeliverOrder" (the ContactList won't allow for interfaces, so we can't run through the scenario fully)
            deliveryMan.bills.get(0).getCook().msgHereIsYourOrder(inventory);
            deliveryMan.bills.get(0).getCashier().msgHereIsMarketBill(inventory, deliveryMan.bills.get(0).getAmountCharged(), deliveryMan);
            deliveryMan.bills.get(0).setState(DeliveryState.waitingForPayment);
            deliveryMan.setState(AgentState.makingDelivery);

            
            //postconditions 2/preconditions 3
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMarketBill from Delivery man for $25.99."));
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            assertFalse("Delivery man's scheduler should have returned false (waiting), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            
            //step 4
            deliveryMan.msgHereIsPayment(50, "ellenRestaurant");
            
            //postconditions 4/preconditions 5
            assertTrue("Delivery man should contain a check with the correct amountPaid. It doesn't.",
                    deliveryMan.bills.get(0).getAmountPaid() == 50);
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should contain a check with event == receivedPayment. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.receivedPayment);
            
            
            //step 5
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 5/preconditions 6
            assertTrue("MockCashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsChange from Delivery man for $24.01."));
            assertTrue("Delivery man should contain a check with the correct amountMarketGets. It doesn't.",
                    deliveryMan.bills.get(0).getAmountMarketGets() == 25.99);
            assertTrue("Delivery man should contain a check with state == waitingForVerification. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForVerification);
            
            
            
            //step 6
            deliveryMan.msgChangeVerified("ellenRestaurant");
            
            //postconditions 6/preconditions 7
            assertEquals("Delivery man should have $25.99 available money but does not.", deliveryMan.getAvailableMoney(), 25.99);
            assertTrue("Delivery man should contain a check with event == changeVerified. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.changeVerified);
            
            
            //step 7 - message semaphore before it's acquired to avoid locking
			deliveryMan.msgAtHome();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            //postconditions 7/preconditions 8
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
			assertTrue("MockDeliveryManGui should have logged \"Told to DoGoToHomePosition\" but didn't.", 
					gui.log.containsString("Gui is told to DoGoToHomePosition by agent."));
			assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
			assertFalse("Delivery man's scheduler should have returned false (nothing left to do), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
		}
		
		
		
		//=============================== NEXT TEST =========================================================================
		public void testTwoNormalBusinessScenario(){
			gui.deliveryMan = deliveryMan;
			
			
			//preconditions
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
            assertEquals("Delivery man should have 0 available money but does not.", deliveryMan.getAvailableMoney(), 0.0);
            assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            Map<String, Integer>inventory2 = new TreeMap<String, Integer>();
            inventory2.put("pizza", 1);		//cost of pizza = 8.99
            inventory2.put("pasta", 2);		//cost of pasta = 20.00
            
            
            //step 1 - calling it enaRestaurant, but its cook/cashier references will really be for my restaurant
            deliveryMan.msgHereIsOrderForDelivery("ellenRestaurant", cook, cashier, inventory, 25.99);
            deliveryMan.msgHereIsOrderForDelivery("enaRestaurant", cook, cashier, inventory2, 48.99);
            
            //postconditions 1/preconditions 2
            assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + cashier.log.toString(), 0, cashier.log.size());
            assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
                    + cook.log.toString(), 0, cook.log.size());
            assertEquals("Cashier should have 2 bills but does not.", deliveryMan.getBills().size(), 2);
            assertTrue("Delivery man should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.newBill);
            assertTrue("Delivery man should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Delivery man should contain a check with the correct cook. It doesn't.",
            		deliveryMan.bills.get(0).getCook() == cook);
            assertTrue("Delivery man should contain a check with the correct cashier. It doesn't.",
                    deliveryMan.bills.get(0).getCashier() == cashier);
            assertTrue("Delivery man should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            assertTrue("Delivery man should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(0).getAmountCharged() == 25.99);
            
            assertTrue("Delivery man should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(1).getState() == DeliveryState.newBill);
            assertTrue("Delivery man should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(1).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Delivery man should contain a check with the correct cook. It doesn't.",
            		deliveryMan.bills.get(1).getCook() == cook);
            assertTrue("Delivery man should contain a check with the correct cashier. It doesn't.",
                    deliveryMan.bills.get(1).getCashier() == cashier);
            assertTrue("Delivery man should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(1).getRestaurant().equalsIgnoreCase("enaRestaurant"));
            assertTrue("Delivery man should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(1).getAmountCharged() == 48.99);
            
            deliveryMan.bills.get(0).setCashier(cashier);
            deliveryMan.bills.get(0).setCook(cook);
            
            //step 2 - we have to manually run through "DeliverOrder" (the ContactList won't allow for interfaces, so we can't run through the scenario fully)
            deliveryMan.bills.get(0).getCook().msgHereIsYourOrder(inventory);
            deliveryMan.bills.get(0).getCashier().msgHereIsMarketBill(inventory, deliveryMan.bills.get(0).getAmountCharged(), deliveryMan);
            deliveryMan.bills.get(0).setState(DeliveryState.waitingForPayment);
            deliveryMan.setState(AgentState.makingDelivery);

            
            //postconditions 2/preconditions 3
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMarketBill from Delivery man for $25.99."));
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            assertFalse("Delivery man's scheduler should have returned false (waiting), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            //step 4
            deliveryMan.msgHereIsPayment(50, "ellenRestaurant");
            
            //postconditions 4/preconditions 5
            assertTrue("Delivery man should contain a check with the correct amountPaid. It doesn't.",
                    deliveryMan.bills.get(0).getAmountPaid() == 50);
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should contain a check with event == receivedPayment. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.receivedPayment);
            
            
            //step 5
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 5/preconditions 6
            assertTrue("MockCashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsChange from Delivery man for $24.01."));
            assertTrue("Delivery man should contain a check with the correct amountMarketGets. It doesn't.",
                    deliveryMan.bills.get(0).getAmountMarketGets() == 25.99);
            assertTrue("Delivery man should contain a check with state == waitingForVerification. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForVerification);
            
            
            
            //step 6
            deliveryMan.msgChangeVerified("ellenRestaurant");
            
            //postconditions 6/preconditions 7
            assertEquals("Delivery man should have $25.99 available money but does not.", deliveryMan.getAvailableMoney(), 25.99);
            assertTrue("Delivery man should contain a check with event == changeVerified. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.changeVerified);
            
            
            //step 7 - message semaphore before it's acquired to avoid locking
			deliveryMan.msgAtHome();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            //postconditions 7/preconditions 8
			assertEquals("Delivery man should have 1 bill in it. It doesn't.", deliveryMan.getBills().size(), 1);
			assertTrue("MockDeliveryManGui should have logged \"Told to DoGoToHomePosition\" but didn't.", 
					gui.log.containsString("Gui is told to DoGoToHomePosition by agent."));
			assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
			
			//now for the next bill
            deliveryMan.bills.get(0).setCashier(cashier);
            deliveryMan.bills.get(0).setCook(cook);
            
            
            //step 3 - manually run through deliver order again
            deliveryMan.bills.get(0).getCook().msgHereIsYourOrder(inventory);
            deliveryMan.bills.get(0).getCashier().msgHereIsMarketBill(inventory, deliveryMan.bills.get(0).getAmountCharged(), deliveryMan);
            deliveryMan.bills.get(0).setState(DeliveryState.waitingForPayment);
            deliveryMan.setState(AgentState.makingDelivery);
            

            //postconditions 3/preconditions 4
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMarketBill from Delivery man for $48.99."));
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            assertFalse("Delivery man's scheduler should have returned false (waiting), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
          //step 4
            deliveryMan.msgHereIsPayment(50, "enaRestaurant");
            
            //postconditions 4/preconditions 5
            assertTrue("Delivery man should contain a check with the correct amountPaid. It doesn't.",
                    deliveryMan.bills.get(0).getAmountPaid() == 50);
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should contain a check with event == receivedPayment. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.receivedPayment);
            
            
            //step 5
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 5/preconditions 6
            assertTrue("MockCashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsChange from Delivery man for $1.01."));
            assertTrue("Delivery man should contain a check with the correct amountMarketGets. It doesn't.",
                    deliveryMan.bills.get(0).getAmountMarketGets() == 48.99);
            assertTrue("Delivery man should contain a check with state == waitingForVerification. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForVerification);
            
            
            
            //step 6
            deliveryMan.msgChangeVerified("enaRestaurant");
            
            //postconditions 6/preconditions 7
            assertEquals("Delivery man should have $74.98 available money but does not.", deliveryMan.getAvailableMoney(), 74.98);
            assertTrue("Delivery man should contain a check with event == changeVerified. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.changeVerified);
            
            
            //step 7 - message semaphore before it's acquired to avoid locking
			deliveryMan.msgAtHome();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            //postconditions 7/preconditions 8
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
			assertTrue("MockDeliveryManGui should have logged \"Told to DoGoToHomePosition\" but didn't.", 
					gui.log.containsString("Gui is told to DoGoToHomePosition by agent."));
			assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
			
		}
		
		
		
		//=============================== NEXT TEST =========================================================================
		public void testOneFlakeBusinessScenario(){
			gui.deliveryMan = deliveryMan;
			deliveryMan.deliveryGui = gui;
			

			//preconditions
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
            assertEquals("Delivery man should have 0 available money but does not.", deliveryMan.getAvailableMoney(), 0.0);
            assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            
            //step 1
            deliveryMan.msgHereIsOrderForDelivery("ellenRestaurant", cook, cashier, inventory, 25.99);
            
            //postconditions 1/preconditions 2
            assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + cashier.log.toString(), 0, cashier.log.size());
            assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
                    + cook.log.toString(), 0, cook.log.size());
            assertEquals("Delivery man should have 1 bill but does not.", deliveryMan.getBills().size(), 1);
            assertTrue("Delivery man should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.newBill);
            assertTrue("Delivery man should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Delivery man should contain a check with the correct cook. It doesn't.",
            		deliveryMan.bills.get(0).getCook() == cook);
            assertTrue("Delivery man should contain a check with the correct cashier. It doesn't.",
                    deliveryMan.bills.get(0).getCashier() == cashier);
            assertTrue("Delivery man should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            assertTrue("Delivery man should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(0).getAmountCharged() == 25.99);
            
            deliveryMan.bills.get(0).setCashier(cashier);
            deliveryMan.bills.get(0).setCook(cook);
            
            
            //step 2 - we have to manually run through "DeliverOrder" (the ContactList won't allow for interfaces, so we can't run through the scenario fully)
            deliveryMan.bills.get(0).getCook().msgHereIsYourOrder(inventory);
            deliveryMan.bills.get(0).getCashier().msgHereIsMarketBill(inventory, deliveryMan.bills.get(0).getAmountCharged(), deliveryMan);
            deliveryMan.bills.get(0).setState(DeliveryState.waitingForPayment);
            deliveryMan.setState(AgentState.makingDelivery);
            
            
            //postconditions 2/preconditions 3
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMarketBill from Delivery man for $25.99."));
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            assertFalse("Delivery man's scheduler should have returned false (waiting), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            
            //step 4
            deliveryMan.msgHereIsPayment(10, "ellenRestaurant");		//less than bill charge
            
            //postconditions 4/preconditions 5
            assertTrue("Delivery man should contain a check with the correct amountPaid. It doesn't.",
                    deliveryMan.bills.get(0).getAmountPaid() == 10);
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should contain a check with event == receivedPayment. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.receivedPayment);
            
            
            //step 5
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 5/preconditions 6
            assertTrue("MockCashier should have logged \"Received msgNotEnoughMoney\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgNotEnoughMoney, amount owed = $15.99"));
            assertTrue("Delivery man should contain a check with the correct amountMarketGets. It doesn't.",
                    deliveryMan.bills.get(0).getAmountMarketGets() == 10);
            assertTrue("Delivery man should contain a check with the correct amountOwed. It doesn't. Its amountOwed = " + deliveryMan.bills.get(0).getAmountMarketGets(),
                    deliveryMan.bills.get(0).getAmountOwed() == 15.99);
            assertTrue("Delivery man should contain a check with state == oweMoney. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.oweMoney);
            
            
            
            //step 6
            deliveryMan.msgIOweYou(15.99, "ellenRestaurant");
            
            //postconditions 6/preconditions 7
            assertEquals("Delivery man should have $10 available money but does not.", deliveryMan.getAvailableMoney(), 10.0);
            assertTrue("Delivery man should contain a check with event == acknowledgedDebt. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.acknowledgedDebt);
            
            
            //step 7 - call semaphore release before it's acquired to avoid locking
            deliveryMan.msgAtHome();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            //postconditions 7/preconditions 8
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
			assertTrue("MockDeliveryManGui should have logged \"Told to DoGoToHomePosition\" but didn't.", 
					gui.log.containsString("Gui is told to DoGoToHomePosition by agent."));

			assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
			assertFalse("Delivery man's scheduler should have returned false (nothing left to do), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
			
		}
}