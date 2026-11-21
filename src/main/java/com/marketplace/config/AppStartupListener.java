
package com.marketplace.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Initializes the marketplace database when the web application starts.
 */
@WebListener
public class AppStartupListener implements ServletContextListener {

    /**
     * Runs database initialization when the application context starts.
     * @param event the event
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Marketplace application starting...");
        DatabaseInitializer.initialize();
    }

    /**
     * Handles application context shutdown.
     * @param event the event
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Marketplace application stopping...");
    }
}
