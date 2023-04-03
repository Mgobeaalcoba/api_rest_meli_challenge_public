# API REST Meli Challenge Kotlin

Este proyecto es una API REST construida con Spring Boot en el lenguaje de programación Kotlin. La API se encarga de recibir y procesar información de satélites para determinar la posición y mensaje de una nave enemiga. Para ello usa el método de la trilateración. En este figma pueden encontrar una explicación visual amigable y sencilla sobre este método:

[Trilateración: explicación visual en Figma](https://www.figma.com/file/XUbwl0hIkeKKW8CoYC2YG4/Trilateraci%C3%B3n---Meli-Challenge?node-id=0%3A1&t=iNjGkFVJT2QrVGFA-1)

## Cómo usar

El proyecto contiene un controlador TopSecretController que maneja las solicitudes para determinar la ubicación de la nave y obtener el mensaje secreto. La aplicación está hosteada en http://35.202.30.49:8080 y el servidor se encuentra encendido de forma permanente por lo que se le puede pegar a cualquiera de sus endpoints en cualquier momento. 

## Endpoints

### POST /topsecret

Este endpoint recibe información de tres satélites (Kenobi, Skywalker y Sato) que están en la posición (-500, -200), (100, -100) y (500, 100) respectivamente, y devuelve la posición de la nave enemiga y el mensaje que emitió. La información de los satélites se recibe en el cuerpo de la petición como un objeto JSON que sigue el siguiente formato:

```json
{
  "satellites": [
    {
      "name": "kenobi",
      "distance": 150.0,
      "message": ["", "este", "es", "un", "mensaje"]
    },
    {
      "name": "skywalker",
      "distance": 238.0,
      "message": ["Hola", "este", "", "un", ""]
    },
    {
      "name": "sato",
      "distance": 176.0,
      "message": ["", "", "es", "un", "mensaje"]
    }
  ]
}
```

El atributo name indica el nombre del satélite, distance indica la distancia entre el satélite y la nave enemiga, y message es un arreglo de strings que representa el mensaje que emitió el satélite.

El endpoint devuelve un objeto JSON con la siguiente estructura:

```json
{
  "position": {
    "x": -58.31525292742582,
    "y": -69.55141837312177
  },
  "message": "este es un mensaje"
}
```

Donde position es un objeto que representa la posición de la nave enemiga con las coordenadas x e y, y message es un string que representa el mensaje que emitió la nave enemiga.

### POST /topsecret_split/{satellite_name}

Este endpoint recibe información de un satélite específico (Kenobi, Skywalker o Sato) que se encuentra en una posición conocida, y actualiza la información del mismo. El nombre del satélite se recibe como un parámetro en la URL y la información del satélite se recibe en el cuerpo de la petición como un objeto JSON que sigue el siguiente formato:

```json
{
  "distance": 150.0,
  "message": ["", "este", "es", "un", "mensaje"]
}
```

El atributo distance indica la distancia entre el satélite y la nave enemiga, y message es un arreglo de strings que representa el mensaje que emitió el satélite.

### GET /topsecret_split

Este endpoint devuelve la posición de la nave enemiga y el mensaje que emitió, utilizando la información de los satélites recibida previamente por el endpoint POST /topsecret_split/{satellite_name}. El endpoint no recibe información en el cuerpo de la petición y devuelve un objeto JSON con la siguiente estructura:

```json
{
  "position": {
    "x": -58.31525292742582,
    "y": -69.55141837312177
  },
  "message": "este es un mensaje"
}
```

Si la información de los satélites no es suficiente para determinar la posición y mensaje de la nave enemiga, el endpoint devuelve un error 404 Not Found.

## Consideraciones

La clase TopSecretRequest explicita el formato del body aceptado en la request. El mismo debe contener una lista de los satélites conocidos así con el mensaje secreto tal como lo recibió cada satelite.

La clase TopSecretResponse explicita el formato de response que va a devolver la API REST. El mismo contiene la ubicación de la nave y el mensaje secreto descifrado.

La clase Satellite representa un satélite y contiene su nombre, su distancia a la nave y un fragmento del mensaje secreto.

La clase Coordinates representa las coordenadas que permiten ubicar una nave dentro de un plano cartesiano bi-dimensional.

La enumClass SatelliteName contiene los nombres de los tres satélites que forman parte de nuestros recursos y que usamos para encontrar la nave enemiga y reconstruir el mensaje.

La clase Coordinates representa las coordenadas que permiten ubicar una nave dentro de un plano cartesiano bi-dimensional. 

La enumClass SatelliteName contiene los nombres de los tres satélites que forman parte de nuestros recursos y que usamos para encontrar la nave enemiga y reconstruir el mensaje. 

El método getLocation() de la clase TopSecretController calcula la ubicación de la nave a partir de la distancia a cada uno de los tres satélites conocidos. Si no se proporciona la distancia a alguno de los tres satélites, devuelve null.

El método getMessage() de la clase TopSecretController descifra el mensaje secreto a partir de los fragmentos proporcionados por cada uno de los tres satélites conocidos. Si no se proporciona el fragmento de mensaje de alguno de los tres satélites, devuelve una cadena vacía.

El método postSplitSatellite() de la clase TopSecretController registra la distancia y el fragmento del mensaje secreto de un satélite específico. Si el satélite no está registrado, devuelve un mensaje de error.

El método getSplitSatellite() de la clase TopSecretController devuelve la ubicación y el fragmento del mensaje secreto de un satélite específico. Si el satélite no está registrado, devuelve un mensaje de error. Si no se proporcionan suficientes fragmentos de mensaje, devuelve un mensaje de error.

## Otras consideraciones: 

El challenge estaba dividido en **3 niveles.** Su resolución también fue por niveles y se puede acceder a cada uno de ellos haciendo **checkout entre las ramas del repositorio:**
- **"Main"** es la rama del nivel 3. 
- **"Nivel 2"** es la rama del nivel 2. 
- Para el nivel 1 hay doy ramas: **"Nivel1"** que es la primera solución (sin clases y objetos) y una segunda versión **"Nivel1_with_class"** que es la misma solución pero con una arquitectura mas compleja que involucra clases y data clases.
 
Finalmente pueden encontrar la consigna que se debía cumplir en este Meli Challenge Kotlin en el archivo en la carpeta raiz del repo llamado: **"Consgina Challenge - Operacion Fuego de Quasar v1.1 KOTLIN.pdf"**

## Ejemplos de cURL para importar en Postman, Thunder Client o cualquier otro software de cliente: 

### POST /topsecret

```json
curl --location 'http://35.202.30.49:8080/topsecret' \
--header 'Content-Type: application/json' \
--data '{
"satellites": [
{
"name": "kenobi",
"distance": 100,
"message": ["", "", "un", "mensaje", ""] },
{
"name": "skywalker",
"distance": 215.5,
"message": ["", "es", "", "", "secreto"] },
{
"name": "sato",
"distance": 342.7,
"message": ["este", "", "un", "", ""] }
]
}'
```

### POST /topsecret_split/{satellite_name}

```json
curl --location 'http://35.202.30.49:8080/topsecret_split/keno' \
--header 'Content-Type: application/json' \
--data '{
"distance": 557.0,
"message": ["Este es", "un mensaje","para" , "probar", "si funciona"] 
}'
```

### GET /topsecret_split

```json
curl --location 'http://35.202.30.49:8080/topsecret_split/sato'
```
## Extra:

Podes obtener estas request y otras tres que forman parte de una misma colection de testeo de los endpoints en http://localhost:8080 descargando el el siguiente .JSON de este repo:

**API-REST Meli Challenge.postman_collection.json**

Una vez descargado lo importan en Postman y listo!

Importante: para probarlo en local host primero vas a tener que clonar el repositorio actual en tu PC

```bash
git clone https://github.com/Mgobeaalcoba/api_rest_meli_challenge_public.git
```

luego ubicarte en el directorio clonado y compilarlo con el siguiente comando: 

```bash
./gradlew build
```

Una vez compilado, se debe ejecutar el archivo .jar generado por la compilación con el siguiente comando: 

```bash
java -jar build/libs/{mi-programa}.jar
```

Hay que reemplazar {mi-programa} por el nombre que el compilador le haya puesto en tu PC. 

Hecho esto tendrás el servidor levantado en localhost y podrás probarlo también allí. 

## Autor

Data Dev | Web & App Dev | Sociologist

Dev: Mariano Gobea Alcoba <gobeamariano@gmail.com>

linkendin: <https://www.linkedin.com/in/mariano-gobea-alcoba>

Github: https://github.com/Mgobeaalcoba

Tel: + 54 9 11 27475569







