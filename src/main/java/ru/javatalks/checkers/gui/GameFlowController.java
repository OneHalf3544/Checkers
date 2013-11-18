package ru.javatalks.checkers.gui;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.logic.ComputerLogic;
import ru.javatalks.checkers.logic.PlayerLogic;

import javax.annotation.PostConstruct;

/**
 * <p/>
 * <p/>
 * Created: 26.04.12 8:18
 * <p/>
 *
 * @author OneHalf
 */
@Service
public class GameFlowController {

    private static final Logger log = Logger.getLogger(GameFlowController.class);

    @Autowired
    private UserLogic userLogic;

    @Autowired
    private ComputerLogic computerLogic;

    private Thread thread;

    @PostConstruct
    public void run() {
        thread = new Thread(new PlayersQueueRunner(new PlayerLogic[]{userLogic, computerLogic}));
        thread.start();
    }

    public void stopThread() {
        thread.interrupt();
    }

}
