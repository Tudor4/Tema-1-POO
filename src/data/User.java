package data;

import java.util.ArrayList;
import java.util.Map;


public final class User {
    private final String username;

    private final String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private int numberOfRatings;

    private ArrayList<String> ratedTitles;


    public User(final String username, final String subscriptionType,
                         final Map<String, Integer> history,
                         final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.favoriteMovies = favoriteMovies;
        this.history = history;
        this.numberOfRatings = 0;
        this.ratedTitles = new ArrayList<>();
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

    public ArrayList<String> getRatedTitles() { return ratedTitles;}

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
        int nrViews = history.getOrDefault(title, 0);
        if (nrViews != 0) {
            nrViews++;
            history.replace(title, nrViews);
        } else {
            history.put(title, 1);
            nrViews = 1;
        }
        return nrViews;
    }

    public int isSeen (String title) {
        if (history.containsKey(title)) {
            return 1;
        }
        return 0;
    }

    public boolean isRated (String title) {
        if (ratedTitles.contains(title)) {
            return true;
        }
        return false;
    }
}
