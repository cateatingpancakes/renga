package com.cateatingpancakes;

import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileSet;
import com.cateatingpancakes.wall.WallBuilder;

public class BasicWall extends TileSet 
{
    /**
     * Builds a wall given a WallBuilder strategy whose buildWall() method will be called to provide the tiles.
     * @param strategy The WallBuilder to use.
     */
    public BasicWall(WallBuilder strategy)
    {
        super(strategy.buildWall());
        shuffle();
    }

    /**
     * Builds a wall given a WallBuilder strategy and a seed to shuffle the tiles afterward. If this constructor is called
     * multiple times with the same seed and strategy, it will produce walls identical in composition and order of draws.
     * @param strategy The WallBuilder to use.
     * @param randomSeed The seed to call shuffle() with.
     */
    public BasicWall(WallBuilder strategy, long seed)
    {
        super(strategy.buildWall());
        shuffle(seed);
    }

    /**
     * Copy-constructs a wall.
     * @param other The wall to copy.
     */
    public BasicWall(BasicWall other)
    {
        super(other.tiles);
    }

    /**
     * Returns the tile that will be removed and returned by the next call to drawTop().
     * @return The next tile to be drawn.
     */
    public Tile peekTop()
    {
        return tiles.getLast();
    }

    /**
     * Removes the top tile of the wall and returns it.
     * @return The drawn tile.
     */
    public Tile drawTop()
    {
        if(tiles.isEmpty())
        {
            throw new IllegalStateException("Could not draw from empty wall");
        }

        return tiles.remove(tiles.size() - 1);
    }
}
