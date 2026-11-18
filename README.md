# Clínica — escritorio y servidor SOAP

Java 21 · Swing · Hibernate 6 · PostgreSQL 16 · Maven · Jakarta XML Web Services.

## Proyectos

- Esta carpeta: aplicación de escritorio y servidor SOAP.
- `../clinica-web`: cliente con servlets, JSP, JSTL y htmx. Produce `clinica.war`.

```text
Swing ────────────────────────┐
                             ▼
Navegador → Tomcat → SOAP → IControlador → Controlador → DAO → Hibernate → PostgreSQL
```

## Requisitos

JDK 21, Maven, Docker y Tomcat 10.1 con Java 21 para el cliente web.

## Inicialización

El orden de inicio es PostgreSQL, servidor Java con SOAP y, por último, cliente
web en Tomcat. Se necesitan dos ventanas de PowerShell.

### Ventana 1: base de datos y servidor SOAP

Desde la raíz de este proyecto:

```powershell
cd "$HOME\clinica-escritorio-pa"
docker compose up -d
mvn clean package -DskipTests
```

Si hay una ventana anterior de la aplicación titulada **Clínica - Iniciar
sesión**, cerrala antes de iniciar el servidor. Luego ejecutá:

```powershell
java "-Dws.url=http://0.0.0.0:9876/controlador" -cp target\clinica.jar publicadores.Servidor
```

El servidor publica SOAP y abre también la aplicación de escritorio. Dejá esta
ventana abierta mientras uses el cliente web. Comprobá el servicio en
http://localhost:9876/controlador?wsdl.

### Ventana 2: cliente web y Tomcat

Abrí otra ventana de PowerShell y compilá el cliente:

```powershell
cd "$HOME\clinica-web"
mvn clean package
```

Si el contenedor `clinica-tomcat` ya existe, copiá el WAR y reinicialo:

```powershell
docker cp "$HOME\clinica-web\target\clinica.war" clinica-tomcat:/usr/local/tomcat/webapps/
docker restart clinica-tomcat
```

Si Docker indica que no existe el contenedor, crealo y copiá el WAR:

```powershell
docker run -d --name clinica-tomcat -p 8080:8080 -e "CATALINA_OPTS=-Dws.url=http://host.docker.internal:9876/controlador" tomcat:10.1-jdk21
docker cp "$HOME\clinica-web\target\clinica.war" clinica-tomcat:/usr/local/tomcat/webapps/
```

Cuando Tomcat termine de desplegar la aplicación, abrí
http://localhost:8080/clinica/.

Maven consulta el WSDL y genera los stubs en `target/generated-sources/wsimport`.
Por eso, el servidor SOAP debe estar activo antes de compilar el cliente. Si se
apaga, las operaciones web que necesitan datos del servidor fallarán hasta que
se reinicie.

La aplicación usa por defecto la base `clinica` en localhost:5432, usuario
`postgres` y contraseña `postgres`, como el Compose incluido. Para otros valores,
Java lee `DB_HOST`, `DB_USER` y `DB_PASSWORD` o las propiedades `db.host`,
`db.user` y `db.password`. Cambiar `POSTGRES_USER` o `POSTGRES_PASSWORD` en Compose
no cambia automáticamente las variables de Java. El nombre de base sigue siendo
`clinica`.

Con Tomcat instalado directamente, copiá el WAR a su carpeta `webapps`. El
servicio se busca por defecto en localhost:9876. La propiedad `ws.url` se puede
cambiar en `CATALINA_OPTS` sin recompilar. La propiedad `wsdl.url` cambia la
dirección usada al generar los stubs: `mvn package "-Dwsdl.url=http://IP:9876/controlador?wsdl"`.

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

Los stubs y compilados quedan en `target` y no se versionan.

### Liberar prestaciones usadas en órdenes

En **Administrar prestaciones** hay una acción para borrar todas las órdenes
médicas. Solo un médico puede ejecutarla; pide confirmación y elimina también las
líneas asociadas. Esto borra el historial de todos los pacientes y no se puede
deshacer. Después se pueden eliminar del catálogo las prestaciones que quedaron
sin referencias. Al agregar o cambiar operaciones SOAP, primero recompilá y
reiniciá este servidor y luego reconstruí y desplegá `clinica-web` para actualizar
el WSDL y sus stubs.
