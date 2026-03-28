import { useState } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

export default function Register() {
  const [form, setForm] = useState({ username: '', password: '' })
  const [message, setMessage] = useState('')

  const handleRegister = () => {
    axios.post('http://localhost:8080/api/auth/register', form)
      .then(res => setMessage(res.data))
      .catch(() => setMessage('Registration failed'))
  }

  return (
    <div className="container" style={{ maxWidth: '400px', marginTop: '80px' }}>
      <div className="card">
        <h2 style={{ marginBottom: '20px' }}>Register</h2>
        {message && <p style={{ color: 'green', marginBottom: '10px' }}>{message}</p>}
        <input placeholder="Username" value={form.username}
          onChange={e => setForm({...form, username: e.target.value})} />
        <input placeholder="Password" type="password" value={form.password}
          onChange={e => setForm({...form, password: e.target.value})} />
        <button className="btn btn-primary" onClick={handleRegister}>Register</button>
      </div>
    </div>
  )
}