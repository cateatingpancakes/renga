package com.cateatingpancakes.tile;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscardPond extends TileSet
{
    protected HashMap<Integer, Integer> callers;

    /**
     * Constructs an empty discard pond.
     */
    public DiscardPond()
    {
        super();
        callers = new HashMap<>();
    }

    /**
     * Constructs a discard pond with no called tiles from a given ArrayList of tiles.
     * @param tiles The tiles to be placed in the pond.
     */
    public DiscardPond(ArrayList<Tile> tiles)
    {
        super(tiles);
        callers = new HashMap<>();
    }

    /**
     * Constructs a discard pond with no called tiles from a TileSet.
     * @param tileSet The TileSet of tiles in the pond.
     */
    public DiscardPond(TileSet tileSet)
    {
        super(tileSet);
        callers = new HashMap<>();
    }

    /**
     * Copy-constructs a discard pond.
     * @param other
     */
    public DiscardPond(DiscardPond other)
    {
        super(other.tiles);
        callers = new HashMap<>(other.callers);
    }

    /**
     * Gets the index of the last tile added to the discard pond.
     * @return The last index.
     */
    public int lastIndex()
    {
        return tiles.size() - 1;
    }

    /**
     * Checks whether the tile at a given index was called or not.
     * @param index The index of the tile within the pond.
     * @return True, if the tile at the given index was called.
     */
    public boolean isCalled(int index)
    {
        return callers.containsKey(index);
    }

    /**
     * Finds the caller of a tile at a given index, or throws an exception if that tile wasn't called.
     * @param index The index of the tile within the pond.
     * @return The player who called the tile.
     */
    public int callerOf(int index)
    {
        if(isCalled(index))
        {
            return callers.get(index);
        }
        else
        {
            throw new IllegalStateException("Could not get caller of uncalled tile at index " + index);
        }
    }

    /**
     * Register a tile as having been called by a player. Does nothing if the tile was already called.
     * @param index What tile was called.
     * @param author Who called the tile.
     */
    public void makeCall(int index, int author)
    {
        if(!isCalled(index))
        {
            callers.put(index, author);
        }
    }
}
