package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import fileio.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import data.User;
import data.Actor;
import data.Movie;
import data.Serial;
import data.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        //TODO add here the entry point to your implementation

        List<Actor> actors = new ArrayList<>();       // listele in care vor fi stocate datele
        List<Movie> movies = new ArrayList<>();
        List<Serial> serials = new ArrayList<>();
        List<User> users = new ArrayList<>();

        for (ActorInputData actor : input.getActors()) {
            Actor actor2 = new Actor(actor.getName(), actor.getCareerDescription(),
                    actor.getFilmography(), actor.getAwards());
            actors.add(actor2);
        }

        for (MovieInputData movie : input.getMovies()) {
            Movie movie2 = new Movie(movie.getTitle(), movie.getCast(), movie.getGenres(),
                    movie.getYear(), movie.getDuration());
            movies.add(movie2);
        }

        for (SerialInputData serial : input.getSerials()) {
            Serial serial2 = new Serial(serial.getTitle(), serial.getCast(), serial.getGenres(),
                    serial.getNumberSeason(), serial.getSeasons(), serial.getYear());
            serials.add(serial2);
        }

        for (UserInputData user : input.getUsers()) {
            User user2 = new User(user.getUsername(), user.getSubscriptionType(), user.getHistory(),
                                  user.getFavoriteMovies());
            users.add(user2);
        }

        Data data = new Data(actors, users, movies, serials);
        JSONObject object;

        for (ActionInputData command : input.getCommands()) {                   // scheletul programului
            if (command.getActionType().equals("command")) {                    // se determina ce tip de comanda este,
                if (command.getType().equals("favorite")) {                     // se apeleaza metoda corespunzatoare
                    for (User user : data.getUsers()) {                         // si se afiseaza mesajul
                        if (user.getUsername().equals(command.getUsername())) {
                            int result = user.addFavorite(command.getTitle());
                            if (result == 0) {
                                object = fileWriter.writeFile(command.getActionId(), "", "error -> "
                                        + command.getTitle() + " is already in favourite list");
                            } else if (result == 1) {
                                object = fileWriter.writeFile(command.getActionId(), "", "error -> "
                                        + command.getTitle() + " is not seen");
                            } else {
                                object = fileWriter.writeFile(command.getActionId(), "",
                                        "success -> "
                                        + command.getTitle() + " was added as favourite");
                            }
                            arrayResult.add(object);
                        }
                    }
                } else if (command.getType().equals("view")) {
                    for (User user : data.getUsers()) {
                        if (user.getUsername().equals(command.getUsername())) {
                            int result = user.addView((command.getTitle()));
                            object = fileWriter.writeFile(command.getActionId(), "",
                                    "success -> " + command.getTitle()
                                            + " was viewed with total views of " + result);
                            arrayResult.add(object);
                        }
                    }
                } else {
                    for (User user : data.getUsers()) {
                        if (user.getUsername().equals(command.getUsername())) {
                            int result = user.isSeen(command.getTitle());
                            if (result == 1) {
                                if (command.getSeasonNumber() == 0) {
                                    for (Movie movie : data.getMovies()) {
                                        if (movie.getTitle().equals(command.getTitle())) {
                                            if (!user.isRated(movie.getTitle())) {
                                                user.getRatedTitles().add(movie.getTitle());
                                                movie.addRating(command.getGrade());
                                                object = fileWriter.writeFile(command.getActionId(),
                                                        "", "success -> "
                                                        + command.getTitle()
                                                                + " was rated with "
                                                                + command.getGrade()
                                                                + " by "
                                                        + command.getUsername());
                                            } else {
                                                object = fileWriter.writeFile(command.getActionId(),
                                                        "", "error -> " + command.getTitle()
                                                                + " has been already rated");
                                            }
                                            arrayResult.add(object);
                                        }
                                    }
                                } else {
                                    for (Serial serial : data.getSerials()) {
                                        if (serial.getTitle().equals(command.getTitle())) {
                                            if (!user.isRated(serial.getTitle()
                                                    + command.getSeasonNumber())) {
                                                user.getRatedTitles().add(serial.getTitle()
                                                        + command.getSeasonNumber());
                                                serial.addRating(command.getGrade(),
                                                        command.getSeasonNumber());
                                                object = fileWriter.writeFile(command.getActionId(),
                                                        "", "success -> "
                                                        + command.getTitle()
                                                                + " was rated with "
                                                                + command.getGrade()
                                                                + " by "
                                                        + command.getUsername());
                                            } else {
                                                object = fileWriter.writeFile(command.getActionId(),
                                                        "", "error -> "
                                                        + command.getTitle()
                                                                + " has been already rated");
                                            }
                                            arrayResult.add(object);
                                        }
                                    }
                                }
                                user.setNumberOfRatings(user.getNumberOfRatings() + 1);
                            } else {
                                object = fileWriter.writeFile(command.getActionId(), "", "error -> "
                                        + command.getTitle() + " is not seen");
                                arrayResult.add(object);
                            }
                        }
                    }
                }
            } else if (command.getActionType().equals("query")) {
                if (command.getObjectType().equals("actors")) {
                    if (command.getCriteria().equals("average")) {
                        List<String> result = data.average(command.getNumber(),
                                command.getSortType());
                        object = fileWriter.writeFile(command.getActionId(), "", "Query result: "
                                + result);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("awards")) {
                        List<String> result = data.awards(command.getFilters().get(3),
                                command.getSortType());
                        object = fileWriter.writeFile(command.getActionId(), "", "Query result: "
                                + result);
                        arrayResult.add(object);
                    } else {
                        List<String> result = data.filterDescription(command.getFilters().get(2),
                                command.getSortType());
                        object = fileWriter.writeFile(command.getActionId(), "", "Query result: "
                                + result);
                        arrayResult.add(object);
                    }
                } else if (command.getObjectType().equals("users")) {
                    List<String> result = data.userQuery(command.getNumber(),
                            command.getSortType());
                    object = fileWriter.writeFile(command.getActionId(), "", "Query result: "
                            + result);
                    arrayResult.add(object);
                } else {
                    if (command.getCriteria().equals("ratings")) {
                        List<String> result = data.ratingQuery(command.getNumber(),
                                command.getFilters().get(0),
                                command.getFilters().get(1), command.getSortType(),
                                command.getObjectType());
                        object = fileWriter.writeFile(command.getActionId(), "", "Query result: "
                                + result);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("favorite")) {
                        List<String> result = data.favorite(command.getNumber(),
                                command.getFilters().get(0),
                                command.getFilters().get(1), command.getSortType(),
                                command.getObjectType());
                        object = fileWriter.writeFile(command.getActionId(), "", "Query result: "
                                + result);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("most_viewed")) {
                        List<String> result = data.mostViewed(command.getNumber(),
                                command.getFilters().get(0),
                                command.getFilters().get(1), command.getSortType(),
                                command.getObjectType());
                        object = fileWriter.writeFile(command.getActionId(), "", "Query result: "
                                + result);
                        arrayResult.add(object);
                    } else {
                        List<String> result = data.longest(command.getNumber(),
                                command.getFilters().get(0),
                                command.getFilters().get(1), command.getSortType(),
                                command.getObjectType());
                        object = fileWriter.writeFile(command.getActionId(), "", "Query result: "
                                + result);
                        arrayResult.add(object);
                    }
                }

            } else {
                if (command.getType().equals("standard")) {
                    String result = data.standard(command.getUsername());
                    if (result != null) {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "StandardRecommendation result: " + result);
                    } else {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "StandardRecommendation cannot be applied!");
                    }
                    arrayResult.add(object);
                } else if (command.getType().equals("best_unseen")) {
                    String result = data.bestUnseen(command.getUsername());
                    if (result != null) {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "BestRatedUnseenRecommendation result: " + result);
                    } else {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "BestRatedUnseenRecommendation cannot be applied!");
                    }
                    arrayResult.add(object);
                } else if (command.getType().equals("popular")) {
                    String result = data.popular(command.getUsername());
                    if (result != null) {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "PopularRecommendation result: " + result);
                    } else {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "PopularRecommendation cannot be applied!");
                    }
                    arrayResult.add(object);
                } else if (command.getType().equals("favorite")) {
                    String result = data.favoriteRecommendation(command.getUsername());
                    if (result != null) {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "FavoriteRecommendation result: " + result);
                    } else {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "FavoriteRecommendation cannot be applied!");
                    }
                    arrayResult.add(object);
                } else {
                    List<String> result = data.search(command.getUsername(), command.getGenre());
                    if (result.size() == 0) {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "SearchRecommendation cannot be applied!");
                    } else {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "SearchRecommendation result: " + result);
                    }
                    arrayResult.add(object);
                }
            }
        }
        fileWriter.closeJSON(arrayResult);
    }
}
