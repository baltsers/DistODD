package QL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Agent {

	public double[][][] Qtable = new double[Variant.Framework_height][Variant.Framework_width][Agent.Action
			.values().length];

	public int[][][] actions = new int[Variant.Framework_height][Variant.Framework_width][Agent.Action
			.values().length];

	public double[][][] rewards = new double[Variant.Framework_height][Variant.Framework_width][Agent.Action
			.values().length];

	private Qlearner learner;
	private Random rand = new Random();
	private int[] state;
	private int last_action = 0;
	private ArrayList<Integer> max_index = new ArrayList<Integer>();

	public Agent(Qlearner learner) {
		this.learner = learner;
	}

	public void init(double[][][] Qtable, double[][][] rewards, int[][][] actions) {
		this.Qtable = Qtable;
		this.rewards = rewards;
		this.actions = actions;
	}

	public void setLocation(int[] state) {
		this.state = state;
	}

	//
	public enum Action {
		North, South, West, Eest
	};


	public boolean arriveIn(ArrayList<int[]> ob, int[] state) {
		for (int i = 0; i < ob.size(); i++) {
			if (state[0] == ob.get(i)[0] && state[1] == ob.get(i)[1]) {
				return true;
			}
		}
		return false;
	}


	public int greedyAction(int[] state) {

		int y = state[0];
		int x = state[1];
		int action;
		double temp;
		if ((1 - learner.epsilon) * 1000 > rand.nextInt(1000)) {
			temp = Qtable[y][x][0];

			for (int k = 0; k < Qtable[y][x].length; k++) {
				if (actions[y][x][k] > 0 && k != backwardAction(last_action)) {
					temp = Qtable[y][x][k];
					break;
				}
			}

			for (int k = 0; k < Qtable[y][x].length; k++) {
				if (Qtable[y][x][k] > temp && actions[y][x][k] > 0
						&& k != backwardAction(last_action)) {
					temp = Qtable[y][x][k];
				}
			}

			for (int k = 0; k < Qtable[y][x].length; k++) {
				if (temp == Qtable[y][x][k] && actions[y][x][k] > 0
						&& k != backwardAction(last_action)) {
					max_index.add(k);
				}
			}

			switch (max_index.size()) {
			case 1:
				action = max_index.get(0);
				break;
			case 2:
				action = max_index.get(rand.nextInt(2));
				break;
			case 3:
				action = max_index.get(rand.nextInt(3));
				break;
			default:
				System.out.println("something worng with action choosen");
				action = randAction(state);
			}

			max_index.clear();

		} else {
			action = randAction(state);
		}
		last_action = action;
		return action;
	}



	public void updateQtable(int[] state, int action, double new_Q) {
		Qtable[state[0]][state[1]][action] = new_Q;
		System.out.println(" Q table is updated in x:" + state[1] + ",y="
		+ state[0] + ",action=" + action + ", new value=" + new_Q);
	}

	public double getMaxQvalue(int[] newstate) {
		double[] num = Qtable[newstate[0]][newstate[1]];
		double temp = num[0];
		for (int i = 0; i < num.length; i++) {
			if (num[i] > temp) {
				temp = num[i];
			}
		}
		double result = temp;
		return result;
	}

	public double getQvalue(int[] state, int action) {
		int y = state[0];
		int x = state[1];
		return Qtable[y][x][action];
	}

	public double getReward(int[] state, int action) {
		int y = state[0];
		int x = state[1];
		return rewards[y][x][action];
	}

	public int[] getNextState(int[] state, int action) {
		int y = state[0];
		int x = state[1];
		switch (action) {
		case 0:
			y = y - 1;
			break;
		case 1:
			y = y + 1;
			break;
		case 2:
			x = x - 1;
			break;
		case 3:
			x = x + 1;
			break;
		}

		if (y == state[0] && x == state[1]) {
			System.out.println("Error: The state is not changed");
		} else {
		}

		int[] result = { y, x };
		return result;
	}

	public int randAction(int[] state) {
		int y = state[0];
		int x = state[1];

		int result = rand.nextInt(actions[y][x].length);
		while (actions[y][x][result] == 0
				|| result == backwardAction(last_action)) {
			result = rand.nextInt(actions[y][x].length);
		}

		return result;
	}

	public int backwardAction(int action) {
		int result;
		switch (action) {
		case 0:
			result = 1;
			break;
		case 1:
			result = 0;
			break;
		case 2:
			result = 3;
			break;
		case 3:
			result = 2;
			break;
		default:
			System.out.println("no such action");
			result = action;
		}

		return result;
	}

}
