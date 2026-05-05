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
    public TileSet(TileSet other)
    {
        this(other.tiles);
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
     * Creates a new TileSet with an additional tile added.
     * @param tile The tile to add.
     * @return The new TileSet.
     */
    public TileSet addNew(Tile tile)
    {
        TileSet newSet = new TileSet(tiles);
        newSet.add(tile);
        return newSet;
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
     * @param index The index of the tile to be removed.
     */
    public Tile remove(int index)
    {
        return tiles.remove(index);
    }

    /**
     * Removes the first occurence of a given tile from the tile set.
     * @param tile The tile to remove.
     */
    public void remove(Tile tile)
    {
        tiles.remove(tile);
    }

    /**
     * Creates a new TileSet with a tile removed from a certain index.
     * @param index The index of the tile to be removed.
     * @return A TileSet without the tile at that index.
     */
    public TileSet removeNew(int index)
    {
        TileSet newSet = new TileSet(tiles);
        newSet.remove(index);
        return newSet;
    }

    /**
     * Creates a new TileSet with the first occurence of a certain tile removed.
     * @param tile The tile to remove.
     * @return A TileSet without the first occurence of the given tile.
     */
    public TileSet removeNew(Tile tile)
    {
        TileSet newSet = new TileSet(tiles);
        newSet.remove(tile);
        return newSet;
    }

    /**
     * Returns the index at which a given tile first occurs in the tile set.
     * @param tile The tile to search for.
     * @return The first index of the tile, or -1 if the tile doesn't exist.
     */
    public int find(Tile tile)
    {
        for(int i = 0; i < tiles.size(); i++)
            if(tiles.get(i).equals(tile))
                return i;
        
        return -1;
    }

    /**
     * Returns the index at which a tile with a given index number first occurs in the tile set.
     * @param index The index number of the tile to search for.
     * @return The first index of the tile, or -1 if the tile doesn't exist.
     */
    public int find(int index)
    {
        for(int i = 0; i < tiles.size(); i++)
            if(tiles.get(i).toIndex() == index)
                return i;

        return -1;
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
     * Checks if the tile set contains a specific tile using Tile's equals implementation, that is, accounting for traits.
     * @param tile The tile to check for.
     * @return True, if the tile set contains the given tile, or false if it does not.
     */
    public boolean contains(Tile tile)
    {
        return tiles.contains(tile);
    }

    /**
     * Checks if the tile set contains a specific index number, that is, ignoring traits.
     * @param index The index number of the tile to check for.
     * @return True, if the tile set contains a tile with the given index number.
     */
    public boolean contains(int index)
    {
        for(Tile tile : tiles)
            if(tile.toIndex() == index)
                return true;

        return false;
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
    // TODO: Make an interface like MPSZNotable for this maybe?
    public String getMPSZNotation()
    {
        if(tiles == null || tiles.isEmpty()) 
            throw new IllegalStateException("Could not get MPSZ notation of empty or null tile set");

        // Need to work on a copy of the tiles array.
        // Otherwise, a print would change internal state.
        ArrayList<Tile> copy = new ArrayList<>(tiles);
        copy.sort(null);

        StringBuilder MPSZ = new StringBuilder();
        // Can't simply start from "m" since a hand might not have any manzu!
        // Think of a flush in pinzu, for instance, that should not contain the "m" in MPSZ.
        String lastSuit = copy.get(0).getMPSZCharacter();

        for(Tile tile : copy)
        {
            String suitMPSZ = tile.getMPSZCharacter();

            // Append suit characters when the suit changes as we traverse the ArrayList.
            if(!suitMPSZ.equals(lastSuit)) 
            {
                MPSZ.append(lastSuit);
                lastSuit = suitMPSZ;
            }
            
            MPSZ.append(tile.getMPSZNumber());
        }

        // Also append the last suit character,
        // since there would be no suit change to trigger the append.
        MPSZ.append(lastSuit);

        return MPSZ.toString();
    }

    /**
     * Counts how many tiles with a given trait are in the tile set.
     * @param trait The trait to check for.
     * @return The number of tiles with the given trait.
     */
    public int countAllTrait(TileTrait trait)
    {
        int count = 0;
        for(Tile tile : tiles)
            if(tile.hasTrait(trait))
                count++;
        
        return count;
    }

    /**
     * Returns how many tiles have a given index number in the set.
     * @param index The index number.
     * @return The count.
     */
    public int countOf(int index)
    {
        int count = 0;
        for(Tile tile : tiles)
            if(tile.toIndex() == index)
                count++;

        return count;
    }

    /**
     * Returns how many tiles are equals(target) in the set.
     * @param target The target tile.
     * @return The count of equal tiles.
     */
    public int countOf(Tile target)
    {
        int count = 0;
        for(Tile tile : tiles)
            if(tile.equals(target))
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
