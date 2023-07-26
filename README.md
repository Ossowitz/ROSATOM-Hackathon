![img.png](../img.png)

# • Написать скрипт для Saltstack передачи файла дистрибутива nginx в папку /tmp на ВМ01 средствами Saltstack;
# • Написать скрипт для Saltstack, который даёт команду на ВМ01 по установке nginx из папки /tmp в ОС Astra Linux;
# • Написать скрипт для Saltstack, который подменяет текст тестовой веб-страницы index.html у nginx на строку “Hello Greenatom”.

[Подготовка стенда](README.md)

**1. Была установлена ОС Astra Linux. Системе было присвоено имя «VM01». Далее были произведены настройки сети: осуществление подключения к внешней сети через соединение с NAT.**

![img_5.png](Case4%2F.idea%2Fphotos%2Fimg_5.png)

1. Bash-скрипт:
```bash
sudo su              # Получение прав администратора
nm-connection-editor # Открытие настроек сетевого подключения
```
2. В настройках сетевого подключения было добавлено новое подключение: «Соединение типа «Ethernet».

![img.png](Case4%2F.idea%2Fphotos%2Fimg.png)

Далее соединение было создано и ему было присвоено имя «mainLock». Метод соединения изменён.

![img_1.png](Case4%2F.idea%2Fphotos%2Fimg_1.png)

Соединение на VM01 было изменено с «Соединение по умолчанию» на «mainLock».

![img_2.png](Case4%2F.idea%2Fphotos%2Fimg_2.png)

**2. Далее была успешно развёрнута вторая виртуальная машина, с отличным IP-адресом.**

![img_7.png](Case4%2F.idea%2Fphotos%2Fimg_7.png)

**3. На второй виртуальной машине (которой было присвоено имя «VM02»), было установлено средство управления Saltstack master:**

Bash-скрипт:

```bash
# Добавление ключа репозитория SaltStack для master
wget -O - https://repo.saltstack.com/py3/debian/11/amd64/latest/SALTSTACK-GPG-KEY.pub | sudo apt-key add -

# Добавление репозитория SaltStack в нашу систему
echo "deb https://repo.saltstack.com/py3/debian/11/amd64/latest/SALTSTACK-GPG-KEY.pub xenial main" >> /etc/apt/sources.list.d/saltstack.list

# После добавления репозитория и ключа репозитория
apt update
apt install salt-master -y
```

**Результат:**

![img_4.png](Case4%2F.idea%2Fphotos%2Fimg_4.png)

**4. На первой виртуальной машине (VM01) был установлен SaltStack minion.**

Bash-скрипт:

```bash
# Добавление ключа репозитория SaltStack для master
wget -O - https://repo.saltstack.com/py3/debian/11/amd64/latest/SALTSTACK-GPG-KEY.pub | sudo apt-key add -

# Добавление репозитория SaltStack в нашу систему
echo "deb https://repo.saltstack.com/py3/debian/11/amd64/latest/SALTSTACK-GPG-KEY.pub xenial main" >> /etc/apt/sources.list.d/saltstack.list

# После добавления репозитория и ключа репозитория
apt update
apt install salt-minion -y 
```

**5. Была произведена настройка подключения агента SaltStack minion к средству управления SaltStack master.**

Bash-скрипт:

```bash
# На VM01
sudo su
cd /etc/salt/minion.d
touch tikhomirov.conf
vi tikhomirov.conf

# Записываем в файл .conf
master: 192.168.0.155 # 192.168.0.155 - IP-адрес master'а
master_port: 4506 # 4506 - это тот порт, по которому мастер отдаёт команды для minion's
```

После того как были установлены и настроены мастер и миньоны Salt, они были запущены:

```bash
# На VM02
systemctl start salt-master
```

```bash
# На VM01
systemctl start salt-minion
```

Последним шагом в процессе установки является то, что мастер соли принимает ключи миньона соли. Как только ключи приняты, мастер Salt может отдавать команды миньону и получать входящие сообщения от миньона.

![img_8.png](Case4%2F.idea%2Fphotos%2Fimg_8.png)

# Выполнение действий

Salt Master (Мастер) – это сервер, который выступает в качестве центра управления для своих миньонов, именно от Master отправляются запросы на удаленное выполнение команд.

**В папку системы с Saltstack master был установлен дистрибутив ngnix.**

```bash
sudo apt install nginx
```

![img_9.png](Case4%2F.idea%2Fphotos%2Fimg_9.png)

## 1. Далее был написан скрипт для Saltstack передачи файла дистрибутива nginx в папку /tmp на ВМ01
средствами Saltstack.

Листинг программ:

1. Вариант Bash-скрипта:

https://github.com/Ossowitz/Case4/blob/master/.idea/script.sh

```bash
#!/bin/bash

minion_id="key_2"  # ID миньона, на который нужно передать файл
file_path="/etc/nginx/"  # путь к файлу на мастер-ноде
dest_path="/tmp"  # путь к папке, в которую нужно передать файл на миньоне

salt "$minion_id" cp.push "$file_path" "$dest_path/"
```

В данном скрипте переменные `minion_id`, `file_path` и `dest_path` можно заменить на соответствующие значения для конкретного случая.

Команда `cp.push` передает файл с мастер-ноды на заданный миньон. Она принимает два параметра: путь к файлу на мастер-ноде и путь к папке на миньоне, куда нужно передать файл. В данном случае файл передается из папки `/etc/nginx` на мастер-ноде в папку `/tmp` на миньоне.

2. Вариант Groovy-скрипта:

https://github.com/Ossowitz/Case4/blob/master/.idea/script.groovy

```groovy
import org.yaml.snakeyaml.Yaml   // импортируем библиотеку для чтения YAML-файлов

def minion = 'minion-name'   // имя minion, на который нужно передать файл
def source = '/path/to/nginx.tar.gz'   // путь к дистрибутиву nginx на master
def target = '/tmp/nginx.tar.gz'   // путь, по которому нужно разместить дистрибутив на minion

// генерируем YAML-файл для передачи данных
def data = """
${minion}:
  file.managed:
    - source: ${source}
    - dest: ${target}
"""

// отправляем данные на master и применяем изменения на minion
def result = sh "salt ${minion} state.apply ${data}"
println(result)
```

**Однако, при попытке отправки дистрибутива, master выдаёт исключение:**

![img111.png](Case4%2F.idea%2Fphotos%2Fimg111.png)

**Хотя minion работает и опция принятия файлов включена:**

![img_1222.png](Case4%2F.idea%2Fphotos%2Fimg_1222.png)

## 2. Из-за появления исключения в примере-1, корректность работы скрипта-2 проверить невозможно.

Листинг программ:

1. Скрипт на Bash:

https://github.com/Ossowitz/Case4/blob/master/.idea/script2.sh

```bash
#!/bin/bash

# Устанавливаем nginx из папки /tmp
sudo apt-get update
sudo apt-get install -y nginx
sudo cp /tmp/nginx.conf /etc/nginx/nginx.conf

# Перезапускаем nginx
sudo systemctl restart nginx.service
```

Этот скрипт обновляет список пакетов, устанавливает nginx и копирует конфигурационный файл из `/tmp/nginx.conf` в `/etc/nginx/nginx.conf`. Затем он перезапускает службу nginx, чтобы применить изменения.

2. Скрипт, написанный на Groovy:

https://github.com/Ossowitz/Case4/blob/master/.idea/script2.groovy

```groovy
import org.yaml.snakeyaml.Yaml
import com.google.common.collect.ImmutableList

salt = new Salt()

// Получаем список minion
minions = salt.cmd('*', 'test.ping')

// Формируем параметры для установки Nginx
nginxParams = [
        'name': 'nginx',
        'pkgs': ImmutableList.of('nginx'),
        'fromrepo': 'nginx',
        'allow_downgrade': false,
        'refresh_db': true,
        'saltenv': 'base',
        'source': 'salt://nginx/custom_config',
        'cwd': '/tmp'
]

// Устанавливаем Nginx на каждом minion
minions.each { minion ->
    salt.cmd(minion, 'pkg.install', new Yaml().dump(nginxParams))
}
```

В данном примере мы используем библиотеку Salt, чтобы выполнять команды на minion через master. Сначала мы получаем список minion с помощью команды `test.ping`. Затем мы формируем параметры для установки Nginx в виде YAML-структуры и передаем их в команду `pkg.install`. В качестве источника пакета указываем настраиваемый конфигурационный файл Nginx из папки /tmp.

## Из-за появления исключения в примере-1, корректность работы скрипта-3 проверить невозможно.

Листинг программ:

1. Скрипт на Bash:

https://github.com/Ossowitz/Case4/blob/master/.idea/script3.sh

```bash
#!/bin/bash
salt 'key-2' cmd.run 'echo "Hello Greenatom" > /tmp/index.html' # key_2 - это имя minion
salt 'key_2' cmd.run 'sudo cp /tmp/index.html /var/www/html' # key_2 - это имя minion
salt 'key_2' cmd.run 'sudo systemctl restart nginx' # key_2 - это имя minion
```

2. Скрипт на Groovy:

https://github.com/Ossowitz/Case4/blob/master/.idea/script3-1.groovy
https://github.com/Ossowitz/Case4/blob/master/.idea/script3-2.groovy

Для выполнения данной задачи в Saltstack можно использовать Groovy и модуль `salt.modules.file` для работы с файлами на удаленных узлах. Вот пример скрипта:

```groovy
import salt.modules.file

String minionId = 'webserver'
String file = '/usr/share/nginx/html/index.html'
String content = '<html><body>Hello Greenatom</body></html>'

// Проверяем, что файл существует на удаленном узле
if (salt.modules.file.file_exists(minionId, file)) {
    // Заменяем содержимое файла на строку "Hello Greenatom"
    salt.modules.file.replace(minionId, file, content)
} else {
    println("Файл $file не найден на $minionId")
}
```

В данном скрипте мы указываем id узла-миниона `webserver`, на котором нужно заменить содержимое файла `'/usr/share/nginx/html/index.html'`. Если файл существует, то заменяем его содержимое на указанную строку. Если файла не существует, то выводим сообщение об ошибке.

Для запуска скрипта в Saltstack мы можем использовать модуль `salt.modules.cmd`, например так:

```groovy
import salt.modules.cmd

String command = 'groovy /path/to/script.groovy'
String output = salt.modules.cmd.run(command)

println(output)
```

Здесь мы запускаем скрипт `/path/to/script.groovy` на Saltstack master и получаем результат выполнения через модуль `salt.modules.cmd`.