import java.io.Serializable;

/**
 * Created by Aditya on 27/06/2016.
 */
class MovieModel implements Serializable {
    private String title, releaseDate, overview, posterURL, backdropURL;


    private int id;
    private float rating, popularity;

    MovieModel(String movieTitle, String releaseDate, String overView, String s, String s1, int id, float rating, float popularity) {
        this.title = movieTitle;
        this.releaseDate = releaseDate;
        this.overview = overView;
        this.rating = rating;
        this.popularity = popularity;
        this.backdropURL = s1;
        this.posterURL = s;
        this.id = id;
    }


    String getTitle() {
        return title;
    }

    String getReleaseDate() {
        return releaseDate;
    }

    String getOverview() {
        return overview;
    }

    String getPosterURL() {
        return posterURL;
    }

    String getBackdropURL() {
        return backdropURL;
    }

    int getId() {
        return id;
    }

    float getRating() {
        return rating;
    }

    float getPopularity() {
        return popularity;
    }
}
