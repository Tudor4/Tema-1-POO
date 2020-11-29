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

    /**
     *  Metoda foloseste un treemap pentru a stoca, pentru fiecare actor, rating-ul sau.
     *  Se vor lua in considerare toate rating-urile diferite de 0(film sau serial).
     *  Map-ul este apoi sortat dupa valorea, iar numele actorilor sunt pusi intr-o lista.
     *  La final se afiseaza number actori in ordinea indicata in sortType.
     * @param number
     * @param sortType
     * @return
     */
    public List<String> average(final int number, final String sortType) {
        int numberr = number;
        TreeMap<String, Double> actorRatings = new TreeMap<>();
        for (Actor actor : actorsData) {
            double ratingSum = 0;
            int numberOfVideos = 0;
            for (Movie movie : moviesData) {
                double movieRatings = 0;
                if (movie.getCast().contains(actor.getName())) {  // daca actorul a jucat in film
                    for (Double rating : movie.getRatings()) {
                        movieRatings = movieRatings + rating;
                    }
                    if (movieRatings != 0) {
                        movieRatings = movieRatings / movie.getRatings().size();
                    }
                    if (movieRatings != 0) {
                        numberOfVideos++;
                        ratingSum = ratingSum + movieRatings;  // se aduna la rating-ul total
                    }
                }
            }
            for (Serial serial : serialsData) {
                if (serial.getCast().contains(actor.getName())) {
                    double serialRatings = 0;
                    for (Season season : serial.getSeasons()) {
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
                actorRatings.put(actor.getName(), average);   // se introduce in map actorul
            }
        }
        Map<String, Double> sortedRatings = actorRatings.entrySet().stream() // sortare map
                .sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> result = new ArrayList<>();
        List<String> partialResult = new ArrayList<>();   // lista actori
        int size = sortedRatings.size();
        for (Map.Entry<String, Double> entry : sortedRatings.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (!sortType.equals("asc")) {         // formare rezultat
            int index = partialResult.size() - 1;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                numberr--;
            }
        } else {
            int index = 0;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                numberr--;
            }
        }
        return result;
    }

    /**
     * Se ia fiecare actor din baza de date si i se verifica premiile.
     * Daca le-a obtinut pe toate din lista de awards data, acesta este adaugat intr-un treemp
     * impreuna cu numarul de premii castigate.
     * Map-ul este sortat apoi dupa numarul de premii.
     * Actorii sunt pusi intr-o lista si afisati in ordinea data de sortType
     * @param awards
     * @param sortType
     * @return
     */
    public List<String> awards(final List<String> awards, final String sortType) {
        TreeMap<String, Integer> actorAwards = new TreeMap<>();
        int size = awards.size();
        for (Actor actor : actorsData) {
            int numberOfAwards = 0;
            boolean ok = true;
            int index = 0;
            while (ok && index < awards.size()) {   // verificare premii
                if (!actor.getAwards().containsKey(Utils.stringToAwards(awards.get(index)))) {
                    ok = false;
                } else {
                    index++;
                }
            }
            if (index == size) {     // numar total de premii
                for (Map.Entry<ActorsAwards, Integer> entry : actor.getAwards().entrySet()) {
                    numberOfAwards = numberOfAwards + entry.getValue();
                }
            }
            if (numberOfAwards != 0) {   // daca a obtinut toate premiile este adaugat in map
                actorAwards.put(actor.getName(), numberOfAwards);
            }
        }
        Map<String, Integer> sortedAwards = actorAwards.entrySet().stream() // sortare map
                .sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> result = new ArrayList<>();
        List<String> partialResult = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedAwards.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {   // formare si afisare rezultat
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

    /**
     *  Pentru fiecare actor din baza de date sunt cautate toate cuvintele date ca parametru.
     *  Daca toate cuvintele sunt regasite in descrierea actorului aceste este adaugat intr-un
     *  tree map(deoarece treemap sorteaza automat alfabetic cheile).
     *  La final se afiseaza intr-o lista actorii in ordinea data.
     * @param filters
     * @param sortType
     * @return
     */
    public List<String> filterDescription(final List<String> filters, final String sortType) {
        TreeMap<String, Integer> filteredActors = new TreeMap<>();
        for (Actor actor : actorsData) {
            boolean ok = true;
            int index = 0;
            while (ok && index < filters.size()) {  // verificare cuvinte
                if (!actor.getCareerDescription().toLowerCase().
                        contains(" " + filters.get(index) + " ")) { // in mijlocul textului
                    if (!actor.getCareerDescription().toLowerCase().
                            contains(" " + filters.get(index) + ".")) { // la finalul textului
                        if (!actor.getCareerDescription().toLowerCase().
                                contains(" " + filters.get(index) + ",")) { // intr-o enumeratie
                            if (!actor.getCareerDescription().toLowerCase().
                                    contains("-" + filters.get(index) + " ")) { // cuvant compus
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
            if (index == filters.size()) {   // introducere in map
                filteredActors.put(actor.getName(), 1);
            }
        }
        List<String> result = new ArrayList<>();
        List<String> partialResult = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : filteredActors.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {  // formare si afisare rezultat
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

    /**
     * Metoda ia fiecare user din baza de date si de la acestia preia numarul de raintg-uri date.
     * Daca acel numar este diferit de 0, acesta este adaugat intr-un treemap impreuna cu
     * numarul de rating-uri.
     * Map-ul este sortat dupa numarul de rating-uri, iar apoi userii sunt pusi intr-o lista
     * si afisati in ordinea ceruta.
     * @param number
     * @param sortType
     * @return
     */
    public List<String> userQuery(final int number, final String sortType) {
        int numberr = number;
        TreeMap<String, Integer> nrOfRatings = new TreeMap<>();
        for (User user : usersData) {
            if (user.getNumberOfRatings() != 0) {
                nrOfRatings.put(user.getUsername(), user.getNumberOfRatings());  // introdecere in map
            }
        }
        List<String> partialResult = new ArrayList<>();
        Map<String, Integer> sortedNrOfRatings = nrOfRatings.entrySet() // sortare map
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<String, Integer> entry : sortedNrOfRatings.entrySet()) {
            partialResult.add(entry.getKey());
        }
        List<String> result = new ArrayList<>();
        if (sortType.equals("asc")) {  // formare si afisare rezultat
            int index = 0;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                numberr--;
            }
        } else {
            int index = partialResult.size() - 1;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                numberr--;
            }
        }
        return result;
    }

    /**
     *  Pentru fiecare film si serial din baza de date, mai intai se verificare filtrele
     *  de an si de gen, si apoi se calculeaza rating-ul. Daca rating-ul este diferit de 0,
     *  titul este adaugat, impreuna cu rating-ul, intr-un treemap. Acesta este mai apoi sortat
     *  dupa rating. Titlurile sunt puse intr-o lista si afisate in ordinea ceruta.
     * @param number
     * @param yearFilters
     * @param genreFilters
     * @param sortType
     * @param objectType
     * @return
     */
    public List<String> ratingQuery(final int number, final List<String> yearFilters,
                                     final List<String> genreFilters, final String sortType,
                                     final String objectType) {
        TreeMap<String, Double> videoRatings = new TreeMap<>();
        int numberr = number;
        if (objectType.equals("movies")) {
            for (Movie movie : moviesData) {
                boolean ok = true;
                int index = 0;
                while (ok && index < genreFilters.size()) { // verificare gen
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == genreFilters.size()) {
                    if (yearFilters.get(0) != null) {  // verificare an
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == movie.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    double ratingSum = 0;   // calculare rating
                    for (Double rating : movie.getRatings()) {
                        ratingSum = ratingSum + rating;
                    }
                    if (ratingSum != 0) {  // adaugare in map
                        videoRatings.put(movie.getTitle(), ratingSum / movie.getRatings().size());
                    }
                }
            }
        } else {    // analog ca la filme
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
                        videoRatings.put(serial.getTitle(),
                                serialRating / serial.getNumberSeason());
                    }
                }
            }
        }
        Map<String, Double> sortedRatings = videoRatings.entrySet()    // sortare map
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                .toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> partialResult = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sortedRatings.entrySet()) {
            partialResult.add(entry.getKey());
        }
        List<String> result = new ArrayList<>();
        if (sortType.equals("asc")) { // formare si afisare rezultat
            int index = 0;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(0));
                partialResult.remove(index);
                numberr--;
            }
        } else {
            int index = partialResult.size() - 1;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                numberr--;
            }
        }
        return result;
    }

    /**
     *  Pentru fiecare film sau serial se verifica filtrele de an si de gen. Daca acestea
     *  sunt validate, se calculeaza numarul de ori de care a fost titlul inclus
     *  la favorite(iterand prin toti userii). Titlul este inclus in treemap impreuna cu
     *  acest numar. Map-ul este apoi sortat valoare, iar rezultatul pus intr-o lista si
     *  afisat in ordinea ceruta.
     * @param number
     * @param yearFilters
     * @param genreFilters
     * @param sortType
     * @param objectType
     * @return
     */
    public List<String> favorite(final int number, final List<String> yearFilters,
                                 final List<String> genreFilters, final String sortType,
                                 final String objectType) {
        TreeMap<String, Integer> favorites = new TreeMap<>();
        int numberr = number;
        if (objectType.equals("movies")) {  // pentru filme
            for (Movie movie : moviesData) {
                boolean ok = true;
                int index = 0;
                int size;
                if (genreFilters.get(0) == null) {
                    size = 0;
                } else {
                    size = genreFilters.size();
                }
                while (ok && index < size) {    // verificare gen
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == size) {
                    if (yearFilters.get(0) != null) {   // verificare an
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
                    for (User user : usersData) {   // calculare numar de adaugari la favorite
                        if (user.getFavoriteMovies().contains(movie.getTitle())) {
                            nrFavorites++;
                        }
                    }
                    if (nrFavorites != 0) {   // introducere in map
                        favorites.put(movie.getTitle(), nrFavorites);
                    }
                }
            }
        } else {
            for (Serial serial : serialsData) {   // pentru seriale, analog ca la filme
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
        Map<String, Integer> sortedFavorites = favorites.entrySet()   // sortare map
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> partialResult = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedFavorites.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {   // formare si afisare rezultat
            int index = 0;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(0));
                partialResult.remove(index);
                numberr--;
            }
        } else {
            int index = partialResult.size() - 1;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                numberr--;
            }
        }
        return result;
    }

    /**
     *  Pentru fiecare film sau serial, se verifica filtrele de an si de gen.
     *  Dupa validarea acestora, se calculeaza numarul de vizualizari si se
     *  adauga in treemap, titlul impreuna cu numarul de vizualizari. Se sorteaza
     *  map-ul dupa numarul de vizualizari.
     *  Titlurile sunt incluse apoi intr-o lista si afisare in ordinea ceruta.
     * @param number
     * @param yearFilters
     * @param genreFilters
     * @param sortType
     * @param objectType
     * @return
     */
    public List<String> mostViewed(final int number, final List<String> yearFilters,
                                   final List<String> genreFilters, final String sortType,
                                   final String objectType) {
        TreeMap<String, Integer> mostViewed = new TreeMap<>();
        int numberr = number;
        if (objectType.equals("movies")) {   // filme
            for (Movie movie : moviesData) {
                boolean ok = true;
                int index = 0;
                int size;
                if (genreFilters.get(0) == null) {
                    size = 0;
                } else {
                    size = genreFilters.size();
                }
                while (ok && index < size) {  // verificare gen
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == size) {
                    if (yearFilters.get(0) != null) {  // verificare an
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
                    for (User user : usersData) {   // nr vizualizari
                        if (user.getHistory().containsKey(movie.getTitle())) {
                            nrViews = nrViews + user.getHistory().get(movie.getTitle());
                        }
                    }
                    if (nrViews != 0) {  // adaugare in map
                        mostViewed.put(movie.getTitle(), nrViews);
                    }
                }
            }
        } else {
            for (Serial serial : serialsData) { // pentru seriale, analog cu filmele
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
        Map<String, Integer> sortedViewed = mostViewed.entrySet()   // sortare map
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> partialResult = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedViewed.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {  // formare si afisare rezultat
            int index = 0;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(0));
                partialResult.remove(index);
                numberr--;
            }
        } else {
            int index = partialResult.size() - 1;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                numberr--;
            }
        }
        return result;
    }

    /**
     *  Se verifica filtrele de gen si de an, si apoi se adauga in treemap
     *  titlul imrepuna cu lungimea acestuia. Se sorteaza map-ul dupa durata,
     *  iar apoi titlurile sunt mutate intr-o lista pentru a fi afisare, in ordinea
     *  data ca paramentru.
     * @param number
     * @param yearFilters
     * @param genreFilters
     * @param sortType
     * @param objectType
     * @return
     */
    public List<String> longest(final int number, final List<String> yearFilters,
                                final List<String> genreFilters, final String sortType,
                                final String objectType) {
        TreeMap<String, Integer> longestVideos = new TreeMap<>();
        int numberr = number;
        if (objectType.equals("movies")) {
            for (Movie movie : moviesData) {  // filme
                boolean ok = true;
                int index = 0;
                int size;
                if (genreFilters.get(0) == null) {
                    size = 0;
                } else {
                    size = genreFilters.size();
                }
                while (ok && index < size) {  // verificare gen
                    if (!movie.getGenres().contains(genreFilters.get(index))) {
                        ok = false;
                    } else {
                        index++;
                    }
                }
                ok = false;
                if (index == size) {
                    if (yearFilters.get(0) != null) {  // verificare an
                        int year = Integer.parseInt(yearFilters.get(0));
                        if (year == movie.getYear()) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {      // adaugare in map
                    longestVideos.put(movie.getTitle(), movie.getDuration());
                }
            }
        } else {
            for (Serial serial : serialsData) { // seriale, analog cu filme
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
        Map<String, Integer> sortedLongest = longestVideos.entrySet()   // sortare map
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> partialResult = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedLongest.entrySet()) {
            partialResult.add(entry.getKey());
        }
        if (sortType.equals("asc")) {  // formare si afisare rezultat
            int index = 0;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(0));
                partialResult.remove(index);
                numberr--;
            }
        } else {
            int index = partialResult.size() - 1;
            while (numberr != 0 && !partialResult.isEmpty()) {
                result.add(partialResult.get(index));
                partialResult.remove(index);
                index--;
                numberr--;
            }
        }
        return result;
    }

    /**
     *  se gaseste obiectul user corespunzator username-ului dat si se
     *  itereaza prin toate filmele si serialele pana cand se gaseste unul
     *  nevazut. Cand este gasit, este returnat si apoi afisat.
     * @param username
     * @return
     */
    public String standard(final String username) {
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

    /**
     * Similar cu celelalte comenzi bazate pe rating. Toate videoclipurile, in
     * ordinea din baza de date, sunt adaugate impreuna cu rating-ul intr-un hashmap
     * (nu tree map deoarece vrem sa pastram ordinea din baza de date). Map-ul este sortat
     * dupa rating, iar apoi primul titlu nevazut de utilizator este returnat.
     * @param username
     * @return
     */
    public String bestUnseen(final String username) {
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
        Map<String, Double> sortedRatings = videoRatings.entrySet()
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> sortedVideos = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sortedRatings.entrySet()) {
            sortedVideos.add(entry.getKey());
        }
        for (String title : zeroRating) {
            sortedVideos.add(0, title);
        }
        for (User user : usersData) {
            if (user.getUsername().equals(username)) {
                int index = sortedVideos.size() - 1;
                while (index != -1) {
                    if (!user.getHistory().containsKey(sortedVideos.get(index))) {
                        result = sortedVideos.get(index);
                        return result;
                    }
                    index--;
                }
            }
        }
        return result;
    }

    /**
     *  Utilizam un Hashmap pentru genuri. Acesta contine toate genurile din baza de date,
     *  impreuna cu numarul de vizualizari ale tuturor videoclipurilor din acel gen.
     *  Toate videoclipurile din baza de date sunt luate in ordine si se verifica: daca
     *  face parte din cel mai popular gen la care suntem in prezent si daca acesta a fost
     *  vazut. Primul nevazut din cel mai popular gen posibil este returnat.
     * @param username
     * @return
     */
    public String popular(final String username) {
        String result = null;
        HashMap<String, Integer> popularGenres = new HashMap<>();  // genuri
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
        Map<String, Integer> sortedGenres = popularGenres.entrySet()   // sortare map genuri
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> genres = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedGenres.entrySet()) {
            genres.add(entry.getKey());
        }
        for (User user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getSubscriptionType().equals("BASIC")) {
                    return result;
                } else {
                    int index = genres.size() - 1;
                    while (index != -1) {
                        for (Movie movie : moviesData) {
                            if (movie.getGenres().contains(genres.get(index))) {
                                if (!user.getHistory().containsKey(movie.getTitle())) {
                                    result = movie.getTitle();
                                    return result;
                                }
                            }
                        }
                        for (Serial serial : serialsData) {
                            if (serial.getGenres().contains(genres.get(index))) {
                                if (!user.getHistory().containsKey(serial.getTitle())) {
                                    result = serial.getTitle();
                                    return result;
                                }
                            }
                        }
                        index--;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Toate videoclipurile, in ordinea din baza de date, sunt adaugate in hashmap
     * impreuna cu numarul de adaugari la recomandate. Primul videoclip nevazut este
     * returnar si afisat.
     * @param username
     * @return
     */
    public String favoriteRecommendation(final String username) {
        String result = null;
        LinkedHashMap<String, Integer> favoriteVideos = new LinkedHashMap<>();
        List<String> titles = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (Movie movie : moviesData) {
            int nrFavorites = 0;
            for (User user : usersData) {
                if (user.getFavoriteMovies().contains(movie.getTitle())) {
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
        while (!titles.isEmpty()) {
            favoriteVideos.put(titles.get(titles.size() - 1), values.get(values.size() - 1));
            titles.remove(titles.size() - 1);
            values.remove(values.size() - 1);
        }
        Map<String, Integer> sortedFavorites = favoriteVideos.entrySet()
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> favorites = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedFavorites.entrySet()) {
            favorites.add(entry.getKey());
        }
        for (User user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getSubscriptionType().equals("BASIC")) {
                    return result;
                }
                int index = favorites.size() - 1;
                while (index != -1) {
                    if (!user.getHistory().containsKey(favorites.get(index))) {
                        result = favorites.get(index);
                        return result;
                    }
                    index--;
                }
            }
        }
        return result;
    }

    /**
     *  Toate videoclipurile care fac parte din un anumit gen sunt adaugate intr-un
     *  treemap impreuna cu rating-ul lor(calculat la fel ca in alte metode).
     *  Map-ul este sortat dupa rating. La final toate videoclipurile nevazute sunt
     *  adaugate intr-o lista, aceasta urmand a fi afisata.
     * @param username
     * @param genre
     * @return
     */
    public List<String> search(final String username, final String genre) {
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
        Map<String, Double> sortedRatings = videoRatings.entrySet()
                .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> sortedVideos = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sortedRatings.entrySet()) {
            sortedVideos.add(entry.getKey());
        }
        for (User user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getSubscriptionType().equals("BASIC")) {
                    return result;
                }
                int index = 0;
                while (index != sortedVideos.size()) {
                    if (!user.getHistory().containsKey(sortedVideos.get(index))) {
                        result.add(sortedVideos.get(index));
                    }
                    index++;
                }
            }
        }
        return result;
    }
}
