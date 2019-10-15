package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.GameState;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.state.PlayerState;
import no.ntnu.trostespel.state.MovableState;

import java.util.EnumSet;
import java.util.concurrent.Callable;

public class PlayerUpdateProcessor implements Callable {

    private PlayerActions actions;
    private long startTime;
    private long delta;
    private long pid;
    private Vector2 displacement;
    private PlayerState playerState;
    private float playerAngle;


    private enum Direction {
        UP(90),
        RIGHT(0),
        DOWN(-90),
        LEFT(180);
        private int dir;

        Direction(int i) {
            this.dir = i;
        }

        public int value() {
            return this.dir;
        }
    }

    public PlayerUpdateProcessor(PlayerState state, PlayerActions actions, long startTime) {
        this.actions = actions;
        this.startTime = startTime;
        this.playerState = state;
    }

    @Override
    public PlayerState call() {
        displacement = new Vector2();
        delta = startTime - System.currentTimeMillis();
        pid = actions.pid;
        processActionButtons(actions);
        processMovement(actions);
        processAttack(actions);

        return playerState;
    }

    private void processAttack(PlayerActions action) {
        if (playerState.getAttackTimer() <= 0) {
            MovableState projectile = new MovableState();
            EnumSet<Direction> attackDir = EnumSet.noneOf(Direction.class);
            if (action.isattackDown) {
                attackDir.add(Direction.DOWN);
            }
            if (action.isattackUp) {
                attackDir.add(Direction.UP);
            }
            if (action.isattackLeft) {
                attackDir.add(Direction.LEFT);
            }
            if (action.isattackRight) {
                attackDir.add(Direction.RIGHT);
            }
            if (attackDir.size() <= 2) {
                float direction = 0;
                for (Direction dir : attackDir) {
                    direction += dir.value();
                }
                direction = direction / 2;
                projectile.setAngle(direction);
            } else {
                projectile.setAngle(playerAngle);
            }
            if (!attackDir.isEmpty()) {
                playerState.getSpawnedObjects().put(projectile.getId(), projectile);
                playerState.setAttackTimer(0);
            }
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
        PlayerState oldState = playerState;
        playerState.addPostion(displacement);
        playerAngle = oldState.getPosition().angle(playerState.getPosition());
    }
}
