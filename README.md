# Clínica — escritorio y servidor SOAP

Java 21 · Swing · Hibernate 6 · PostgreSQL 16 · Maven · Jakarta XML Web Services.

## Proyectos

- Esta carpeta: aplicación de escritorio y servidor SOAP.
- `../clinica-web`: cliente con servlets, JSP, JSTL y htmx. Produce `clinica.war`.


Swing ────────────────────────┐
                             ▼
Navegador → Tomcat → SOAP → IControlador → Controlador → DAO → Hibernate → PostgreSQL
```

## Requisitos

JDK 21, Maven, Docker y Tomcat 10.1 con Java 21 para el cliente web.

## Parte 1

Desde esta carpeta:

```powershell
docker compose up -d
mvn clean package
mvn exec:java
```

La clínica no precarga usuarios: registrá un médico y un paciente desde el login.
La aplicación usa por defecto la base `clinica` en localhost:5432,
usuario postgres y contraseña postgres, como el Compose incluido.
Para otros valores, Java lee DB_HOST, DB_USER y DB_PASSWORD o las propiedades
db.host, db.user y db.password. Cambiar POSTGRES_USER/PASSWORD en Compose no cambia
automáticamente las variables de Java. El nombre de base sigue siendo clinica.

## Parte 2: orden de arranque

El orden es obligatorio: **1) PostgreSQL, 2) servidor Java con SOAP publicado,
3) cliente web y Tomcat**. La web no contiene la lógica ni accede a la base por
su cuenta: todas las operaciones pasan por el servicio SOAP.

| Paso | Componente | Cómo comprobarlo |
|---|---|---|
| 1 | PostgreSQL | `docker compose ps` muestra el contenedor de base en ejecución. |
| 2 | Servidor y SOAP | `http://localhost:9876/controlador?wsdl` muestra el WSDL. |
| 3 | Cliente web | Maven genera `../clinica-web/target/clinica.war`. |
| 4 | Tomcat | Abrir `http://localhost:8080/clinica/`. |

No continúes al siguiente paso hasta que la comprobación del anterior funcione.

### 1. Base de datos y servidor

```powershell
docker compose up -d
mvn clean package
java "-Dws.url=http://0.0.0.0:9876/controlador" -cp target/clinica.jar publicadores.Servidor
```

Se publica el servicio y se abre también Swing. La dirección con 0.0.0.0 permite
que el Tomcat de Docker alcance el servidor del equipo. Para uso local sin
contenedores, se puede usar localhost.

WSDL para el navegador y la compilación: http://localhost:9876/controlador?wsdl

El proceso Java debe permanecer activo mientras se compila o se usa la aplicación
web.

### 2. Compilar el cliente en otra terminal

```powershell
cd ..\clinica-web
mvn clean package
```

Maven consulta el WSDL y genera los stubs en target/generated-sources/wsimport.
Si SOAP no está arriba, `wsimport` no puede leer el WSDL y `mvn clean package`
falla con `Connection refused` / `Failed to parse the WSDL`. En ese caso, volvé
al paso 1, iniciá el servidor, comprobá la dirección WSDL y repetí la compilación.
Si SOAP se apaga después del despliegue, las operaciones que necesitan datos del
servidor fallarán hasta que se reinicie.

### 3. Tomcat y despliegue

```powershell
docker run -d --name clinica-tomcat -p 8080:8080 -e "CATALINA_OPTS=-Dws.url=http://host.docker.internal:9876/controlador" tomcat:10.1-jdk21
docker cp target/clinica.war clinica-tomcat:/usr/local/tomcat/webapps/
```

Abrir http://localhost:8080/clinica/

Para abrir la web a mano, escribí esa dirección en el navegador después de que
Tomcat termine el despliegue.

Con Tomcat instalado directamente, copiar el WAR a su carpeta webapps. El servicio
se busca por defecto en localhost:9876. La propiedad ws.url se puede cambiar en
CATALINA_OPTS sin recompilar. La propiedad wsdl.url cambia la dirección usada al
generar los stubs: `mvn package "-Dwsdl.url=http://IP:9876/controlador?wsdl"`.

## Publicadores

`Servidor` publica `ControladorPublish`. Este traduce los DTO existentes a JavaBeans
`*WS`, con constructor vacío y getters/setters. Las fechas viajan como texto ISO y
la solicitud como lista de identificadores y cantidades. Las excepciones propias
se transmiten como SOAP faults.

La modificación cambia nombre, precio y franja; no modifica los atributos
específicos de estudio o terapia.

## Validar la entrega

1. Registrar médico y paciente desde la web; iniciar y cerrar sesión.
2. Como médico, agregar un estudio y una terapia, modificar precio y borrar una prestación sin órdenes.
3. Como paciente, abrir /prestaciones: debe devolver 403, también en POST.
4. Buscar por nombre, ordenar y abrir detalle sin recargar toda la página.
5. Agregar/quitar seguidas.
6. Armar una solicitud desde el catálogo, ajustar cantidades y revisar el total.
7. Confirmar y revisar fecha, líneas, total e historial de más reciente a más antiguo.
8. Intentar borrar una prestación usada: el mensaje de negocio debe llegar desde SOAP.
9. Cambiar su precio: las líneas de las órdenes anteriores conservan el precio unitario.
10. Angostar la ventana: catálogo en una columna y tablas en bloques rotulados.

Los stubs y compilados quedan en target y no se versionan. Para entregar ambos
proyectos, incluir también la carpeta hermana clinica-web en el repositorio de entrega.

### Liberar prestaciones usadas en órdenes

En **Administrar prestaciones** hay una acción para borrar todas las órdenes
médicas. Solo un médico puede ejecutarla; pide confirmación y elimina también las
líneas asociadas. Esto borra el historial de todos los pacientes y no se puede
deshacer. Después se pueden eliminar del catálogo las prestaciones que quedaron
sin referencias. Al agregar o cambiar operaciones SOAP, primero recompilá y
reiniciá este servidor y luego reconstruí y desplegá `clinica-web` para actualizar
el WSDL y sus stubs.
