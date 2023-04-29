# Подготовка стенда

**1. Была установлена ОС Astra Linux. Системе было присвоено имя «VM01». Далее были произведены настройки сети: осуществление подключения к внешней сети через соединение с NAT.**

![img_5.png](photos%2Fimg_5.png)

1. Bash-скрипт:
```bash
sudo su              # Получение прав администратора
nm-connection-editor # Открытие настроек сетевого подключения
```
2. В настройках сетевого подключения было добавлено новое подключение: «Соединение типа «Ethernet»».

![img.png](photos%2Fimg.png)

Далее соединение было создано и ему было присвоено имя «mainLock». Метод соединения изменён.

![img_1.png](photos%2Fimg_1.png)

Соединение на VM01 было изменено с «Соединение по умолчанию» на «mainLock».

![img_2.png](photos%2Fimg_2.png)

**2. Далее была успешно развёрнута вторая виртуальная машина, с отличным IP-адресом.**

![img_7.png](photos%2Fimg_7.png)

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

![img_4.png](photos%2Fimg_4.png)

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

После того, как были установлены и настроены мастер и миньоны Salt, они были запущены:

```bash
# На VM02
systemctl start salt-master
```

```bash
# На VM01
systemctl start salt-minion
```

Последним шагом в процессе установки является то, что мастер соли принимает ключи миньона соли. Как только ключи приняты, мастер Salt может отдавать команды миньону и получать входящие сообщения от миньона.

![img_8.png](photos%2Fimg_8.png)

# Выполнение действий

Salt Master (Мастер) – это сервер, который выступает в качестве центра управления для своих миньонов, именно от Master отправляются запросы на удаленное выполнение команд.

**1. В папку системы с Saltstack master был установлен дистрибутив ngnix.**

```bash
sudo apt install nginx
```

![img_9.png](photos%2Fimg_9.png)

**2. Далее был написан скрипт для Saltstack передачи файла дистрибутива nginx в папку /tmp на ВМ01
средствами Saltstack.**

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

![img111.png](photos%2Fimg111.png)

**Хотя minion работает и опция принятия файлов включена:**

![img_1222.png](photos%2Fimg_1222.png)

