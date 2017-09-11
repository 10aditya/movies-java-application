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
    private static ArrayList<MovieModel> movieItems = new ArrayList<>();
    private static String backdropURL;
    private static URL url2;
    private static ArrayList<MovieModel> movies2;
    private static JPanel pl;
    private static GridBagConstraints gridBagConstraints;
    private static GridBagConstraints constraints;
    private JButton button;
    private JTextArea searchArea;
    private static JPanel sc = new JPanel();

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

        JSONObject jsonData = new JSONObject(data);
        JSONArray resultArray = jsonData.getJSONArray(RESULT_ARRAY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String status = null;
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject movieItem = resultArray.getJSONObject(i);
            String movieTitle = movieItem.getString(MOVIE_TITLE);
            String releaseDate = movieItem.getString(RELEASE_DATE);
            String overView = movieItem.getString(OVERVIEW);
            double rating = movieItem.getDouble(RATING);
            double popularity = movieItem.getDouble(POPULARITY);
            String posterURL = movieItem.getString(POSTER_URL);
            try {
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

    public static void main(String[] a) throws IOException {
        /*final String BASE_URL = "http://api.themoviedb.org/3/";
        final String Movie_SEGEMENT = "movie";
        final String API_KEY = "78649d641d4f004c03de3691a37fdfa2";
        final String API_KEY_PARAM = "api_key";
        final String DISCOVER = "discover";*/

        NativeInterface.open();
        try {
            url = new URL("https://api.themoviedb.org/3/discover/movie?api_key="+Constants.API_KEY+"&release_date.gte=2017-05-01&release_date.lte=2017-12-22");
            url2 = new URL("https://api.themoviedb.org/3/discover/movie?api_key="+Constants.API_KEY+"&release_date.gte=2017-05-01&release_date.lte=2017-12-22&page=2");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            HttpURLConnection client2 = (HttpURLConnection) url2.openConnection();

            client.setRequestMethod("GET");
            client2.setRequestMethod("GET");

            client.connect();
            client2.connect();

            InputStream inputStream = client.getInputStream();
            InputStream inputStream2 = client2.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
            StringBuilder builder2 = new StringBuilder();

            String line, line2;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            while ((line2 = reader2.readLine()) != null) {
                builder2.append(line2);
            }

            String JSONstr = builder.toString();
            String JSONstr2 = builder2.toString();

            movies = getJSON(JSONstr);
            movies2 = getJSON(JSONstr2);
            System.out.println(JSONstr);
            // return getJSON(JSONstr);
        } catch (IOException | ParseException | JSONException e) {
            e.printStackTrace();
        }


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
         //   if (i == 4) break;
            constraints.gridx = (constraints.gridx + 1) % 2;
            if (constraints.gridx == 0) constraints.gridy++;
        }
        container.setBackground(Color.LIGHT_GRAY);
        constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        JScrollPane jScrollPane = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //jScrollPane.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        //jScrollPane.setAlignmentY(JScrollPane.TOP_ALIGNMENT);
        jScrollPane.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5) + 300, Constants.dimension.height));

        //constraints.gridwidth = (int) (Constants.dimension.width * 0.5) + 400;
        sc.add(jScrollPane, constraints);


        Box box = Box.createHorizontalBox();
        box.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5) + 200, Constants.dimension.height));
        box.add(jScrollPane);
        Box box1 = Box.createHorizontalBox();

        box1.setBackground(Color.LIGHT_GRAY);
        //box.setPreferredSize(new Dimension(250, 50));
        box1.add(Box.createRigidArea(new Dimension(20, 50)));
        JTextArea searchArea = new JTextArea();
        searchArea.setBackground(Color.WHITE);
        searchArea.setPreferredSize(new Dimension(200, 50));

        //  box1.add(getSearchPanel());

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
        // don't forget to properly close native components
        Runtime.getRuntime().addShutdownHook(new Thread(NativeInterface::close));

    }

    private JPanel getSearchPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(450, 100));
        //panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridx = constraints.gridy = 0;
        searchArea = new JTextArea();
        searchArea.setBackground(Color.WHITE);
        searchArea.setPreferredSize(new Dimension(250, 25));
        panel.add(searchArea);
        button = new JButton("Search");
        // button.setPreferredSize(new Dimension(75, 25));
        button.addMouseListener(this);
        constraints.gridy = 1;
        panel.add(button);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        String searchQuery = searchArea.getText().trim();
        try {
            url = new URL("https://api.themoviedb.org/3/search/movie?api_key="+Constants.API_KEY+"&query=" + searchQuery);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        HttpURLConnection client;
        try {
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");


            client.connect();

            InputStream inputStream = client.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String JSONstr = builder.toString();

            JSONObject jsonData = new JSONObject(JSONstr);
            JSONstr = jsonData.toString();

            ArrayList<MovieModel> mo = getJSON(JSONstr);
            constraints.gridy = 1;
            constraints.gridx = 1;
            sc.add(new MovieCard(mo.get(0)), constraints);

        } catch (IOException | JSONException | ParseException ee) {
            ee.printStackTrace();
        }


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
