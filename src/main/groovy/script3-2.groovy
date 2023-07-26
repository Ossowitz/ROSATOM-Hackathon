import salt.modules.cmd

String command = 'groovy /path/to/script.groovy'
String output = salt.modules.cmd.run(command)

println(output)