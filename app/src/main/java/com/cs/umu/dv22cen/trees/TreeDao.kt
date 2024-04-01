package com.cs.umu.dv22cen.trees

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * TreeDao interface for interacting with database.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
@Dao
interface TreeDao {
    /**
     * Gets all trees from database.
     */
    @Query("SELECT * FROM treedb")
    suspend fun getAll(): List<TreeDB>

    /**
     * Gets a tree based on coordinates.
     */
    @Query("SELECT * FROM treedb WHERE lat LIKE :lat AND lon LIKE :lon")
    suspend fun findByCoord(lat: Double, lon: Double): TreeDB?

    /**
     * Gets amount of trees in database.
     */
    @Query("SELECT COUNT(*) FROM treedb")
    suspend fun getRowCount(): Int

    /**
     * Removes all rows in database.
     */
    //Should not be used unless testing
    @Query("DELETE FROM treedb")
    suspend fun nukeTable()

    /**
     * Inserts all trees in list.
     */
    @Insert
    suspend fun insertAll(vararg trees: TreeDB)

    /**
     * Inserts a tree.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tree: TreeDB)

    /**
     * Deletes a tree.
     */
    @Delete
    suspend fun delete(tree: TreeDB)
}