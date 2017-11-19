import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QTree {

    public static final int QUAD_SPLIT = -1;
    private FourZipNode root;
    private int dim;
    private int[][] rawImage;
    private int rawSize;
    private int compressedSize;


    QTree(){
        root = null;
    }


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


    public static QTree compressedFromFile(String fileName) throws IOException{
        QTree theQTree = new QTree();
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            theQTree.rawSize = Integer.parseInt(reader.readLine());
            theQTree.dim = (int)(Math.sqrt(theQTree.rawSize));
            theQTree.root = parse(reader);
        }
        return theQTree;
    }

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

    public String toString(){
        String result = preorder(root);
        return result;
    }

    //Legal after uncompress has been called
    public int getRawSize() throws FourZipException{
        if (rawImage == null){
            throw new FourZipException("The raw image does not exist");
        }else{
            return rawSize;
        }

    }

    //Legal after uncompress has been called
    public int[][] getRawImage() throws FourZipException{
        if(rawImage == null){
            throw new FourZipException("The raw image does not exist");
        }else{
            return rawImage;
        }

    }

    public int getSideDim(){
        return dim;
    }

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

    public void compress() throws FourZipException{
        compress(Coordinate.ORIGIN,rawSize);
    }

    //Use pre-order
    public void uncompress() throws FourZipException{
        rawImage = new int[dim][dim];
        uncompress(Coordinate.ORIGIN,dim,root);
    }


    public void writeCompressed(String outFile) throws IOException, FourZipException{
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))){
            writer.write(Integer.toString(rawSize));
            writeCompressed(root,writer);
        }
    }

    private void writeCompressed(FourZipNode node, BufferedWriter writer) throws IOException{
        String result = "";
        if(node.getValue() != QUAD_SPLIT){
            result += node.getValue() +" ";
        }
        if(node.getValue() == -1) {
            preorder(node.getChild(Quadrant.UL));
            preorder(node.getChild(Quadrant.UR));
            preorder(node.getChild(Quadrant.LL));
            preorder(node.getChild(Quadrant.LR));
        }
    }

    public int getCompressedSize() throws FourZipException{
        return 0;
    }
}
