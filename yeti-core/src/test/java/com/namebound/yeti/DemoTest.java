package com.namebound.yeti;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class DemoTest {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
