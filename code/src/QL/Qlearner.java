package QL;

public class Qlearner {
	
	public Agent agent;
	public Obstacle obstacle;

	public Terminal terminal;
	public MapLoader mapper;

	public double gamma;
	public double alpha;
	public double epsilon;
	

	public Qlearner() {
		agent = new Agent(this);
		obstacle = new Obstacle();
		terminal = new Terminal();
		mapper = new MapLoader(this);
		mapper.load(agent);
	}

	public Qlearner(String mazeFile) {
		agent = new Agent(this);
		obstacle = new Obstacle();
		terminal = new Terminal();
		mapper = new MapLoader(this, mazeFile);
		mapper.load(agent);
	}

	public void updateMAP(String mazeFile) {
		mapper = new MapLoader(this, mazeFile);
		mapper.load(agent);
	}
	
	public Qlearner(String rewardFile, String budgetFile) {
		agent = new Agent(this);
		obstacle = new Obstacle();
		terminal = new Terminal();
		mapper = new MapLoader(this, rewardFile, budgetFile);
		mapper.load(agent);
	}



	public void updateMAP(String rewardFile, String budgetFile) {
		mapper = new MapLoader(this, rewardFile, budgetFile);
		mapper.load(agent);
	}
	
	public int[] getNextState(int[] current_s) {
		int steps = 0;
		double this_Q;
		double max_Q;
		double new_Q;
		int[] newstate = current_s;
		//int[] current_s;
		double reward;
		int action;
	
			
		if (!agent.arriveIn(terminal.location, current_s)) {

			action = agent.greedyAction(current_s);
			reward = agent.getReward(current_s, action);
			newstate = agent.getNextState(current_s, action);
			max_Q = agent.getMaxQvalue(newstate);
			this_Q = agent.getQvalue(current_s, action);
			new_Q = this_Q + alpha
					* (reward + gamma * max_Q - this_Q);
			//System.out.println("action=" + action + ", reward=" + reward + ", max_Q=" + max_Q  + ", this_Q =" + this_Q  + ", new_Q=" + new_Q);
			agent.updateQtable(current_s, action, new_Q);
			current_s = newstate;

		}

		return newstate;
	}

}



