# Selenium Browser Launcher with Proxy Support

This Java application allows you to launch multiple Chrome browser instances using Selenium WebDriver with optional proxy configuration. The application provides a simple GUI where you can input proxy settings and manage multiple browser instances.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
  - [Launching Browsers](#launching-browsers)
  - [Proxy Configuration](#proxy-configuration)
- [Code Overview](#code-overview)
  - [Launcher.java](#launcherjava)
  - [App.java](#appjava)
- [Important Notes](#important-notes)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Features

- **Launch Multiple Browsers**: Open multiple Chrome browser instances from a single GUI.
- **Proxy Support**: Input proxy settings directly in the GUI to launch browsers with proxy configurations.
- **Dynamic Proxy Extension**: Automatically generates a Chrome extension on-the-fly to handle proxy authentication.
- **Browser Count Tracking**: Displays the number of open browsers in real-time.
- **Automatic Cleanup**: Cleans up temporary files created for proxy extensions when browsers are closed.

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher. Java 17 or newer is recommended.
- **Maven**: For dependency management.
- **Chrome Browser**: Ensure that Google Chrome is installed on your system.
- **Internet Connection**: Required for downloading dependencies and WebDriver binaries.

## Setup and Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/selenium-browser-launcher.git
   cd selenium-browser-launcher
   ```

2. **Import the Project**

   - **Eclipse**:
     - Open Eclipse.
     - Go to **File > Import > Existing Maven Projects**.
     - Select the cloned repository folder.
     - Click **Finish**.
   - **IntelliJ IDEA**:
     - Open IntelliJ IDEA.
     - Click on **Open** and select the project folder.
     - IntelliJ will automatically detect and import the Maven project.

3. **Build the Project**

   - **Maven**:

     ```bash
     mvn clean install
     ```

   - **IDE**: Use your IDE's build function to compile the project.

4. **Dependencies**

   The project uses the following main dependencies, managed by Maven:

   - **Selenium WebDriver**: For browser automation.
   - **WebDriverManager**: To automatically manage WebDriver binaries.
   - **Java Swing**: For the GUI (part of the JDK).

   These dependencies are specified in the `pom.xml` file.

## Usage

### Launching Browsers

1. **Run the Application**

   - **From IDE**: Run the `Launcher` class.
   - **From Command Line**:

     ```bash
     mvn exec:java -Dexec.mainClass="com.pppi.selenium_test.Launcher"
     ```

2. **Using the GUI**

   - **Proxy Input Field**: (Optional) Enter your proxy settings in the format `username:password@host:port`.
   - **Start Browser Button**: Click to launch a new browser instance.
   - **Open Browsers Label**: Displays the number of currently open browsers.

3. **Managing Browsers**

   - **Multiple Instances**: Click the **Start Browser** button multiple times to open multiple browser instances.
   - **Closing Browsers**: Close the browser windows manually or use a **Close All Browsers** button if implemented.
   - **Browser Count**: The application updates the count of open browsers in real-time.

### Proxy Configuration

- **Proxy Format**: The proxy should be entered in the format `username:password@host:port`.

  **Example**:

  ```
  myuser:mypass@proxy.example.com:8080
  ```

- **Proxy Types**: The application currently supports HTTP proxies with basic authentication.

- **No Proxy**: Leave the proxy field empty to launch a browser without proxy settings.

## Code Overview

### Launcher.java

This class is the entry point of the application. It sets up the GUI and handles user interactions.

**Key Components**:

- **JFrame and JPanel**: The main window and panel for arranging components.
- **JTextField (`proxyField`)**: Input field for proxy settings.
- **JButton (`startButton`)**: Button to launch browsers.
- **JLabel (`browserCountLabel`)**: Displays the count of open browsers.
- **Action Listeners**: Handle button clicks and initiate browser launches.

**Main Functionality**:

- When the **Start Browser** button is clicked, it reads the proxy input and creates a new instance of the `App` class.
- Updates the `browserCountLabel` to reflect the current number of open browsers.

### App.java

This class manages individual browser instances.

**Key Components**:

- **Static `browserCount`**: Tracks the total number of open browsers across all instances.
- **WebDriver (`driver`)**: Controls the Chrome browser instance.
- **Proxy Extension Generation**: Dynamically creates a Chrome extension to handle proxy settings with authentication.
- **Status Check Thread**: Monitors the browser window to detect if it has been closed manually.
- **Resource Cleanup**: Deletes temporary files created for the proxy extension when the browser is closed.

**Main Functionality**:

- **Constructors**: Two constructors handle launching browsers with or without proxy settings.
- **Proxy Extension Creation**:
  - Parses the proxy string to extract credentials and host information.
  - Generates `manifest.json` and `background.js` for the Chrome extension.
  - Zips the extension files for Chrome to load.
- **Browser Launch**:
  - Sets up `ChromeOptions` with necessary arguments and loads the proxy extension if provided.
  - Launches Chrome and navigates to the specified URL.
- **Browser Monitoring**:
  - Starts a background thread to monitor the browser window's status.
  - Updates `browserCount` and the GUI label when the browser is closed.

## Important Notes

- **Proxy Security**: Be cautious when handling proxy credentials. Avoid logging sensitive information. Ensure that any credentials are stored and transmitted securely.
- **Temporary Files**: The application creates temporary files for the proxy extension, which are cleaned up when browsers are closed. Ensure that the application has permission to create and delete files in the temporary directory.
- **Browser Compatibility**: The application is designed for Google Chrome. Ensure that the ChromeDriver version matches your installed Chrome browser version.
- **Selenium Limitations**: Some interactions may be limited by Selenium's capabilities. For advanced browser manipulation, additional tools or settings may be required.

## Troubleshooting

### Common Issues

- **Browser Not Launching**:
  - Ensure that Chrome is installed and the ChromeDriver version is compatible.
  - Check for exceptions or error messages in the console.

- **Proxy Not Working**:
  - Verify that the proxy details are correct and in the expected format.
  - Ensure that the proxy supports HTTP connections and that the credentials are valid.

- **Temporary Files Not Deleted**:
  - Check if the application has the necessary permissions to delete files.
  - Ensure that no other process is using the temporary files.

- **GUI Not Displaying Correctly**:
  - Ensure that all Swing components are added to the panel and frame properly.
  - Check for layout issues and adjust the layout manager if necessary.

### Logging and Debugging

- **Enable Selenium Logging**:
  - Add verbose logging to capture more details from Selenium:

    ```java
    System.setProperty("webdriver.chrome.verboseLogging", "true");
    ```

- **Print Stack Traces**:
  - Exceptions are printed to the console. Review stack traces to identify issues.

- **Use IDE Debugger**:
  - Set breakpoints and step through the code to inspect variables and application flow.

## License

This project is licensed under the [MIT License](LICENSE). You are free to use, modify, and distribute this software as per the terms of the license.

---

**Disclaimer**: This application is provided as-is without any guarantees. Use it responsibly and ensure compliance with all relevant laws and terms of service when using proxies and automating browser interactions.