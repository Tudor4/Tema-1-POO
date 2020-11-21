package data;

import java.util.ArrayList;
import java.util.Map;


public final class User {
    private final String username;

    private final String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private int numberOfRatings;


    public User(final String username, final String subscriptionType,
                         final Map<String, Integer> history,
                         final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.favoriteMovies = favoriteMovies;
        this.history = history;
        this.numberOfRatings = 0;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public int getNumberOfRatings() { return numberOfRatings; }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    @Override
    public String toString() {
        return "User{" + "username='"
                + username + '\'' + ", subscriptionType='"
                + subscriptionType + '\'' + ", history="
                + history + ", favoriteMovies="
                + favoriteMovies + '}';
    }

    public int addFavorite (String title) {
        for (String movie_title : favoriteMovies) {
            if (movie_title.equals(title)) {
                return 0;
            }
        }
        int result = history.getOrDefault(title, 0);
        if (result == 0) {
            return 1;
        }
        favoriteMovies.add(title);
        return 2;
    }

    public int addView (String title) {
        int nrViews = history.getOrDefault(title, 1);
        if (nrViews != 1) {
            nrViews++;
            history.replace(title, nrViews);
        }
        return nrViews;
    }

    public int isSeen (String title) {
        if (history.containsKey(title)) {
            return 1;
        }
        return 0;
    }
}
