import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { db } from '../../config/database.js';

export const UserService = {
  async register(userData) {
    const { email, password, name } = userData;
    
    // Check if user already exists
    const [existingUser] = await db.query(
      'SELECT * FROM users WHERE email = ?',
      [email]
    );

    if (existingUser.length > 0) {
      throw new Error('User already exists');
    }

    // Hash password
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    const rol = 'user';
    // Insert user into database
    const [result] = await db.query(
      'INSERT INTO users (email, password, name, rol) VALUES (?, ?, ?, ?)',
      [email, hashedPassword, name, rol]
    );

    // Generate JWT token
    const token = jwt.sign(
      { userId: result.insertId, email,rol },
      process.env.JWT_SECRET,
      { expiresIn: '24h' }
    );

    return { token, userId: result.insertId, rol };
  },

  async login(data) {
    const [users] = await db.query(
      'SELECT * FROM users WHERE email = ?',
      [data.email]
    );
    
    if (!users || users.length === 0) {
      throw new Error("User not found");
    }

    const user = users[0]; // Get the first user from the array
    
    const isPasswordValid = await bcrypt.compare(data.password, user.password);
    if (!isPasswordValid) {
      throw new Error("Invalid password");
    }

    const token = jwt.sign(
      { userId: user.id, email: user.email, rol: user.rol },
      process.env.JWT_SECRET,
      { expiresIn: '24h' }
    );
    return { token, userId: user.id, rol: user.rol };
  },

  async isValid(token) {
    try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      return decoded;
    } catch (error) {
      throw new Error("Invalid token");
    }
  }
};
