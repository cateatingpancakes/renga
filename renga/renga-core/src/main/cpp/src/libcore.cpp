#include <jni.h>
#include "com_cateatingpancakes_BasicHand.h"

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

void naway_dfs(jint * __restrict cnt_array, int idx, int groups, int pairs, int waits, int& min_away)
{
    static constexpr int MOD_9[] = {
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8
    };

    while(idx < TILE_INDEX_NUMBER_ALG_MAX && cnt_array[idx] == 0)
    {
        idx++;
    }
    
    if(idx >= TILE_INDEX_NUMBER_ALG_MAX) 
    {
        int head_ok = pairs > 0, waits_pairs = waits + max(0, pairs - 1), away;

        if(groups + waits_pairs > 4)
            waits_pairs = 4 - groups;

        away = 8 - (2 * groups) - waits_pairs - head_ok;
        min_away = min(min_away, away);
        return;
    }

    if(cnt_array[idx] >= 3) 
    {
        cnt_array[idx] -= 3;
        naway_dfs(cnt_array, idx, groups + 1, pairs, waits, min_away);
        cnt_array[idx] += 3;
    }

    if(cnt_array[idx] >= 2) 
    {
        cnt_array[idx] -= 2;
        naway_dfs(cnt_array, idx, groups, pairs + 1, waits, min_away);
        cnt_array[idx] += 2;
    }

    if(idx < TILE_INDEX_NUMBER_SUIT_MAX && MOD_9[idx] <= 6) 
    {
        if(cnt_array[idx + 1] > 0 && cnt_array[idx + 2] > 0) 
        {
            cnt_array[idx]--; cnt_array[idx + 1]--; cnt_array[idx + 2]--;
            naway_dfs(cnt_array, idx, groups + 1, pairs, waits, min_away);
            cnt_array[idx]++; cnt_array[idx + 1]++; cnt_array[idx + 2]++;
        }
        
        if(cnt_array[idx + 2] > 0) 
        {
            cnt_array[idx]--; cnt_array[idx + 2]--;
            naway_dfs(cnt_array, idx, groups, pairs, waits + 1, min_away);
            cnt_array[idx]++; cnt_array[idx + 2]++;
        }
    }

    if(idx < TILE_INDEX_NUMBER_SUIT_MAX && MOD_9[idx] <= 7)
    {
        if(cnt_array[idx + 1] > 0) 
        {
            cnt_array[idx]--; cnt_array[idx + 1]--;
            naway_dfs(cnt_array, idx, groups, pairs, waits + 1, min_away);
            cnt_array[idx]++; cnt_array[idx + 1]++;
        }
    }

    cnt_array[idx]--;
    naway_dfs(cnt_array, idx, groups, pairs, waits, min_away);
    cnt_array[idx]++;
}

void naway_pairs(jint * __restrict cnt_array, int& min_away) 
{
    int pairs = 0, types = 0, away;

    for(int i = 0; i < TILE_INDEX_NUMBER_ALG_MAX; i++) 
    {
        if(cnt_array[i] >= 2) 
            pairs++;
        if(cnt_array[i] > 0) 
            types++;
    }

    away = 6 - pairs;

    if(types < 7)
        away += (7 - types);

    min_away = min(min_away, away);
}

void naway_kokushi(jint * __restrict cnt_array, int& min_away) 
{
    static constexpr int ORPHANS_IDX[] = {
        0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33
    };

    int orphans_cnt = 0, head_ok = 0, away;

    for(int i = 0; i < 13; i++) 
    {
        int idx = ORPHANS_IDX[i];

        if(cnt_array[idx] > 0) 
        {
            orphans_cnt++;
            
            if(cnt_array[idx] >= 2) 
                head_ok = 1;
        }
    }

    away = 13 - orphans_cnt - head_ok;
    min_away = min(min_away, away);
}

void naway(jint * __restrict cnt_array, int calls, int& min_away)
{
    naway_dfs(cnt_array, 0, calls, 0, 0, min_away);

    // Saves some checks here. Seven Pairs and kokushi are only in closed hands.
    if(calls == 0)
    {
        naway_pairs(cnt_array, min_away);

        naway_kokushi(cnt_array, min_away);
    }
}

JNIEXPORT jint JNICALL Java_com_cateatingpancakes_BasicHand_tilesAway
    (JNIEnv * env, jobject jthis, jintArray tileCounts)
{
    static constexpr int CALLS[] = {
        4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0
    };

    jint *cnt_array = env->GetIntArrayElements(tileCounts, NULL);

    int tile_cnt = 0;
    // Not 34, since hand may have undiscarded flowers/seasons up to 42.
    for(int i = 0; i < TILE_INDEX_NUMBER_MAX; i++)
        tile_cnt += cnt_array[i];


    int calls = CALLS[tile_cnt], min_away = 8;
    naway(cnt_array, calls, min_away);

    env->ReleaseIntArrayElements(tileCounts, cnt_array, JNI_ABORT);
    return min_away;
}