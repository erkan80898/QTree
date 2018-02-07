import java.io.IOException;

/**
 * 4-Zip uncompressor. This program takes a 4-Zip-compressed file,
 * uncompresses it, and then displays the image using the provided GrayPicViewer.
 *
 * @author Sean Strout, James Heliotis
 */
public class FourZipUncompress {

    /**
     * The main routine.
     *
     * @param args an array with a single string holding the file name
     */
    public static void main( String[] args ) {
        if ( args.length != 1 ) {
            System.err.println( "Usage: FourZipUncompress filename" );
            return;
        }

        try {
            // Initialize with the compressed image file
            QTree tree = QTree.compressedFromFile( args[ 0 ] );
            System.out.println(tree.toString());

            // uncompress the tree
            tree.uncompress();

            // print the tree in preorder
            System.out.println( tree );

            // create a separate viewer and pass it the raw image data
            GrayPicViewer view =
                    new GrayPicViewer( tree.getRawImage(), tree.getSideDim() );

            // finally display the image
            view.display( args[ 0 ] );

        }
        catch( IOException | FourZipException e) {
            System.err.println( e.getMessage() );
        }
    }
}
