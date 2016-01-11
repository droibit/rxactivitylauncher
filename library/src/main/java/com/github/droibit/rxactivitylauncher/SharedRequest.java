package com.github.droibit.rxactivitylauncher;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author kumagai
 */
final class SharedRequest {

    static final int DEFAULT_SUBJECT_SIZE = 3;

    private static final Map<String, Set<Integer>> mNonCompleteRequests
            = new HashMap<>(DEFAULT_SUBJECT_SIZE);

    static Set<Integer> restore(String sourceName) {
        Set<Integer> requests = mNonCompleteRequests.get(sourceName);
        if (requests == null) {
            requests = new HashSet<>(DEFAULT_SUBJECT_SIZE);
            mNonCompleteRequests.put(sourceName, requests);
        }
        return requests;
    }

    static void store(String sourceName, @Nullable Set<Integer> nonCompleteRequests) {
        Set<Integer> storeRequests = nonCompleteRequests;
        if (storeRequests == null) {
            storeRequests = new HashSet<>(DEFAULT_SUBJECT_SIZE);
        }
        mNonCompleteRequests.put(sourceName, storeRequests);
    }
}
