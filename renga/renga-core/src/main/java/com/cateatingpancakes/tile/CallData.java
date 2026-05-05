package com.cateatingpancakes.tile;

import java.io.Serializable;

public record CallData(Tile tile, int source) implements Serializable
{
    /**
     * Copy-constructs call data.
     * @param other The call data to copy.
     */
    public CallData(CallData other)
    {
        this(other.tile, other.source);
    }
}
