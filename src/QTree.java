import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *This class represents the quadtree data structure, QTree, used to compress raw grayscale images and
 *uncompress back. Conceptually, the tree is a collection of FourZipNode's. A FourZipNode either holds
 *a grayscale rawImage value (0-255), or QUAD_SPLIT, meaning the node is split into four sub-nodes that
 *are equally sized sub-regions that divide up the current space.
 *
 * @author Erkan Uretener @ RIT CS
 */

public class QTree {

    public static final int QUAD_SPLIT = -1;
    private FourZipNode root;
    private int dim;
    private int[][] rawImage;
    private int rawSize;
    private int compressedSize = 0;


    QTree(){
        root = null;
    }

    /**
     *Parse the file being read and find the next FourZipNode subtree. This method is called recursively to read and create the node's children.
     *Recursively speaking, the input file stream contains the root node's value followed when appropriate by the string
     *values of each of its sub-nodes, going in a L-to-R, top-to-bottom order (quadrants UL, UR, LL, LR).
     *@param file - a file that may have already been partially parsed
     *@return the root node of the subtree that has been created
     *@throws IOException - if there is any problem with the file, or file format
     */
    private static FourZipNode parse(BufferedReader file) throws IOException{
        FourZipNode result;
        int value = Integer.parseInt(file.readLine());
        if(value != -1){
            result = new FourZipNode(value);
        }else {
            result = new FourZipNode(parse(file),
                    parse(file),
                    parse(file),
                    parse(file));
        }
        return result;
    }

    /**
     *This is the core routine for uncompressing an image stored in a file into its raw image (a 2-D array of grayscale values (0-255). The main steps are as follows.
     *Open the compressed image file.
     *Read the file size.
     *Build the FourZip tree from the remaining numerical values in the file.
     *There is only one integer value on each line.
     *@param - the name of the file containing the compressed image
     *@return the QTree instance created from the file data
     *@throws IOException - if something goes wrong with the file, including formatting errors.
     */
    public static QTree compressedFromFile(String fileName) throws IOException{
        QTree theQTree = new QTree();
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            theQTree.rawSize = Integer.parseInt(reader.readLine());
            theQTree.dim = (int)(Math.sqrt(theQTree.rawSize));
            theQTree.root = parse(reader);
        }
        return theQTree;
    }

    /**
     * A preorder (parent, left, right) traversal of a node. It returns a string
     * which is empty if the node is null. Otherwise it returns a
     * string that concatenates the current node's value with the values
     * of the 4 sub-regions (with spaces between).
     * @param node - the node being traversed on
     * @return the string of the node
     */
    private String preorder(FourZipNode node){
        if(node == null) return null;
        String result = "";
        if(node.getValue() != QUAD_SPLIT){
            result += node.getValue() +" ";
        }
        if(node.getValue() == -1) {
            result += "( ";
            result += preorder(node.getChild(Quadrant.UL));
            result += preorder(node.getChild(Quadrant.UR));
            result += preorder(node.getChild(Quadrant.LL));
            result += preorder(node.getChild(Quadrant.LR));
            result += ") ";
        }
        return result;

    }

    /**
     * Return a string that represents a preorder traversal of the tree. The node's (grayscale) image value is
     * returned as a decimal string. However when the node's value is QUAD_SPLIT that value is not shown.
     * Instead a left parenthesis is added before the children's to-string methods are called,
     * and a right parenthesis is added afterwards. Spaces are inserted between all items.
     * @return the qtree string representation
     */
    public String toString(){
        String result = preorder(root);
        return result;
    }

    /**
     * Get the size of the raw image.
     * @return raw image size
     * @throws FourZipException - if the raw image does not exist (yet)
     */
    public int getRawSize() throws FourZipException{
        if (rawImage == null){
            throw new FourZipException("The raw image does not exist");
        }else{
            return rawSize;
        }
    }


    /**
     * Get the raw image.
     * @return the raw image.
     * @throws FourZipException - if the raw image does not exist (yet)
     */
    public int[][] getRawImage() throws FourZipException{
        if(rawImage == null){
            throw new FourZipException("The raw image does not exist");
        }else{
            return rawImage;
        }

    }


    /**
     * Get the image's square dimension.
     * @return the square dimension
     */
    public int getSideDim(){
            return dim;
    }

    /**
     * Convert a subtree of the FourZip tree into a square section of the raw image matrix.
     * The main idea is that we are working with a tree whose root represents the entire 2^n x 2^n rawImage.
     * @param coord - the coordinate of the upper left corner of the square to be filled
     * @param dim2 - both the length and width of the square to be filled
     * @param node - the root of the FourZip subtree that will be converted
     */
    private void uncompress(Coordinate coord, int dim2, FourZipNode node){
        if(node.getValue() != -1){
            for(int i = coord.getRow();i<coord.getRow()+dim2;i++){
                for(int j = coord.getCol();j<coord.getCol()+dim2;j++){
                    rawImage[i][j] = node.getValue();
                }
            }

        }else{
            uncompress(new Coordinate(coord.getRow(),coord.getCol()),dim2/2,node.getChild(Quadrant.UL));
            uncompress(new Coordinate(coord.getRow(),coord.getCol()+dim2/2),dim2/2,node.getChild(Quadrant.UR));
            uncompress(new Coordinate(coord.getRow()+dim2/2,coord.getCol()),dim2/2,node.getChild(Quadrant.LL));
            uncompress(new Coordinate(coord.getRow()+dim2/2,coord.getCol()+dim2/2),dim2/2,node.getChild(Quadrant.LR));

        }
    }

    /**
     * Load a raw image. The input file is ASCII text. It contains a series of grayscale values
     * as decimal numbers (0-255). The dimension is assumed square,
     * and is computed from the length of file. There is one value per line.
     * @param inputFile - the name of the file representing the raw image
     * @return the QTree instance created from the raw data
     * @throws IOException - if there are issues working with the file
     * @post The raw image array is filled in, along, as well as the side size and full size.
     */

    public static QTree rawFromFile(String inputFile) throws IOException{
        QTree theQTree = new QTree();
        Path path = Paths.get(inputFile);
        long lineCount = Files.lines(path).count();
        theQTree.rawSize = (int)lineCount;
        theQTree.dim = (int)Math.sqrt(theQTree.rawSize);
        theQTree.rawImage = new int[theQTree.dim][theQTree.dim];
        try(BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            for(int i = 0;i<theQTree.dim;i++){
                for(int j = 0;j<theQTree.dim;j++){
                    if((line = reader.readLine())!=null)
                    theQTree.rawImage[i][j] = Integer.parseInt(line);
                }
            }

        }
        return theQTree;
    }


    /**
     * Check to see whether a region in the raw image contains the same value.
     * This routine is used by the private compress routine so that it can construct the nodes in the tree.
     * @param start - the starting coordinate in the region
     * @param size - the size of the region
     * @return whether the region can be compressed or not
     */
    private boolean canCompressBlock(Coordinate start, int size){
        if(size == 1){
            return true;
        }
        int value = -1;
        int side = (int)Math.sqrt(size);
        for (int i = start.getRow();i<start.getRow()+side;i++){
            for (int j = start.getCol();j<start.getCol()+side;j++){
                if(value==rawImage[i][j] || value == -1){
                    value = rawImage[i][j];
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This is the core compression routine. Its job is to work over a region of the rawImage and compress it.
     * @param start - the start coordinate for this region
     * @param size - the size this region represents
     * @return
     */
    private FourZipNode compress(Coordinate start, int size){
        if(canCompressBlock(start,size)){
            root = new FourZipNode(rawImage[start.getRow()][start.getCol()]);
        }else{
            root = new FourZipNode(compress(new Coordinate(start.getRow(),start.getCol()),(int)((Math.sqrt(size)/2)*(Math.sqrt(size))/2)),
                    compress(new Coordinate(start.getRow(),start.getCol()+(int)(Math.sqrt(size)/2)),(int)((Math.sqrt(size)/2)*(Math.sqrt(size))/2)),
                    compress(new Coordinate(start.getRow()+(int)(Math.sqrt(size)/2),start.getCol()),(int)((Math.sqrt(size)/2)*(Math.sqrt(size))/2)),
                    compress(new Coordinate(start.getRow()+(int)(Math.sqrt(size)/2),start.getCol()+(int)(Math.sqrt(size)/2)),(int)((Math.sqrt(size)/2)*(Math.sqrt(size))/2)));
        }
        return root;
    }

    /**
     * Compress a raw image file already read in to this object.
     * @throws FourZipException - if there is no raw image (yet)
     */
    public void compress() throws FourZipException{
        if(rawImage == null){
            throw new FourZipException("The raw image does not exist");
        }
        compress(Coordinate.ORIGIN,rawSize);
    }

    /**
     * Create the uncompressed image from the internal FourZip tree.
     * @throws FourZipException - if not compressed image has been read in.
     * @post getRawImage() and getRawSize() are now legal to be called.
     */
    public void uncompress() throws FourZipException{
        if(root == null){
            throw new FourZipException("Compressed image has been read in");
        }
        rawImage = new int[dim][dim];
        uncompress(Coordinate.ORIGIN,dim,root);
    }


    /**
     * Write the compressed rawImage to the output file.
     * This routine is meant to be called from a client after it has been compressed
     * @param outFile - the name of the file to write the compressed rawImage to
     * @throws IOException - any errors involved with writing the file out
     * @throws FourZipException - if the file has not been compressed yet
     * @pre client has called compress() to compress the input file
     */
    public void writeCompressed(String outFile) throws IOException, FourZipException{
        if(root == null){
            throw new FourZipException("Compressed image has been read in");
        }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))){
            writer.write(Integer.toString(rawSize)+"\n");
            writeCompressed(root,writer);
        }
    }

    /**
     * The private writer is a recursive helper routine that writes out the compressed rawImage.
     * It goes through the tree in preorder fashion writing out the values of each node as they are encountered.
     * @param node - the current node in the tree
     * @param writer - the writer to write the node data out to
     * @throws IOException - if there are issues with the writer
     */
    private void writeCompressed(FourZipNode node, BufferedWriter writer) throws IOException{
        if(node != null) {
            writer.write(node.getValue() + "\n");
            compressedSize += 1;
            if (node.getValue() == -1) {
                writeCompressed(node.getChild(Quadrant.UL), writer);
                writeCompressed(node.getChild(Quadrant.UR), writer);
                writeCompressed(node.getChild(Quadrant.LL), writer);
                writeCompressed(node.getChild(Quadrant.LR), writer);
            }
        }
    }

    /**
     * Get the size of the compressed rawImage.
     * @return compressed rawImage size
     * @throws FourZipException - if an image has not been compressed or no compressed image has been read in
     */
    public int getCompressedSize() throws FourZipException{
        if(root == null){
            throw new FourZipException("Compressed image has been read in");
        }else {
            compressedSize += 1;
            return compressedSize;
        }
    }
}
