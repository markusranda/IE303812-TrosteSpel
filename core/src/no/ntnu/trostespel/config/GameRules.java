package no.ntnu.trostespel.config;

public class GameRules {

    public class Player {
        public static final long RESPAWN_TIME = 3000; // millis

        private transient static final double BASE_PLAYER_SPEED = 300d;
        public transient static final double SPEED = BASE_PLAYER_SPEED / CommunicationConfig.TICKRATE;


    }

    public class Projectile {
        public static final int MAX_TIME_ALIVE = 5 * CommunicationConfig.TICKRATE; // n seconds
        private static final double BASE_PROJECTILE_SPEED = 300d;
        public static final double SPEED = BASE_PROJECTILE_SPEED / CommunicationConfig.TICKRATE;
    }
}
