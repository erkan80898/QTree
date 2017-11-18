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
        String result = "";
        result += node.getValue() +"\n";
        if(node.getValue() == -1) {
            result += preorder(node.getChild(Quadrant.UL));
            result += preorder(node.getChild(Quadrant.UR));
            result += preorder(node.getChild(Quadrant.LL));
            result += preorder(node.getChild(Quadrant.LR));

        }
        return result;

    }

    public String toString(){
        String result = preorder(root);
        return result;
    }


/*
    //Legal after uncompress has been called
    public int getRawSize() throws FourZipException{
        if (rawImage == null){
            throw new FourZipException("The raw image does not exist");
        }else{
            return root.
        }

    }

    //Legal after uncompress has been called
    public int[][] getRawImage() throws FourZipException{
        if(rawImage == null){
            throw new FourZipException("The raw image does not exist");
        }else{

        }

    }

    private void uncompress(Coordinate coord, int dim2, FourZipNode node){

    }


    //Use pre-order
    public void uncompress() throws FourZipException{
        uncompress(Coordinate.ORIGIN,);
    }
*/
}
