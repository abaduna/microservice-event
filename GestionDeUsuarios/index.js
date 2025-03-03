import express from "express";

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware para parsear JSON
app.use(express.json());

// Ruta de prueba
app.get("/", (req, res) => {
    res.json({ mensaje: "¡Hola, API con Express!" });
});

import UserRouter from "./src/modules/users/UserRouter.js";
app.use("/api/users", UserRouter);

// Iniciar servidor
app.listen(PORT, () => {
    console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
