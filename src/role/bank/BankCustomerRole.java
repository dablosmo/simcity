package role.bank;

import java.util.concurrent.Semaphore;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.gui.BankCustomerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.market.gui.CustomerGui;


public class BankCustomerRole extends Role implements BankCustomer {
	PersonAgent person;
	String name;
	BankManagerRole bm;
	Banker banker;
	int bankernumber;
	BankTeller teller;
	int tellernumber;
	//customer should know how much money he has beforehand
	private double myaccountnumber;
	private double bankbalance;
	private BankCustomerGui custGui;
	
	private Semaphore atHome = new Semaphore(0,false);
	private Semaphore atWaiting = new Semaphore(0,false);
	private Semaphore atTeller = new Semaphore(0, false);
	private Semaphore atBanker = new Semaphore(0,false);
	
	
	//will be used for all transactions, loan requests, etc
	private int amount;
	
	public enum BankCustomerTransactionState{ none,wantToDeposit, wantToWithdraw, wantNewAccount, wantLoan}
	
	public enum DeferredTransaction{none,deposit,withdraw,loan}

	BankCustomerTransactionState tstate=BankCustomerTransactionState.none;
	
	DeferredTransaction dtrans =DeferredTransaction.none;
	
	public enum BankCustomerState{ none,waitingInBank, atTeller, atBanker, assignedTeller, assignedBanker, goingToTeller, goingToBanker, talking, done, leaving,left
	}
	
	BankCustomerState bcstate=BankCustomerState.none;
	
	public BankCustomerRole(PersonAgent p,String name){
		super(p);
		Do("bank customer initiated");
		this.person=p;
		this.name=name;
		this.myaccountnumber= -1;
		this.bankbalance= -1;
		this.amount=50;
	}
	
	
	public void setBankManager(BankManagerRole bm){
		this.bm=bm;
	}
	
	
	public void setBanker(Banker b){
		this.banker=b;
	}
	

	public void setBankTeller(BankTeller t){
		this.teller=t;
	}

//Messages

	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), s);
	}
	

	public void msgBankClosed() {
		Do("Bank closed");
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	

	public void msgAtTeller() {
		atTeller.release();
		bcstate=BankCustomerState.atTeller;
		stateChanged();
		
	}
	
	
	public void msgAtBanker(){
		log("arrived at banker");
		atBanker.release();
		bcstate=BankCustomerState.atBanker;
		stateChanged();
	}

	
	public void msgAtWaiting(){
		//Do("arrived at waiting");
		atWaiting.release();
		stateChanged();
	}
	

	public void msgLeftBank(){
		log("finished leaving bank");
		atHome.release();
		
		bcstate=BankCustomerState.left;
		setInactive();
		
	}
	


	public void msgNeedLoan(){
		log("Recieved message need loan");
	    tstate=BankCustomerTransactionState.wantLoan;
	    if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			dtrans=DeferredTransaction.loan;
			log("no account exists, making account");
		}
	    stateChanged();
	}
	
	

	public void msgWantNewAccount(){
		log("Recieved message want new account");
		tstate=BankCustomerTransactionState.wantNewAccount;
		stateChanged();
	}
	

	public void msgWantToDeposit(){
		amount = (int) person.getCash()-100;
		System.out.println(amount);
		if(amount<100){
			bcstate=BankCustomerState.done;
			dtrans=DeferredTransaction.none;
			System.out.println("setting done");
			//setInactive();
		}
		log("Recieved message want to deposit");
		tstate=BankCustomerTransactionState.wantToDeposit;
		bcstate=BankCustomerState.none;
		if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			//dtrans=DeferredTransaction.deposit;
			log("no account exists, making account");
			System.out.println("tstate is " + tstate);
			System.out.println("bcstate is " + bcstate);
		}
		stateChanged();
	}
	
	

	public void msgWantToWithdraw(){
		log("Recieved message want to withdraw");
		amount = 50;
		tstate=BankCustomerTransactionState.wantToWithdraw;
		if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			dtrans=DeferredTransaction.withdraw;
			log("no account exists, making account");
			stateChanged();
			return;
			
		}
		if(bankbalance < amount){
			tstate=BankCustomerTransactionState.wantLoan;
			log("Not enough money in bank. requesting loan");
			stateChanged();
			return;
			
		}
		stateChanged();
		
	}
	
	
	

	public void msgGoToTeller(BankTeller te, int tn) {
		log("Recieved message go to teller");
		teller=te;
	    tellernumber=tn;
	    bcstate=BankCustomerState.assignedTeller;
	    stateChanged();
		
	}

	

	public void msgGoToBanker(Banker bk, int bn) {
		log("Recieved message go to banker");
		banker=bk;
		bankernumber=bn;
		bcstate=BankCustomerState.assignedBanker;
		stateChanged();
		
	}
	
	

	public void msgAccountCreated(double temp) {
		log("Recieved message account created");
		setMyaccountnumber(temp);
		person.setAccountnumber(temp);
		
	}
	
	

	public void msgRequestComplete(double change, double balance){
		log("Recieved message request complete");
	    person.setCash((int) (person.getCash()+change));
		//mymoney += change;
	    setBankbalance(balance);
	    bcstate=BankCustomerState.done;
	    stateChanged();
	}

	
	

	public void msgLoanApproved(double loanamount){
		log("Recieved message loan approved");
		person.setCash(person.getCash()+loanamount);
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	


public void msgLoanDenied(double loanamount){
		log("Recieved message loan denied");
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	
	
	
//Scheduler	
	

	public boolean pickAndExecuteAnAction() {
		
		if(bcstate==BankCustomerState.none && tstate==BankCustomerTransactionState.wantToDeposit){
			bcstate=BankCustomerState.waitingInBank;
			
			tellBankManagerDeposit();	
			return true;
		}
		
		if(bcstate==BankCustomerState.none && tstate==BankCustomerTransactionState.wantToWithdraw){
			bcstate=BankCustomerState.waitingInBank;
			
			tellBankManagerWithdraw();	
			return true;
		}
		
		if(bcstate == BankCustomerState.none && tstate==BankCustomerTransactionState.wantNewAccount){
			bcstate=BankCustomerState.waitingInBank;
			log("waiting in bank");
			tellBankManagerNewAccount();
			return true;
		}
		
		if(bcstate == BankCustomerState.none && tstate==BankCustomerTransactionState.wantLoan){
			bcstate=BankCustomerState.waitingInBank;
			tellBankManagerLoan();
			return true;
		}
		
		if(bcstate==BankCustomerState.assignedTeller){
			
			bcstate=BankCustomerState.goingToTeller;
			doGoToTeller();	
			return true;
		}
		if(bcstate==BankCustomerState.assignedBanker){
			
			bcstate=BankCustomerState.goingToBanker;
			doGoToBanker();	
			return true;
		}
		
		
		
		if(bcstate==BankCustomerState.atTeller){
			bcstate=BankCustomerState.talking;
			
			if(tstate==BankCustomerTransactionState.wantToWithdraw){
				withdrawTeller(getAmount());
				return true;
			}
			
			if(tstate==BankCustomerTransactionState.wantToDeposit){
				System.out.println(amount);
				depositTeller(getAmount());
				return true;
			}
		}	
			
		if(bcstate==BankCustomerState.atBanker){
			bcstate=BankCustomerState.talking;
			
			if(tstate==BankCustomerTransactionState.wantNewAccount){
				requestNewAccount(amount);
				return true;
			}
			
			if(tstate==BankCustomerTransactionState.wantLoan){
				requestLoan(amount);
				return true;
			}
			
			
		}	
		
		if(bcstate==BankCustomerState.done && dtrans!=DeferredTransaction.none){
			switch (dtrans){
			
			case deposit:
				dtrans=DeferredTransaction.none;
				bcstate=BankCustomerState.none;
				tstate=BankCustomerTransactionState.wantToDeposit;
				break;
			case withdraw:
				dtrans=DeferredTransaction.none;
				bcstate=BankCustomerState.none;
				tstate=BankCustomerTransactionState.wantToWithdraw;
				break;

			case loan:
				dtrans=DeferredTransaction.none;
				bcstate=BankCustomerState.none;
				tstate=BankCustomerTransactionState.wantLoan;
				break;
			}
			
			return true;
			
			
		}
			
		if(bcstate==BankCustomerState.done && dtrans==DeferredTransaction.none){
			
			bcstate=BankCustomerState.leaving;
			log("leaving");
			log("New account balance is " + bankbalance);
			log("current cash balance is " + person.getCash());
			setInactive();
			doLeaveBank();
			
		}
			
			
			
	
		
		
		
		return false;
	}
	
//Actions
	


	/*////////////////////////GUI ACTIONS  //////////////////////////////////////*/
	private void doLeaveBank() {
		custGui.DoLeaveBank();
		bm.msgImLeaving(this);
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		bcstate=BankCustomerState.none;
	}
	private void doGoToWaiting(){
		custGui.doGoToWaitingArea();
		try {
			atWaiting.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void doGoToTeller(){
		if (tellernumber==0){
			custGui.doGoToTeller1();
			try {
				atTeller.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(tellernumber==1){
			custGui.doGoToTeller2();
			try {
				atTeller.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void doGoToBanker(){
		custGui.doGoToBanker();
		try {
			atBanker.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*////////////////////NON GUI ACTIONS////////////////////////////////////////*/	
	private void tellBankManagerDeposit(){
		doGoToWaiting();
		log("Telling Bank manager i want to deposit");
	    bm.msgIWantToDeposit(this);


	}

	private void tellBankManagerWithdraw(){
		doGoToWaiting();
		log("Telling Bank manager i want to withdraw");
	    bm.msgIWantToWithdraw(this);

	}
	
	private void tellBankManagerNewAccount(){
		doGoToWaiting();
		log("Telling Bank Manager i want new account");
		bm.msgIWantNewAccount(this);
		
	}
	
	private void tellBankManagerLoan(){
		doGoToWaiting();
		log("Telling bank manager want loan");
		bm.msgIWantALoan(this);
	}
	
	private void withdrawTeller( int n){
		log("Telling teller i want to withdraw");
	   teller.msgIWantToWithdraw(this,getMyaccountnumber() ,n);

	}

	private void depositTeller( int n){
		log("Telling teller i want to deposit");
	   teller.msgIWantToDeposit(this,getMyaccountnumber(), n);

	}

	private void requestLoan(int n){
		log("requesting loan");
	    banker.msgIWantALoan(this, getMyaccountnumber() ,n);
	}

	private void requestNewAccount(int n){
		log("requesting new acccount");
	    banker.msgIWantNewAccount(person, this, name, n);

	}

	

	public int getAmount() {
		return amount;
	}

	
	public void setAmount(int amount) {
		this.amount = amount;
	}

	
	public double getMyaccountnumber() {
		return myaccountnumber;
	}

	
	public void setMyaccountnumber(double myaccountnumber) {
		this.myaccountnumber = myaccountnumber;
	}

	
	public double getBankbalance() {
		return bankbalance;
	}

	
	public void setBankbalance(double bankbalance) {
		this.bankbalance = bankbalance;
	}

	
	public void setGui(BankCustomerGui bcGui) {
		custGui=bcGui;
		
	}

	
	public BankCustomerGui getGui() {
		return custGui;
		}

	
	public boolean bankClosed() {
		
		if(bm != null && bm.isActive() && bm.isOpen()){
			return false;
		}
		log("customer checked and bank is closed");
		return true;
	}

	



	


}
