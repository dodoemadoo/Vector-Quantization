import java.util.Comparator;

class Block implements Comparator<Block>
{
    int matrix[][];
    int distanceFromZero = 0;
    int rows;
    int columns;

    public Block(int blockLength)
    {
        matrix = new int[blockLength][blockLength];
        rows = blockLength;
        columns = blockLength;
    }

    // the euclidean distance from matrix to the zero matrix
    public void calculateDistance()
    {
        for(int row = 0; row < rows; row++)
        {
            for(int column = 0; column < columns; column++)
            {
                distanceFromZero += ( matrix[row][column] )*( matrix[row][column] );
            }
        }
        distanceFromZero = (int)Math.sqrt(distanceFromZero);
    }

    // matrix + number
    public Block add(int num)
    {
        Block m = new Block(rows);
        for(int row = 0; row < rows; row++)
        {
            for(int column = 0; column < columns; column++)
            {
                m.matrix[row][column] = matrix[row][column] + num;
            }
        }
        return m;
    }

    // matrix - number
    public Block sub(int num)
    {
        Block m = new Block(rows);
        for(int row = 0; row < rows; row++)
        {
            for(int column = 0; column < columns; column++)
            {
                m.matrix[row][column] = matrix[row][column] - num;
            }
        }
        return m;
    }

    @Override
    public int compare(Block block, Block t1) {
        return block.distanceFromZero - t1.distanceFromZero;
    }
}
