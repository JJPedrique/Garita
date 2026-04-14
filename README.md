# Garita 
Proyecto de ingenieria del Software I y II, Sistema de Administracion de Control de Acceso de una Asociacion de Vecinos

<div align="left">
  <img src="https://skillicons.dev/icons?i=postgres" height="30" alt="postgresql logo"  />
  <img width="12" />
  <img src="https://skillicons.dev/icons?i=java" height="30" alt="java logo"  />
</div>

### Idea Principal
El sistema debe permtir el acceso a la vivienda de manera automatica si estas solvente con el pago, de ser el caso contrario, deberar abrir el porton de manera manual. 

En la tabla <b>registros</b> se alamcenan todas los pago que una vivienda ha realizado, la cantidad de pagos realizaodos debe coincidir con la cantidad de <b>pagos</b> que se han emitido (o guardados en la tabla de pagos), de lo contrario esta "moroso"

<ul>
  <li>Existen dos roles: Administrador y Empleado</li>
  <li>Registro de sesion con cedula y contrasena</li>
  <li>El panel principal se divide en:</li>
    <ul>
      <li>Viviendas</li>
        <ul>
          <li>Muestra todas las viviendas registradas y la solvencia de las mismas</li>
          <li>Se puede filtrar con un panel a la derecha</li>
          <li>Se puede agregar una nueva vivienda</li>
          <li>Cada tupla de vivienda cuenta con dos botones: uno para editar la informacion y otro para borrar </li>
          <li>Permite sacar reportes de viviendas, propietarios y carnets</li>
          <li>Permite sacar un reporte personalizados</li>
        </ul>
      <li>Pagos</li>
        <ul>
          <li>Muestra todas los pagos que las viviendas deben realizar y la cantidad de personas que pagaron</li>
          <li>Se puede filtrar con un panel a la derecha</li>
          <li>Permite programar un nuevo pago, aqui se puede modificar y borrar</li>
          <li>Para poner en marcha el cobro, se debera activar el pago</li>
          <li>Cada tupla de pagos programados cuenta con dos botones: uno para editar la informacion y otro para borrar </li>
          <li>Cada tupla de pagos activos cuenta con un boton para ver quienes han pagado y generar la factura </li>
          <li>Permite sacar reportes de pagos registrados, balance</li>
          <li>Permite sacar un reporte personalizados</li>
        </ul>      
      <li>Control de Acceso</li>
        <ul>
          <li>Muestra todas los intentos de acceso al recinto, sea propietario o visitante</li>
          <li>Se puede filtrar con un panel a la derecha</li>
          <li>Se puede permtir el paso a visita, la misma debera decir el nombre del propietario para pasar
          <li>Permite sacar reportes de entradas de propietario y visitante</li>
          <li>Permite sacar un reporte personalizados</li>          
        </ul>  
      <li>Empleados (SOLO ADMINISTRADOR)</li>
        <ul>
          <li>Muestra todas los usuario (empleados) que pueden manejar el sistema</li>
          <li>Se puede filtrar con un panel a la derecha</li>
          <li>Se puede agregar nuevo usuario y colocar su rol</li>
          <li>Cada tupla de pagos cuenta con dos botones: uno para editar la informacion y otro para borrar</li>
          <li>Permite sacar reportes de usuarios</li>
          <li>Permite sacar un reporte personalizados</li>          
        </ul> 
      <li>Configuracion (SOLO ADMINISTRADOR)</li>
        <ul>
          <li>Gestiona la categoria para organizar las casas</li>
          <li>Cualquier otra cosa que se pueda presentar</li>
        </ul>       
    </ul>
    <li>Se debe enviar notificaciones a los propietarios por whatsapp/sms</li>
      <ul>
        <li>cuando se active una nueva cuota</li>
        <li>cuando un propietario pague una couta</li>  
        <li>cuando un propietario no pague en la fecha limite</li> 
      </ul>
    <li>Agregar,Actualizar,Borrar son pantallas emergentes:</li>
        <ul>
          <li>En la pantallas de vivienda, Se puede agregar y borar carnets referidos a dicha casa</li>
          <li>En la pantallas vivienda, Sale otra pantalla emergente para el crud de propietarios (pensar esto mejor)</li>
        </ul>
</ul>

### Diagrama de Caso de Uso
<img src ="https://github.com/JJPedrique/Garita/blob/main/DIAGRAMA%20CASOS%20DE%20USO.png">

### Diagrama de BDD
<img src ="https://github.com/JJPedrique/Garita/blob/main/BDD%20Diagram.png">

Nota: en el diagrama existe la tabla propietarios, la idea es que pueda haber una alternativa en caso de que una peronsa no este. De  ser descartada la idea, colocar los datos de esa tabla en la tabla vivienda
