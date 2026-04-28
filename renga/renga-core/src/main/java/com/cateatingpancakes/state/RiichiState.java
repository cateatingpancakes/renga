package com.cateatingpancakes.state;

import com.cateatingpancakes.tile.TileSet;
import com.cateatingpancakes.wall.BasicWall;

public abstract class RiichiState extends GameState
{
    protected TileSet deadWallTop;
    protected TileSet deadWallBottom;
    protected int     kanCount;

    /**
     * Constructs an empty Riichi game state.
     */
    public RiichiState()
    {
        super();
    }

    /**
     * Constructs a Riichi game state from a given wall.
     * @param wall
     */
    public RiichiState(BasicWall wall)
    {
        super(wall);
    }

    @Override
    public boolean drawExhausted()
    {
        return (wall.size() <= 4);
    }
}
