import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;

class MovieCard extends JPanel implements MouseListener {

    private final JPanel card;
    private final MovieModel model;

    MovieCard(MovieModel movieModel) throws IOException {

        this.model = movieModel;
        JLabel movieTitle = new JLabel(movieModel.getTitle().trim());
        JLabel movieAvgVote = new JLabel();
        JLabel movieReleaseDate = new JLabel(movieModel.getReleaseDate().trim()
                + "       \uD83D\uDC4D " + String.valueOf((int) (movieModel.getRating() * 10) + "%"));

        movieTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        movieAvgVote.setFont(movieAvgVote.getFont().deriveFont(16.0f));
        movieTitle.setFont(movieAvgVote.getFont().deriveFont(16.0f));
        movieReleaseDate.setFont(movieAvgVote.getFont().deriveFont(16.0f));

        JLabel moviePoster = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("loading.jpg")), SwingConstants.CENTER);

        card = new JPanel(new GridBagLayout());

        JPanel movieDetail = new JPanel(new GridBagLayout());
        movieDetail.setBackground(Color.white);
        int w = 400;
        movieDetail.setPreferredSize(new Dimension(w, 75));
        card.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.insets = new Insets(5, 5, 5, 5);
        bagConstraints.gridx = 0;
        bagConstraints.gridy = 0;
        bagConstraints.anchor = GridBagConstraints.NORTHWEST;

        Box details = Box.createVerticalBox();

        Box b = Box.createHorizontalBox();
        b.add(Box.createRigidArea(new Dimension(10, 10)));
        b.add(movieTitle);
        b.add(Box.createRigidArea(new Dimension(w, 10)));

        details.add(b);
        details.add(Box.createVerticalStrut(15));

        Box box = Box.createHorizontalBox();
        box.add(Box.createRigidArea(new Dimension(10, 10)));
        box.add(movieReleaseDate);
        box.add(Box.createRigidArea(new Dimension(w, 10)));
        details.add(box);
        //bagConstraints.insets = new Insets(0,10,0,0);
        movieDetail.add(details, bagConstraints);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        movieDetail.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        card.add(moviePoster, constraints);

        constraints.gridy = 1;

        card.add(movieDetail, constraints);
        new ImageLoader(moviePoster, movieModel.getBackdropURL(), 1).execute();

        card.addMouseListener(this);

    }


    Component getPanel() {
        return card;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            new Elaborate(this.model);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
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

