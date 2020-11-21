package data;

import java.util.ArrayList;
import java.util.List;

public final class Movie extends Show{
    private final int duration;

    private List<Double> ratings;

    public Movie(final String title, final ArrayList<String> cast,
                          final ArrayList<String> genres, final int year,
                          final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
        this.ratings = new ArrayList<>();
    }

    public List<Double> getRatings() { return ratings; }

    public int getDuration() {
        return duration;
    }

    public void addRating(double rating) {
        this.ratings.add(rating);
    }

    @Override
    public String toString() {
        return "Movie{" + "title= "
                + super.getTitle() + "year= "
                + super.getYear() + "duration= "
                + duration + "cast {"
                + super.getCast() + " }\n"
                + "genres {" + super.getGenres() + " }\n ";
    }
}
