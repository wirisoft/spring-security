Documento de Requerimientos v1:
Aplicación Móvil de Comida
1. Introducción
1.1. Propósito
El propósito de este documento es definir de manera formal y detallada los requerimientos
funcionales, no funcionales y de interfaz para el desarrollo de una aplicación móvil de entrega
de comida. Este documento servirá como guía para los equipos de diseño, desarrollo y
control de calidad.
1.2. Alcance
El proyecto abarca el diseño, desarrollo, y despliegue de una aplicación nativa para
plataformas iOS y Android. Incluirá un portal para clientes, y sienta las bases para futuras
expansiones (portal para restaurantes y repartidores).
2. Requerimientos Funcionales (RF)
2.1. Módulo: Gestión de Cuentas y Perfil
RF-01: Registro de Usuarios
Atributo Valor
Número de Requisito RF-01
Nombre Registro de Usuarios
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El usuario podrá crear una cuenta
mediante: a) Formulario de email y
contraseña. b) Autenticación con redes
sociales (Google, Facebook). El proceso

deberá validar la unicidad del email.

RF-02: Inicio de Sesión
Atributo Valor
Número de Requisito RF-02
Nombre Inicio de Sesión
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El usuario podrá autenticarse en la
aplicación de forma segura utilizando sus
credenciales registradas. Se incluirá la
opción "Recordar sesión".

RF-03: Recuperación de Contraseña
Atributo Valor
Número de Requisito RF-03
Nombre Recuperación de Contraseña
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El usuario podrá solicitar un

restablecimiento de su contraseña a través
de su correo electrónico registrado. Se

enviará un enlace seguro para definir una
nueva contraseña.

RF-04: Gestión de Perfil
Atributo Valor
Número de Requisito RF-04
Nombre Gestión de Perfil
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El usuario tendrá una sección de perfil
donde podrá ver y editar su información
personal (nombre, foto, teléfono) y
gestionar sus direcciones y métodos de
pago.

RF-05: Gestión de Direcciones
Atributo Valor
Número de Requisito RF-05
Nombre Gestión de Direcciones
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El usuario podrá añadir, editar y eliminar

múltiples direcciones de entrega. Podrá
marcar una como "predeterminada". La
app deberá permitir nombrar las
direcciones (Ej: "Casa", "Oficina").

RF-06: Gestión de Métodos de Pago
Atributo Valor
Número de Requisito RF-06
Nombre Gestión de Métodos de Pago
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El usuario podrá añadir y eliminar de forma
segura los datos de sus tarjetas de
crédito/débito. La información no se
almacenará localmente, sino a través de un
token de la pasarela de pago.

2.2. Módulo: Búsqueda y Visualización de Restaurantes
RF-07: Pantalla Principal (Home)
Atributo Valor
Número de Requisito RF-07
Nombre Pantalla Principal (Home)
Tipo Funcional
Fuente Entrevista y Cuestionario

Prioridad Alta
Descripción Al abrir la app, se mostrará un dashboard
con: un carrusel de promociones,
categorías de comida, restaurantes
destacados y una lista de restaurantes
cercanos basados en la geolocalización.

RF-08: Búsqueda Inteligente
Atributo Valor
Número de Requisito RF-08
Nombre Búsqueda Inteligente
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El usuario podrá buscar restaurantes o
platos específicos a través de una barra de
búsqueda. La búsqueda deberá ofrecer
sugerencias autocompletadas en tiempo
real.

RF-09: Filtros Avanzados
Atributo Valor
Número de Requisito RF-09
Nombre Filtros Avanzados
Tipo Funcional

Fuente Entrevista y Cuestionario
Prioridad Media
Descripción El usuario podrá filtrar la lista de

restaurantes por: tipo de comida, rango de
precios, calificación, promociones activas
y opciones dietéticas (vegano, sin gluten,
etc.).

RF-10: Vista de Mapa
Atributo Valor
Número de Requisito RF-10
Nombre Vista de Mapa
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción Se incluirá una opción para visualizar los
restaurantes cercanos en un mapa
interactivo, permitiendo al usuario explorar
opciones geográficamente.

RF-11: Lista de Favoritos
Atributo Valor
Número de Requisito RF-11
Nombre Lista de Favoritos
Tipo Funcional

Fuente Entrevista y Cuestionario
Prioridad Media
Descripción El usuario podrá marcar restaurantes
como "favoritos" para acceder a ellos
rápidamente desde una sección dedicada
en su perfil.

2.3. Módulo: Proceso de Pedido y Pago
RF-12: Visualización de Menú
Atributo Valor
Número de Requisito RF-12
Nombre Visualización de Menú
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción Al seleccionar un restaurante, el usuario
podrá ver su menú completo, organizado
por categorías. Cada plato incluirá
nombre, descripción, foto y precio.

RF-13: Carrito de Compras
Atributo Valor
Número de Requisito RF-13
Nombre Carrito de Compras
Tipo Funcional

Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción Un carrito de compras persistente
permitirá al usuario añadir, modificar la
cantidad y eliminar productos. El carrito
deberá mostrar un subtotal actualizado
constantemente.

RF-14: Personalización de Platos
Atributo Valor
Número de Requisito RF-14
Nombre Personalización de Platos
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción Para platos que lo permitan, el usuario
podrá seleccionar opciones de
personalización (ej. término de la carne,
ingredientes extra, quitar ingredientes).

RF-15: Proceso de Checkout
Atributo Valor
Número de Requisito RF-15
Nombre Proceso de Checkout
Tipo Funcional

Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El proceso de pago será guiado en pasos
claros: 1) Resumen del pedido, 2) Selección
de dirección, 3) Selección de método de
pago, 4) Confirmación final con desglose
de costos.

RF-16: Programación de Pedidos
Atributo Valor
Número de Requisito RF-16
Nombre Programación de Pedidos
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Baja
Descripción El usuario tendrá la opción de realizar el
pedido para entrega inmediata o
programarlo para una fecha y hora
específicas.

2.4. Módulo: Seguimiento, Calificaciones y Soporte
RF-17: Historial de Pedidos
Atributo Valor
Número de Requisito RF-17
Nombre Historial de Pedidos

Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El usuario podrá consultar una lista de
todos sus pedidos pasados y en curso.
Cada pedido pasado tendrá la opción de
"Volver a pedir".

RF-18: Seguimiento en Tiempo Real
Atributo Valor
Número de Requisito RF-18
Nombre Seguimiento en Tiempo Real
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción Para pedidos en curso, el usuario podrá
ver el estado en tiempo real (Confirmado,
En preparación, En camino, Entregado). La
fase "En camino" mostrará la ubicación del
repartidor en un mapa.

RF-19: Notificaciones Push
Atributo Valor
Número de Requisito RF-19
Nombre Notificaciones Push

Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción La aplicación enviará notificaciones
automáticas para informar sobre cambios
de estado del pedido, ofertas especiales y
recordatorios.

RF-20: Calificaciones y Reseñas
Atributo Valor
Número de Requisito RF-20
Nombre Calificaciones y Reseñas
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción Tras la entrega, el usuario podrá calificar el
restaurante y el repartidor (escala de 1 a 5
estrellas) y dejar una reseña escrita. Podrá
adjuntar fotos a su reseña.

RF-21: Soporte al Cliente
Atributo Valor
Número de Requisito RF-21
Nombre Soporte al Cliente

Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción Se implementará un chat en vivo dentro de
la app para que los usuarios puedan
contactar con el equipo de soporte para
resolver incidencias con sus pedidos.

2.5. Módulo: Gestión de Personal y Administración
RF-22: Gestión de Trabajador
Atributo Valor
Número de Requisito RF-22
Nombre Gestión de Trabajador
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción Se debe poder gestionar la información de
los trabajadores (repartidores, personal de
soporte, etc.). Cada perfil de trabajador
debe incluir como mínimo: Nombre
completo, Teléfono y Correo electrónico.

RF-23: Panel de Control del Dueño
Atributo Valor
Número de Requisito RF-23

Nombre Panel de Control del Dueño
Tipo Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción Existirá un panel de control accesible solo
para el rol de "Dueño". Este panel permitirá
visualizar métricas clave (pedidos diarios,
ingresos, etc.) y gestionar configuraciones
generales de la aplicación.

3. Requerimientos No Funcionales (RNF)
RNF-01: Rendimiento
Atributo Valor
Número de Requisito RNF-01
Nombre Rendimiento
Tipo No Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción Los tiempos de respuesta de la API no
deben exceder los 500ms. La carga de la
interfaz principal no debe superar los 2
segundos en una conexión 4G.

RNF-02: Usabilidad y Accesibilidad
Atributo Valor

Número de Requisito RNF-02
Nombre Usabilidad y Accesibilidad
Tipo No Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción La interfaz debe ser intuitiva, cumplir con
las directrices de Apple (HIG) y Google
(Material Design) y alcanzar el nivel AA de
las pautas de accesibilidad WCAG 2.1.

RNF-03: Compatibilidad
Atributo Valor
Número de Requisito RNF-03
Nombre Compatibilidad
Tipo No Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción La aplicación debe ser compatible con las
últimas 3 versiones mayores de iOS y
Android. El diseño debe ser responsivo
para móviles y tablets.

RNF-04: Seguridad
Atributo Valor

Número de Requisito RNF-04
Nombre Seguridad
Tipo No Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción Toda la comunicación debe ser por HTTPS.

Los datos sensibles deben estar
encriptados. La app debe protegerse
contra vulnerabilidades comunes
(inyección SQL, XSS).

RNF-05: Disponibilidad
Atributo Valor
Número de Requisito RNF-05
Nombre Disponibilidad
Tipo No Funcional
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción Los servicios del backend deberán
garantizar una disponibilidad del 99.8%
(Uptime).

RNF-06: Escalabilidad

Atributo Valor

Número de Requisito RNF-06
Nombre Escalabilidad
Tipo No Funcional
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción La arquitectura del backend debe estar
diseñada para soportar un crecimiento del
50% en usuarios y pedidos durante el
primer año sin degradación del
rendimiento.

RNF-07: Mantenibilidad

Atributo Valor
Número de Requisito RNF-07
Nombre Mantenibilidad
Tipo No Funcional
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción El código fuente debe estar bien
documentado, seguir un patrón de
arquitectura claro (ej. MVVM, VIPER) y
contar con pruebas unitarias para la lógica
de negocio crítica.

4. Requerimientos de Interfaz de Usuario (UI/UX)
UI-01: Identidad Visual

Atributo Valor
Número de Requisito UI-01
Nombre Identidad Visual
Tipo Interfaz de Usuario
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción El diseño debe ser limpio, moderno y
consistente con la marca. Se utilizarán
imágenes de alta resolución para generar
apetito y confianza.

UI-02: Barra de Navegación
Atributo Valor
Número de Requisito UI-02
Nombre Barra de Navegación
Tipo Interfaz de Usuario
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción La app contará con una barra de
navegación inferior (Tab Bar) con 4
accesos directos: Inicio, Búsqueda,
Pedidos y Perfil.

UI-03: Microinteracciones

Atributo Valor
Número de Requisito UI-03
Nombre Microinteracciones
Tipo Interfaz de Usuario
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción Se deben incluir animaciones sutiles y
feedback visual para las acciones del
usuario (ej. al añadir un item al carrito)
para mejorar la experiencia.

UI-04: Onboarding de Usuario
Atributo Valor
Número de Requisito UI-04
Nombre Onboarding de Usuario
Tipo Interfaz de Usuario
Fuente Entrevista y Cuestionario
Prioridad Media
Descripción Para los nuevos usuarios, se mostrará una
serie de pantallas introductorias que
expliquen las principales funcionalidades y
ventajas de la aplicación.

UI-05: Estados Vacíos y de Error

Atributo Valor
Número de Requisito UI-05
Nombre Estados Vacíos y de Error
Tipo Interfaz de Usuario
Fuente Entrevista y Cuestionario
Prioridad Alta
Descripción Se deben diseñar pantallas específicas
para estados vacíos (ej. "Aún no tienes
pedidos") y para mensajes de error, de
forma clara y amigable.