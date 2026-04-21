package com.cateatingpancakes.tile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class TileSet implements Iterable<Tile>, Serializable 
{
    protected ArrayList<Tile> tiles;

    /**
     * Constructs an empty tile set.
     */
    public TileSet()
    {
        this.tiles = new ArrayList<>();
    }

    /**
     * Constructs a tile set from a list of tiles.
     * @param tiles The list of tiles to construct from.
     */
    public TileSet(ArrayList<Tile> tiles)
    {
        this.tiles = new ArrayList<>(tiles);
    }

    /**
     * Copy-constructs a tile set.
     * @param tileSet The TileSet object to copy.
     */
    public TileSet(TileSet tileSet)
    {
        this(tileSet.tiles);
    }

    /**
     * Adds a tile to the set.
     * @param tile The tile to add.
     */
    public void add(Tile tile)
    {
        tiles.add(tile);
    }

    /**
     * Returns the tile at a specified index in the tile set.
     * @param index The index to acquire the tile from.
     * @return The tile at the given index.
     */
    public Tile get(int index)
    {
        return tiles.get(index);
    }

    /**
     * Removes the tile at a specified index in the tile set.
     * @param index The index to remove the tile from.
     */
    public void remove(int index)
    {
        tiles.remove(index);
    }

    /**
     * Returns the number of tiles in the tile set.
     * @return The tile count.
     */
    public int size()
    {
        return tiles.size();
    }

    /**
     * Checks if the tile set contains a specific tile using Tile's equals implementation, that is, accounting for redness.
     * @param The tile to check for.
     * @return True, if the tile set contains the given tile, or false if it does not.
     */
    public boolean contains(Tile tile)
    {
        return tiles.contains(tile);
    }

    /**
     * Sorts a tile set based on the underlying order of Tile objects, as defined by their Comparable instance.
     */
    public void sort()
    {
        tiles.sort(null);
    }

    /**
     * Shuffles the tile set. This method uses pseudo-RNG with seed defaulted to the current Unix time to shuffle.
     */
    public void shuffle()
    {
        shuffle(System.currentTimeMillis());
    }

    /**
     * Shuffles the tile set according to a given random seed.
     * @param randomSeed The seed to shuffle by.
     */
    public void shuffle(long seed)
    {
        Collections.shuffle(tiles, new Random(seed));
    }

    /**
     * Returns the unique MPSZ notation of a set of tiles.
     * @return The MPSZ notation.
     */
    public String getMPSZNotation()
    {
        if(tiles == null || tiles.isEmpty()) 
            throw new IllegalStateException("Could not get MPSZ notation of empty or null tile set");

        // Need to work on a copy of the tiles array
        // Otherwise, a print would change internal state
        ArrayList<Tile> copy = new ArrayList<>(tiles);
        copy.sort(null);

        StringBuilder MPSZ = new StringBuilder();
        // Can't simply start from "m" since a hand might not have any manzu
        // Think of a flush in pinzu, for instance, that should not contain the "m" in MPSZ
        String lastSuit = copy.get(0).getMPSZCharacter();

        for(Tile tile : copy)
        {
            String suitMPSZ = tile.getMPSZCharacter();

            // Append suit characters when the suit changes as we traverse the ArrayList
            if(!suitMPSZ.equals(lastSuit)) 
            {
                MPSZ.append(lastSuit);
                lastSuit = suitMPSZ;
            }
            
            MPSZ.append(tile.getMPSZNumber());
        }

        // Also append the last suit character
        // Since there would be no suit change to trigger the append
        MPSZ.append(lastSuit);

        return MPSZ.toString();
    }

    /**
     * Counts how many red tiles are in the tile set.
     * @return The number of red tiles in the collection.
     */
    public int countRed()
    {
        int count = 0;
        for(Tile tile : tiles)
            if(tile.isRed())
                count++;
        
        return count;
    }

    /**
     * Returns a human-readable String representation of a set of tiles. This is taken to be its MPSZ notation.
     */
    @Override
    public String toString()
    {
        return getMPSZNotation();
    }

    /**
     * Returns an iterator over the underlying ArrayList of tiles.
     */
    @Override
    public Iterator<Tile> iterator()
    {
        return tiles.iterator();
    }
}
