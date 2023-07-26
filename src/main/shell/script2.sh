#!/bin/bash

# Устанавливаем nginx из папки /tmp
sudo apt-get update
sudo apt-get install -y nginx
sudo cp /tmp/nginx.conf /etc/nginx/nginx.conf

# Перезапускаем nginx
sudo systemctl restart nginx.service