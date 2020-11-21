package data;

import java.util.List;

public final class Data {
    private final List<Actor> actorsData;

    private final List<User> usersData;

    private final List<Movie> moviesData;

    private final List<Serial> serialsData;

    public Data() {
        this.actorsData = null;
        this.usersData = null;
        this.moviesData = null;
        this.serialsData = null;
    }

    public Data(final List<Actor> actors, final List<User> users,
                 final List<Movie> movies,
                 final List<Serial> serials) {
        this.actorsData = actors;
        this.usersData = users;
        this.moviesData = movies;
        this.serialsData = serials;
    }

    public List<Actor> getActors() {
        return actorsData;
    }

    public List<User> getUsers() {
        return usersData;
    }

    public List<Movie> getMovies() {
        return moviesData;
    }

    public List<Serial> getSerials() {
        return serialsData;
    }
}
