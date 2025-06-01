# 🦷 DentAgenda - Backend

**DentAgenda** es un sistema de gestión de citas para una clínica dental, desarrollado como parte del proyecto semestral de Ingeniería de Software.  
Este repositorio contiene el backend del MVP funcional construido con **Spring Boot** y asegurado con **JWT**.

---

## Tecnologías Utilizadas

- Java 17 + Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- Oracle SQL (Base de Datos)
- Maven

---

## Estructura del Proyecto

```
com.dentagenda
├─── config                     # Configuración de seguridad
├─── controller                 # Controladores REST por módulo
├─── dto                        # Objetos de transferencia de datos
├─── exception                  # Manejo centralizado de errores
├─── model                      # Entidades JPA
├─── repository                 # Repositorios JPA
├─── security                   # Filtro y utilidades JWT
├─── service                    # Interfaces de servicio
| ├─ service.impl               # Implementaciones de servicios
└─── DentAgendaApplication.java # Main
```

---

## Acceso por Roles

| Rol            | Acceso a Módulos                        |
|----------------|------------------------------------------|
| `ADMIN`        | Gestión de odontólogos y recepcionistas |
| `RECEPCIONISTA`| Citas, pacientes, agenda                |
| `ODONTOLOGO`   | Agenda, tratamientos, citas             |
| `PACIENTE`     | Historial, agendar, reprogramar         |

---

## Usuario ADMIN para pruebas

Asegúrate de insertar un usuario administrador manualmente en la base de datos:

```sql
INSERT INTO usuario (rut, password, rol)
VALUES (
    '11111111-1',
    '$2a$12$cfgwwcyUlvW8c9GqYHJcB.nvaQEKDHdwUaTYOdZCdfKKiSHhCWGZi',  -- ClaveAdmin123
    'ADMINISTRADOR
);
```
---

## Funcionalidades del Backend

- Registro de pacientes, odontólogos y recepcionistas.
- Inicio de sesión y gestión de contraseña.
- Agendamiento, reprogramación y cancelación de citas.
- Confirmación de asistencia.
- Visualización de historial clínico y tratamientos.
- Seguridad por roles con JWT.
- Validación de disponibilidad y bloqueos de agenda.

---


