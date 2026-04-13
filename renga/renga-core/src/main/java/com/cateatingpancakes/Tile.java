package com.cateatingpancakes;

import java.io.Serializable;

public final class Tile implements Comparable<Tile>, Serializable
{
    public static enum TileType 
    {
        MANZU,
        PINZU,
        SOUZU,
        HONOR,
        FLOWER // For completeness, but this is only used in hana mahjong (which is not implemented)
    }

    private final TileType tileType;
    private final int      number;
    private final boolean  isRed;

    /**
     * Returns the 1-character String uniquely associated with a given tile suit in MPSZ notation.
     * @param tileType The type/suit of the tile.
     * @return The corresponding String.
     */
    static String getMPSZCharacter(TileType tileType)
    {
        switch(tileType)
        {
            case MANZU -> {
                return "m";
            }
            case PINZU -> {
                return "p";
            }
            case SOUZU -> {
                return "s";
            }
            case HONOR -> {
                return "z";
            }
            case FLOWER -> {
                return "f";
            }
            default -> throw new AssertionError("Could not resolve tile type " + tileType);
        }
    }

    /**
     * Returns the 1-character String uniquely associated with the suit of this tile in MPSZ notation.
     * @return The corresponding String.
     */
    public String getMPSZCharacter()
    {
        return Tile.getMPSZCharacter(tileType);
    }

    /**
     * Constructs a tile based on its index number. The tile is defaulted to be non-red.
     * @param tileIndex The tile index.
     */
    public Tile(int tileIndex)
    {
        this(tileIndex, false);
    }

    /**
     * Constructs a tile based on its index number and redness.
     * @param tileIndex The tile index.
     * @param isRed Redness of the tile.
     */
    public Tile(int tileIndex, boolean isRed)
    {
        Tile baseTile = Tile.fromIndex(tileIndex);
        this(baseTile.tileType, baseTile.number, isRed);
    }

    /**
     * Constructs a tile based on its type and number. The tile is defaulted to be non-red.
     * @param tileType The type/suit of the tile.
     * @param number The number of the tile within its suit, between 0 and SUIT_MAX - 1.
     */
    public Tile(TileType tileType, int number) 
    {
        this(tileType, number, false);
    }

    /**
     * Constructs a tile given its complete description in suit, rank and redness.
     * @param tileType The type/suit of the tile.
     * @param number The number of the tile within its suit, between 0 and SUIT_MAX - 1.
     * @param isRed Redness of the tile.
     */
    public Tile(TileType tileType, int number, boolean isRed) 
    {
        this.tileType = tileType;
        this.number   = number;
        this.isRed    = isRed;
    }

    /**
     * Copy-constructs a tile.
     * @param tile The tile to copy.
     */
    public Tile(Tile tile)
    {
        this.tileType = tile.tileType;
        this.number   = tile.number;
        this.isRed    = tile.isRed;
    }

    /**
     * Converts the tile to an index number, ignoring redness.
     * This is most useful for algorithmic purposes, such as calculating how many tiles a hand is away from being ready.
     * @return The tile index number.
     */
    public int toIndex() 
    {
        int index = 0;

        switch(tileType)
        {
            // Suit ranges listed (commented) below
            case MANZU -> 
                // 0-8
                index = 0;
            case PINZU -> 
                // 9-17
                index = 9;
            case SOUZU -> 
                // 18-26
                index = 18;
            case HONOR -> 
                // 27-33
                index = 27;
            case FLOWER -> 
                // 34-37
                index = 34;
            default -> throw new AssertionError("Could not resolve tile type " + tileType);
        }

        index += number;
        return index;
    }

    /**
     * Converts an index number into a non-red Tile object of that index number.
     * @param index The index number of the tile.
     * @return The Tile object.
     */
    public static Tile fromIndex(int index)
    {
        TileType tileType = null;
        int number = 0;

        if(0 <= index && index <= 8)
        {
            tileType = TileType.MANZU;
            number = index;
        }
        else if(9 <= index && index <= 17)
        {
            tileType = TileType.PINZU;
            number = index - 9;
        }
        else if(18 <= index && index <= 26)
        {
            tileType = TileType.SOUZU;
            number = index - 18;
        }
        else if(27 <= index && index <= 33)
        {
            tileType = TileType.HONOR;
            number = index - 27;
        }
        else if(34 <= index && index <= 37)
        {
            tileType = TileType.FLOWER;
            number = index - 34;
        }
        else
            throw new IllegalArgumentException("Could not resolve index number " + index);

        return new Tile(tileType, number);
    }

    /**
     * Returns the MPSZ number of a tile as a String of exactly 1 character.
     * This method assumes the convention that red tiles are always unique in their suit and assigns 0 to those tiles.
     * Do not confuse this result with index number of the tile, which is mainly for internal use, and ignores redness.
     * @return The MPSZ number.
     */
    public String getMPSZNumber()
    {
        if(isRed)
            return "0";
        else
            // We have to transform the internal representation range [0, SUIT_SIZE - 1] 
            // into the more human-readable (MPSZ) [1, SUIT_SIZE] for tiles that aren't red
            return Integer.toString(number + 1);
    }

    /**
     * Returns the unique MPSZ notation of a tile as a 2-character String.
     * @return The MPSZ notation.
     */
    public String getMPSZNotation()
    {
        return getMPSZNumber() + Tile.getMPSZCharacter(tileType);
    }

    /**
     * Returns a human-readable String representation of a tile. This is taken to be its MPSZ notation.
     * @return The String representation.
     */
    @Override
    public String toString()
    {
        return getMPSZNotation();
    }

    /**
     * Compares a tile to another in MPSZ order.
     */
    @Override
    public int compareTo(Tile other)
    {
        int thisIndex = this.toIndex(), otherIndex = other.toIndex();

        if(thisIndex == otherIndex)
        {
            if(this.isRed == other.isRed)
                // Same index and same redness = same tile
                return 0;
            else
                // Tiles aren't of the same redness = only one is red, is it this or that
                return this.isRed ? 1 : -1;
        }
        else
            return thisIndex - otherIndex;
    }

    @Override
    public boolean equals(Object other) 
    {
        if(this == other) 
            return true;

        if(other == null || getClass() != other.getClass()) 
            return false;

        Tile otherTile = (Tile)other;

        return tileType == otherTile.tileType &&
               number == otherTile.number &&
               isRed == otherTile.isRed;
    }

    @Override
    public int hashCode() 
    {
        return java.util.Objects.hash(tileType, number, isRed);
    }
}
