# import gym
import tensorflow as tf
import numpy as np
from tensorflow import keras

from collections import deque
import time
import random

from continuousDS import CDS
import sys
import os

start_time=time.time()
RANDOM_SEED = 5
tf.random.set_seed(RANDOM_SEED)

env = CDS() 
env.seed(RANDOM_SEED)
np.random.seed(RANDOM_SEED)

print("Action Space: {}".format(env.action_space))
print("State space: {}".format(env.observation_space))
print("env.action_space.n=",env.action_space.n)
print("env.observation_space.shape[0]=",env.observation_space.shape[0])
# An episode a full game
train_episodes = sys.maxsize
# test_episodes = 10

def agent(state_shape, action_shape):
    learning_rate = 0.1
    init = tf.keras.initializers.he_uniform()
    model = keras.Sequential()
    model.add(keras.layers.Dense(32, input_shape=state_shape, activation='relu', kernel_initializer=init))
    model.add(keras.layers.Dense(32, activation='relu', kernel_initializer=init))
    model.add(keras.layers.Dense(action_shape, activation='linear', kernel_initializer=init))
    model.compile(loss=tf.keras.losses.Huber(), optimizer=tf.keras.optimizers.Adam(lr=learning_rate), metrics=['accuracy'])
    return model

def get_qs(model, state, step):
    return model.predict(state.reshape([1, state.shape[0]]))[0]

def train(env, replay_memory, model, target_model, done):
    learning_rate = 0.7 
    discount_factor = 0.618

    MIN_REPLAY_SIZE = 1000
    if len(replay_memory) < MIN_REPLAY_SIZE:
        return

    batch_size = 64 * 2
    mini_batch = random.sample(replay_memory, batch_size)
    current_states = np.array([encode_observation(transition[0], env.observation_space.shape) for transition in mini_batch])
    current_qs_list = model.predict(current_states)
    new_current_states = np.array([encode_observation(transition[3], env.observation_space.shape) for transition in mini_batch])
    future_qs_list = target_model.predict(new_current_states)

    X = []
    Y = []
    for index, (observation, action, reward, new_observation, done) in enumerate(mini_batch):
        if not done:
            max_future_q = reward + discount_factor * np.max(future_qs_list[index])
        else:
            max_future_q = reward

        current_qs = current_qs_list[index]
        current_qs[action] = (1 - learning_rate) * current_qs[action] + learning_rate * max_future_q

        X.append(encode_observation(observation, env.observation_space.shape))
        Y.append(current_qs)
    model.fit(np.array(X), np.array(Y), batch_size=batch_size, verbose=0, shuffle=True)

def encode_observation(observation, n_dims):
    return observation

def main():
    epsilon = 0.5  
    model = agent(env.observation_space.shape, env.action_space.n)
    target_model = agent(env.observation_space.shape, env.action_space.n)
    target_model.set_weights(model.get_weights())
    replay_memory = deque(maxlen=50_000)
    target_update_counter = 0
    X = []
    y = []
    steps_to_update_target_model = 0
    for episode in range(train_episodes):
        total_training_rewards = 0
        observation = env.reset()
        done = False
        while not done:
            steps_to_update_target_model += 1
            random_number = np.random.rand()
            if random_number <= epsilon:
                action = env.action_space.sample()
            else:
                encoded = encode_observation(observation, env.observation_space.shape[0])
                encoded_reshaped = encoded.reshape([1, encoded.shape[0]])
                predicted = model.predict(encoded_reshaped).flatten()
                action = np.argmax(predicted)
            new_observation, reward, done, info = env.step(action)
            replay_memory.append([observation, action, reward, new_observation, done])
            if steps_to_update_target_model % 4 == 0 or done:
                train(env, replay_memory, model, target_model, done)

            observation = new_observation
            total_training_rewards += reward
            if done:
                total_training_rewards += 1
                if steps_to_update_target_model >= 100:
                    target_model.set_weights(model.get_weights())
                    steps_to_update_target_model = 0
                break
            if os.access("budget.txt", os.R_OK):
                with open('budget.txt', 'r') as fp:
                    lines = fp.readlines()
                    last_line = lines[-1].strip().replace('\n','').replace('   ',' ').replace('  ',' ')				
                    if len(last_line)>2:                    
                        seconds=int(last_line)/1000
                        time.sleep(seconds)
        time.sleep(9.4)
    env.close()
    func_time=time.time()-start_time

if __name__ == '__main__':
    main()
