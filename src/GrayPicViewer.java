import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * A class that visually displays a compressed image that was uncompressed
 * using QTree. It currently uses the older Java Swing library.
 * (and it always will Jim, muwhahahahaha!).
 *
 * @author Sean Strout @ RIT
 */
public class GrayPicViewer extends JPanel {
    /**
     * the raw image of grayscale values (0-255)
     */
    private final int image[][];

    /**
     * the square dimension of the image
     */
    private final int dim;

    /**
     * Construct the viewer
     *
     * @param image the raw image
     * @param dim   the square dimension of image
     */
    public GrayPicViewer( int image[][], int dim ) {
        this.image = image;
        this.dim = dim;
    }

    /**
     * Display the following image.  This causes paintComponent
     * to get called to load the image.
     *
     * @param title the title of the window
     */
    public void display( String title ) {
        setPreferredSize( new Dimension( this.dim, this.dim ) );
        JFrame f = new JFrame();
        f.setTitle( title );
        f.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        f.getContentPane().add( this );
        f.pack();
        f.setVisible( true );
    }

    /**
     * Set the pixel values in the graphics context to the color
     * values in the image.
     *
     * @param g the graphics context we are drawing into
     */
    public void paintComponent( Graphics g ) {
        for ( int row = 0; row < this.dim; row++ ) {
            for ( int col = 0; col < this.dim; col++ ) {
                int c = image[ row ][ col ];
                Color color = new Color( c, c, c );
                g.setColor( color );
                g.fillRect( col, row, 1, 1 );
            }
        }
    }
}
