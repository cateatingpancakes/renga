package com.cateatingpancakes;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.scijava.nativelib.NativeLoader;

final class LoaderBridge 
{
    private static final Set<String> loaded = ConcurrentHashMap.newKeySet();

    /**
     * Requests that a library be loaded. If the library is already loaded, nothing happens.
     * @param libName The name of the library to be loaded.
     */
    public static void requestLibrary(String libName) 
    {
        // Don't load if already loaded.
        if(loaded.contains(libName)) 
            return;

        synchronized(LoaderBridge.class) 
        {
            if(!loaded.contains(libName)) 
            {
                try 
                {
                    NativeLoader.loadLibrary(libName);
                    loaded.add(libName);
                } 
                catch(java.io.IOException e) 
                {
                    throw new RuntimeException("Could not load native library " + libName, e);
                }
            }
        }
    }
}