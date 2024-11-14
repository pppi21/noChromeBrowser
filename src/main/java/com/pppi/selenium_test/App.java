package com.pppi.selenium_test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class App {
	
	public static int browserCount;
	private int browserNum;
	private String proxy = "";
	private String secProxy = "";
	private WebDriver driver;
	private boolean isRunning = true;
	private JLabel counter;
	private String extensionZipPath;
	
	public App(JLabel counter) {
		
	  WebDriverManager.chromedriver().setup();
	 

      
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--remote-allow-origins=*");

    
      driver = new ChromeDriver(options);
      
     
      driver.get("https://abrahamjuliot.github.io/creepjs/");
      browserCount++;
      browserNum = browserCount;
      this.counter = counter;
      System.out.println("Browser " + browserCount + " running on: localhost");
      
      startStatusCheckThread();
     
  }
	
	public App(JLabel counter, String proxy, String secProxy) {
	    this.counter = counter;
	    this.proxy = proxy;
	    this.secProxy = secProxy;
	    WebDriverManager.chromedriver().setup();

	    ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");

	   
	    try {
	        String extensionPath = createProxyExtension(proxy, secProxy);
	        options.addExtensions(new File(extensionPath));
	    } catch (IOException | IllegalArgumentException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Failed to create proxy extension: " + e.getMessage(),
	                "Error", JOptionPane.ERROR_MESSAGE);
	        return; 
	    }

	    driver = new ChromeDriver(options);
	    driver.get("https://abrahamjuliot.github.io/creepjs/");

	    browserCount++;
	    browserNum = browserCount;
	    System.out.println("Browser " + browserCount + " running on: " + proxy);

	    startStatusCheckThread();
	}
	
	private void startStatusCheckThread() {
	    Thread statusCheckThread = new Thread(() -> {
	        while (isRunning) {
	            try {
	                driver.getTitle();
	                Thread.sleep(1000);
	            } catch (NoSuchWindowException | InterruptedException e) {
	                System.out.println("Browser window closed by user.");
	                close();
	                break;
	            }
	        }
	    });
	    statusCheckThread.setDaemon(true);
	    statusCheckThread.start();
	}
	
	private String createProxyExtension(String proxy, String secProxy) throws IOException {
	   
	    Path extensionDir = Files.createTempDirectory("proxy_extension");

	   
	    String manifestContent = "{\n" +
	            "  \"manifest_version\": 2,\n" +
	            "  \"name\": \"Proxy Auth Extension\",\n" +
	            "  \"version\": \"1.0\",\n" +
	            "  \"permissions\": [\n" +
	            "    \"proxy\",\n" +
	            "    \"tabs\",\n" +
	            "    \"unlimitedStorage\",\n" +
	            "    \"storage\",\n" +
	            "    \"<all_urls>\",\n" +
	            "    \"webRequest\",\n" +
	            "    \"webRequestBlocking\"\n" +
	            "  ],\n" +
	            "  \"background\": {\n" +
	            "    \"scripts\": [\"background.js\"]\n" +
	            "  }\n" +
	            "}";

	    
	    

	    // Prepare the background.js content
	    String backgroundJsContent = "var config = {\n" +
	            "    mode: \"fixed_servers\",\n" +
	            "    rules: {\n" +
	            "      singleProxy: {\n" +
	            "        scheme: \"http\",\n" +
	            "        host: \"" + data(proxy,"host") + "\",\n" +
	            "        port: parseInt(\"" + data(proxy,"port") + "\")\n" +
	            "      }\n" +
	            "    }\n" +
	            "};\n" +
	            "\n" +
	            "chrome.proxy.settings.set({value: config, scope: \"regular\"}, function() {});\n" +
	            "\n" +
	            "chrome.webRequest.onAuthRequired.addListener(\n" +
	            "    function(details) {\n" +
	            "        return {\n" +
	            "            authCredentials: {\n" +
	            "                username: \"" + data(proxy,"username") + "\",\n" +
	            "                password: \"" + data(proxy,"password") + "\"\n" +
	            "            }\n" +
	            "        };\n" +
	            "    },\n" +
	            "    {urls: [\"<all_urls>\"]},\n" +
	            "    [\"blocking\"]\n" +
	            ");";

	    
	    Path manifestPath = extensionDir.resolve("manifest.json");
	    Files.write(manifestPath, manifestContent.getBytes(StandardCharsets.UTF_8));

	    Path backgroundJsPath = extensionDir.resolve("background.js");
	    Files.write(backgroundJsPath, backgroundJsContent.getBytes(StandardCharsets.UTF_8));

	    
	    String extensionZipPath = extensionDir.toString() + ".zip";
	    zipFolder(extensionDir.toFile(), new File(extensionZipPath));

	   
	    this.extensionZipPath = extensionZipPath;
	    
	    return extensionZipPath;
	}
	
	private String data(String proxy, String i) {
		String[] proxyParts = proxy.split(":");
	    if (proxyParts.length != 4) {
	        throw new IllegalArgumentException("Invalid proxy format. Expected format: host:port:username:password");
	    }
	    if(i.equals("host")){
	    	return proxyParts[0];
	    }
	    else if(i.equals("port")) {
	    	return proxyParts[1];
	    }
	    else if(i.equals("username")) {
	    	return proxyParts[2];
	    }
	    else if(i.equals("password")) {
	    	return proxyParts[3];
	    }
	    else {
	    	return null;
	    }
        
	}
	private void zipFolder(File sourceFolder, File zipFile) throws IOException {
	    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
	        zipFolderRecursive(zos, sourceFolder, sourceFolder.getAbsolutePath().length() + 1);
	    }
	}

	private void zipFolderRecursive(ZipOutputStream zos, File folder, int prefixLength) throws IOException {
	    for (File file : folder.listFiles()) {
	        if (file.isDirectory()) {
	            zipFolderRecursive(zos, file, prefixLength);
	        } else {
	            String entryName = file.getAbsolutePath().substring(prefixLength).replace("\\", "/");
	            ZipEntry entry = new ZipEntry(entryName);
	            zos.putNextEntry(entry);

	            Files.copy(file.toPath(), zos);
	            zos.closeEntry();
	        }
	    }
	}


	
	public String getProxy() {
		return proxy;
	}
	
	public void close() {
	    if (driver != null) {
	        driver.quit(); 
	    }
	    browserCount--; 
	    System.out.println("Browser " + browserNum + " closed. Remaining open browsers: " + browserCount);
	    isRunning = false;
	    counter.setText("Open Browsers: " + App.browserCount);

	   
	    if (extensionZipPath != null) {
	        try {
	            Files.deleteIfExists(Paths.get(extensionZipPath));
	            
	            Path extensionDir = Paths.get(extensionZipPath.replace(".zip", ""));
	            Files.walk(extensionDir)
	                    .sorted(Comparator.reverseOrder())
	                    .map(Path::toFile)
	                    .forEach(File::delete);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
}

