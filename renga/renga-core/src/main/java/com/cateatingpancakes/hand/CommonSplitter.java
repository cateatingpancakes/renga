package com.cateatingpancakes.hand;

import java.util.ArrayList;
import java.util.List;

import com.cateatingpancakes.LoaderBridge;
import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileMeld;
import com.cateatingpancakes.tile.TileSet;

public class CommonSplitter implements Splitter
{
    static
    {
        LoaderBridge.requestLibrary("core");
    }

    /**
     * Constructs a common hand splitter. This hand splitter should work for most variants,
     * including 3-player and 4-player Riichi, as well as any ruleset of Chinese mahjong.
     */
    public CommonSplitter()
    {

    }

    /**
     * Native method. Returns the n-away number of a hand in all forms.
     * @param countArray The hand, as a count array.
     * @return The n-away number.
     */
    private native int __tilesAway(int[] countArray);

    @Override
    public int tilesAway(Splittable hand) 
    {
        // TODO: Implement and call a Java-only fallback if the native isn't available.
        return __tilesAway(hand.toCountArray());
    }

    @Override
    public ArrayList<HandInterpretation> interpret(Splittable hand) 
    {
        ArrayList<HandInterpretation> validSet = new ArrayList<>();
        int[] counts = hand.toCountArray();

        for(int i = 0; i < Tile.INDEX_NUMBER_ALG_MAX; i++) 
        {
            // Over all possible pairs of the hand:
            if(counts[i] >= 2) 
            {
                counts[i] -= 2;
                
                Tile tile = Tile.fromIndex(i);
                HandInterpretation interpretation = new HandInterpretation();

                // Add the pair.
                ArrayList<Tile> pair = new ArrayList<>(List.of(tile, tile));
                interpretation.setPair(pair);

                // Copies the open melds over.
                for(TileMeld meld : hand.getOpenMelds())
                    interpretation.addMeld(meld);

                // Then add all interpretations found with this pair/head.
                ArrayList<HandInterpretation> validSubset = new ArrayList<>();
                findGroups(0, counts, interpretation, validSubset);
                validSet.addAll(validSubset);

                counts[i] += 2;
            }
        }

        return validSet;
    }

    @Override
    public boolean interpretOrphans(Splittable hand) 
    {
        int[] counts = hand.toCountArray();
        boolean hasPair = false;

        for(int index : Tile.INDEX_NUMBER_ORPHANS) 
        {
            // Too few/absent orphans:
            if(counts[index] == 0)
                return false;
            
            // Extra pair of orphans, when one has been found already:
            if(counts[index] == 2 && hasPair)
                return false;

            // OK, but mark a found pair.
            if(counts[index] == 2 && !hasPair)
                hasPair = true;
            
            // Too many orphans:
            if(counts[index] > 2) 
                return false;
        }

        return hasPair;
    }

    @Override
    public boolean interpretPairs(Splittable hand) 
    {
        int pairs = 0;
        int[] counts = hand.toCountArray();

        for(int i = 0; i < Tile.INDEX_NUMBER_ALG_MAX; i++) 
            if(counts[i] == 2) 
                pairs++;

        return pairs == 7;
    }
    
    /**
     * Finds groups of tiles from a tile count array and a starting index, adds them to
     * a current interpretation, and collects all valid hand interpretations in a result 
     * ArrayList.
     * @param index The index in counts[] at which to start parsing.
     * @param counts The tile count array.
     * @param current The current hand interpretation in the recursion to add melds to.
     * @param result The ArrayList collecting valid interpretations.
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

        // Triplets:
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

        // Sequences:
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
