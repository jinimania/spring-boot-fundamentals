package org.example.ws.model;

import java.math.BigInteger;

/**
 * @author LeeSoohoon
 */
public class Greeting {

    private BigInteger id;

    private String text;

    public Greeting() {
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(final BigInteger id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
