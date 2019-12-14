import java.util.ArrayList;
import java.util.List;


import java.util.Comparator;


public class Quantizer
{
    int blockLength;
    public Quantizer(int bl)
    {
        blockLength = bl;
    }

    public CompressOutput compress (ArrayList<Block> blockList, int codeBookSize, int blocksPerRow, int blocksPerColumn)
    {
        // transform the ArrayList<Block> int a 2d block matrix
        Block[][] blockMatrix = ListToMatrix(blockList, blocksPerRow, blocksPerColumn);

        Node root = new Node(blockLength);
        root.blockList = blockList;
        root.calculateAverage();

        ArrayList<Node> nodes = Distribute(root, codeBookSize);
        ArrayList <Block> codeBook = new ArrayList<>() ;

        for (Node node : nodes)
        {
            node.calculateAverage();
            node.avg.calculateDistance();
            codeBook.add(node.avg);
        }

        // replace every block with it's corresponding block from code book
        int[][] compressed = new int[blocksPerRow][blocksPerColumn];
        for(int i=0 ; i < blocksPerRow ; i++)
        {
            for(int j=0 ; j < blocksPerColumn; j++)
            {
                int minDistanceNodeIndex = 0;
                int minDistance = 10000;
                for(int n = 0; n < codeBook.size(); n++)
                {
                    if (Math.abs(blockMatrix[i][j].distanceFromZero - codeBook.get(n).distanceFromZero) < minDistance)
                    {
                        minDistanceNodeIndex = n;
                        minDistance = Math.abs(blockMatrix[i][j].distanceFromZero - codeBook.get(n).distanceFromZero);
                    }
                }
                compressed[i][j] = minDistanceNodeIndex;
            }
        }

        return new CompressOutput(codeBook, compressed);
    }

    public int[][] decompress (CompressOutput co, int blocksPerRow, int blocksPerColumn)
    {
        // for every block in QS, replace is with the corresponding block from code block
        ArrayList <Block> codeBook = co.codeBook;
        int[][] QS = co.compressed ;
        int rows = QS.length * blockLength;
        int columns = QS[0].length * blockLength;

        int[][] decompressed = new int[columns][rows];

        for (int i = 0 ; i< blocksPerRow; i++ )
        {
            for (int j=0 ; j < blocksPerColumn ; j++ )
            {
                Block b = codeBook.get(QS[i][j]);
                for(int r = 0; r < blockLength; r++)
                {
                    for(int c = 0; c < blockLength; c++)
                    {
                        decompressed[j*blockLength+c][i*blockLength+r] = b.matrix[r][c];
                    }
                }
            }
        }
        return decompressed;
    }

    public ArrayList<Node> Distribute (Node root , int codeBookSize)
    {
        ArrayList<Block> fullBlockList = root.blockList;
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(root);
        while( nodes.size() < codeBookSize )
        {
            ArrayList<Node> newNodes = new ArrayList<Node>();
            for(Node node : nodes)
            {
                node.calculateAverage();
                node.left  = new Node(blockLength);            node.left.avg = node.avg.sub(1);
                node.right = new Node(blockLength);            node.right.avg = node.avg.add(1);
                newNodes.add(node.left);            newNodes.add(node.right);
                node.left.calculateAverage();       node.right.calculateAverage();
                node.left.avg.calculateDistance();  node.right.avg.calculateDistance();
            }
            for(Block block : fullBlockList)
            {
                int minDistanceNodeIndex = 0;
                int minDistance = 10000;
                for(int n = 0; n < newNodes.size(); n++)
                {
                    if( Math.abs(newNodes.get(n).avg.distanceFromZero - block.distanceFromZero) < minDistance)
                    {
                        minDistanceNodeIndex = n;
                        minDistance = Math.abs(newNodes.get(n).avg.distanceFromZero - block.distanceFromZero);
                    }
                }
                newNodes.get(minDistanceNodeIndex).blockList.add(block);
            }
            nodes = newNodes;
        }
        return nodes;
    }

    public Block[][] ListToMatrix(List<Block> blockList, int blocksPerRow, int blocksPerColumn)
    {
        Block[][] blockMatrix = new Block[blocksPerRow][blocksPerColumn];
        int x = 0;
        int y = 0;
        int blocksCount = blocksPerRow*blocksPerColumn;
        for(int i = 0; i < blocksCount; i++)
        {
            blockMatrix[x][y] = blockList.get(i);
            blockMatrix[x][y].calculateDistance();
            y++;
            x += y/blocksPerColumn;
            y %= blocksPerColumn;
        }
        return blockMatrix;
    }
}
