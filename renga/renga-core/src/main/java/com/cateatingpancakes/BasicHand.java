package com.cateatingpancakes;

import java.util.ArrayList;
import java.util.List;

import com.cateatingpancakes.hand.HandInterpretation;
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
    public BasicHand(BasicHand basicHand)
    {
        this(basicHand.tiles, basicHand.melds);
    }

    /**
     * Transforms the non-melded part of the hand into a frequency array of tile index numbers.
     * @return The array of counts.
     */
    private int[] toCountArray()
    {
        int[] counts = new int[Tile.INDEX_NUMBER_MAX];

        for(Tile tile : tiles)
        {
            int index = tile.toIndex();
            counts[index]++;
        }

        return counts;
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

    /**
     * Interprets a winning hand in the standard form of 4 melds and 1 pair.
     * @return An ArrayList containing all possible groupings of tiles within the hand.
     */
    public ArrayList<HandInterpretation> interpret()
    {
        ArrayList<HandInterpretation> validSet = new ArrayList<>();
        int[] counts = toCountArray();

        for(int i = 0; i < Tile.INDEX_NUMBER_ALG_MAX; i++) 
        {
            // Iterate over all possible pairs of the hand:
            if(counts[i] >= 2) 
            {
                counts[i] -= 2;
                
                Tile tile = Tile.fromIndex(i);
                HandInterpretation interpretation = new HandInterpretation();

                // Construct and add pair.
                ArrayList<Tile> pair = new ArrayList<>(List.of(tile, tile));
                interpretation.setPair(pair);

                // Copy open melds over.
                for(TileMeld meld : melds)
                    interpretation.addMeld(meld);

                // Add all interpretations found with this pair/head.
                ArrayList<HandInterpretation> validSubset = new ArrayList<>();
                findGroups(0, counts, interpretation, validSubset);
                validSet.addAll(validSubset);

                counts[i] += 2;
            }
        }

        return validSet;
    }

    /**
     * Returns whether the hand has a valid interpretation as seven pairs.
     * @return True, if the hand is seven pairs.
     */
    public boolean interpretPairs()
    {
        int pairs = 0;
        int[] counts = toCountArray();

        for(int i = 0; i < Tile.INDEX_NUMBER_ALG_MAX; i++) 
            if(counts[i] == 2) 
                pairs++;

        return pairs == 7;
    }

    /**
     * Returns whether the hand has a valid interpretation as kokushi musou.
     * See: https://riichi.wiki/Kokushi_musou
     * @return True, if the hand is kokushi musou.
     */
    public boolean interpretKokushi()
    {
        int[] counts = toCountArray();
        boolean hasPair = false;

        for(int index : Tile.INDEX_NUMBER_ORPHANS) 
        {
            // Missing orphan.
            if(counts[index] == 0)
                return false;
            
            if(counts[index] == 2) 
            {
                // Only 1 pair in kokushi, otherwise return early with false.
                if(hasPair) 
                    return false;
                
                // Otherwise we found this hand's pair.
                hasPair = true;
            } 
            
            // Too many orphans.
            if(counts[index] > 2) 
                return false;
        }

        return hasPair;
    }

    /**
     * Finds groups of tiles from a tile-count array and a starting index, adds them to a current hand interpretation, and collects
     * all valid hand interpretations in a result ArrayList.
     * @param index The index at which to start. Usually, this should be 0.
     * @param counts The tile count array.
     * @param current The hand interpretation to add melds to.
     * @param result The ArrayList in which to collect valid hand interpretations.
     */
    private static void findGroups(int index, int[] counts, HandInterpretation current, ArrayList<HandInterpretation> result)
    {
        while(index < Tile.INDEX_NUMBER_ALG_MAX && counts[index] == 0) 
            index++;

        if(index >= Tile.INDEX_NUMBER_ALG_MAX) 
        {
            if(current.meldCount() == 4)
            {
                HandInterpretation valid = new HandInterpretation(current);
                result.add(valid);
            }

            return;
        }

        if(counts[index] >= 3) 
        {
            counts[index] -= 3;

            TileSet meldTiles = new TileSet(new ArrayList<>(List.of(
                Tile.fromIndex(index),
                Tile.fromIndex(index),
                Tile.fromIndex(index)
            )));
            current.addMeld(new TileMeld(TileMeld.MeldType.PON, meldTiles));
            
            findGroups(index, counts, current, result);
            
            counts[index] += 3;
            current.removeLastMeld();
        }

        if(index < Tile.INDEX_NUMBER_SUIT_MAX && index % 9 <= 6)
        {
            if(counts[index] >= 1 && counts[index + 1] >= 1 && counts[index + 2] >= 1) 
            {
                counts[index]--; 
                counts[index + 1]--; 
                counts[index + 2]--;

                TileSet meldTiles = new TileSet(new ArrayList<>(List.of(
                    Tile.fromIndex(index),
                    Tile.fromIndex(index + 1),
                    Tile.fromIndex(index + 2)
                )));
                current.addMeld(new TileMeld(TileMeld.MeldType.CHII, meldTiles));
                
                findGroups(index, counts, current, result);
                
                counts[index]++; 
                counts[index + 1]++; 
                counts[index + 2]++;
                current.removeLastMeld();
            }
        }
    }
}
