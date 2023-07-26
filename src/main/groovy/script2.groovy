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