/**
 * An enum for the "locations" of the four quadrants of a block in an image
 * to be compressed to a FourZipTree. Here are some sample uses.
 * <pre>
 *     // Get the child of this node in the quad tree
 *     // that represents the node's upper right quadrant.
 *     node.getChild( Quadrant.UR );
 *
 *     // Create the home coordinate of one of the quadrants based on the
 *     // home of the upper left corner of a size X size box.
 *     Coordinate upperLeftCorner = ...;
 *     int ulRow = upperLeftCorner.getRow();
 *     int ulCol = upperLeftCorner.getCol();
 *     Coordinate lowerLeftCorner =
 *                   new Coordinate(
 *                           ulRow + Quadrant.LL.rowDelta( size / 2 ),
 *                           ulCol + Quadrant.LL.colDelta( size / 2 )
 *                   );
 *
 *     // Loop over all four quadrants.
 *     for ( Quadrant quadrant: Quadrant.values() ) { . . . }
 * </pre>
 *
 * @author James Heliotis
 */
public enum Quadrant {
    /**
     * quadrant II
     */
    UL( 0, 0 ),
    /**
     * quadrant I
     */
    UR( 0, 1 ),
    /**
     * quadrant III
     */
    LL( 1, 0 ),
    /**
     * quadrant IV
     */
    LR( 1, 1 );

    private final int r_off;
    private final int c_off;

    Quadrant( int r_off, int c_off ) {
        this.r_off = r_off;
        this.c_off = c_off;
    }

    /**
     * How far down is this quadrant from the upper-left-hand (UL) corner of
     * the box containing all four quadrants?
     *
     * @param dimension the size (rows or columns) of one dimension
     * @return what to add to the row location of the UL coordinate to
     *         get to this quadrant
     */
    public int rowDelta( int dimension ) {
        return this.r_off * dimension;
    }

    /**
     * How far to the right is this quadrant from the upper-left-hand (UL)
     * corner of the box containing all four quadrants?
     *
     * @param dimension the size (rows or columns) of one dimension
     * @return what to add to the column location of the UL coordinate to
     *         get to this quadrant
     */
    public int colDelta( int dimension ) {
        return this.c_off * dimension;
    }
}
