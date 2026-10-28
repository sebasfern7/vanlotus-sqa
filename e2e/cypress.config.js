const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    // Front de VanLotus corriendo en local (npm run dev). Se puede cambiar con CYPRESS_BASE_URL.
    baseUrl: 'http://localhost:5173',
    supportFile: false,
  },
  // Video y capturas como evidencia de la ejecución
  video: true,
  screenshotOnRunFailure: true,
});
