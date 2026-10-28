import { describe, it, expect } from 'vitest';
import { validarRegistro } from './registroUsuario';

// TDD en el frontend: mismas reglas de dominio que el backend (NombreUsuario y Contrasena),
// para avisar al usuario antes de llamar a la API.
describe('validarRegistro', () => {
    it('no devuelve errores con datos válidos', () => {
        expect(validarRegistro({ username: 'lucia.vargas', password: 'Joyas2026', confirmacion: 'Joyas2026' }))
            .toEqual({});
    });

    it('rechaza un nombre de usuario con menos de 3 caracteres', () => {
        expect(validarRegistro({ username: 'ab', password: 'Joyas2026', confirmacion: 'Joyas2026' }).username)
            .toBe('El nombre de usuario debe tener entre 3 y 50 caracteres');
    });

    it('rechaza espacios internos en el nombre de usuario', () => {
        expect(validarRegistro({ username: 'lucia vargas', password: 'Joyas2026', confirmacion: 'Joyas2026' }).username)
            .toBe('El nombre de usuario solo puede contener letras, números y los caracteres . _ - @');
    });

    it('rechaza contraseñas con menos de 8 caracteres', () => {
        expect(validarRegistro({ username: 'lucia', password: 'abc12', confirmacion: 'abc12' }).password)
            .toBe('La contraseña debe tener al menos 8 caracteres');
    });

    it('rechaza contraseñas sin letras o sin números', () => {
        const mensaje = 'La contraseña debe contener al menos una letra y un número';
        expect(validarRegistro({ username: 'lucia', password: 'solamenteletras', confirmacion: 'solamenteletras' }).password)
            .toBe(mensaje);
        expect(validarRegistro({ username: 'lucia', password: '12345678', confirmacion: '12345678' }).password)
            .toBe(mensaje);
    });

    it('rechaza si la confirmación no coincide', () => {
        expect(validarRegistro({ username: 'lucia', password: 'Joyas2026', confirmacion: 'Joyas2025' }).confirmacion)
            .toBe('Las contraseñas no coinciden');
    });
});
