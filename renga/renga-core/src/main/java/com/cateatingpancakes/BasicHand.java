package com.cateatingpancakes;

import java.util.ArrayList;

import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileMeld;
import com.cateatingpancakes.tile.TileSet;

public class BasicHand extends TileSet 
{
    static
    {
        LoaderBridge.requestLibrary("core");
    }

    protected ArrayList<TileMeld> melds;

    /**
     * Constructs an empty hand.
     */
    public BasicHand()
    {
        melds = new ArrayList<>();
    }

    /**
     * Constructs a hand from a list of tiles without any melds.
     * @param tiles The list of tiles to construct from.
     */
    public BasicHand(ArrayList<Tile> tiles)
    {
        super(tiles);
    }

    /**
     * Constructs a hand from a list of tiles and melds.
     * @param tiles The list of tiles to construct from.
     * @param melds The list of melds to use.
     */
    public BasicHand(ArrayList<Tile> tiles, ArrayList<TileMeld> melds)
    {
        super(tiles);
        this.melds = new ArrayList<>(melds);
    }

    /**
     * Constructs a meldless hand by drawing tiles from a wall.
     * @param wall The wall to draw from.
     * @param drawCount How many tiles to draw.
     */
    public BasicHand(BasicWall wall, int drawCount)
    {
        super();
        for(int i = 0; i < drawCount; i++)
            tiles.add(wall.drawTop());
    }

    /**
     * Copy-constructs a hand.
     * @param basicHand The hand to copy.
     */
    public BasicHand(BasicHand basicHand)
    {
        this(basicHand.tiles, basicHand.melds);
    }

    /**
     * Transforms the hand into a frequency array of tile index numbers.
     * @return The array of counts.
     */
    private int[] toCountArray()
    {
        // TODO: A real method
        return new int[]{-1};
    }

    /**
     * Native method. Calculates the n-away number of the hand.
     * @param countArray A frequency array of tile index numbers.
     * @return The n-away number.
     */
    private native int tilesAway(int[] countArray);

    /**
     * Calculates the n-away number of the hand.
     * @return The n-away number.
     */
    public int getTilesAway() 
    {
        return tilesAway(toCountArray());
    }
}
