package com.cateatingpancakes.tile;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;

public final class Tile implements Comparable<Tile>, Serializable
{
    /**
     * Maximum index number possible for a tile. This is probably not the constant you're looking for.
     * The range for Riichi is 0-33, and almost all Mahjong variants should be covered by 0-41.
     */
    public static final int      INDEX_NUMBER_MAX = 42;

    /**
     * Maximum index number relevant for algorithmic purposes.
     * Regardless of flowers/seasons that may or may not be present, only the tiles with
     * index number 0-33 will ever be relevant for algorithmic purposes, such as determining
     * a hand's n-away number or finding all its interpretations.
     */
    public static final int  INDEX_NUMBER_ALG_MAX = 34;

    /**
     * Maximum index number of tiles with numbers within their suit.
     * This is also useful for algorithmic purposes where some branches of the n-away algorithm
     * are only taken for numbered-suit tiles, such as checking for a sequence which is impossible
     * to do with the honors that can form only triplets.
     */
    public static final int INDEX_NUMBER_SUIT_MAX = 27;

    /**
     * The index numbers for the "thirteen orphan" tiles.
     */
    public static final List<Integer> INDEX_NUMBER_ORPHANS = List.of(
    0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33
    );

    /**
     * Tile suits.
     */
    public static enum TileType 
    {
        MANZU, PINZU, SOUZU, HONOR, FLOWER, SEASON
    }


    private final TileType           tileType;
    private final int                number;
    private final EnumSet<TileTrait> traits;

    /**
     * Returns the 1-character String uniquely associated with a given tile suit in MPSZ notation.
     * @param tileType The type/suit of the tile.
     * @return The corresponding String.
     */
    static String getMPSZCharacter(TileType tileType)
    {
        switch(tileType)
        {
            case  MANZU -> {
                return "m";
            }
            case  PINZU -> {
                return "p";
            }
            case  SOUZU -> {
                return "s";
            }
            case  HONOR -> {
                return "z";
            }
            case FLOWER -> {
                return "f";
            }
            case SEASON -> {
                // Unsure if this is a standard name, it likely isn't. Send a pull request if there's a standard notation.
                return "a";
            }
            default -> throw new AssertionError("Could not resolve tile type " + tileType);
        }
    }

    /**
     * Constructs a traitless tile based on its index number.
     * @param tileIndex The tile index number.
     */
    public Tile(int tileIndex)
    {
        this(tileIndex, null);
    }

    /**
     * Constructs a tile based on its index number and traits.
     * @param tileIndex The tile index number.
     * @param traits The traits of the tile.
     */
    public Tile(int tileIndex, EnumSet<TileTrait> traits)
    {
        Tile base = Tile.fromIndex(tileIndex);
        this(base.tileType, base.number, traits);
    }

    /**
     * Constructs a traitless tile given its suit and rank.
     * @param tileType The type/suit of the tile.
     * @param number The number of the tile within its suit, between 0 and SUIT_MAX - 1;
     */
    public Tile(TileType tileType, int number)
    {
        this(tileType, number, null);
    }

    /**
     * Constructs a tile given its complete description in suit, rank and traits.
     * @param tileType The type/suit of the tile.
     * @param number The number of the tile within its suit, between 0 and SUIT_MAX - 1.
     * @param traits The traits of the tile.
     */
    public Tile(TileType tileType, int number, EnumSet<TileTrait> traits) 
    {
        this.tileType = tileType;
        this.number   = number;
        
        if(traits == null || traits.isEmpty()) 
        {
            this.traits = EnumSet.noneOf(TileTrait.class);
        } 
        else 
        {
            this.traits = EnumSet.copyOf(traits);
        }
    }

    /**
     * Copy-constructs a tile.
     * @param tile The tile to copy.
     */
    public Tile(Tile other)
    {
        this.tileType = other.tileType;
        this.number   = other.number;
        this.traits   = EnumSet.copyOf(other.traits);
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
     * Converts the tile to an index number, erasing traits.
     * This is most useful for algorithmic purposes, such as calculating how many tiles a hand is away from being ready.
     * @return The tile index number.
     */
    public int toIndex() 
    {
        int index = 0;

        switch(tileType)
        {
            // Suit ranges listed below.
            case  MANZU -> 
                //   Manzu range: 0-8
                index = 0;
            case  PINZU -> 
                //   Pinzu range: 9-17
                index = 9;
            case  SOUZU -> 
                //   Souzu range: 18-26
                index = 18;
            case  HONOR -> 
                //  Honors range: 27-33
                index = 27;
            case FLOWER -> 
                // Flowers range: 34-37
                index = 34;
            case SEASON ->
                // Seasons range: 38-41
                index = 38;
            default -> throw new AssertionError("Could not resolve tile type " + tileType);
        }

        index += number;
        return index;
    }

    /**
     * Converts an index number into a traitless Tile object of that index number.
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
        else if(38 <= index && index <= 41)
        {
            tileType = TileType.SEASON;
            number = index - 38;
        }
        else
            throw new IllegalArgumentException("Could not resolve index number " + index);

        return new Tile(tileType, number);
    }

    /**
     * Checks if the tile has a given trait.
     * @param trait The trait to check for.
     * @return True, if the tile has the given trait.
     */
    public boolean hasTrait(TileTrait trait)
    {
        return traits.contains(trait);
    }

    /**
     * Checks if the tile has a given set of traits.
     * @param traits The traits to check for.
     * @return True, if the tile has the given traits.
     */
    public boolean hasTraits(EnumSet<TileTrait> traits)
    {
        for(TileTrait trait : traits)
            if(!hasTrait(trait))
                return false;

        return true;
    }

    /**
     * Gets the tile's number within its suit.
     * @return The tile number.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Returns the MPSZ number of a tile as a String of exactly 1 character.
     * This method assumes the convention that red tiles are always unique in their suit and assigns 0 to those tiles.
     * Do not confuse this result with index number of the tile, which is mainly for internal use, and ignores redness.
     * @return The MPSZ number.
     */
    public String getMPSZNumber()
    {
        if(hasTrait(TileTrait.RED))
            return "0";
        else
            // We have to transform the internal representation range [0, SUIT_SIZE - 1] 
            // into the more human-readable (MPSZ) [1, SUIT_SIZE] for tiles that aren't red.
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
            // For now we know how to compare only redness.
            // TODO: Make this so it compares arbitrarily-traited tiles, for when, say, TileTrait.GLASS is added from Washizu mahjong.
            
            if(this.hasTrait(TileTrait.RED) == other.hasTrait(TileTrait.RED))
                // Same index and same redness = literal same tile.
                return 0;
            else
                // Tiles aren't of the same redness = only one is red, is it this or that?
                return this.hasTrait(TileTrait.RED) ? 1 : -1;
        }
        else
            return thisIndex - otherIndex;
    }

    @Override
    public boolean equals(Object other) 
    {
        if(other == this) 
            return true;

        if(other == null || getClass() != other.getClass()) 
            return false;

        Tile tile = (Tile)other;

        return tileType == tile.tileType &&
               number   == tile.number &&
               traits.equals(tile.traits);
    }

    /**
     * Compares the index number with another tile's index number and returns true if they are equal.
     * @param tile The other tile.
     * @return True, if the index numbers are equal.
     */
    public boolean indexEquals(Tile tile)
    {
        return toIndex() == tile.toIndex();
    }

    @Override
    public int hashCode() 
    {
        return java.util.Objects.hash(tileType, number, traits);
    }
}
