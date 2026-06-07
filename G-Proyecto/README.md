# 🚀 Entregable Backend - Sistema RedNorte

Esta carpeta contiene los archivos compilados y configuraciones necesarias para levantar la infraestructura completa del backend de RedNorte mediante Docker, sin necesidad de compilar el código fuente.

### Pasos para levantar los contenedores:

1. Abrir una terminal (preferencia Git Bash o PowerShell) y dirigirse a la raíz del proyecto usando el comando `cd`.
2. Dirigirse a esta carpeta principal ejecutando:
   `cd Contenedores_RedNorte`
3. Ya dentro de esta carpeta, asegurarse de que exista el archivo `docker-compose.yml` y la carpeta `Contenedor_Backend`. Dentro del contenedor debe verificar la existencia de la carpeta `target/` y el `Dockerfile`. Puede confirmar el contenido de los archivos usando el comando `ls`.
4. Ahora se debe realizar el siguiente comando para levantar la base de datos (PostgreSQL, MySQL) y la API:
   `docker-compose up -d --build`

---

### 📋 Lista de comandos de valor:

* `docker-compose down`: Elimina las instancias Docker que componen el proyecto.
* `docker-compose up -d --build`: Crea las instancias Docker en segundo plano (`-d`) y fuerza la reconstrucción de la imagen (`--build`) en caso de existir actualizaciones en la carpeta target.
* `docker ps`: Permite ver que las instancias estén correctamente levantadas y corriendo.

**Ver logs de las instancias:**
Para visualizar en tiempo real que el backend de Spring Boot se inició sin problemas:
* `docker logs -f rednorte-backend`
