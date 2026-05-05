package com.cateatingpancakes.tile;

import java.io.Serializable;

public final class TileMeld implements Comparable<TileMeld>, Serializable
{
    public static enum MeldType
    {
        CHII, PON, KAN_CLOSED, KAN_OPEN, KAN_ADDED
        // See: https://riichi.wiki/Kan
    }

    private MeldType meldType;
    private TileSet  tiles;
    private Tile     leadingTile;
    private CallData callData;

    /**
     * Constructs a tile meld from the set of component tiles.
     * @param meldType The type of the meld.
     * @param tiles A non-null, non-empty ArrayList of of tiles participating in the meld.
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
    public TileMeld(TileMeld other)
    {
        this(other.meldType, other.tiles);
    }

    /**
     * Returns the type of the tile meld.
     * @return The meld type.
     */
    public MeldType getType()
    {
        return meldType;
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
     * Counts how many tiles with a given trait are in the meld.
     * @param trait The trait to check for.
     * @return The number of tiles with the trait.
     */
    public int countAllTrait(TileTrait trait)
    {
        return tiles.countAllTrait(trait);
    }

    /**
     * Sets information about the call used to form the meld.
     * @param data The call data.
     */
    public void setData(CallData data)
    {
        this.callData = data;
    }

    /**
     * Gets information about the call used to form the meld if it exists.
     * @return The call data, or null if the meld is a closed kan or has no registered call data. Note that it is not
     * necessarily indicative of some error if the latter case occurs, as TileMelds other than ankans with no call data
     * can be validly produced by HandSplitter.interpret() representing melds present in the closed/hidden part of the hand.
     */
    public CallData getData()
    {
        return callData;
    }

    /**
     * Checks whether the meld has call data.
     * @return True, if call data exists.
     */
    public boolean hasData()
    {
        return (callData != null);
    }

    /**
     * Upgrades a pon into a shouminkan.
     * @param addedTile The tile to be added to the meld.
     * @throws IllegalMeldUpgrade If the meld is not a pon that can be upgraded into shouminkan with the added tile, this exception is thrown.
     */
    public void upgradePonIntoKan(Tile addedTile)
    {
        if(meldType != MeldType.PON)
        {
            throw new IllegalMeldUpgrade("Could not upgrade meld of type " + meldType);
        }

        if(addedTile.toIndex() != leadingTile.toIndex())
        {
            throw new IllegalMeldUpgrade("Could not upgrade pon of " + leadingTile + " with " + addedTile);
        }

        // Change the meld type, it's not a pon anymore.
        meldType = MeldType.KAN_ADDED;

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
