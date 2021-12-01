package QL;


public class MapLoader {

	private Qlearner learner;

	//  -99 obstacle
	public void MapGenerater() {
		int endI=0;
		int endJ=0;
		double maxValue=0;
		for (int i = 0; i < Variant.Framework_height; i++) {
			for (int j = 0; j < Variant.Framework_width; j++) {
				//System.out.printf("Variant.MAP["+i+"]["+j+"]="+Variant.MAP[i][j]+" ");
				if (Variant.MAP[i][j]>maxValue)  {
					endI=i;
					endJ=j;
					maxValue=Variant.MAP[i][j];
				}
			}
		}

		int[] num = { endI, endJ };
		learner.terminal.location.add(num);
	}

	public MapLoader(Qlearner learner) {
		this.learner = learner;
		MapGenerater();
	}

	public MapLoader(Qlearner learner, String mazeFile) {
		this.learner = learner;
		Variant.loadFromFile(mazeFile);
		MapGenerater();
	}
	
	public MapLoader(Qlearner learner, String rewardFile, String budgetFile) {
		this.learner = learner;
		Variant.loadFromFile(rewardFile,budgetFile);
		MapGenerater();
	}
	public void load(Agent agent) {

		double[][][] Qtable = agent.Qtable;
		double[][][] rewards = agent.rewards;
		int[][][] actions = agent.actions;
		
		// init_Q
		for (int i = 0; i < Qtable.length; i++) {
			for (int j = 0; j < Qtable[i].length; j++) {
				for (int k = 0; k < Qtable[i][j].length; k++) {
					Qtable[i][j][k] = 0;
				}
			}
		}

		// init_Reward

		for (int i = 0; i < rewards.length; i++) {
			for (int j = 0; j < rewards[i].length; j++) {
				for (int k = 0; k < rewards[i][j].length; k++) {
					rewards[i][j][k] = -1;
				}
			}
		}

		for (int i = 0; i < Variant.MAP.length; i++) {
			for (int j = 0; j < Variant.MAP[i].length; j++) {
				double item = Variant.MAP[i][j];
				if ((i+1) < Variant.MAP.length)
					rewards[i + 1][j][0] = item;
				if ((i-1)>=0)
					rewards[i - 1][j][1] = item;
				if ((j+1) < Variant.MAP[i].length)
					rewards[i][j + 1][2] = item;
				if ((j-1)>=0)
					rewards[i][j - 1][3] = item;
			}
		}

		// init_Action avaliable 1, not 0
		for (int i = 0; i < actions.length; i++) {
			for (int j = 0; j < actions[i].length; j++) {
				for (int k = 0; k < actions[i][j].length; k++) {
					actions[i][j][k] = 1;
				}
			}
		}


		for (int i = 0; i < Variant.MAP.length; i++) {
			for (int j = 0; j < Variant.MAP[i].length; j++) {
				double item = Variant.MAP[i][j];
				if (item <= -99) {

					// find obstacle
					int[] num = { i, j };
					learner.obstacle.location.add(num);
					if ((i+1)<Variant.Framework_height)
						actions[i + 1][j][0] = 0;
					if ((i-1)>0)
						actions[i - 1][j][1] = 0;
					if ((j+1)<Variant.Framework_width)					
						actions[i][j + 1][2] = 0;
					if ((j-1)>0)
						actions[i][j - 1][3] = 0;
				}
			}
		}

		// border
		for (int i = 0; i < Variant.Framework_width; i++) {
			actions[0][i][0] = 0;
			actions[Variant.Framework_height - 1][i][1] = 0;
		}

		for (int i = 0; i < Variant.Framework_height; i++) {
			actions[i][0][2] = 0;
			actions[i][Variant.Framework_width - 1][3] = 0;
		}	


		agent.init(Qtable, rewards, actions);
	}


}
