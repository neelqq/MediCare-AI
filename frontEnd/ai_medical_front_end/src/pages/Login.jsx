import { useState } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleLogin = () => {
    axios.post('http://localhost:8080/api/auth/login', form)
      .then(res => {
        localStorage.setItem('token', res.data.token)
        const payload = JSON.parse(atob(res.data.token.split('.')[1]))
        localStorage.setItem('role', payload.role)
        navigate('/')
      })
      .catch(() => setError('Invalid username or password'))
  }

  return (
    <div className="container" style={{ maxWidth: '400px', marginTop: '80px' }}>
      <div className="card">
        <h2 style={{ marginBottom: '20px' }}>💊 MediCare Login</h2>
        {error && <p style={{ color: 'red', marginBottom: '10px' }}>{error}</p>}
        <input placeholder="Username" value={form.username}
          onChange={e => setForm({...form, username: e.target.value})} />
        <input placeholder="Password" type="password" value={form.password}
          onChange={e => setForm({...form, password: e.target.value})}
          onKeyDown={e => e.key === 'Enter' && handleLogin()} />
        <button className="btn btn-primary" onClick={handleLogin}>Login</button>
      </div>
    </div>
  )
}