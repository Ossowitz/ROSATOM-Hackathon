#!/bin/bash
salt 'key-2' cmd.run 'echo "Hello Greenatom" > /tmp/index.html' # key_2 - это имя minion
salt 'key_2' cmd.run 'sudo cp /tmp/index.html /var/www/html' # key_2 - это имя minion
salt 'key_2' cmd.run 'sudo systemctl restart nginx' # key_2 - это имя minion