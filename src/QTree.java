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

    public static QTree compressedFromFile(String fileName) throws IOException{

    }


}
