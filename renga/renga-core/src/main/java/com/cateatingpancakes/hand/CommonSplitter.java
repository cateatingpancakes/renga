package com.cateatingpancakes.hand;

import java.util.ArrayList;
import java.util.List;

import com.cateatingpancakes.LoaderBridge;
import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileMeld;
import com.cateatingpancakes.tile.TileSet;

public class CommonSplitter implements Splitter
{
    /**
     * Whether we failed to request the library, or not.
     */
    private static boolean CORE_REQUEST_FAIL = false;

    static
    {
        try 
        {
            LoaderBridge.requestLibrary("core");
        } 
        catch(Throwable t) 
        {
            CORE_REQUEST_FAIL = true;
        }
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


    /**
     * Fallback methods block start:
     */

    private int minAway;

    private void nAwayStd(int[] cntArray, int idx, int groups, int pairs, int waits)
    {
        final int[] MOD_9 = 
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8
        };

        while(idx < Tile.INDEX_NUMBER_ALG_MAX && cntArray[idx] == 0)
        {
            idx++;
        }

        if(idx >= Tile.INDEX_NUMBER_ALG_MAX)
        {
            int headOk = (pairs > 0) ? 1 : 0,
                waitsPairs = waits + Math.max(0, pairs - 1),
                away;

            if(groups + waitsPairs > 4)
            {
                waitsPairs = 4 - groups;
            }

            away = 8 - (2 * groups) - waitsPairs - headOk;
            minAway = Math.min(minAway, away);
            return;
        }

        if(cntArray[idx] >= 3)
        {
            cntArray[idx] -= 3;

            nAwayStd(cntArray, idx, groups + 1, pairs, waits);

            cntArray[idx] += 3;
        }

        if(cntArray[idx] >= 2) 
        {
            cntArray[idx] -= 2;

            nAwayStd(cntArray, idx, groups, pairs + 1, waits);

            cntArray[idx] += 2;
        }

        if(idx < Tile.INDEX_NUMBER_SUIT_MAX && MOD_9[idx] <= 6) 
        {
            if(cntArray[idx + 1] > 0 && cntArray[idx + 2] > 0) 
            {
                cntArray[idx]--; 
                cntArray[idx + 1]--; 
                cntArray[idx + 2]--;

                nAwayStd(cntArray, idx, groups + 1, pairs, waits);

                cntArray[idx]++; 
                cntArray[idx + 1]++; 
                cntArray[idx + 2]++;
            }
            
            if(cntArray[idx + 2] > 0) 
            {
                cntArray[idx]--; 
                cntArray[idx + 2]--;

                nAwayStd(cntArray, idx, groups, pairs, waits + 1);

                cntArray[idx]++; 
                cntArray[idx + 2]++;
            }
        }

        if(idx < Tile.INDEX_NUMBER_SUIT_MAX && MOD_9[idx] <= 7)
        {
            if(cntArray[idx + 1] > 0) 
            {
                cntArray[idx]--; 
                cntArray[idx + 1]--;

                nAwayStd(cntArray, idx, groups, pairs, waits + 1);

                cntArray[idx]++; 
                cntArray[idx + 1]++;
            }
        }

        cntArray[idx]--;
        nAwayStd(cntArray, idx, groups, pairs, waits);
        cntArray[idx]++;
    }

    private void nAwayPairs(int[] cntArray)
    {
        int pairs = 0, types = 0, away;

        for(int i = 0; i < Tile.INDEX_NUMBER_ALG_MAX; i++) 
        {
            if(cntArray[i] >= 2) 
                pairs++;

            if(cntArray[i] > 0) 
                types++;
        }

        away = 6 - pairs;

        if(types < 7)
            away += (7 - types);

        minAway = Math.min(minAway, away);
    }

    private void nAwayOrphans(int[] cntArray) 
    {
        final int[] ORPHANS_IDX = 
        {
            0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33
        };

        int orphansCnt = 0, headOk = 0, away;

        for(int i = 0; i < 13; i++) 
        {
            int idx = ORPHANS_IDX[i];

            if(cntArray[idx] > 0) 
            {
                orphansCnt++;
                
                if(cntArray[idx] >= 2) 
                    headOk = 1;
            }
        }

        away = 13 - orphansCnt - headOk;
        minAway = Math.min(minAway, away);
    }

    private void nAway(int[] cntArray, int calls)
    {
        nAwayStd(cntArray, 0, calls, 0, 0);

        if(calls == 0)
        {
            nAwayPairs(cntArray);
            
            nAwayOrphans(cntArray);
        }
    }

    /**
     * Fallback methods block end.
     */


    @Override
    public int tilesAway(Splittable hand) 
    {
        if(CORE_REQUEST_FAIL)
        {
            // This bit is also ported literally from the C++ library.
            // Slightly ugly to put this here, but functional and an exact translation.

            final int[] CALLS = 
            {
                4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0
            };

            int[] cntArray = hand.toCountArray();

            int tileCnt = 0, calls;
            
            for(int i = 0; i < Tile.INDEX_NUMBER_MAX; i++)
                tileCnt += cntArray[i];

            minAway = 8;

            calls = CALLS[tileCnt];
            nAway(cntArray, calls);

            return minAway;
        }
        else
        {
            return __tilesAway(hand.toCountArray());
        }
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
