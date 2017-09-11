import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.ExecutionException;

class ImageLoader extends SwingWorker<BufferedImage, String> {


    private final String imageURL;
    private final JLabel label;
    private final int which;

    ImageLoader(JLabel label, String imageURL, int which) {
        this.label = label;
        this.imageURL = imageURL;
        this.which = which;
    }

    @Override
    protected BufferedImage doInBackground() throws Exception {
        return imageURL != null ? ImageIO.read(new URL(imageURL)) : null;
    }

    @Override
    protected void done() {
        super.done();

        int w, h;
        if (which == 1) {
            w = 400;
            h = 175;
        } else if (which == 2) {
            w = 150;
            h = 200;
        } else if (which == 3) {
            w = (int) (Constants.dimension.width * 0.5) - 100;
            h = (int) (Constants.dimension.height * 0.5) - 90;
        } else {
            w = 100;
            h = 100;
        }
        try {
            BufferedImage bufferedImage = get();
            if (bufferedImage == null) return;
            BufferedImage finalImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
            Image temp = new ImageIcon(bufferedImage).getImage();


            if (which == 1) {
                Image image = temp.getScaledInstance(2000, 1750, Image.SCALE_SMOOTH);
                finalImage.getGraphics().drawImage(image, 0, 0, w, h, 0, 0, image.getWidth(null), (int) (image.getHeight(null) * 0.6), null);
                label.setIcon(new ImageIcon(finalImage));
            } else {
                Image image = new ImageIcon(bufferedImage).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(image));
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }
}
