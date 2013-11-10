package checkers;
import java.util.ArrayList;
import java.util.List;

public class EvaluatePosition // Ta klasa jest wymagana - nie usuwaj jej
{
    static private final int WIN=Integer.MAX_VALUE/2;
    static private final int LOSE=Integer.MIN_VALUE/2;
    static private boolean _color; // To pole jest wymagane - nie usuwaj go
    static private int size_;
    static private int diagonalSize_;

    private static final int tableWeight[] = {
            4, 4, 4, 4,
            4, 3, 3, 3,
            3, 2, 2, 4,
            4, 2, 1, 3,
            3, 1, 2, 4,
            4, 2, 2, 3,
            3, 3, 3, 4,
            4, 4, 4, 4};

    static public void changeColor(boolean color) // Ta metoda jest wymagana - nie zmieniaj jej
    {
        _color=color;
    }
    static public boolean getColor() // Ta metoda jest wymagana - nie zmieniaj jej
    {
        return _color;
    }
    static public int evaluatePosition(AIBoard board) // Ta metoda jest wymagana. Jest to główna metoda heurystyki - umieść swój kod tutaj
    {
        int myRating=0;
        int opponentsRating=0;
        size_ = board.getSize();
        diagonalSize_ = (int)Math.sqrt(size_*size_*2);
//        updateStatistics(board);
        for (int i=0;i<size_;i++)
        {
            for (int j=(i+1)%2;j<size_;j+=2)
            {
                if (!board._board[i][j].empty) // pole nie jest puste
                {
                    if (board._board[i][j].white==getColor()) // to jest moj pionek
                    {
                        myRating += evaluatesFigure(board, i, j, board._board[i][j].white);
                    }
                    else
                    {
                        opponentsRating += evaluatesFigure(board, i, j, board._board[i][j].white);
                    }
                }
            }
        }
        return getResult(myRating, opponentsRating);
    }

    private static List<Integer[]> getEnemiesPositions(AIBoard board)
    {
        List<Integer[]> enemiesPositions = new ArrayList<Integer[]>();
        for (int i=0;i<size_;i++)
        {
            for (int j=(i+1)%2;j<size_;j+=2)
            {
                if (!board._board[i][j].empty) // pole nie jest puste
                {
                    if (board._board[i][j].white!=getColor()) // to jest moj pionek
                    {
                        Integer myPos[] = new Integer[2];
                        myPos[0] = i;
                        myPos[1] = j;
                        enemiesPositions.add(myPos);

                    }
                }
            }
        }
        return enemiesPositions;
    }

    private static List<Integer> getStats(AIBoard board)
    {
        List<Integer> stats = new ArrayList<Integer>();
        Integer myKings = 0;
        Integer myPieces = 0;
        Integer enemiesKings = 0;
        Integer enemiesPieces = 0;
        for (int i=0;i<size_;i++)
        {
            for (int j=(i+1)%2;j<size_;j+=2)
            {
                if (!board._board[i][j].empty) // pole nie jest puste
                {
                    if (board._board[i][j].white==getColor()) // to jest moj pionek
                    {
                        if(board._board[i][j].king)
                        {
                            myKings++;
                        }
                        else
                        {
                            myPieces++;
                        }
                    }
                    else
                    {
                        if(board._board[i][j].king)
                        {
                            enemiesKings++;
                        }
                        else
                        {
                            enemiesPieces++;
                        }
                    }
                }
            }
        }
        stats.add(myPieces);
        stats.add(myKings);
        stats.add(enemiesPieces);
        stats.add(enemiesKings);
        return stats;

    }


    private static int getSumDistanceToEnemies(AIBoard board, int i, int j)
    {
        int sumDistance =0;
        List<Integer[]> enemiesPositions = getEnemiesPositions(board);
        for(Integer[] pos: enemiesPositions)
        {
            sumDistance += Math.sqrt((i-pos[0])*(i-pos[0])+(j-pos[1])*(j-pos[1]));
        }
        return sumDistance;
    }

    private static int getMinDistanceToEnemies(AIBoard board, int i, int j)
    {
        List<Integer[]> enemiesPositions = getEnemiesPositions(board);
        Integer firstPos[] = enemiesPositions.get(0);
        double minDistance = Math.sqrt((i-firstPos[0])*(i-firstPos[0])+(j-firstPos[1])*(j-firstPos[1]));
        for(Integer[] pos: enemiesPositions)
        {
            double tmpResult = Math.sqrt((i-pos[0])*(i-pos[0])+(j-pos[1])*(j-pos[1]));
            if(minDistance <tmpResult )
            {
                minDistance = tmpResult;
            }
        }
        return (int)minDistance;
    }    

    private static int getResult(final int myRating,final int enemyRating)
    {
        if (myRating<=0)
        {
            return LOSE;
        }
        else if (enemyRating<=0)
        {
            return WIN;
        }
        else
        {
            return myRating-enemyRating;
        }
    }

    private static boolean isPlayerWinning(AIBoard board)
    {
        List<Integer> stats = getStats(board);
        if((stats.get(0)+stats.get(1)) >= (stats.get(2)+stats.get(3)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static boolean onlyKingsLeft(AIBoard board)
    {
        List<Integer> stats = getStats(board);
        if(stats.get(0) == 0 && stats.get(2) == 0)
        {
            // Judge.updateLog("Kings only\n");
            return true;
        }
        else
        {
            return false;
        }
    }

    private static int getWeight(int i, int j)
    {
        if( (i+1)%2 == 1)
        {
            return tableWeight[i*4+((j-1)/2)];
        }
        else
        {
            return tableWeight[i*4+(j/2)];
        }
    }

    private static int evaluatesFigure(AIBoard board, int i, int j, boolean isWhite)
    {
        int rating = 0;
        if (board._board[i][j].king)
        {
            if(isWhite)
            {
                rating = 10*getWeight(i,j);
            }
            else
            {
                rating = 10*getWeight(i,j);
            }
        }
        else
        {
            if(isWhite)
            {
                rating = (5+i)*getWeight(i,j);
            }
            else
            {
                rating = (5+(size_-1)-i)*getWeight(i,j);
            }
        }   
        if(onlyKingsLeft(board))
        {
            int minDistance = getMinDistanceToEnemies(board, i, j);
            // Judge.updateLog("Distance sum"+Integer.toString(minDistance)+"\n");
            if(isPlayerWinning(board))
            {
                rating += (diagonalSize_-minDistance);
            }
            else
            {
                rating -= (diagonalSize_-minDistance);
            }
        }
        return rating;
    }   

    private static List<Integer[]> getSurroundingPositions(int i, int j)
    {
        List<Integer[]> positions = new ArrayList<Integer[]>();
        
        if(i>0 && j >0)
        {
            Integer myPos[] = new Integer[2];
            myPos[0] = i-1;
            myPos[1] = j-1;
            positions.add(myPos);  
        }

        if(i< size_-1 && j >0)
        {
            Integer myPos[] = new Integer[2];
            myPos[0] = i+1;
            myPos[1] = j-1;
            positions.add(myPos);  
        }

        if(i>0 && j <size_-1)
        {
            Integer myPos[] = new Integer[2];
            myPos[0] = i-1;
            myPos[1] = j+1;
            positions.add(myPos);  
        }
        if(i<size_-1 && j <size_-1)
        {
            Integer myPos[] = new Integer[2];
            myPos[0] = i+1;
            myPos[1] = j+1;
            positions.add(myPos);  
        } 
        return positions;
    }

}
