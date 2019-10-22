package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.state.PlayerState;
import no.ntnu.trostespel.state.MovableState;

import java.util.EnumSet;

public class PlayerUpdateProcessor implements Runnable {

    private PlayerActions actions;
    private long startTime;
    private long delta;
    private long pid;
    private Vector2 displacement;
    private PlayerState playerState;
    private float playerAngle;

    private int count = 0;


    private enum Direction {
        // Angle is relative to x-axis, counterclockwise
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

    /**
     * @param playerState the playerstate object that will be updated
     * @param actions     the actions to process
     * @param startTime
     */
    public PlayerUpdateProcessor(PlayerState playerState, PlayerActions actions, long startTime) {
        this.actions = actions;
        this.startTime = startTime;
        this.playerState = playerState;
        this.displacement = new Vector2(0, 0);
        this.pid = actions.pid;
    }

    @Override
    public void run() {
        pid = actions.pid;
        displacement.setZero();
        processActionButtons(actions);
        processMovement(actions);
        processAttack(actions);
    }

    private void processAttack(PlayerActions action) {
        if (playerState.getAttackTimer() <= 0) {
            MovableState projectile = new MovableState(action.pid, GameState.projectileSpeed);
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
                direction = direction / attackDir.size();
                projectile.setAngle(direction);
            } else {
                projectile.setAngle(playerAngle);
            }
            if (!attackDir.isEmpty()) {
                playerState.getSpawnedObjects().add(projectile);
                // allow attacks every 0.3 seconds
                playerState.setAttackTimer(.3 * CommunicationConfig.TICKRATE + 1);
            }
        }
        double attackTimer = playerState.getAttackTimer();
        playerState.setAttackTimer(attackTimer - 1);
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
                displacement.x += -GameState.playerSpeed;
            }
            if (action.isright) {
                displacement.x += GameState.playerSpeed;
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
        playerState.addPostion(displacement);
        playerAngle = displacement.angle();
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getPid() {
        return this.pid;
    }
}
