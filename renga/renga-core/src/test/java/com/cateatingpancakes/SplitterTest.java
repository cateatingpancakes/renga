package com.cateatingpancakes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.cateatingpancakes.hand.BasicHand;
import com.cateatingpancakes.hand.CommonSplitter;
import com.cateatingpancakes.hand.Splitter;
import com.cateatingpancakes.wall.BasicWall;
import com.cateatingpancakes.wall.RiichiBuilder4P;
import com.cateatingpancakes.wall.WallBuilder;

class SplitterTest 
{
    @Test
    void testTilesAway()
    {
        int sum = 0;
        Splitter splitter = new CommonSplitter();

        for(long seed = 0; seed < 100000; seed++)
        {
            WallBuilder strategy = new RiichiBuilder4P();
            BasicWall wall = new BasicWall(strategy, seed);
            BasicHand hand = new BasicHand(wall, 13);
            sum += splitter.tilesAway(hand);
        }

        // This value was produced by a build that I think had correct logic.
        // Should it change upon refactoring/additional optimizations then this is
        // how we know that something went wrong.
        assertEquals(sum, 358001);
    }
}
