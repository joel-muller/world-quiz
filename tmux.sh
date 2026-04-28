#!/bin/bash

SESSION="world-quiz"

tmux new-session -d -s "$SESSION" -n docker
tmux new-window -t "$SESSION" -n server
tmux new-window -t "$SESSION" -n client

tmux send-keys -t "$SESSION":docker "cd ~/world-quiz" C-m
tmux send-keys -t "$SESSION":server "cd ~/world-quiz/server" C-m
tmux send-keys -t "$SESSION":client "cd ~/world-quiz/client" C-m

tmux attach -t "$SESSION"
