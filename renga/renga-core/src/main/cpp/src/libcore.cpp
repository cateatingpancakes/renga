#include <jni.h>
#include "com_cateatingpancakes_hand_CommonSplitter.h"

// Useful constants from Tile.java:
const int TILE_INDEX_NUMBER_MAX      = 42;
const int TILE_INDEX_NUMBER_ALG_MAX  = 34;
const int TILE_INDEX_NUMBER_SUIT_MAX = 27;

constexpr int min(int a, int b)
{
    return (a < b) ? a : b;
}

constexpr int max(int a, int b)
{
    return (a > b) ? a : b;
}

void nAwayStd(jint * __restrict cntArray, int idx, int groups, int pairs, int waits, int& minAway)
{
    static constexpr int MOD_9[] = 
    {
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8
    };

    while(idx < TILE_INDEX_NUMBER_ALG_MAX && cntArray[idx] == 0)
    {
        // Skip tiles with 0 copies:
        idx++;
    }
    
    if(idx >= TILE_INDEX_NUMBER_ALG_MAX) 
    {
        int headOk = pairs > 0, 
            waitsPairs = waits + max(0, pairs - 1), 
            away;

        if(groups + waitsPairs > 4)
        {
            waitsPairs = 4 - groups;
        }

        away = 8 - (2 * groups) - waitsPairs - headOk;
        minAway = min(minAway, away);
        return;
    }

    // Triplets:
    if(cntArray[idx] >= 3) 
    {
        cntArray[idx] -= 3;

        nAwayStd(cntArray, idx, groups + 1, pairs, waits, minAway);

        cntArray[idx] += 3;
    }

    // Pairs:
    if(cntArray[idx] >= 2) 
    {
        cntArray[idx] -= 2;

        nAwayStd(cntArray, idx, groups, pairs + 1, waits, minAway);

        cntArray[idx] += 2;
    }

    if(idx < TILE_INDEX_NUMBER_SUIT_MAX && MOD_9[idx] <= 6) 
    {
        // Full sequences/runs:
        if(cntArray[idx + 1] > 0 && cntArray[idx + 2] > 0) 
        {
            cntArray[idx]--; 
            cntArray[idx + 1]--; 
            cntArray[idx + 2]--;

            nAwayStd(cntArray, idx, groups + 1, pairs, waits, minAway);

            cntArray[idx]++; 
            cntArray[idx + 1]++; 
            cntArray[idx + 2]++;
        }
        
        // Closed waits:
        if(cntArray[idx + 2] > 0) 
        {
            cntArray[idx]--; 
            cntArray[idx + 2]--;

            nAwayStd(cntArray, idx, groups, pairs, waits + 1, minAway);

            cntArray[idx]++; 
            cntArray[idx + 2]++;
        }
    }

    if(idx < TILE_INDEX_NUMBER_SUIT_MAX && MOD_9[idx] <= 7)
    {
        // Edge waits, double-sided waits:
        if(cntArray[idx + 1] > 0) 
        {
            cntArray[idx]--; 
            cntArray[idx + 1]--;

            nAwayStd(cntArray, idx, groups, pairs, waits + 1, minAway);

            cntArray[idx]++; 
            cntArray[idx + 1]++;
        }
    }

    cntArray[idx]--;
    nAwayStd(cntArray, idx, groups, pairs, waits, minAway);
    cntArray[idx]++;
}

void nAwayPairs(jint * __restrict cntArray, int& minAway) 
{
    int pairs = 0, types = 0, away;

    for(int i = 0; i < TILE_INDEX_NUMBER_ALG_MAX; i++) 
    {
        if(cntArray[i] >= 2) 
            pairs++;

        if(cntArray[i] > 0) 
            types++;
    }

    away = 6 - pairs;

    if(types < 7)
        away += (7 - types);

    minAway = min(minAway, away);
}

void nAwayOrphans(jint * __restrict cntArray, int& minAway) 
{
    static constexpr int ORPHANS_IDX[] = 
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
    minAway = min(minAway, away);
}

void nAway(jint * __restrict cntArray, int calls, int& minAway)
{
    nAwayStd(cntArray, 0, calls, 0, 0, minAway);
    
    if(calls == 0)
    {
        // Saves a check here, seven pairs is only in closed hands.
        nAwayPairs(cntArray, minAway);

        // Same situation for kokushi.
        nAwayOrphans(cntArray, minAway);
    }
}

JNIEXPORT jint JNICALL Java_com_cateatingpancakes_hand_CommonSplitter__1_1tilesAway
    (JNIEnv * env, jobject jthis, jintArray tileCounts)
{
    static constexpr int CALLS[] = 
    {
        4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0
    };

    jint *cntArray = env->GetIntArrayElements(tileCounts, NULL);

    int tileCnt = 0, calls, minAway;
    // Not 34, since hand may have undiscarded flowers/seasons up to 42.
    for(int i = 0; i < TILE_INDEX_NUMBER_MAX; i++)
        tileCnt += cntArray[i];

    // No hand can be worse than 8-away.
    minAway = 8;

    calls = CALLS[tileCnt];
    nAway(cntArray, calls, minAway);

    env->ReleaseIntArrayElements(tileCounts, cntArray, JNI_ABORT);
    return minAway;
}