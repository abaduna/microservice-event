import mysql from 'mysql2/promise';
import dotenv from 'dotenv';

dotenv.config();

const pool = mysql.createPool({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

// Helper function to get connection from pool
export const db = {
  async query(sql, params) {
    try {
      const [results] = await pool.execute(sql, params);
      return [results];
    } catch (error) {
      console.error('Database Error:', error);
      throw error;
    }
  }
};