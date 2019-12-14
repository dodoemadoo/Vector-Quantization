import java.util.ArrayList;

class CompressOutput
{
    ArrayList<Block> codeBook;
    int[][] compressed;
    CompressOutput(ArrayList<Block> codeBook, int[][] compressed)
    {
        this.codeBook = codeBook;
        this.compressed = compressed;
    }
}
