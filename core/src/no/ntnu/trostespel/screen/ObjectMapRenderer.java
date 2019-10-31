package no.ntnu.trostespel.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Projectile;

import static no.ntnu.trostespel.screen.GameplayScreen.MAP_OBJECT_ID_PLAYER;
import static no.ntnu.trostespel.screen.GameplayScreen.MAP_OBJECT_ID_PROJECTILE;


public class ObjectMapRenderer extends com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer {

    public ObjectMapRenderer(TiledMap map) {
        super(map);
    }

    /**
     * Renders all objects on the map
     *
     * @param object The object to be rendered
     */
    @Override
    public void renderObject(MapObject object) {
        // TODO: 29/10/2019 Player needs to turn around
        if (object.getProperties().containsKey(MAP_OBJECT_ID_PLAYER)) {
            Player player = (Player) object.getProperties().get(MAP_OBJECT_ID_PLAYER);
            if (player.getTexture() != null) {
                if (player.isMoving()) {
                    player.animateWalking();
                    batch.draw(player.getCurrentframe(),
                            player.getFlip() ? player.getPos().x + player.getWidth() : player.getPos().x,
                            player.getPos().y,
                            player.getFlip() ? -player.getWidth() : player.getWidth(),
                            player.getHeight());
                } else {
                    batch.draw(player.getTexture(),
                            player.getFlip() ? player.getPos().x + player.getWidth() : player.getPos().x,
                            player.getPos().y,
                            player.getFlip() ? -player.getWidth() : player.getWidth(),
                            player.getHeight());
                }
                if (player.isAttacking()) {
                    if (player.getAttackStateTime() > 0.15f) {
                        player.setAttacking(false);
                        player.resetAttackStateTime();
                    } else {
                        batch.draw(Assets.attack,
                                player.getFlip() ? player.getPos().x + player.getWidth() : player.getPos().x,
                                player.getPos().y,
                                player.getFlip() ? -player.getWidth() : player.getWidth(),
                                player.getHeight());
                    }
                }
            }
        } else if (object.getProperties().containsKey(MAP_OBJECT_ID_PROJECTILE)) {
            Projectile projectile = (Projectile) object.getProperties().get(MAP_OBJECT_ID_PROJECTILE);
            if (projectile.getTextureRegion() != null) {
                batch.draw(projectile.getTextureRegion(),
                        projectile.getPos().x,
                        projectile.getPos().y,
                        projectile.getWidth(),
                        projectile.getHeight());
            }
        }
    }
}