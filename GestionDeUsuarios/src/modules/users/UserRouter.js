import express from "express";
import { UserController } from "./UserControllers.js";

const router = express.Router();

router.post("/login", UserController.login);
router.post("/register", UserController.register);
router.get("/validarUsuario", UserController.isValid);
export default router;
