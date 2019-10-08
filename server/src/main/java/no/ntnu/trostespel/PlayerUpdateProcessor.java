package no.ntnu.trostespel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.concurrent.Callable;

public class PlayerUpdateProcessor implements Callable {

    private Queue<PlayerActions> actions;
    private long startTime;
    private long delta;

    private Vector2 displacement;


    public PlayerUpdateProcessor(Queue<PlayerActions> actions, long startTime) {
        this.actions = actions;
        this.startTime = startTime;
    }

    @Override
    public Boolean call() {
        displacement = new Vector2();
        delta = startTime - System.currentTimeMillis();
        PlayerActions action = actions.removeFirst();

        while (actions.notEmpty()) {
            processActionButtons(action);
            processMovement(action);
            processAttack(action);
        }

        // sendUpdate();
        return true;
    }

    private void processAttack(PlayerActions action) {
        if (action.isattackDown) {

        }
        if (action.isattackUp) {

        }
        if (action.isattackLeft) {

        }
        if (action.isattackRight) {

        }
    }

    private void processActionButtons(PlayerActions action) {
        if (action.isaction1) {

        }
        if (action.isaction2) {

        }
        if (action.isaction3) {
        }
    }

    private void processMovement(PlayerActions action) {
        if (displacement.x == 0) {
            if (action.isleft) {
                displacement.y += -GameState.playerSpeed;
            }
            if (action.isright) {
                displacement.y += GameState.playerSpeed;
            }
        }
        if (displacement.y == 0) {
            if (action.isup) {
                displacement.y += GameState.playerSpeed;
            }
            if (action.isdown) {
                displacement.y += -GameState.playerSpeed;
            }
        }
    }
}
