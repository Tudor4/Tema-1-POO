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
                    if (movieRatings != 0) {
                        movieRatings = movieRatings / movie.getRatings().size();
                    }
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
                if (!actor.getCareerDescription().toLowerCase().contains(" " + filters.get(index) + " ")) {
                    if (!actor.getCareerDescription().toLowerCase().contains(" " + filters.get(index) + ".")) {
                        if (!actor.getCareerDescription().toLowerCase().contains(" " + filters.get(index) + ",")) {
                            if (!actor.getCareerDescription().toLowerCase().contains("-" + filters.get(index) + " ")) {
                                ok = false;
                            } else {
                                index++;
                            }
                        } else {
                            index++;
                        }
                    } else {
                        index++;
                    }
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
                        videoRatings.put(movie.getTitle(), ratingSum / movie.getRatings().size());
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
                    double serialRating = 0;
                    for (Season season : serial.getSeasons()) {
                        double seasonRating = 0;
                        for (Double rating : season.getRatings()) {
                            seasonRating = seasonRating + rating;
                        }
                        if (seasonRating != 0) {
                            seasonRating = seasonRating / season.getRatings().size();
                        }
                        serialRating = serialRating + seasonRating;
                    }
                    if (serialRating != 0) {
                        videoRatings.put(serial.getTitle(), serialRating / serial.getNumberSeason());
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
                int size;
                if(genreFilters.get(0) == null) {
                    size = 0;
                } else {
                    size = genreFilters.size();
                }
                while (ok && index < size) {
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == size) {
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
                int size;
                if (genreFilters.get(0) == null) {
                    size = 0;
                } else {
                    size = genreFilters.size();
                }
                while (ok && index < size) {
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == size) {
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
                int size;
                if (genreFilters.get(0) == null) {
                    size = 0;
                } else {
                    size = genreFilters.size();
                }
                while (ok && index < size) {
                    if (!serial.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == size) {
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
                int size;
                if (genreFilters.get(0) == null) {
                    size = 0;
                } else {
                    size = genreFilters.size();
                }
                while (ok && index < size) {
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == size) {
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
                int size;
                if (genreFilters.get(0) == null) {
                    size = 0;
                } else {
                    size = genreFilters.size();
                }
                while (ok && index < size) {
                    if (!serial.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == size) {
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

    public String standard (String username) {
        String result = null;
        for (User user : usersData) {
            if (user.getUsername().equals(username)) {
                for (Movie movie : moviesData) {
                    if (!user.getHistory().containsKey(movie.getTitle())) {
                        result = movie.getTitle();
                        return result;
                    }
                }
                for (Serial serial : serialsData) {
                    if (!user.getHistory().containsKey(serial.getTitle())) {
                        result = serial.getTitle();
                        return result;
                    }
                }
            }
        }
        return result;
    }

    public String bestUnseen (String username) {
        String result = null;
        HashMap<String, Double> videoRatings = new HashMap<>();
        List<String> zeroRating = new ArrayList<>();
        for (Movie movie : moviesData) {
            double movieRating = 0;
            for (Double rating : movie.getRatings()) {
                movieRating += rating;
            }
            if (movieRating != 0) {
                movieRating /= movie.getRatings().size();
            }
            if (movieRating != 0 ) {
                videoRatings.put(movie.getTitle(), movieRating);
            } else {
                zeroRating.add(movie.getTitle());
            }
        }
        for (Serial serial : serialsData) {
            double serialRating = 0;
            for (Season season : serial.getSeasons()) {
                double seasonRating = 0;
                for (double rating : season.getRatings()) {
                    seasonRating += rating;
                }
                if (seasonRating != 0) {
                    seasonRating /= season.getRatings().size();
                }
                serialRating += seasonRating;
            }
            if (serialRating != 0) {
                videoRatings.put(serial.getTitle(), serialRating / serial.getNumberSeason());
            } else {
                zeroRating.add(serial.getTitle());
            }
        }
        Map<String, Double> sortedRatings = videoRatings.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> sortedVideos = new ArrayList<>();
        for(Map.Entry<String, Double> entry : sortedRatings.entrySet()) {
            sortedVideos.add(entry.getKey());
        }
        for(String title : zeroRating) {
            sortedVideos.add(0, title);
        }
        for (User user : usersData) {
            if (user.getUsername().equals(username)) {
                int index = sortedVideos.size() - 1;
                while (index != -1) {
                    if(!user.getHistory().containsKey(sortedVideos.get(index))) {
                        result = sortedVideos.get(index);
                        return result;
                    }
                    index --;
                }
            }
        }
        return result;
    }

    public String popular(String username) {
        String result = null;
        HashMap<String, Integer> popularGenres = new HashMap<>();
        for (User user : usersData) {
            for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
                for (Movie movie : moviesData) {
                    if (movie.getTitle().equals(entry.getKey())) {
                        for (String genre : movie.getGenres()) {
                            if (!popularGenres.containsKey(genre)) {
                                popularGenres.put(genre, entry.getValue());
                            } else {
                                int value = popularGenres.get(genre);
                                value += entry.getValue();
                                popularGenres.put(genre, value);
                            }
                        }
                    }
                }
                for (Serial serial : serialsData) {
                    if (serial.getTitle().equals(entry.getKey())) {
                        for (String genre : serial.getGenres()) {
                            if (!popularGenres.containsKey(genre)) {
                                popularGenres.put(genre, entry.getValue());
                            } else {
                                int value = popularGenres.get(genre);
                                value += entry.getValue();
                                popularGenres.put(genre, value);
                            }
                        }
                    }
                }
            }
        }
        Map<String, Integer> sortedGenres = popularGenres.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> genres = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : sortedGenres.entrySet()) {
            genres.add(entry.getKey());
        }
        for (User user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getSubscriptionType().equals("BASIC")) {
                    return result;
                } else {
                    int index = genres.size() - 1;
                    while (index != - 1) {
                        for (Movie movie : moviesData) {
                            if (movie.getGenres().contains(genres.get(index))) {
                                if(!user.getHistory().containsKey(movie.getTitle())) {
                                    result = movie.getTitle();
                                    return result;
                                }
                            }
                        }
                        for (Serial serial : serialsData) {
                            if (serial.getGenres().contains(genres.get(index))) {
                                if(!user.getHistory().containsKey(serial.getTitle())) {
                                    result = serial.getTitle();
                                    return result;
                                }
                            }
                        }
                        index --;
                    }
                }
            }
        }
        return result;
    }

    public String favoriteRecommendation (String username) {
        String result = null;
        LinkedHashMap<String, Integer> favoriteVideos = new LinkedHashMap<>();
        List<String> titles = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (Movie movie : moviesData) {
            int nrFavorites = 0;
            for (User user : usersData) {
                if(user.getFavoriteMovies().contains(movie.getTitle())) {
                    nrFavorites++;
                }
            }
            titles.add(movie.getTitle());
            values.add(nrFavorites);
        }
        for (Serial serial : serialsData) {
            int nrFavorites = 0;
            for (User user : usersData) {
                if (user.getFavoriteMovies().contains(serial.getTitle())) {
                    nrFavorites++;
                }
            }
            titles.add(serial.getTitle());
            values.add(nrFavorites);
        }
        while(!titles.isEmpty()) {
            favoriteVideos.put(titles.get(titles.size() - 1), values.get(values.size() - 1));
            titles.remove(titles.size() - 1);
            values.remove(values.size() - 1);
        }
        Map<String, Integer> sortedFavorites = favoriteVideos.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> favorites = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : sortedFavorites.entrySet()) {
            favorites.add(entry.getKey());
        }
        for (User user : usersData) {
            if (user.getUsername().equals(username)) {
                if(user.getSubscriptionType().equals("BASIC")) {
                    return result;
                }
                int index = favorites.size() - 1;
                while (index != -1) {
                    if(!user.getHistory().containsKey(favorites.get(index))) {
                        result = favorites.get(index);
                        return result;
                    }
                    index--;
                }
            }
        }
        return result;
    }

    public List<String> search(String username, String genre) {
        List<String> result = new ArrayList<>();
        TreeMap<String, Double> videoRatings = new TreeMap<>();
        for (Movie movie : moviesData) {
            double movieRating = 0;
            for (Double rating : movie.getRatings()) {
                movieRating += rating;
            }
            if (movieRating != 0) {
                movieRating /= movie.getRatings().size();
            }
            if (movie.getGenres().contains(genre)) {
                videoRatings.put(movie.getTitle(), movieRating);
            }
        }
        for (Serial serial : serialsData) {
            double serialRating = 0;
            for (Season season : serial.getSeasons()) {
                double seasonRating = 0;
                for (double rating : season.getRatings()) {
                    seasonRating += rating;
                }
                if (seasonRating != 0) {
                    seasonRating /= season.getRatings().size();
                }
                serialRating += seasonRating;
            }
            if (serial.getGenres().contains(genre)) {
                videoRatings.put(serial.getTitle(), serialRating / serial.getNumberSeason());
            }
        }
        Map<String, Double> sortedRatings = videoRatings.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new ));
        List<String> sortedVideos = new ArrayList<>();
        for(Map.Entry<String, Double> entry : sortedRatings.entrySet()) {
            sortedVideos.add(entry.getKey());
        }
        for (User user : usersData) {
            if(user.getUsername().equals(username)) {
                if(user.getSubscriptionType().equals("BASIC")) {
                    return result;
                }
                int index = 0;
                while(index != sortedVideos.size()) {
                    if(!user.getHistory().containsKey(sortedVideos.get(index))) {
                        result.add(sortedVideos.get(index));
                    }
                    index++;
                }
            }
        }
        return result;
    }
}
