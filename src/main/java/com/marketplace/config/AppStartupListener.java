
package com.marketplace.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Marketplace application starting...");
        DatabaseInitializer.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Marketplace application stopping...");
    }
}
