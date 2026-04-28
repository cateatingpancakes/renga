package com.cateatingpancakes.hand;

import java.util.ArrayList;

import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileMeld;
import com.cateatingpancakes.tile.TileSet;
import com.cateatingpancakes.wall.BasicWall;

public class BasicHand extends TileSet implements Splittable
{
    protected ArrayList<TileMeld> melds;

    /**
     * Constructs an empty hand.
     */
    public BasicHand()
    {
        this.melds = new ArrayList<>();
    }

    /**
     * Constructs a hand from a list of tiles without any melds.
     * @param tiles The list of tiles to construct from.
     */
    public BasicHand(ArrayList<Tile> tiles)
    {
        super(tiles);
        this.melds = new ArrayList<>();
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
        this.melds = new ArrayList<>();

        // Draw from the wall:
        for(int i = 0; i < drawCount; i++)
            tiles.add(wall.drawTop());
    }

    /**
     * Copy-constructs a hand.
     * @param basicHand The hand to copy.
     */
    public BasicHand(BasicHand other)
    {
        this(other.tiles, other.melds);
    }

    @Override
    public int[] toCountArray()
    {
        int[] counts = new int[Tile.INDEX_NUMBER_MAX];

        for(Tile tile : tiles)
        {
            int index = tile.toIndex();
            counts[index]++;
        }

        return counts;
    }

    @Override
    public ArrayList<TileMeld> getOpenMelds()
    {
        return new ArrayList<>(melds);
    }
}
