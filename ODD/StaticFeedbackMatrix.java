package ODD;

public class StaticFeedbackMatrix {
    public int get(int x, int y)
    {
        return R[x][y];
    }
    
    public int[] getRow(int x)
    {
        return R[x];
    }
    
    private static int[][] R = new int[2][2];
    static 
    {
        R[0][0] = 100;   //700
        R[0][1] = -54;   //854
        
        R[1][0] = 127;   //683
        R[1][1] = 57;    // 857    
 
    }
}