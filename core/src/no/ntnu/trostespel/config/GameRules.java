package no.ntnu.trostespel.config;

import com.badlogic.gdx.math.Vector2;

public class GameRules {

    public static class Player {
        public static final long RESPAWN_TIME = 3000; // millis

        private transient static final double BASE_PLAYER_SPEED = 300d;
        public transient static final double SPEED = BASE_PLAYER_SPEED / CommunicationConfig.TICKRATE;
        private final static int TEXTURE_HEIGHT_PADDING = 11;
        public final static int TEXTURE_HEIGHT = 90;
        public final static int TEXTURE_WIDTH = 72;
        public final static int HITBOX_HEIGHT = (int) (TEXTURE_HEIGHT / 2.5d);
        public final static int HITBOX_WIDTH = (TEXTURE_WIDTH / 4) * 2;
        //public final static int HEIGHT_OFFSET = ((TEXTURE_HEIGHT - ((TEXTURE_HEIGHT / 2) - HITBOX_HEIGHT / 2)));
        public final static int HEIGHT_OFFSET = TEXTURE_HEIGHT_PADDING;
        public final static int WIDTH_OFFSET = (TEXTURE_WIDTH - HITBOX_WIDTH) / 2;

    }

    public static class Projectile {
        public static final int MAX_TIME_ALIVE = 5 * CommunicationConfig.TICKRATE; // n seconds
        private static final double BASE_PROJECTILE_SPEED = 300d;
        public static final double SPEED = BASE_PROJECTILE_SPEED / CommunicationConfig.TICKRATE;
        public static final float SPAWN_POINT_Y_OFFSET = Player.HITBOX_HEIGHT;
        public static final float SPAWN_POINT_x_OFFSET = Player.TEXTURE_WIDTH / 2f;
        public static final Vector2 SPAWN_OFFSET = new Vector2(GameRules.Projectile.SPAWN_POINT_x_OFFSET, GameRules.Projectile.SPAWN_POINT_Y_OFFSET);
    }
}
