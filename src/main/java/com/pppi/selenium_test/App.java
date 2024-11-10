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
	private WebDriver driver;
	private boolean isRunning = true;
	private JLabel counter;
	private String extensionZipPath;
	
	public App(JLabel counter) {
		
	  WebDriverManager.chromedriver().setup();
	  // Set the path to the driver

      
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--remote-allow-origins=*"); // Sometimes needed for compatibility

      // Initialize WebDriver
      driver = new ChromeDriver(options);
      
      // Open a website
      driver.get("https://abrahamjuliot.github.io/creepjs/");
      browserCount++;
      browserNum = browserCount;
      this.counter = counter;
      System.out.println("Browser " + browserCount + " running on: localhost");
      
      startStatusCheckThread();
     
  }
	
	public App(JLabel counter, String proxy) {
	    this.counter = counter;
	    this.proxy = proxy;
	    WebDriverManager.chromedriver().setup();

	    ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");

	    // Generate the proxy extension with the provided proxy
	    try {
	        String extensionPath = createProxyExtension(proxy);
	        options.addExtensions(new File(extensionPath));
	    } catch (IOException | IllegalArgumentException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Failed to create proxy extension: " + e.getMessage(),
	                "Error", JOptionPane.ERROR_MESSAGE);
	        return; // Exit constructor if proxy setup fails
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
	                driver.getTitle(); // Check if driver session is still active
	                Thread.sleep(1000); // Wait 1 second between checks
	            } catch (NoSuchWindowException | InterruptedException e) {
	                System.out.println("Browser window closed by user.");
	                close();
	                break;
	            }
	        }
	    });
	    statusCheckThread.setDaemon(true); // Ensure the thread stops when the app exits
	    statusCheckThread.start();
	}
	
	private String createProxyExtension(String proxy) throws IOException {
	    // Create a temporary directory for the extension
	    Path extensionDir = Files.createTempDirectory("proxy_extension");

	    // Prepare the manifest.json content
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

	    // Parse the proxy string (assumed format: username:password@host:port)
	    String[] proxyParts = proxy.split("@");
	    if (proxyParts.length != 2) {
	        throw new IllegalArgumentException("Invalid proxy format. Expected format: username:password@host:port");
	    }

	    String credentials = proxyParts[0];
	    String hostPort = proxyParts[1];

	    String[] credentialParts = credentials.split(":", 2);
	    if (credentialParts.length != 2) {
	        throw new IllegalArgumentException("Invalid credentials format. Expected format: username:password");
	    }

	    String username = credentialParts[0];
	    String password = credentialParts[1];

	    String[] hostPortParts = hostPort.split(":", 2);
	    if (hostPortParts.length != 2) {
	        throw new IllegalArgumentException("Invalid host and port format. Expected format: host:port");
	    }

	    String proxyHost = hostPortParts[0];
	    String proxyPort = hostPortParts[1];

	    // Prepare the background.js content
	    String backgroundJsContent = "var config = {\n" +
	            "    mode: \"fixed_servers\",\n" +
	            "    rules: {\n" +
	            "      singleProxy: {\n" +
	            "        scheme: \"http\",\n" +
	            "        host: \"" + proxyHost + "\",\n" +
	            "        port: parseInt(\"" + proxyPort + "\")\n" +
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
	            "                username: \"" + username + "\",\n" +
	            "                password: \"" + password + "\"\n" +
	            "            }\n" +
	            "        };\n" +
	            "    },\n" +
	            "    {urls: [\"<all_urls>\"]},\n" +
	            "    [\"blocking\"]\n" +
	            ");";

	    // Write manifest.json and background.js to the extension directory
	    Path manifestPath = extensionDir.resolve("manifest.json");
	    Files.write(manifestPath, manifestContent.getBytes(StandardCharsets.UTF_8));

	    Path backgroundJsPath = extensionDir.resolve("background.js");
	    Files.write(backgroundJsPath, backgroundJsContent.getBytes(StandardCharsets.UTF_8));

	    // Zip the extension directory
	    String extensionZipPath = extensionDir.toString() + ".zip";
	    zipFolder(extensionDir.toFile(), new File(extensionZipPath));

	    // Store the path for cleanup later
	    this.extensionZipPath = extensionZipPath;

	    // Return the path to the zipped extension
	    return extensionZipPath;
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
	        driver.quit();  // Close the browser window
	    }
	    browserCount--; // Decrement the count when a browser is closed
	    System.out.println("Browser " + browserNum + " closed. Remaining open browsers: " + browserCount);
	    isRunning = false;
	    counter.setText("Open Browsers: " + App.browserCount);

	    // Delete the extension zip file and temporary directory
	    if (extensionZipPath != null) {
	        try {
	            Files.deleteIfExists(Paths.get(extensionZipPath));
	            // Delete the temporary extension directory
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

