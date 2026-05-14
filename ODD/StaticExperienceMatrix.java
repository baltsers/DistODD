package ODD;

public class StaticExperienceMatrix {
	    public int get(int x, int y)
	    {
	        return Q[x][y];
	    }
	    
	    public int[] getRow(int x)
	    {
	        return Q[x];
	    }
	    
	    public void set(int x, int y, int value)
	    {
	        Q[x][y] = value;
	    }
	    
	    public void print()
	    {
	        for(int i = 0; i < 2; i++)
	        {
	            for(int j = 0; j < 2; j++)
	            {
	                String s = Q[i][j] + "  ";
	                if(Q[i][j] < 10)
	                {
	                    s = s + "  ";
	                }
	                else if(Q[i][j] < 100)
	                {
	                    s = s + " ";
	                }
	                System.out.print(s);
	            }
	            System.out.println();
	        }
	    }
	    
	    private static int[][] Q = new int[2][2];
	    static
	    {
	    	for(int i = 0; i < 2; i++)
	        {
	            for(int j = 0; j < 2; j++)
	            {
	                Q[i][j]=0;	                
	            }
	        }

	    }
	}