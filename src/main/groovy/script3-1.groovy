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