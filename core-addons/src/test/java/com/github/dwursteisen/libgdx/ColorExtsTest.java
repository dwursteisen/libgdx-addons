package com.github.dwursteisen.libgdx;

import com.badlogic.gdx.graphics.Color;
import org.junit.Assert;
import org.junit.Test;

public class ColorExtsTest {

    @Test
    public void toColor() {
        Color color = ColorExtsKt.toColor("#FFFFFF");
        Assert.assertEquals(color, Color.WHITE);
    }
}
