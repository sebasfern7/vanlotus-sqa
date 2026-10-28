# VanLotus — Repositorio de QA

Evidencia de pruebas del **Test Plan ágil (XP)** de VanLotus, sistema de inventario y ventas de una joyería.

> El sistema completo es un producto privado. Por confidencialidad del cliente, este repositorio
> contiene **solo el módulo piloto de registro de usuario** y sus pruebas, sin código de inventario ni ventas.

## Contenido

| Carpeta | Qué hay | Pruebas |
|---|---|---|
| `backend/` | Dominio y caso de uso del registro (Java 17) | 16 unitarias (TDD) + escenarios Gherkin con Cucumber (ATDD/BDD) |
| `frontend/` | Validación del registro en el cliente | 6 pruebas con Vitest |
| `docs/` | Dinámica SQA del registro y evidencias del ciclo rojo → verde → refactor | — |
| `.github/workflows/` | Integración continua: corre todas las pruebas en cada push | — |

## Cómo correr las pruebas

```bash
cd backend && ./mvnw test
```

```bash
cd frontend && npm install && npm test
```
