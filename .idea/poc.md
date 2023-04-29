# Подготовка стенда

**1. Была установлена ОС Astra Linux. Системе было присвоено имя «VM01». Далее были произведены настройки сети: осуществление подключения к внешней сети через соединение с NAT.**

![img_5.png](img_5.png)

1. Bash-скрипт:
```bash
sudo su              # Получение прав администратора
nm-connection-editor # Открытие настроек сетевого подключения
```
2. В настройках сетевого подключения было добавлено новое подключение: «Соединение типа «Ethernet»».

![img.png](img.png)

Далее соединение было создано и ему было присвоено имя «mainLock». Метод соединения изменён.

![img_1.png](img_1.png)

Соединение на VM01 было изменено с «Соединение по умолчанию» на «mainLock».

![img_2.png](img_2.png)

**2. Далее была успешно развёрнута вторая виртуальная машина, с отличным IP-адресом.**

![img_7.png](img_7.png)

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

![img_4.png](img_4.png)

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

![img_8.png](img_8.png)