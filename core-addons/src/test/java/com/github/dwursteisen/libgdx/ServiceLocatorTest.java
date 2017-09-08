package com.github.dwursteisen.libgdx;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class ServiceLocatorTest {

    @Test
    public void register() {
        Random random = new Random();

        ServiceLocator.register(random, Random.class);
        Random fromServiceLocator = ServiceLocator.INSTANCE.get(Random.class);

        Assert.assertSame(random, fromServiceLocator);

    }

}
