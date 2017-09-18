import java.awt.Dimension;
import java.awt.Toolkit;

class Constants {

    final static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    final static String API_KEY = "API_KEY";
    final static String BASE_URL = "http://api.themoviedb.org/3/";
    final static String MOVIE_SEGMENT = "movie";
    final static String API_KEY_PARAM = "?api_key=";
    final static String QUERY_PARAM = "&query=";
    final static String DISCOVER = "discover/";
    final static String SEARCH = "search/";
    final static String VIDEO_SEGMENT = "/videos";
    final static String LANGUAGE_QUERY = "&language=en-US";
    final static String REVIEW_SEGMENT = "/reviews";
    static final String CREDITS_SEGMENT = "/credits";
}
