package com.cateatingpancakes.tile;

import java.io.Serializable;

public record CallData(Tile tile, int source) implements Serializable
{
    
}
