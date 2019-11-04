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
        if (object.getProperties().containsKey(MAP_OBJECT_ID_PLAYER)) {
            Player player = (Player) object.getProperties().get(MAP_OBJECT_ID_PLAYER);
            if (player.getTexture() != null) {
                player.draw(batch);
            }
        }
        if (object.getProperties().containsKey(MAP_OBJECT_ID_PROJECTILE)) {
            Projectile projectile = (Projectile) object.getProperties().get(MAP_OBJECT_ID_PROJECTILE);
            if (projectile.getTextureRegion() != null) {
                projectile.draw(batch);
            }
        }
    }

    public void begin() {
        this.beginRender();
    }

    public void end() {
        this.endRender();
    }
}