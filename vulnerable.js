// Vulnerable JavaScript Application
const express = require('express');
const mysql = require('mysql');
const crypto = require('crypto');
const app = express();

// CRITICAL: Hardcoded secrets
const JWT_SECRET = "hardcoded-jwt-secret";
const DB_PASSWORD = "root123";
const API_TOKEN = "ghp_1234567890abcdef";

// HIGH: SQL Injection
app.post('/login', (req, res) => {
    const { username, password } = req.body;
    
    // Vulnerable query
    const query = `SELECT * FROM users WHERE username = '${username}' AND password = '${password}'`;
    connection.query(query, (error, results) => {
        if (results.length > 0) {
            res.json({ success: true });
        } else {
            res.json({ success: false });
        }
    });
});

// CRITICAL: Code injection
app.post('/eval', (req, res) => {
    const code = req.body.code;
    // Dangerous eval usage
    const result = eval(code);
    res.json({ result });
});

// HIGH: XSS vulnerability
app.get('/search', (req, res) => {
    const query = req.query.q;
    // No sanitization
    res.send(`<h1>Results for: ${query}</h1>`);
});

// MEDIUM: Weak random number generation
function generateToken() {
    // Using weak Math.random()
    return Math.random().toString(36).substring(2);
}

// HIGH: Prototype pollution
app.post('/merge', (req, res) => {
    const target = {};
    const source = req.body;
    
    // Vulnerable merge function
    function merge(target, source) {
        for (let key in source) {
            if (typeof source[key] === 'object') {
                target[key] = merge(target[key] || {}, source[key]);
            } else {
                target[key] = source[key];
            }
        }
        return target;
    }
    
    merge(target, source);
    res.json(target);
});

// CRITICAL: Command injection
app.get('/exec', (req, res) => {
    const cmd = req.query.cmd;
    const { exec } = require('child_process');
    
    // Dangerous command execution
    exec(cmd, (error, stdout, stderr) => {
        res.json({ output: stdout, error: stderr });
    });
});

// HIGH: Insecure direct object reference
app.get('/user/:id', (req, res) => {
    const userId = req.params.id;
    // No authorization check
    const query = `SELECT * FROM users WHERE id = ${userId}`;
    connection.query(query, (error, results) => {
        res.json(results[0]);
    });
});

// MEDIUM: Information disclosure
app.get('/error', (req, res) => {
    try {
        throw new Error("Database connection failed");
    } catch (e) {
        // Exposing sensitive error information
        res.status(500).json({
            error: e.message,
            stack: e.stack,
            env: process.env
        });
    }
});

app.listen(3000, '0.0.0.0', () => {
    console.log('Vulnerable app running on port 3000');
});