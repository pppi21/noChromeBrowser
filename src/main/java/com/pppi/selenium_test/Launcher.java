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


       
        panel.add(proxyLabel);
        panel.add(proxyField);
        panel.add(secProxyField);
        panel.add(startButton);
        panel.add(switchButton);
        panel.add(currProxy);
        panel.add(browserCountLabel);
        
        frame.add(panel);
        
        frame.setVisible(true);
        
        
            }
	
		

}