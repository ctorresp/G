Pasos para levantar los contenedores:

Abrir una terminal y dirigete a la carpeta raiz utilizando el comando cd para mover entre directorios

Dirigirse a la carpeta "Stack/BackEnd", carpeta principal del proyecto.

Ya dentro de esta carpeta, asegurarse de que exista el archivo de docker-compose y las carpetas del microservicio separada en partes el auth_service ,datos_clinicos y paciente y  verificar la existencia de la carpeta target/ y Dockerfile. Puedes confirmar el contenido de los archivos con ls.

Ahora se debe realizar el siguiente comando: docker-compose up -d --build

Lista de comandos de valor:

docker-compose up -d --build, crea las instancias
docker-compose up -d, levanta las instancias ya creadas
docker ps, para ver que las instancias están correctamente levantadas
