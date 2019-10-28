package no.ntnu.trostespel.screen;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import no.ntnu.trostespel.entity.Player;


public class OrthogonalTiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {

    public OrthogonalTiledMapRendererWithSprites(TiledMap map) {
        super(map);
    }

    @Override
    public void renderObject(MapObject object) {
        Player player = (Player) object.getProperties().get("player");
        if (player.getTextureRegion() != null) {
            batch.draw(player.getTextureRegion(), player.getPos().x, player.getPos().y, player.getWidth(), player.getHeight());
        }
    }
}