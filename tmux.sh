#!/bin/bash

SESSION="world-quiz"

tmux new-session -d -s "$SESSION" -n root

tmux new-window -t "$SESSION" -n server-nvim
tmux new-window -t "$SESSION" -n server-cli1
tmux new-window -t "$SESSION" -n server-cli2
tmux new-window -t "$SESSION" -n client-cli1
tmux new-window -t "$SESSION" -n notes

tmux send-keys -t "$SESSION":root "cd ~/world-quiz" C-m
tmux send-keys -t "$SESSION":server-nvim "cd ~/world-quiz/server" C-m
tmux send-keys -t "$SESSION":server-cli1 "cd ~/world-quiz/server" C-m
tmux send-keys -t "$SESSION":server-cli2 "cd ~/world-quiz/server" C-m
tmux send-keys -t "$SESSION":client-cli1 "cd ~/world-quiz/client" C-m
tmux send-keys -t "$SESSION":notes "cd ~/notes" C-m

tmux attach -t "$SESSION"
