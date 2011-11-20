package ru.javatalks.checkers.gui.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.gui.L10nBundle;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Date: 12.11.11
 * Time: 21:13
 *
 * @author OneHalf
 */
@Component
public class ExitAction extends AbstractAction {

    @Autowired
    private L10nBundle bundle;

    @PostConstruct
    public void initialise() {
        putValue(NAME, bundle.getString("exitTitle"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
