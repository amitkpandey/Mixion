package com.taskail.mixion.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

/**
 *Created by ed on 1/26/18.
 *
 * Data Access object for the tags table
 */
@Dao interface TagsDao {

    @Query("SELECT * FROM Tags") fun getTags(): List<RoomTags>
}