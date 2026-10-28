// Reglas del dominio "Registro de usuario" (espejo de NombreUsuario y Contrasena del backend).
// El backend sigue siendo la fuente de verdad; esto solo da feedback inmediato en el formulario.

const LONGITUD_MIN_USUARIO = 3;
const LONGITUD_MAX_USUARIO = 50;
const LONGITUD_MIN_CONTRASENA = 8;
const CARACTERES_PERMITIDOS = /^[A-Za-z0-9._@-]+$/;

const validarNombreUsuario = (username = '') => {
    const valor = username.trim();
    if (valor.length < LONGITUD_MIN_USUARIO || valor.length > LONGITUD_MAX_USUARIO) {
        return `El nombre de usuario debe tener entre ${LONGITUD_MIN_USUARIO} y ${LONGITUD_MAX_USUARIO} caracteres`;
    }
    if (!CARACTERES_PERMITIDOS.test(valor)) {
        return 'El nombre de usuario solo puede contener letras, números y los caracteres . _ - @';
    }
    return null;
};

const validarContrasena = (password = '') => {
    if (password.length < LONGITUD_MIN_CONTRASENA) {
        return `La contraseña debe tener al menos ${LONGITUD_MIN_CONTRASENA} caracteres`;
    }
    if (!/\p{L}/u.test(password) || !/\p{Nd}/u.test(password)) {
        return 'La contraseña debe contener al menos una letra y un número';
    }
    return null;
};

/**
 * Devuelve un objeto { campo: mensaje } con los errores encontrados; vacío si todo es válido.
 */
export const validarRegistro = ({ username, password, confirmacion }) => {
    const errores = {};
    const errorUsuario = validarNombreUsuario(username);
    const errorContrasena = validarContrasena(password);

    if (errorUsuario) errores.username = errorUsuario;
    if (errorContrasena) errores.password = errorContrasena;
    if (password !== confirmacion) errores.confirmacion = 'Las contraseñas no coinciden';

    return errores;
};
