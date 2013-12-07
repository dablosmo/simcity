package mainCity.market1.test.mock;

import mainCity.market1.interfaces.EmployeeGuiInterface;



public class MockEmployeeGui extends Mock implements EmployeeGuiInterface {
	
	public MockEmployeeGui(String name){
		super(name);
	}
	@Override
	public void DoGoToStation(){
		log.add(new LoggedEvent("Gui told to DoGoToStation by agent."));
	}
    public void DoGoToCashier(){
    	log.add(new LoggedEvent("Gui told to DoGoToCashier by agent."));
    }
    public void DoFulfillOrder(){
    	log.add(new LoggedEvent("Gui told to DoFulfillOrder by agent."));
    }
    public void DoGoToDeliveryMan(){
    	
    }
    public void DoLeaveMarket(){
    	
    }
    @Override
    public void DoPickUpWaitingCustomer(int x, int y){
    	log.add(new LoggedEvent("Gui told to DoPickUpWaitingCustomer by agent."));
    }
	
}