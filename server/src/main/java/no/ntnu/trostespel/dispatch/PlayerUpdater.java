package no.ntnu.trostespel.dispatch;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.config.GameRules;
import no.ntnu.trostespel.game.GameStateMaster;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.ConcurrentModificationException;
import java.util.EnumSet;

public class PlayerUpdater {

    private GameState<PlayerState, MovableState> gameState;
    private long pid;
    private Vector2 displacement;
    private PlayerState playerState;
    private float playerAngle;

    private int count = 0;

    private short shouldflipCounter = 0;


    private enum Direction {
        // Angle is relative to x-axis, counterclockwise
        UP(90),
        RIGHT(0),
        DOWN(270),
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
     * @param gameState the playerstate object that will be updated
     */
    public PlayerUpdater(GameState<PlayerState, MovableState> gameState) {
        this.gameState = gameState;
        this.displacement = new Vector2(0, 0);

        // this.playerState = playerState;
        // this.actions = actions;
        // this.pid = actions.pid;

    }

    public void run(PlayerActions actions) {

        // reset old values
        this.count = 0;
        this.shouldflipCounter = 0;
        this.playerAngle = 0;
        this.displacement = new Vector2(0, 0);
        pid = actions.pid;

        // perform update
        this.playerState = gameState.getPlayers().getOrDefault(pid, null);
        if (playerState != null) {
            if (!playerState.isDead()) {
                addUsername(actions);
                processActionButtons(actions);
                processMovement(actions);
                processAttack(actions);
            }
        } else {
            throw new ConcurrentModificationException("Player no longer exists in gamestate");
        }
    }

    private void addUsername(PlayerActions actions) {
        String username = "";
        for (Connection connection : Connections.getInstance().getConnections()) {
            if (connection.getPid() == actions.pid) {
                username = connection.getUsername();
                break;
            }
        }
        playerState.setUsername(username);
    }

    private void processAttack(PlayerActions action) {
        if (playerState.getAttackTimer() <= 0 &&
                (action.isattackDown || action.isattackUp || action.isattackLeft || action.isattackRight)) {
//            MovableState projectile = new MovableState(action.pid, GameRules.Projectile.SPEED);
            MovableState projectile = GameStateMaster.getInstance().getMovablesPool().borrowObject();
            projectile.setPid(action.pid);

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

            // calculate angle of the bullet
            float direction = 0;
            if (attackDir.size() <= 2) {
                for (Direction dir : attackDir) {
                    direction += dir.value();
                    if (dir == Direction.RIGHT || dir == Direction.DOWN) {
                        // fix edge case
                        shouldflipCounter++;
                    }
                }
                direction = direction / attackDir.size();
                if(shouldflipCounter == 2) {
                    // fix edge case
                    direction += 180;
                }
                projectile.setAngle(direction);
            } else {
                projectile.setAngle(playerAngle);
            }
            // check if player and bullet is moving in the same direction
            if (!attackDir.isEmpty()) {
                double playerbulletangle = Math.abs(playerAngle - direction);
                if (playerbulletangle <= 90 || playerbulletangle >= 270) {
                    // apply players velocity to bullet
                    Vector2 heading = projectile.getHeading();
                    heading.add(displacement);
                    projectile.setHeading(heading);
                }
                // add resulting projectile to spawned objects list
                projectile.setPositionWithSpawnOffset(playerState.getPosition());
                putProjectile(projectile.getId(), projectile);

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
        if (action.isleft) {
            displacement.x += -GameRules.Player.SPEED;
        }
        if (action.isright) {
            displacement.x += GameRules.Player.SPEED;
        }

        if (action.isup) {
            displacement.y += GameRules.Player.SPEED;
        }
        if (action.isdown) {
            displacement.y += -GameRules.Player.SPEED;
        }
        if (!displacement.isZero()) {
            Vector2 pos = playerState.getPosition();
            pos.x += displacement.x;
            pos.y += displacement.y;
            playerState.setPosition(pos);
        }
        playerAngle = displacement.angle();
    }

    private void putProjectile ( long k, MovableState v){
        gameState.getProjectileEvents().add(v);
        gameState.getProjectiles().put(k, v);
    }

    public long getPid() {
        return this.pid;
    }
}
