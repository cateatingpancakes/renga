package com.cateatingpancakes.tile;

import java.io.Serializable;

public final class TileMeld implements Comparable<TileMeld>, Serializable
{
    public static enum MeldType
    {
        CHII,
        PON,
        KAN_C, // Closed kan
        KAN_O, // Open kan
        KAN_A  // Added/upgraded kan
               // See: https://riichi.wiki/Kan for an explanation of kan types
    }

    private MeldType meldType;
    private TileSet  tiles;
    private Tile     leadingTile;

    /**
     * Constructs a tile meld from the set of component tiles.
     * @param meldType The type of the meld.
     * @param tiles An ArrayList of of tiles participating in the meld.
     */
    public TileMeld(MeldType meldType, TileSet tiles)
    {
        TileSet meldTiles = new TileSet(tiles);
        meldTiles.sort();

        this.meldType    = meldType;
        this.tiles       = meldTiles;
        this.leadingTile = meldTiles.get(0);
    }

    /**
     * Copy-constructs a tile meld.
     * @param tileMeld The tile meld to copy.
     */
    public TileMeld(TileMeld tileMeld)
    {
        this(tileMeld.meldType, tileMeld.tiles);
    }

    /**
     * Returns the leading tile of the meld. In the case of Chii/sequence melds, this is taken to be the lowest-numbered tile from among them.
     * As such, only 7 possible Chii melds exist within each sequence of tiles. In the case of Pon/Kan melds made up of multiple copies of identical
     * tiles, the leading tile is any of the multiple identical tiles in the meld.
     * @return
     */
    public Tile getLeadingTile()
    {
        return leadingTile;
    }

    /**
     * Counts how many red tiles are in the meld.
     * @return The number of red tiles in the meld.
     */
    public int countRed()
    {
        return tiles.countRed();
    }

    /**
     * Upgrades a pon into a shouminkan.
     * @param addedTile The tile to be added to the meld.
     * @throws IllegalMeldUpgrade If the meld is not a pon that can be upgraded into shouminkan with the added tile, this exception is thrown.
     */
    public void upgradePonIntoKan(Tile addedTile)
    {
        if(meldType != MeldType.PON)
            throw new IllegalMeldUpgrade("Could not upgrade meld of type " + meldType);

        if(addedTile.toIndex() != leadingTile.toIndex())
            throw new IllegalMeldUpgrade("Could not upgrade pon of " + leadingTile + " into shouminkan with " + addedTile);

        meldType = MeldType.KAN_A;
        tiles.add(addedTile);
    }

    /**
     * Returns a human-readable String representation of a meld. This is taken to be the entire meld, in MPSZ notation.
     * @return The String representation.
     */
    @Override
    public String toString()
    {
        return tiles.getMPSZNotation();
    }

    /**
     * Compares a tile meld to another based on the relative order of their leading tiles.
     */
    @Override
    public int compareTo(TileMeld other)
    {
        Tile thisTile  = leadingTile, 
             otherTile = other.leadingTile;

        return thisTile.compareTo(otherTile);
    }

}
