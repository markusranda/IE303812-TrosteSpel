package no.ntnu.trostespel.controller;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.KeyConfig;

import java.security.Key;

public abstract class ObjectController {
    public float x = 0;
    public float y = 0;
    public Vector2 displacement;

    public abstract Vector2 update(float delta);
}
