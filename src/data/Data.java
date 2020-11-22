package data;

import actor.ActorsAwards;
import entertainment.Season;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<String> average(int number, String sortType) {
        TreeMap<String, Double> actorRatings = new TreeMap<>();
        for (Actor actor : actorsData) {
            double ratingSum = 0;
            int numberOfVideos = 0;
            for (Movie movie : moviesData) {
                double movieRatings = 0;
                if (movie.getCast().contains(actor.getName())) {
                    for (Double rating : movie.getRatings()) {
                        movieRatings = movieRatings + rating;
                    }
                    movieRatings = movieRatings / movie.getRatings().size();
                    if (movieRatings != 0) {
                        numberOfVideos++;
                        ratingSum = ratingSum + movieRatings;
                    }
                }
            }
            for (Serial serial : serialsData) {
                if (serial.getCast().contains(actor.getName())) {
                    double serialRatings = 0;
                    for(Season season : serial.getSeasons()) {
                        double seasonRatings = 0;
                        for (Double rating : season.getRatings()) {
                            seasonRatings = seasonRatings + rating;
                        }
                        if (seasonRatings != 0) {
                            seasonRatings = seasonRatings / season.getRatings().size();
                        }
                        serialRatings = serialRatings + seasonRatings;
                    }
                    if (serialRatings != 0) {
                        numberOfVideos++;
                        ratingSum = ratingSum + serialRatings / serial.getNumberSeason();
                    }
                }
            }
            double average = ratingSum / numberOfVideos;
            if (ratingSum != 0) {
                actorRatings.put(actor.getName(), average);
            }
        }
        Map<String, Double> sortedRatings = actorRatings.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> result = new ArrayList<>();
        List<String> partialResult = new ArrayList<>();
        int size = sortedRatings.size();
        for (Map.Entry<String, Double> entry : sortedRatings.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (!sortType.equals("asc")) {
            int index = partialResult.size() - 1;
            while (number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index --;
                number--;
            }
        } else {
            int index = 0;
            while (number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                number--;
            }
        }
        return result;
    }

    public List<String> awards(List<String> awards, String sortType) {
        TreeMap<String, Integer> actorAwards = new TreeMap<>();
        int size = awards.size();
        for (Actor actor : actorsData) {
            int numberOfAwards = 0;
            boolean ok = true;
            int index = 0;
            while (ok && index < awards.size()) {
                if (!actor.getAwards().containsKey(Utils.stringToAwards(awards.get(index)))) {
                    ok = false;
                } else {
                    index++;
                }
            }
            if (index == size) {
                for (Map.Entry<ActorsAwards, Integer> entry : actor.getAwards().entrySet()) {
                    numberOfAwards = numberOfAwards + entry.getValue();
                }
            }
            if (numberOfAwards != 0) {
                actorAwards.put(actor.getName(), numberOfAwards);
            }
        }
        Map<String, Integer> sortedAwards = actorAwards.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> result = new ArrayList<>();
        List<String> partialResult = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedAwards.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {
            return partialResult;
        } else {
            int index = partialResult.size() - 1;
            while (index != -1) {
                result.add(partialResult.get(index));
                index--;
            }
            return result;
        }
    }

    public List<String> filterDescription (List<String> filters, String sortType) {
        TreeMap<String, Integer> filteredActors = new TreeMap<>();
        for (Actor actor : actorsData) {
            boolean ok = true;
            int index = 0;
            while (ok && index < filters.size()) {
                if (!actor.getCareerDescription().contains(filters.get(index))) {
                    ok = false;
                } else {
                    index++;
                }
            }
            if (index == filters.size()) {
                filteredActors.put(actor.getName(), 1);
            }
        }
        List<String> result = new ArrayList<>();
        List<String> partialResult = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : filteredActors.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {
            return partialResult;
        } else {
            int index = partialResult.size() - 1;
            while (index != -1) {
                result.add(partialResult.get(index));
                index--;
            }
            return result;
        }
    }

    public List<String> userQuery (int number, String sortType) {
        TreeMap<String, Integer> nrOfRatings = new TreeMap<>();
        for (User user : usersData) {
            if (user.getNumberOfRatings() != 0) {
                nrOfRatings.put(user.getUsername(), user.getNumberOfRatings());
            }
        }
        List<String> partialResult = new ArrayList<>();
        Map<String, Integer> sortedNrOfRatings = nrOfRatings.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        for (Map.Entry<String, Integer> entry : sortedNrOfRatings.entrySet()) {
            partialResult.add(entry.getKey());
        }
        List<String> result = new ArrayList<>();
        if (sortType.equals("asc")) {
            int index = 0;
            while (number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                number--;
            }
        } else {
            int index = partialResult.size() - 1;
            while (number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                number--;
            }
        }
        return result;
    }

    public List<String> ratingQuery (int number, List<String> yearFilters, List<String> genreFilters,
                                     String sortType, String objectType) {
        TreeMap<String, Double> videoRatings = new TreeMap<>();
        if (objectType.equals("movies")) {
            for (Movie movie : moviesData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) {
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == movie.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    double ratingSum = 0;
                    for (Double rating : movie.getRatings()) {
                        ratingSum = ratingSum + rating;
                    }
                    if (ratingSum != 0) {
                        videoRatings.put(movie.getTitle(), ratingSum);
                    }
                }
            }
        } else {
            for (Serial serial : serialsData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) {
                    if (!serial.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == serial.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    double ratingSum = 0;
                    for (Season season : serial.getSeasons()) {
                        for (Double rating : season.getRatings()) {
                            ratingSum = ratingSum + rating;
                        }
                    }
                    if (ratingSum != 0) {
                        videoRatings.put(serial.getTitle(), ratingSum / serial.getNumberSeason());
                    }
                }
            }
        }
        Map<String, Double> sortedRatings = videoRatings.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> partialResult = new ArrayList<>();
        for(Map.Entry<String, Double> entry : sortedRatings.entrySet()) {
            partialResult.add(entry.getKey());
        }
        List<String> result = new ArrayList<>();
        if (sortType.equals("asc")) {
            int index = 0;
            while(number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(0));
                partialResult.remove(index);
                number--;
            }
        } else {
            int index = partialResult.size() - 1;
            while(number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                number--;
            }
        }
        return result;
    }

    public List<String> favorite (int number, List<String> yearFilters, List<String> genreFilters,
                                  String sortType, String objectType) {
        TreeMap<String, Integer> favorites = new TreeMap<>();
        if (objectType.equals("movies")) {
            for (Movie movie : moviesData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) {
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == movie.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    int nrFavorites = 0;
                    for (User user : usersData) {
                        if (user.getFavoriteMovies().contains(movie.getTitle())) {
                            nrFavorites++;
                        }
                    }
                    if (nrFavorites != 0) {
                        favorites.put(movie.getTitle(), nrFavorites);
                    }
                }
            }
        } else {
            for (Serial serial : serialsData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) {
                    if (!serial.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == serial.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    int nrFavorites = 0;
                    for (User user : usersData) {
                        if (user.getFavoriteMovies().contains(serial.getTitle())) {
                            nrFavorites++;
                        }
                    }
                    if (nrFavorites != 0) {
                        favorites.put(serial.getTitle(), nrFavorites);
                    }
                }
            }
        }
        Map<String, Integer> sortedFavorites = favorites.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> partialResult = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedFavorites.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {
            int index = 0;
            while(number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(0));
                partialResult.remove(index);
                number--;
            }
        } else {
            int index = partialResult.size() - 1;
            while(number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                number--;
            }
        }
        return result;
    }

    public List<String> mostViewed (int number, List<String> yearFilters, List<String> genreFilters,
                                  String sortType, String objectType) {
        TreeMap<String, Integer> mostViewed = new TreeMap<>();
        if (objectType.equals("movies")) {
            for (Movie movie : moviesData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) {
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == movie.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    int nrViews = 0;
                    for (User user : usersData) {
                        if (user.getHistory().containsKey(movie.getTitle())) {
                            nrViews = nrViews + user.getHistory().get(movie.getTitle());
                        }
                    }
                    if (nrViews != 0) {
                        mostViewed.put(movie.getTitle(), nrViews);
                    }
                }
            }
        } else {
            for (Serial serial : serialsData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) {
                    if (!serial.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == serial.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    int nrViews = 0;
                    for (User user : usersData) {
                        if (user.getHistory().containsKey(serial.getTitle())) {
                            nrViews = nrViews + user.getHistory().get(serial.getTitle());
                        }
                    }
                    if (nrViews != 0) {
                        mostViewed.put(serial.getTitle(), nrViews);
                    }
                }
            }
        }
        Map<String, Integer> sortedViewed = mostViewed.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> partialResult = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedViewed.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {
            int index = 0;
            while(number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(0));
                partialResult.remove(index);
                number--;
            }
        } else {
            int index = partialResult.size() - 1;
            while(number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                number--;
            }
        }
        return result;
    }

    public List<String> longest (int number, List<String> yearFilters, List<String> genreFilters,
                                    String sortType, String objectType) {
        TreeMap<String, Integer> longestVideos = new TreeMap<>();
        if (objectType.equals("movies")) {
            for (Movie movie : moviesData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) {
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == movie.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    longestVideos.put(movie.getTitle(), movie.getDuration());
                }
            }
        } else {
            for (Serial serial : serialsData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) {
                    if (!serial.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == serial.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    int duration = 0;
                    for (Season season : serial.getSeasons()) {
                        duration = duration + season.getDuration();
                    }
                    longestVideos.put(serial.getTitle(), duration);
                }
            }
        }
        Map<String, Integer> sortedLongest = longestVideos.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> partialResult = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedLongest.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {
            int index = 0;
            while(number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(0));
                partialResult.remove(index);
                number--;
            }
        } else {
            int index = partialResult.size() - 1;
            while(number != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                number--;
            }
        }
        return result;
    }
}
