import org.yaml.snakeyaml.Yaml // импортируем библиотеку для чтения YAML-файлов

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