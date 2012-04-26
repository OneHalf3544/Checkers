package ru.javatalks.checkers.gui;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.logic.CheckerStepException;
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

    private volatile PlayerLogic[] players;

    @Autowired
    private UserLogic userLogic;

    @Autowired
    private ComputerLogic computerLogic;

    private Thread thread;

    @PostConstruct
    public void run() {
        run(new PlayerLogic[]{userLogic, computerLogic});
    }

    public void run(PlayerLogic[] playerLogics) {
        this.players = new PlayerLogic[]{userLogic, computerLogic};
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }

                    PlayerLogic currentPlayer = players[i];
                    log.info("step by " + currentPlayer);
                    makeStep(currentPlayer);
                    i = 1 - i;
                }
            }

            private void makeStep(PlayerLogic currentPlayer) {
                boolean fail;
                do {
                    try {
                        currentPlayer.doStep();
                        fail = false;
                    } catch (CheckerStepException e) {
                        log.info(e);
                        fail = true;
                    }
                } while (fail);
            }
        });
        thread.start();
    }

    public void stopThread() {
        thread.interrupt();
    }
}
