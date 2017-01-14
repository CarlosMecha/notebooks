
# Create session
new-session -Ad -c ~/Workspace/Notebooks/ -n src -s notebooks '/bin/bash --rcfile envrc'
set-option -t notebooks status-style "bg=colour235"
set-option -a -t notebooks status-style "fg=colour39"
set-option -t notebooks default-command "/bin/bash --rcfile envrc"

# Windows

## 1. git
new-window -n git -t notebooks

## 2. db
new-window -n db -t notebooks

## 3. mvn
new-window -n mvn -t notebooks

## 4. test
new-window -n test -t notebooks

## 5. deploy
new-window -n deploy -t notebooks

## 6. cloud
new-window -n cloud -t notebooks

# Notification
display "Session notebooks created"

