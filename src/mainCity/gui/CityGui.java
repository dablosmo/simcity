package mainCity.gui;

import javax.swing.*;

import mainCity.gui.AnimationPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;
import mainCity.market.gui.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.contactList.*;
import mainCity.gui.trace.*;

import java.awt.*;
import java.awt.event.*;

public class CityGui extends JFrame{
	
	private AnimationPanel animationPanel = new AnimationPanel(); 
	private CityView view = new CityView(this);
	private TracePanel tracePanel1;
	private TracePanel tracePanel2;
	private TracePanel tracePanel3;
	private TracePanel tracePanel4;
	private TracePanel tracePanel5;
	private CityPanel cityPanel = new CityPanel(this);
	private JPanel mainPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	private JPanel controlPanel = new JPanel();
	private JPanel detailedPanel = new JPanel();
	//private MarketGui marketGui = new MarketGui();
	
	public CityGui() { 
		
		int WINDOWX = 1300; 
		int WINDOWY = 600;
		
		setBounds(50, 50, WINDOWX, WINDOWY+150);
		setLayout(new BorderLayout());
		
		//---MAIN PANEL BEGIN---//
        
        Dimension mainDim = new Dimension((int) (WINDOWX * .6), WINDOWY);
        mainPanel.setPreferredSize(mainDim);
        mainPanel.setMinimumSize(mainDim);
        mainPanel.setMaximumSize(mainDim);
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        add(mainPanel, BorderLayout.CENTER);
        	//Main City View
        Dimension animationDim = new Dimension((int) (WINDOWX * .6), (int) (WINDOWY * .8));
        getAnimationPanel().setPreferredSize(animationDim);
        getAnimationPanel().setMinimumSize(animationDim);
        getAnimationPanel().setMaximumSize(animationDim);
        getAnimationPanel().setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(getAnimationPanel(), BorderLayout.CENTER);
        	//Control Panel
        Dimension controlDim = new Dimension((int) (WINDOWX * .6), (int) (WINDOWY * .2));
        controlPanel.setPreferredSize(controlDim);
        controlPanel.setMinimumSize(controlDim);
        controlPanel.setMaximumSize(controlDim);
        //detailedPanel.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
		
		//---LEFT PANEL BEGIN---//
		//Entire Left Panel Sizing
		Dimension leftDim = new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .5));
		leftPanel.setPreferredSize(leftDim);
		leftPanel.setMinimumSize(leftDim);
		leftPanel.setMaximumSize(leftDim);
		leftPanel.setBorder(BorderFactory.createEtchedBorder());
        add(leftPanel, BorderLayout.WEST);
		
        //Detailed Panel Sizing
        Dimension detailDim = new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .6));
        detailedPanel.setPreferredSize(detailDim);
        detailedPanel.setMinimumSize(detailDim);
        detailedPanel.setMaximumSize(detailDim);
        detailedPanel.add(view);
        leftPanel.add(detailedPanel, BorderLayout.CENTER);
        
        //=============== TRACE PANEL ====================//
        JTabbedPane tabbedPane = new JTabbedPane();
        tracePanel1 = new TracePanel();
        tracePanel1.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel1.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel1.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel1.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel1.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel1.showAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags  
        tracePanel1.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel1.hideAlertsWithTag(AlertTag.BANK);
        tracePanel1.hideAlertsWithTag(AlertTag.RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel1);
        tabbedPane.addTab("PERSON", tracePanel1);
        
        tracePanel2 = new TracePanel();
        tracePanel2.setPreferredSize(new Dimension((int) (WINDOWY * .4), (int) (WINDOWY * .4)));
        tracePanel2.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel2.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel2.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel2.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel2.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel2.showAlertsWithTag(AlertTag.MARKET);
        tracePanel2.hideAlertsWithTag(AlertTag.BANK);
        tracePanel2.hideAlertsWithTag(AlertTag.RESTAURANT);
        
        
        AlertLog.getInstance().addAlertListener(tracePanel2);
        tabbedPane.addTab("MARKET", tracePanel2);
        
        tracePanel3 = new TracePanel();
        tracePanel3.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel3.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel3.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel3.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel3.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel3.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel3.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel3.showAlertsWithTag(AlertTag.BANK);
        tracePanel3.hideAlertsWithTag(AlertTag.RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel3);
        tabbedPane.addTab("BANK", tracePanel3);
        
        
        tracePanel4 = new TracePanel();
        tracePanel4.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel4.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel4.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel4.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel4.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel4.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel4.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel4.hideAlertsWithTag(AlertTag.BANK);
        tracePanel4.showAlertsWithTag(AlertTag.RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel4);
        tabbedPane.addTab("RESTAURANT", tracePanel4);
        
        leftPanel.add(tabbedPane, BorderLayout.SOUTH);       
	}
	
	public static void main(String[] args) {
        CityGui gui = new CityGui();
        gui.setTitle("SimCity201");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
	
	public AnimationPanel getAnimationPanel() {
		return animationPanel;
	}
}
