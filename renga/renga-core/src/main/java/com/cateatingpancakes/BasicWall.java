package com.cateatingpancakes;

import com.cateatingpancakes.wall.WallBuilder;

public class BasicWall extends TileSet 
{
    /**
     * Builds a wall given a WallBuilder strategy whose buildWall() will be called to provide the tiles.
     * @param strategy The WallBuilder to use.
     */
    BasicWall(WallBuilder strategy)
    {
        super(strategy.buildWall());
    }
}
