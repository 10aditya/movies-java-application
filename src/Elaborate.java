import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import org.eclipse.swt.internal.C;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

class Elaborate extends JFrame {
    private static URL url;
    private static MovieModel model;
    private String trailerID;

    Elaborate(MovieModel model) throws MalformedURLException {
        Elaborate.model = model;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("YouTube Viewer");
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.gridy = constraints.gridx = 0;
            try {
                panel.add(getLeftPanel());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            constraints.gridx = 1;
            try {
                panel.add(getRightPanel(), constraints);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            frame.getContentPane().add(panel, BorderLayout.NORTH);
            frame.setLocationByPlatform(true);
            //  frame.setBackground(Color.WHITE);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.setVisible(true);

        });

    }

    private JPanel getBrowserPanel() {


        String url = Constants.BASE_URL
                + Constants.MOVIE_SEGMENT
                + "/"
                + String.valueOf(model.getId())
                + Constants.VIDEO_SEGMENT
                + Constants.API_KEY_PARAM
                + Constants.API_KEY
                + Constants.LANGUAGE_QUERY;


        String JSONstr;
        try {
            JSONstr = Main.getJSONstr(url);
            JSONObject jsonData = new JSONObject(JSONstr);
            JSONArray resultArray = jsonData.getJSONArray("results");
            JSONObject item = resultArray.getJSONObject(0);
            trailerID = item.getString("key");

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        JPanel webBrowserPanel = new JPanel(new BorderLayout());
        JWebBrowser webBrowser = new JWebBrowser();
        webBrowser.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5) - 100,
                400));
        webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
        webBrowser.setBarsVisible(false);
        String link = "https://www.youtube.com/watch_popup?v=" + trailerID;
        webBrowser.navigate(link);
        return webBrowserPanel;
    }

    private JPanel getMovieDetailPanel() {

        JLabel movieTitle = new JLabel(model.getTitle().trim());
        JLabel movieReleaseDate = new JLabel(model.getReleaseDate().trim()
                + "       \uD83D\uDC4D " + String.valueOf((int) (model.getRating() * 10) + "%"));

        JTextArea area = new JTextArea(model.getOverview());
        area.setLineWrap(true);
        area.setEditable(false);
        area.setWrapStyleWord(true);
        area.setBackground(Color.WHITE);
        area.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5) - 125, 125));
        area.setFont(area.getFont().deriveFont(16.0f));
        area.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Overview"));

        movieTitle.setFont(movieTitle.getFont().deriveFont(16.0f));
        movieReleaseDate.setFont(movieReleaseDate.getFont().deriveFont(16.0f));
        JLabel moviePoster = new JLabel(new ImageIcon(
                Toolkit.getDefaultToolkit().getImage("loading.jpg")
                        .getScaledInstance(150,
                                200,
                                Image.SCALE_SMOOTH)));

        new ImageLoader(moviePoster, model.getPosterURL(), 2).execute();

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridy = constraints.gridx = 0;
        panel.add(movieTitle, constraints);
        constraints.gridy = 1;
        panel.add(movieReleaseDate, constraints);
        constraints.gridy = 2;
        panel.add(area, constraints);

        JPanel panel2 = new JPanel();
        panel2.setBackground(Color.WHITE);
        panel2.setLayout(new GridBagLayout());
        constraints.gridx = constraints.gridy = 0;
        panel2.add(moviePoster, constraints);
        constraints.gridx = 1;
        panel2.add(panel, constraints);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = constraints.gridy = 0;
        mainPanel.add(panel2, constraints);

        return mainPanel;
    }

    private JPanel getLeftPanel() throws JSONException {
        JLabel backDropPoster = new JLabel(
                new ImageIcon(Toolkit.getDefaultToolkit().getImage("loading.jpg")
                        .getScaledInstance((int) (Constants.dimension.width * 0.5) - 100,
                                (int) (Constants.dimension.height * 0.5) - 100,
                                Image.SCALE_SMOOTH)),
                SwingConstants.CENTER);
        new ImageLoader(backDropPoster, model.getBackdropURL(), 3).execute();

        JPanel trailerPanel = getBrowserPanel();

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridx = constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        leftPanel.add(backDropPoster, constraints);
        constraints.gridy = 1;
        constraints.insets = new Insets(5, 5, 5, 0);
        leftPanel.add(trailerPanel, constraints);

        return leftPanel;
    }

    private JPanel getRightPanel() throws JSONException {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = constraints.gridy = 0;
        rightPanel.add(getMovieDetailPanel(), constraints);
        constraints.gridy = 1;
        JPanel panel = getReviewsPanel();
        if (panel != null)
            rightPanel.add(panel, constraints);
        constraints.gridy = 2;
        JScrollPane pane = getCrewPanel();
        if (pane != null)
            rightPanel.add(pane, constraints);
        return rightPanel;
    }

    private JPanel getReviewsPanel() {

        JPanel reviewPanel = null;
        String url = Constants.BASE_URL
                + Constants.MOVIE_SEGMENT
                + "/"
                + String.valueOf(model.getId())
                + Constants.REVIEW_SEGMENT
                + Constants.API_KEY_PARAM
                + Constants.API_KEY
                + Constants.LANGUAGE_QUERY;

        String JSONstr;
        JSONArray resultArray = null;
        try {
            JSONstr = Main.getJSONstr(url);
            JSONObject jsonData = new JSONObject(JSONstr);
            resultArray = jsonData.getJSONArray("results");


            assert resultArray != null;
            if (resultArray.length() == 0) return null;

            reviewPanel = new JPanel();
            reviewPanel.setLayout(new GridBagLayout());
            reviewPanel.setBackground(Color.WHITE);

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.insets = new Insets(5, 5, 5, 5);
            constraints.gridx = constraints.gridy = 0;

            for (int i = 0; i < resultArray.length() && i < 3; i++) {
                JSONObject item = resultArray.getJSONObject(i);
                JTextArea jTextArea = new JTextArea(50, 15);
                jTextArea.setLineWrap(true);
                jTextArea.setText(item.getString("content"));
                jTextArea.setEditable(false);
                jTextArea.setWrapStyleWord(true);
                jTextArea.setBackground(Color.WHITE);
                jTextArea.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5 / 3 + 5), 200));
                jTextArea.setFont(jTextArea.getFont().deriveFont(16.0f));
                jTextArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        "Review by " + item.getString("author")));
                JPanel panel = new JPanel();
                panel.add(jTextArea);
                JScrollPane pane = new JScrollPane(jTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                pane.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5 / 3 + 5), 200));
                pane.getVerticalScrollBar().setValue(0);
                reviewPanel.add(pane, constraints);
                constraints.gridx++;
            }
        } catch (JSONException | ParseException e1) {
            e1.printStackTrace();
        }

        return reviewPanel;
    }

    private JScrollPane getCrewPanel() throws JSONException {
        JPanel crewPanel = new JPanel();

        String url = Constants.BASE_URL
                + Constants.MOVIE_SEGMENT
                + "/"
                + String.valueOf(model.getId())
                + Constants.CREDITS_SEGMENT
                + Constants.API_KEY_PARAM
                + Constants.API_KEY
                + Constants.LANGUAGE_QUERY;
        try {

            String JSONstr = Main.getJSONstr(url);

            JSONObject jsonData = new JSONObject(JSONstr);
            JSONArray resultArray = jsonData.getJSONArray("crew");

            if (resultArray.length() == 0) return null;

            crewPanel.setLayout(new GridBagLayout());
            crewPanel.setBackground(Color.WHITE);

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.insets = new Insets(5, 5, 5, 5);
            constraints.gridx = constraints.gridy = 0;

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject item = resultArray.getJSONObject(i);
                try {
                    switch (item.getString("job")) {
                        case "Producer":
                        case "Editor":
                        case "Animation":
                        case "Director": {
                            crewPanel.add(getPeoplePanel(item.getString("name"), item.getString("job"),
                                    item.getString("profile_path"), 0), constraints);
                            constraints.gridx++;
                        }
                    }

                } catch (Exception ignored) {

                }
            }
            resultArray = jsonData.getJSONArray("cast");
            for (int i = 0; i != 10; i++) {
                JSONObject item = resultArray.getJSONObject(i);
                crewPanel.add(getPeoplePanel(item.getString("name"),
                        item.getString("character"),
                        item.get("profile_path") == null ? null : String.valueOf(item.get("profile_path")),
                        1),
                        constraints);
                constraints.gridx++;
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(crewPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension((int) (Constants.dimension.width * 0.5) + 50, 250));

        return scrollPane;
    }

    private JPanel getPeoplePanel(String name, String job, String photoPath, int t) {
        JLabel namePanel = new JLabel(name);
        JLabel jobPanel = new JLabel(job);
        JLabel as = new JLabel("as");
        JLabel photo = new JLabel(
                new ImageIcon(Toolkit.getDefaultToolkit().getImage("default contact.jpg")
                        .getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        new ImageLoader(photo, "https://image.tmdb.org/t/p/original" + photoPath, 4).execute();

        JPanel peoplePanel = new JPanel();
        peoplePanel.setPreferredSize(new Dimension(200, 200));
        peoplePanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridy = constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.CENTER;

        peoplePanel.add(photo, constraints);
        constraints.gridy = 1;
        peoplePanel.add(namePanel, constraints);
        if (t == 1) {
            constraints.gridy = 2;
            peoplePanel.add(as, constraints);
        }

        constraints.gridy++;
        peoplePanel.add(jobPanel, constraints);

        return peoplePanel;

    }
}