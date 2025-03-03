import { UserService } from './UserServices.js';

export const UserController = {
  async login(req, res) {
    const { email, password } = req.body;
    try {   
        if (!email || !password) {
            return res.status(400).json({ 
              error: 'Please provide email and password' 
            });
        }
      const result = await UserService.login({ email, password  });
      res.status(200).json(result);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  },
  async register(req, res) {
    try {
      const { email, password, name } = req.body;
      
      // Validate input
      if (!email || !password || !name) {
        return res.status(400).json({ 
          error: 'Please provide email, password and name' 
        });
      }

      const result = await UserService.register({ email, password, name });
      
      res.status(201).json({
        message: 'User registered successfully',
        token: result.token,
        userId: result.userId
      });
    } catch (error) {
      if (error.message === 'User already exists') {
        return res.status(409).json({ error: error.message });
      }
      res.status(500).json({ error: 'Internal server error' });
    }
  },
  async isValid(req, res) {
    const { token } = req.query;
    try {
      if (!token) {
        return res.status(400).json({ error: 'Token is required' });
      }
      const result = await UserService.isValid(token);
      res.status(200).json(result);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }
};
