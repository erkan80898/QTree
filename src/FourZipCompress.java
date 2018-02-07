import java.io.IOException;

/**
 * 4-Zip compressor.  This program takes a raw image file of
 * grayscale values (0-255) and compresses them into the 4-Zip format.
 * It is expected this raw image file is perfectly square and has side
 * dimensions that are powers of 2, e.g.
 * 1x1, 2x2, 4x4, 16x16, 256x256, 512x512, etc.
 *
 * @author Sean Strout, James Heliotis
 */
public class FourZipCompress {

    /**
     * The main routine.
     *
     * @param args two strings: input file name, output file name
     */
    public static void main( String[] args ) {
        if ( args.length != 2 ) {
            System.err.println(
                    "Usage: java FourZipCompress input-file output-file" );
            return;
        }

        try {
            // initialize the matrix
            QTree tree = QTree.rawFromFile( args[ 0 ] );

            // Create the tree.
            tree.compress();

            // display the tree in preorder
            System.out.println( tree );

            // write the compressed tree out to output-file
            tree.writeCompressed( args[ 1 ] );

            // display statistics regarding the compression efficiency
            System.out.println( "Raw image size: " + tree.getRawSize() );
            System.out.println(
                    "Compressed image size: " + tree.getCompressedSize() );
            System.out.println(
                    "Size reduction: " +
                            ( 100.0 *
                                    ( 1 - (double)tree.getCompressedSize() / tree.getRawSize() )
                            )
                            + '%'
            );
        }
        catch( IOException | FourZipException e) {
            System.err.println( e.getMessage() );
        }
    }
}
