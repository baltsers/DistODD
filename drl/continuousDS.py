import math
import gym
from gym import spaces, logger
from gym.utils import seeding
import numpy as np
import psutil
import os
import glob

class CDS(gym.Env):
    """
    Description:
       

    Source:
        This environment corresponds to the version of the cart-pole problem
        described by Barto, Sutton, and Anderson

    Observation:
        Num     Observation               Min                     Max
        0       Event count               1                       Inf 
        1       Cyclomatic complexity     1                       Inf
        2       Cpu utilization rate      0                       100
        3       Memory availability rate  0                       100
	      4       Code size                 1                       Inf
	      5       Function count            1                       Inf
	      6       Branch count              1                       Inf
	      7       Time cost                 0                       Inf
				
    Actions:
        Type: Discrete(2)
        Num   Action
        0     Deduce the analysis configuration
        1     Add the analysis configuration
        
				
        Note: The amount the velocity that is reduced or increased is not
        fixed; it depends on the actual time cost.

    Reward:
        Reward is 1 for every step taken, including the termination step

    Starting State:
        All observations are assigned a uniform random value in [0..0]

    Episode Termination:
        Episode length is greater than 200.
    """

    metadata = {
        'render.modes': ['human', 'rgb_array'],
        'video.frames_per_second': 50
    }

    def __init__(self):
        self.event_count = 0.0
        self.cyclomatic_complexity  = 0.0
        self.cpu_utilization = 0.0 # psutil.cpu_percent(interval=1)
        self.memory_utilization = 0.0 # psutil.virtual_memory().percent
        # self.cpu_utilization = 10.0
        # self.memory_utilization = 10.0
        self.covered_method = 0.0
        self.attack_surface = 0.0
        self.covered_stmt = 0.0
        self.time_cost = 0.0

        self.code_size = 0.0
        self.function_count = 0.0
        self.branch_count = 0.0
        if os.access("Sizes.txt", os.R_OK):
            with open('Sizes.txt', 'r') as fp:
                lines = fp.readlines()
                if len(lines)>0:
                    last_line = lines[-1].strip().replace('\n','').replace('   ',' ').replace('  ',' ')
                    last_lines=last_line.split(" ")
                    if len(last_lines)>2:
                        self.code_size=float(last_lines[0])
                        self.function_count=float(last_lines[1])
                        self.branch_count=float(last_lines[2])
        if self.code_size==0.0 or self.function_count==0.0 or self.branch_count==0.0:      
            if os.access("stmtids.out", os.R_OK):
                self.code_size = len(open("stmtids.out").readlines())            
            if os.access("functionList.out", os.R_OK):
                self.function_count = len(open("functionList.out").readlines())            
            if os.access("branches.txt", os.R_OK):
                with open('branches.txt', 'r') as fp:
                    lines = fp.readlines()
                    if len(lines)>0:
                        last_line = lines[-1].strip()
                        last_lines=last_line.split("/")
                        if len(last_lines)>1:
                            self.branch_count=float(last_lines[1].replace('\n',''))
            elif os.access("entitystmt.out.branch", os.R_OK):
                self.branch_count = len(open("entitystmt.out.branch").readlines())

        self.event_count_threshold = 100000.0
        self.cyclomatic_complexity_threshold  = 1000.0
        self.cpu_utilization_threshold = 90.0
        self.memory_utilization_threshold = 90.0
        
        self.time_cost = 0.0

        self.budget = 0.0
        if os.access("budget.txt", os.R_OK):
            with open('budget.txt', 'r') as fp:
                lines = fp.readlines()
                last_line = lines[-1].strip()
                self.budget=int(last_line)
        print("self.budget = ", self.budget)
        
        self.configuration = '111111'
        self.configurations = ['000100','000101',
                               '100000','100010','100011','100100','100101','100111',
                               '101000','101010','101011','101100','101101','101111',
                               '110000','110010','110011','110100','110101','110111',
                               '111000','111010','111011','111100','111101','111111']
        
        self.tau = 0.2  # seconds between state updates
        self.kinematics_integrator = 'euler'

        self.action_space = spaces.Discrete(2)
        high = np.array([np.finfo(np.float32).max,
                         np.finfo(np.float32).max,
                         100,
                         100,
                         np.finfo(np.float32).max,
                         np.finfo(np.float32).max,
                         np.finfo(np.float32).max,
                         np.finfo(np.float32).max,
                         np.finfo(np.float32).max,
                         np.finfo(np.float32).max,
                         np.finfo(np.float32).max],
                        dtype=np.float32)
        self.observation_space = spaces.Box(-high, high, dtype=np.float32)
        self.seed()
        self.viewer = None
        self.state = None

        self.steps_beyond_done = None

    def seed(self, seed=None):
        # self.np_random, seed = seeding.np_random(seed)
        user_given_seed = 1
        np_random, seed = seeding.np_random(user_given_seed)
        # np_random_2 = np.randm.RandomState(seed)
        # seed=1
        return [seed]

    def step(self, action):
        err_msg = "%r (%s) invalid" % (action, type(action))
        assert self.action_space.contains(action), err_msg
        # print("action=",action)		
        print("self.state=",self.state)		
				
        # x, x_dot, theta, theta_dot = self.state
        eventCount,cyclomaticComplexity,cpuUtilization,memoryUtilization,coveredMethod,attackSurface,coveredStmt,codeSize,functionCount,branchCount,timeCost = self.state

        # For the interested reader:
        # https://coneural.org/florian/papers/05_cart_pole.pdf
        
        cfgIndex=self.configurations.index(self.configuration)
        print(" old configuration=",self.configuration)		
        # print(" old cfgIndex=", cfgIndex)		
        print(" action=", action)	
        if action==0:
            # print("action==0")
            if self.configuration=='000000' or cfgIndex==0:
                self.configuration='000000'
            else:
                cfgIndex=cfgIndex-1
                       
        if action==1:
            # print("action==1")
            if self.configuration=='111111' or cfgIndex==25:
                self.configuration='111111'
            else:
                cfgIndex=cfgIndex+1                
        # print(" new cfgIndex=", cfgIndex)         
        self.configuration=self.configurations[cfgIndex]
        print(" new configuration=",self.configuration)        
        f1 = open('Configuration.txt','w') 
        f1.write(self.configuration)
        f1.close()
        
        #if os.access("FL.txt", os.R_OK):
        #    self.event_count = len(open("FL.txt").readlines())
        #with open('a.log', 'r') as fp:
        #    lines = fp.readlines()
        #    last_line = lines[-1]
        
        
        self.cpu_utilization = psutil.cpu_percent(interval=1)
        self.memory_utilization = psutil.virtual_memory().percent
        # self.covered_stmt = 0.0
        
        #self.covered_method = 0.0
        if os.access("coveredMethods.txt", os.R_OK):
            self.covered_method = len(open("coveredMethods.txt").readlines())
                
        #self.attack_surface = 0.0
        if os.access("EventCount.txt", os.R_OK):
            with open('EventCount.txt', 'r') as fp:
                lines = fp.readlines()
                if len(lines)>0:
                    last_line = lines[-1].strip()
                    #print("EventCount.txt last_line=", last_line) 
                    last_lines=last_line.split(" ")
                    if len(last_lines)>0:
                        #if not os.access("FL.txt", os.R_OK):
                        self.event_count = float(last_lines[0])
                        if not os.access("coveredMethods.txt", os.R_OK):    
                            self.covered_method = float(last_lines[1])
                        self.attack_surface = float(last_lines[2])
                        self.covered_stmt = float(last_lines[3])
                
        #self.cyclomatic_complexity=0.0
        if os.access("stmtCoverage1.out", os.R_OK):
            with open('stmtCoverage1.out', 'r') as fp:
                lines = fp.readlines()
                if len(lines)>0:
                    last_line = lines[-1].strip()
                    if len(last_lines)>0:
                        # print("stmtCoverage1.out last_line=", last_line) 
                        last_lines=last_line.split("/")
                        self.cyclomatic_complexity=float(last_lines[0].replace('Total branches covered: ',''))+1        
        elif os.access("branches.txt", os.R_OK):
            with open('branches.txt', 'r') as fp:
                lines = fp.readlines()
                if len(lines)>0:
                    last_line = lines[-1].strip()
                    if len(last_lines)>0:
                        last_lines=last_line.split("/")
                        self.cyclomatic_complexity=float(last_lines[0].replace('Total branches covered: ',''))+1

        # list_of_files = glob.glob('TimeCosts*.txt') 
        # latest_file = max(list_of_files, key=os.path.getctime)
        # print(latest_file)
        # self.time_cost = 0.0
        # if os.access(latest_file, os.R_OK):
        #     with open(latest_file, 'r') as fp:
        #         lines = fp.readlines()
        #         last_line = lines[-1]
        #         # print("last_line =", last_line )
        #         last_lines=last_line.split(",")   
        #         # print("last_lines =", last_lines ) 
        #         self.time_cost=int(last_lines[-1].replace('\n',''))
        # print("self.time_cost = ", self.time_cost)    

        # self.time_cost = 0.0
        foundTimeCost=False
        if os.access("TimeCosts.txt", os.R_OK):
            f = open("TimeCosts.txt")            
            line = f.readline()                  
            while line:   
                lines=line.split(" ")
                cfgInLine=lines[0]
                if cfgInLine==self.configuration:
                    self.time_cost=int(lines[-2])
                    foundTimeCost=True
                    # print("self.maxTimeCost = ", maxTimeCost)
                    break
                line = f.readline()      
            f.close()
            print("self.time_cost = ", self.time_cost)

        if not foundTimeCost:
            list_of_files = glob.glob('TimeCost??*.txt') 
            if len(list_of_files)>0:
                latest_file = max(list_of_files, key=os.path.getctime)
                print("latest_file =", latest_file)
                #self.time_cost = 0.0
                if os.access(latest_file, os.R_OK):
                    with open(latest_file, 'r') as fp:
                        lines = fp.readlines()
                        if len(lines)>0:
                            last_line = lines[-1].strip()
                            print("last_line =", last_line )
                            #last_lines=last_line.split(",")   
                            # print("last_lines =", last_lines )
                            self.time_cost=int(last_line)
                            #self.time_cost=int(last_lines[-1].strip().replace('\n',''))
        precision=0.0
        if os.access("DeepLearningLogs.txt", os.R_OK):
            with open('DeepLearningLogs.txt', 'r') as fp:
                lines = fp.readlines()
                if len(lines)>0:
                    last_line = lines[-1].strip()
                    if len(last_lines)>0:
                        # print("stmtCoverage1.out last_line=", last_line) 
                        last_lines=last_line.split(" ")
                        precision=float(last_lines[-1].replace('Precision=',''))
        print("precision = ", precision)                        
        budget_rate=0.0
        print("self.time_cost, self.budget = ", self.time_cost, self.budget)   
        if self.budget >1 :
            budget_rate=self.time_cost/self.budget 
            if budget_rate >1  :
                budget_rate=1 - budget_rate
        print("budget_rate = ", budget_rate)
        
        done = bool(
            budget_rate <= 1.0
            and budget_rate > 0.8
        )


        
        # self.state = (self.event_count,cyclomaticComplexity,self.cpu_utilization,self.memory_utilization,codeSize,functionCount,branchCount,timeCost)
        self.state = (self.event_count,self.cyclomatic_complexity,self.covered_method,self.attack_surface,self.covered_stmt,self.cpu_utilization,self.memory_utilization,self.code_size,functionCount,branchCount,self.time_cost)
        reward = 1/(self.budget-self.time_cost)*1000
        if precision>0:
            reward = reward + abs(1/(self.budget-self.time_cost)*1000*precision)
        if not done:
            reward = budget_rate
        elif self.steps_beyond_done is None:
            self.steps_beyond_done = 0
            #reward = budget_rate
        else:
            if self.steps_beyond_done == 0:
                logger.warn(
                    "You are calling 'step()' even though this "
                    "environment has already returned done = True. You "
                    "should always call 'reset()' once you receive 'done = "
                    "True' -- any further steps are undefined behavior."
                )
            self.steps_beyond_done += 1
            reward = 0.0
        print(" reward = ",reward)	
        return np.array(self.state), reward, done, {}

    def reset(self):
        # self.state = self.np_random.uniform(low=-0.05, high=0.05, size=(4,))
        # self.state = np.random.randint(low=0, high=100, size=8)
        self.state = (self.event_count,self.cyclomatic_complexity,self.covered_method,self.attack_surface,self.covered_stmt,self.cpu_utilization,self.memory_utilization,self.code_size,self.function_count,self.branch_count,self.time_cost)
        self.steps_beyond_done = None
        return np.array(self.state)

    def close(self):
        if self.viewer:
            self.viewer.close()
            self.viewer = None
