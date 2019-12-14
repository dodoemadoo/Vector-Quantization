import java.util.ArrayList;

public class Main {

    public static void main(String[] args)
    {
	// write your code here

        int codeBookSize = 8;
        int blockLingth = 5;

        int[][] matrix = ImageClass.readImage("/home/sorcerer/Desktop/MultiM/dog.jpg");

        ArrayList<Block> blocks = cutTheMatrix(matrix, blockLingth);

        int blocksPerRow = matrix.length/blockLingth;
        int blocksPerColumn = matrix[0].length/blockLingth;

        Quantizer vq = new Quantizer(blockLingth);
        CompressOutput co = vq.compress(blocks, codeBookSize, blocksPerRow, blocksPerColumn);

        ImageClass.writeImage(co.compressed,"D:\\Original-Dog.jpg");

        int[][] decompressed = vq.decompress(co, blocksPerRow, blocksPerColumn);

        ImageClass.writeImage(decompressed, "D:\\Output-Dog.jpg");
    }
    private static ArrayList<Block> cutTheMatrix(int matrix[][], int blockLingth){
        ArrayList<Block> blocks = new ArrayList<>();

        int blocksPerRow = matrix.length/blockLingth;
        int blocksPerColumn = matrix[0].length/blockLingth;

        for(int i = 0; i < blocksPerRow; i++)
        {
            for(int j = 0; j < blocksPerColumn; j++) {
                Block b = new Block(blockLingth);
                int bi = 0; int bj = 0;
                for(int r = i; r < i+blockLingth; r++){
                    for(int c = j; c < j+blockLingth; c++){
                        b.matrix[bi++][bj] = matrix[i*(blockLingth-1)+r][j*(blockLingth-1)+c];
                    }
                    bi = 0;
                    bj++;
                }
                blocks.add(b);
            }
        }
        return blocks;
    }
}
