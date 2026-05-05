package com.cateatingpancakes.state;

import com.cateatingpancakes.hand.BasicHand;
import com.cateatingpancakes.tile.DiscardPond;
import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileSet;
import com.cateatingpancakes.wall.BasicWall;

public abstract class RiichiState extends GameState
{
    /**
     * The standard hand size of a Riichi hand.
     */
    public static final int HAND_SIZE = 13;
    
    /**
     * How many tiles are each reserved for the top and bottom of the dead wall.
     */
    public static final int DEAD_WALL_RESERVED = 5;

    protected TileSet deadWallUp;
    protected TileSet deadWallDn;
    protected int     kans;

    /**
     * Constructs an empty Riichi game state.
     */
    public RiichiState()
    {
        super();
    }

    /**
     * Constructs a Riichi game state from a given wall.
     * @param wall The wall to use.
     */
    public RiichiState(BasicWall wall)
    {
        super(wall);

        this.kans = 0;
        this.moment = new GameMoment(1, 0, GameWind.EAST);

        this.deadWallDn = new TileSet();
        this.deadWallUp = new TileSet();
        
        for(int i = 0; i < DEAD_WALL_RESERVED; i++)
        {
            deadWallUp.add(this.wall.drawTop());
            deadWallDn.add(this.wall.drawTop());
        }
    }

    /**
     * Internal method for use in constructors. Initializes the game for a given count of players.
     * @param count The number of players.
     */
    protected void addPlayers(int count)
    {
        for(int i = 0; i < count; i++)
        {
            this.ponds.add(new DiscardPond());
            this.hands.add(new BasicHand(this.wall, HAND_SIZE));
        }

        this.hands.get(0).add(this.wall.drawTop());
    }

    /**
     * Get a reference to the top/visible side of the dead wall.
     * @return The visible dead wall.
     */
    public TileSet directDeadWallTop()
    {
        return deadWallUp;
    }

    /**
     * Get a reference to the bottom/hidden side of the dead wall.
     * @return The hidden dead wall.
     */
    public TileSet directDeadWallBottom()
    {
        return deadWallDn;
    }

    @Override
    public boolean isExhausted()
    {
        return (wall.size() <= 4);
    }

    /**
     * Returns the next tile, as is relevant for dora indicators and dora.
     * @param tile The current tile.
     * @return The index number of the next tile,
     */
    public abstract int nextTile(Tile tile);
}
