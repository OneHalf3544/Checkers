package ru.javatalks.checkers.logic;

import ru.javatalks.checkers.model.Player;

/**
 * Date: 19.11.11
 * Time: 12:14
 *
 * @author OneHalf
 */
public class CheckerStepException extends Exception {

    public enum Cause {
        MUST_FIGHT("user has fighter checker and must do step by it"),
        DO_NOT_HAS_STEP("opponent don't has possible step"),
        DO_NOT_HAS_CHECKERS("does not has checkers"),
        WRONG_STEP("can't do this step");

        private final String message;

        Cause(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("Cause{message='%s\'}", message);
        }
    }

    private final Player player;
    private final Cause cause;
    
    public CheckerStepException(Player player, Cause cause) {
        super(cause.toString());
        this.player = player;
        this.cause = cause;
    }

    public Cause getCauseOfError() {
        return cause;
    }
}
