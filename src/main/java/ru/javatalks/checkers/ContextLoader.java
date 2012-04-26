package ru.javatalks.checkers;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class for start initializing game with spring
 *
 * Date: 14.11.11
 * Time: 21:00
 *
 * @author OneHalf
 */
public class ContextLoader {

    private ContextLoader() {
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:context.xml");
        context.registerShutdownHook();
    }
}
