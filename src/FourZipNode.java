import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a node in a 4-Zip tree for an image compressed
 * using the FourZip file format.
 * <p>
 * A node contains a value which is either a grayscale color (0-255) for a
 * region, or QTree.QUAD_SPLIT meaning the region this node represents does
 * not hold a single color and thus has split itself into 4 sub-regions.
 *
 * @author James Heliotis
 */
public class FourZipNode {

    /**
     * The node's value: -1 if there are children, grayscale number o/w
     */
    private final int value;

    /**
     * If children, a mapping of each quadrant to its child node
     */
    private final Map< Quadrant, FourZipNode> children;

    /**
     * Construct a leaf node with no children.
     *
     * @param value the node's grayscale value
     */
    public FourZipNode(int value ) {
        this.value = value;
        this.children = null;
    }

    /**
     * Construct an interior node with four children.
     * Note: order of the children is important. When compressing or
     * decompressing, be sure to use the order signified by this
     * method's parameter, be it an array or four individual arguments.
     *<dl>
     * <dt>ul(#0)</dt><dd>the upper left sub-node</dd>
     * <dt>ur(#1)</dt><dd>the upper right sub-node</dd>
     * <dt>ll(#2)</dt><dd>the lower left sub-node</dd>
     * <dt>lr(#3)</dt><dd>the lower right sub-node</dd>
     *</dl>
     * @param children the array, or sequence of,
     *                 already-initialized child nodes
     */
    public FourZipNode(FourZipNode... children ) {
        this.value = -1;
        this.children = new EnumMap<>( Quadrant.class );
        this.children.put( Quadrant.UL, children[ 0 ] );
        this.children.put( Quadrant.UR, children[ 1 ] );
        this.children.put( Quadrant.LL, children[ 2 ] );
        this.children.put( Quadrant.LR, children[ 3 ] );
    }

    /**
     * Get the node's value. It will be {@link QTree#QUAD_SPLIT} if the
     * node has children.
     *
     * @return node's value
     */
    public int getValue() { return this.value; }

    /**
     * Get one of the child nodes.
     *
     * @param quadrant which quadrant to fetch
     * @return upper left sub-node
     * @throws NullPointerException if this node is a leaf
     */
    public FourZipNode getChild(Quadrant quadrant ) {
        return this.children.get( quadrant );
    }

    /**
     * Get a string suitable for displaying the entire FourZip tree.
     * @see QTree#toString()
     *
     * @return the numerical value in this node, converted to a String
     */
    @Override
    public String toString() {
        return String.valueOf( this.value );
    }
}
