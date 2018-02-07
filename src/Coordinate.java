/**
 * Represents a 2-D coordinate.  This class's objects are immutable.
 *
 * @author James Heliotis
 */
public class Coordinate {

    /** the row */
    private final int row;

    /** the column */
    private final int col;

    /**
     * Create a new coordinate object.
     *
     * @param row the row
     * @param col the column
     */
    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Get the row.
     *
     * @return this coordinate's row
     */
    public int getRow() { return this.row; }

    /**
     * Get the column.
     *
     * @return this coordinate's column
     */
    public int getCol() { return this.col; }

    /**
     * Upper left corner convenience constant
     */
    public static final Coordinate ORIGIN = new Coordinate( 0, 0 );

    /**
     * Returns a string in the format "(row, col)".
     *
     * @return string representation of coordinate
     */
    @Override
    public String toString() {
        return "(" + this.row + ", " + this.col + ")";
    }
}
