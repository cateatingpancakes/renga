package com.cateatingpancakes;

import java.io.Serializable;

public class Tile implements Comparable<Tile>, Serializable
{
    public static enum TileType 
    {
        MANZU,
        PINZU,
        SOUZU,
        HONOR,
        FLOWER // For completeness, but this is only used in hana mahjong and is not implemented.
    }

    private TileType tileType;
    private int number;
    private boolean isRed;

    /**
     * Returns a 1-character String uniquely associated with a given tile type.
     * @param tileType The type of the tile.
     * @return The corresponding String.
     */
    static String typeString(TileType tileType)
    {
        switch(tileType)
        {
            // Note that case-switch fallthrough isn't an issue here, since we return immediately.
            case MANZU:
                return "m";
            case PINZU:
                return "p";
            case SOUZU:
                return "s";
            case HONOR:
                return "z";
            case FLOWER:
                return "f";
            default:
                throw new IllegalArgumentException("Cannot acquire type string of unrecognized tile type " + tileType);
        }
    }

    /**
     * Constructs a tile based on its integer index. The tile is defaulted to be non-red.
     * @param tileIndex The tile index.
     */
    public Tile(int tileIndex)
    {
        this(tileIndex, false);
    }

    /**
     * Constructs a tile based on its integer index.
     * @param tileIndex The tile index.
     * @param isRed Redness of the tile.
     */
    public Tile(int tileIndex, boolean isRed)
    {
        this(Tile.fromIndex(tileIndex));
        this.isRed = isRed;
    }

    /**
     * Constructs a tile based on its type and number. The tile is defaulted to be non-red.
     * @param tileType The type/suit of the tile.
     * @param number The number of the tile within its suit, between 0 and SUIT_MAX - 1.
     */
    public Tile(TileType tileType, int number) 
    {
        // Default to non-red tiles
        this(tileType, number, false);
    }

    /**
     * Constructs a tile based on all its attributes.
     * @param tileType The type/suit of the tile.
     * @param number The number of the tile within its suit, between 0 and SUIT_MAX - 1.
     * @param isRed Redness of the tile.
     */
    public Tile(TileType tileType, int number, boolean isRed) 
    {
        this.tileType = tileType;
        this.number = number;
        this.isRed = isRed;
    }

    /**
     * Copy-constructs a tile.
     * @param tile The tile to copy.
     */
    public Tile(Tile tile)
    {
        this.tileType = tile.tileType;
        this.number = tile.number;
        this.isRed = tile.isRed;
    }

    /**
     * Converts the tile to an integer index number, ignoring redness.
     * This is most useful for algorithmic purposes such as calculating how many tiles a hand is away from being ready.
     * @return The tile index number.
     */
    public int toIndex() 
    {
        int index = 0;

        switch(tileType)
        {
            // Tile ranges listed below:
            case MANZU:
                // 0-8
                index = 0;
                break;
            case PINZU:
                // 9-17
                index = 9;
                break;
            case SOUZU:
                // 18-26
                index = 18;
                break;
            case HONOR:
                // 27-33
                index = 27;
                break;
            case FLOWER:
                // 34-37
                index = 34;
                break;
        }

        index += number;
        return index;
    }

    /**
     * Converts a tile index number into a non-red Tile object of that index.
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
            throw new IllegalArgumentException("Cannot resolve a tile from tile index " + index);

        return new Tile(tileType, number);
    }

    /**
     * Returns the common number associated with a tile.
     * This method assumes the convention that red tiles are always unique in their suit and assigns 0 to those tiles.
     * Do not confuse this with index of the tile, which is mainly for internal use, and ignores redness.
     * @return The common tile number.
     */
    public String getCommonNumber()
    {
        if(isRed)
            return "0";
        else
            return Integer.toString(number + 1); // Transform internal representation range [0, SUIT_SIZE - 1] 
                                                 // into the more human-readable [1, SUIT_SIZE] for non-reds.
    }

    /**
     * Returns a human-readable String representation of a tile.
     * @return The String representation.
     */
    @Override
    public String toString()
    {
        return getCommonNumber() + Tile.typeString(tileType);
    }

    @Override
    public int compareTo(Tile other)
    {
        int thisIndex = this.toIndex(), otherIndex = other.toIndex();

        if(thisIndex == otherIndex)
        {
            if(this.isRed == other.isRed)
                return 0; // Same index and same redness = same tile.
            else
                return this.isRed ? 1 : -1; // Tiles aren't of the same redness = only one is red. Is it this or that?
        }
        else
            return thisIndex - otherIndex;
    }
}
