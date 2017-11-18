import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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



    //Use pre-order
    public void uncompress() throws FourZipException{
        rawImage = new int[dim][dim];
        uncompress(Coordinate.ORIGIN,dim,root);
    }

}
