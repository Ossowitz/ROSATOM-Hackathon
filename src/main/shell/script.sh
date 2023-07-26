#!/bin/bash

minion_id="key_2"  # ID миньона, на который нужно передать файл
file_path="/etc/nginx/"  # путь к файлу на мастер-ноде
dest_path="/tmp"  # путь к папке, в которую нужно передать файл на миньоне

salt "$minion_id" cp.push "$file_path" "$dest_path/"