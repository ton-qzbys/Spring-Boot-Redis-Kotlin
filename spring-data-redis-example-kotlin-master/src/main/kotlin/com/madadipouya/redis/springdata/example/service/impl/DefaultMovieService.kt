package com.madadipouya.redis.springdata.example.service.impl

import com.madadipouya.redis.springdata.example.controller.MovieController
import com.madadipouya.redis.springdata.example.model.Movie
import com.madadipouya.redis.springdata.example.producer.MovieAddedProducer
import com.madadipouya.redis.springdata.example.repository.MovieRepository
import com.madadipouya.redis.springdata.example.service.MovieService
import com.madadipouya.redis.springdata.example.service.exception.MovieNotFoundException
import org.springframework.stereotype.Service

@Service
class DefaultMovieService(val movieRepository: MovieRepository, val movieAddedProducer: MovieAddedProducer) : MovieService {

    override fun getMovie(id: String): Movie = movieRepository.findById(id).orElseThrow {
        MovieNotFoundException("Unable to find movie for $id id")
    }

    override fun getAllMovies(): List<Movie> = movieRepository.findAll().toList()

    override fun updateMovie(id: String, movieDto: MovieController.MovieDto): Movie {
        val movie: Movie = movieRepository.findById(id).orElseThrow { MovieNotFoundException("Unable to find movie for $id id") }
        val updatedMovie = movie.copy(name = movieDto.name.orEmpty(), genre = movieDto.genre.orEmpty(), year = movieDto.year)
        updatedMovie.id = movie.id
        return movieRepository.save(updatedMovie)
    }

    override fun updateMovie(movie: Movie): Movie = movieRepository.save(movie)

    override fun createMovie(movieDto: MovieController.MovieDto): Movie {
        val movie = movieRepository.save(Movie(name = movieDto.name.orEmpty(), genre = movieDto.genre.orEmpty(), year = movieDto.year))
        movieAddedProducer.publish(movie)
        return movie
    }

    override fun deleteMovie(id: String) = movieRepository.delete(getMovie(id))
}