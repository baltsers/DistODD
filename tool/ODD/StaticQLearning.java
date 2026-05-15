package ODD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class StaticQLearning {

	    StaticFeedbackMatrix R = new StaticFeedbackMatrix();
	    
	    StaticExperienceMatrix Q = new StaticExperienceMatrix();
	    
	    public static void main(String[] args)
	    {
	    	StaticQLearning ql = new StaticQLearning();
	        
	        for(int i = 0; i < 500; i++)
	        {
	            Random random = new Random();
	            int x = random.nextInt(100) % 2;
	            
	            System.out.println("The " + i + "th learning. The initial room is " + x);
	            ql.learn(x);
	            System.out.println();
	        }
	    }
	    
	    public void learn(int x)
	    {
	        do
	        {
	            // 
	            int y =  chooseRandomRY(x);
	            
	            // 
	            int qy = getMaxQY(y);
	        
	            int value = calculateNewQ(x, y, qy);
	            Q.set(x, y, value);
	            x = y;
	        }
	        while(2 != x);
	        
	        Q.print();
	    }
	    
	    public int chooseRandomRY(int x)
	    {
	        int[] qRow = R.getRow(x);
	        System.out.println("qRow.size()="+qRow.length+" qRow="+qRow);
	        List<Integer> yValues = new ArrayList<Integer>();
	        for(int i = 0; i < qRow.length; i++)
	        {
	        	System.out.println("i="+i+" qRow[i]="+qRow[i]);
	            if(qRow[i] >= 0)
	            {
	                yValues.add(i);
	            }
	        }
	        System.out.println("yValues.size()="+yValues.size()+" yValues="+yValues);
	        Random random = new Random();
	        int i = random.nextInt(yValues.size()) % yValues.size();
	        return yValues.get(i);
	    }
	    
	    public int getMaxQY(int x)
	    {
	        int[] qRow = Q.getRow(x);
	        int length = qRow.length;
	        List<YAndValue> yValues = new ArrayList<YAndValue>();
	        for(int i = 0; i < length; i++)
	        {
	            YAndValue yv = new YAndValue(i, qRow[i]);
	            yValues.add(yv);
	        }
	        
	        Collections.sort(yValues);
	        int num = 1;
	        int value = yValues.get(0).getValue();
	        for(int i = 1; i < length; i++)
	        {
	            if(yValues.get(i).getValue() == value)
	            {
	                num = i + 1;
	            }
	            else
	            {
	                break;
	            }
	        }
	        
	        Random random = new Random();
	        int i = random.nextInt(num) % num;
	        return yValues.get(i).getY();
	    }
	    
	    // Q(x,y) = R(x,y) + 0.8 * max(Q(y,i))
	    public int calculateNewQ(int x, int y, int qy)
	    {
	        return (int) (R.get(x, y) + 0.8 * Q.get(y, qy));
	    }
	    
	    public static class YAndValue implements Comparable<YAndValue>
	    {
	        int y;
	        int value;
	        
	        public int getY() {
	            return y;
	        }
	        public void setY(int y) {
	            this.y = y;
	        }
	        public int getValue() {
	            return value;
	        }
	        public void setValue(int value) {
	            this.value = value;
	        }
	        public YAndValue(int y, int value)
	        {
	            this.y = y;
	            this.value = value;
	        }
	        public int compareTo(YAndValue o) 
	        {
	            return o.getValue() - this.value;
	        }
	    }
	}