package com.cs.umu.dv22cen.trees

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Tree database class.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
@Database(entities = [TreeDB::class], version = 2)
abstract class TreeDatabase : RoomDatabase() {
    /**
     * Gets the treeDao
     * @return TreeDao
     */
    abstract fun treeDao(): TreeDao

    companion object {
        @Volatile
        private var INSTANCE: TreeDatabase? = null

        /**
         * Gets database instance.
         * @param  context Context
         * @return Database instance.
         */
        fun getInstance(context: Context): TreeDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        /**
         * Builds the database.
         * @param  context Context
         */
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TreeDatabase::class.java, "tree_database.db"
            ).fallbackToDestructiveMigration().build()
    }
}