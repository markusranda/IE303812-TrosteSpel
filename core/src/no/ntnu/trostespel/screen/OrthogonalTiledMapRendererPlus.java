package no.ntnu.trostespel.screen;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Projectile;

import static no.ntnu.trostespel.screen.GameplayScreen.MAP_OBJECT_ID_PLAYER;
import static no.ntnu.trostespel.screen.GameplayScreen.MAP_OBJECT_ID_PROJECTILE;


public class OrthogonalTiledMapRendererPlus extends com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer {

    public OrthogonalTiledMapRendererPlus(TiledMap map) {
        super(map);
    }

    @Override
    public void renderObject(MapObject object) {
        // TODO: 29/10/2019 Player needs to turn around
        if (object.getProperties().containsKey(MAP_OBJECT_ID_PLAYER)) {
            Player player = (Player) object.getProperties().get(MAP_OBJECT_ID_PLAYER);
            if (player.getTextureRegion() != null) {
                batch.draw(player.getTextureRegion(), player.getPos().x, player.getPos().y, player.getWidth(), player.getHeight());
            }
        }
        else if (object.getProperties().containsKey(MAP_OBJECT_ID_PROJECTILE)) {
            Projectile projectile = (Projectile) object.getProperties().get(MAP_OBJECT_ID_PROJECTILE);
            if (projectile.getTextureRegion() != null) {
                batch.draw(projectile.getTextureRegion(), projectile.getPos().x, projectile.getPos().y, projectile.getWidth(), projectile.getHeight());
            }
        }
    }
}