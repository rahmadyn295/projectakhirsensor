package com.hp.projekakhirsensor;

import android.provider.BaseColumns;

public final class DatabaseContract {

    private DatabaseContract() {
    }

    public static class StepEntry implements BaseColumns {
        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_STEP_COUNT = "step_count";
    }
}

