package com.autobot.watchparty.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.autobot.watchparty.database.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movie WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Movie

    @Query("SELECT * FROM movie WHERE name LIKE :movieName LIMIT 1")
    fun insertMovieByName(movieName: String): Movie

    @Insert
    fun insertAll(vararg movies: Movie)

    @Delete
    fun delete(movie: Movie)


}