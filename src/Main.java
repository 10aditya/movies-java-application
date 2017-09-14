import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Main extends JFrame implements MouseListener {
    private static URL url;
    private static ArrayList<MovieModel> movies;
    private static String backdropURL, posterURL;
    private static URL url2;
    private static ArrayList<MovieModel> movies2;
    private static JPanel pl;
    private static GridBagConstraints gridBagConstraints;
    private static GridBagConstraints constraints;
    private JButton button;
    private JTextArea searchArea;
    private static JPanel sc = new JPanel();
    private ArrayList<MovieModel> searchResult;

    private Main() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }

    private static ArrayList<MovieModel> getJSON(String data) throws JSONException, java.text.ParseException {
        final String RESULT_ARRAY = "results";
        final String MOVIE_TITLE = "title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";
        final String POSTER_URL = "poster_path";
        final String BACKDROP_URL = "backdrop_path";
        final String POPULARITY = "popularity";
        final String ID = "id";

        ArrayList<MovieModel> movieItems = new ArrayList<>();

        JSONObject jsonData = new JSONObject(data);
        JSONArray resultArray = jsonData.getJSONArray(RESULT_ARRAY);

        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject movieItem = resultArray.getJSONObject(i);
            String movieTitle = movieItem.get(MOVIE_TITLE) == null ? null : movieItem.getString(MOVIE_TITLE);
            String releaseDate = movieItem.get(RELEASE_DATE) == null ? null : movieItem.getString(RELEASE_DATE);
            String overView = movieItem.get(OVERVIEW) == null ? null : movieItem.getString(OVERVIEW);
            double rating = movieItem.getDouble(RATING);
            double popularity = movieItem.getDouble(POPULARITY);

            try {
                posterURL = movieItem.getString(POSTER_URL);
                backdropURL = movieItem.getString(BACKDROP_URL);
            } catch (Exception ignored) {

            }
            int id = movieItem.getInt(ID);

            movieItems.add(new MovieModel(movieTitle,
                    releaseDate, overView, "https://image.tmdb.org/t/p/original" + posterURL,
                    "https://image.tmdb.org/t/p/original" + backdropURL, id, (float) rating, (float) popularity));
        }
        return movieItems;
    }

    static String getJSONstr(String link) throws JSONException, ParseException {
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String JSONstr = null;
        try {
            HttpURLConnection client = (HttpURLConnection) url.openConnection();

            client.setRequestMethod("GET");

            client.connect();

            InputStream inputStream = client.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONstr = builder.toString();

            System.out.println(JSONstr);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONstr;
    }

    public static void main(String[] a) throws IOException, JSONException, ParseException {


        NativeInterface.open();

        String linkToFirst20Movies = Constants.BASE_URL
                + Constants.DISCOVER
                + Constants.MOVIE_SEGMENT
                + Constants.API_KEY_PARAM
                + Constants.API_KEY
                + "&release_date.gte=2017-05-01&release_date.lte=2017-12-22";

        String linkToNext20Movies = Constants.BASE_URL
                + Constants.DISCOVER
                + Constants.MOVIE_SEGMENT
                + Constants.API_KEY_PARAM
                + Constants.API_KEY
                + "&release_date.gte=2017-05-01&release_date.lte=2017-12-22&page=2";

        movies = getJSON(getJSONstr(linkToFirst20Movies));
        //movies2 = getJSON(getJSONstr(linkToNext20Movies));

        for (MovieModel movy : movies) {
            System.out.println(movy.getTitle());
        }

        JFrame frame = new JFrame("Movies");
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Discover"));

        sc.setLayout(new GridLayout(1, 2));
        container.setLayout(new GridBagLayout());
        frame.add(sc, BorderLayout.CENTER);
        constraints = new GridBagConstraints();
        constraints.gridy = constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(10, 0, 10, 10);
        int i = 1;

        for (MovieModel model : movies) {
            MovieCard movieCard = null;
            try {
                movieCard = new MovieCard(model);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (movieCard != null) {
                container.add(movieCard.getPanel(), constraints);
            }
            i++;
            if (i > 4) break;
            constraints.gridx = (constraints.gridx + 1) % 2;
            if (constraints.gridx == 0) constraints.gridy++;
        }

        container.setBackground(Color.LIGHT_GRAY);
        constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        JScrollPane jScrollPane = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5) + 300, Constants.dimension.height));

        sc.add(jScrollPane, constraints);


        Box box = Box.createHorizontalBox();
        box.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5) + 200, Constants.dimension.height));
        box.add(jScrollPane);
        Box box1 = Box.createHorizontalBox();

        box1.setBackground(Color.LIGHT_GRAY);
        box1.add(Box.createRigidArea(new Dimension(20, 50)));
        JTextArea searchArea = new JTextArea();
        searchArea.setBackground(Color.WHITE);
        searchArea.setPreferredSize(new Dimension(200, 50));

        JPanel na = new JPanel();
        na.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.gridx = constraints.gridy = 0;
        na.add(new Main().getSearchPanel(), gridBagConstraints);

        Box box2 = Box.createHorizontalBox();
        box2.add(box);
        pl = new Main().getSearchPanel();
        box2.add(pl, gridBagConstraints);
        sc.add(box2, constraints);
        frame.add(sc);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        System.out.println("Hello Java");


        NativeInterface.runEventPump();
        Runtime.getRuntime().addShutdownHook(new Thread(NativeInterface::close));
    }

    private JPanel getSearchPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(450, 100));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridx = constraints.gridy = 0;
        searchArea = new JTextArea();
        searchArea.setBackground(Color.WHITE);
        searchArea.setFont(searchArea.getFont().deriveFont(18.0f));
        searchArea.setPreferredSize(new Dimension(250, 35));
        panel.add(searchArea);
        button = new JButton("Search");
        button.addMouseListener(this);
        constraints.gridy = 1;
        panel.add(button);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        String searchQuery = searchArea.getText().trim();
        String linkToSearchMovie = Constants.BASE_URL
                + Constants.SEARCH
                + Constants.MOVIE_SEGMENT
                + Constants.API_KEY_PARAM
                + Constants.API_KEY
                + Constants.QUERY_PARAM
                + searchQuery;


        try {
            searchResult = getJSON(getJSONstr(linkToSearchMovie));
        } catch (JSONException | ParseException e1) {
            e1.printStackTrace();
        }

        JFrame frame = new JFrame("Movies");

        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        for (MovieModel model : searchResult) {
            try {
                MovieCard card = new MovieCard(model);
                container.add(card.getPanel(), c);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            c.gridy = c.gridy + 1;
        }

        container.setBackground(Color.LIGHT_GRAY);

        JScrollPane pane = new JScrollPane(container, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(pane);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setExtendedState(MAXIMIZED_BOTH);

        frame.setVisible(true);

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
