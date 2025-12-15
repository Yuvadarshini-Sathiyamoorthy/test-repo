# Vulnerable Python Application for Testing
import os
import sqlite3
import hashlib
import pickle
import subprocess
from flask import Flask, request, render_template_string

app = Flask(__name__)

# CRITICAL: Hardcoded credentials
DATABASE_PASSWORD = "admin123"
API_KEY = "sk-1234567890abcdef"
SECRET_KEY = "super-secret-key"

# HIGH: SQL Injection vulnerability
@app.route('/login', methods=['POST'])
def login():
    username = request.form['username']
    password = request.form['password']
    
    # Vulnerable SQL query
    query = f"SELECT * FROM users WHERE username = '{username}' AND password = '{password}'"
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    cursor.execute(query)  # SQL Injection here
    user = cursor.fetchone()
    
    if user:
        return "Login successful"
    return "Login failed"

# HIGH: Command Injection
@app.route('/ping')
def ping():
    host = request.args.get('host', 'localhost')
    # Dangerous command execution
    result = os.system(f"ping -c 1 {host}")
    return f"Ping result: {result}"

# CRITICAL: Deserialization vulnerability
@app.route('/load_data', methods=['POST'])
def load_data():
    data = request.get_data()
    # Unsafe deserialization
    obj = pickle.loads(data)
    return str(obj)

# HIGH: XSS vulnerability
@app.route('/search')
def search():
    query = request.args.get('q', '')
    # No input sanitization
    template = f"<h1>Search Results for: {query}</h1>"
    return render_template_string(template)

# MEDIUM: Weak cryptography
def hash_password(password):
    # Using weak MD5 hash
    return hashlib.md5(password.encode()).hexdigest()

# HIGH: Path traversal
@app.route('/file')
def read_file():
    filename = request.args.get('file')
    # No path validation
    with open(filename, 'r') as f:
        return f.read()

# MEDIUM: Information disclosure
@app.route('/debug')
def debug():
    try:
        1/0
    except Exception as e:
        # Exposing stack trace
        return str(e.__traceback__)

# HIGH: LDAP Injection
def authenticate_ldap(username, password):
    import ldap
    ldap_filter = f"(&(uid={username})(password={password}))"
    # LDAP injection vulnerability
    return ldap_filter

if __name__ == '__main__':
    # CRITICAL: Debug mode in production
    app.run(debug=True, host='0.0.0.0', port=5000)