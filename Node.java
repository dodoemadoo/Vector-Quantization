import java.util.ArrayList;

class Node
{
    Node left , right;
    ArrayList<Block> blockList ;
    Block avg = null ;
    Node (int blockLength)
    {
        left = right = null ;
        blockList = new ArrayList<>();
        avg = new Block(blockLength);
    }
    public void calculateAverage()
    {
        for(Block b : blockList)
        {
            for(int row = 0; row < b.rows; row++)
            {
                for(int column = 0; column < b.columns; column++)
                {
                    avg.matrix[row][column] += ( b.matrix[row][column] );
                }
            }
        }
        if( blockList.size() == 0 )
            return;
        for(int row = 0; row < avg.rows; row++)
        {
            for(int column = 0; column < avg.columns; column++)
            {
                avg.matrix[row][column] /= blockList.size();
            }
        }
    }
}
