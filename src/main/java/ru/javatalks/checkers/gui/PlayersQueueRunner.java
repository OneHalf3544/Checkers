package ru.javatalks.checkers.gui;

import org.apache.log4j.Logger;
import ru.javatalks.checkers.logic.CheckerStepException;
import ru.javatalks.checkers.logic.PlayerLogic;

/**
* <p/>
* <p/>
* Created: 29.04.12 13:44
* <p/>
*
* @author OneHalf
*/
class PlayersQueueRunner implements Runnable {

    private PlayerLogic[] players;
    private static final Logger log = Logger.getLogger(PlayersQueueRunner.class);

    PlayersQueueRunner(PlayerLogic[] players) {
        this.players = players;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            PlayerLogic currentPlayer = players[i];
            log.debug("step by " + currentPlayer);
            makeStep(currentPlayer);
            i = 1 - i;
        }
    }

    private void makeStep(PlayerLogic currentPlayer) {
        while (true) {
            try {
                currentPlayer.doStep();
                break;
            } catch (CheckerStepException e) {
                log.info(e);
            }
        }
    }
}
