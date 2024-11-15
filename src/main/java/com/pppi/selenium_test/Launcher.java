package com.pppi.selenium_test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Launcher {
	public static void main(String[] args) {
       
        JFrame frame = new JFrame("Browser Settings");
        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

   
        JButton startButton = new JButton("Start Browser");
        JButton allBrowsers = new JButton("Manage Browsers");
        JLabel browserCountLabel = new JLabel("Open Browsers: 0");
        JButton browserInfo = new JButton("Current Proxy:");
        JTextField proxyField = new JTextField(20);
        JTextField secProxyField = new JTextField(20);
        JLabel proxyLabel = new JLabel("Proxy (optional):");
        proxyField.setToolTipText("Format: host:port:username:password");



        ArrayList<App> browsers = new ArrayList<>();

       
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String proxyInput = proxyField.getText().trim();
                String secProxyInput = secProxyField.getText().trim();
                App browser;
                if (proxyInput.isEmpty()) {
                   
                    browser = new App(browserCountLabel);
                } else {
                   
                    browser = new App(browserCountLabel, proxyInput, secProxyInput);
                }
                browsers.add(browser);
                browserCountLabel.setText("Open Browsers: " + App.browserCount);
            }
        });

        allBrowsers.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		JFrame browserFrame = new JFrame("Browser Manager");
        		JPanel browserPanel = new JPanel();
        		browserPanel.setLayout(new BoxLayout(browserPanel, BoxLayout.Y_AXIS));
        	
        		JScrollPane browserScroll = new JScrollPane(browserPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        		
        		for(int i = 0; i < 20; i++) {
        			JPanel row = newRow(browserPanel);
        		browserPanel.add(row);
        		for(int j = 0; j < 3; j++) {
        		row.add(newBox(row));
        		}
        		}
        		browserFrame.pack();
        		browserFrame.add(browserScroll);
        		browserFrame.setVisible(true);
        	}
        });
        
        

       
        panel.add(proxyLabel);
        panel.add(proxyField);
        panel.add(secProxyField);
        panel.add(startButton);
        panel.add(allBrowsers);
 //       panel.add(currProxy);
        panel.add(browserCountLabel);
        
        frame.add(panel);
        
        frame.setVisible(true);
        
        
            }
	
	public static JPanel newRow(JPanel e) {
		JPanel newPanel = new JPanel();
    	newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.X_AXIS));
    	
    	return newPanel;
    }
	
	public static JPanel newBox(JPanel e) {
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
		JLabel test = new JLabel("Open Browsers: 0");
		newPanel.add(test);
    	return newPanel;
	}
		

}