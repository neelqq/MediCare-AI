import { Routes, Route, Link, useNavigate, Navigate } from 'react-router-dom'
import MedicineList from './pages/MedicineList'
import AddMedicine from './pages/AddMedicine'
import Suggest from './pages/Suggest'
import Orders from './pages/Orders'
import Login from './pages/Login'
import Register from './pages/Register'
import Cart from './pages/Cart'

// 🔐 Protected Route
function ProtectedRoute({ children }) {
  const token = localStorage.getItem('token')
  return token ? children : <Navigate to="/login" />
}

export default function App() {
  const role = localStorage.getItem('role')
  const token = localStorage.getItem('token')
  const navigate = useNavigate()

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    navigate('/login')
  }

  return (
    <>
      {/* 🔵 Navbar */}
      <nav className="navbar">
        <h1>💊 MediCare AI</h1>

        {/* ❌ Not Logged In */}
        {!token && (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}

        {/* ✅ Logged In */}
        {token && (
          <>
            <Link to="/">Medicine List</Link>
            <Link to="/suggest">AI Suggest</Link>

            {role === 'ADMIN' && (
              <Link to="/add">Add Medicine</Link>
            )}

            <Link to="/orders">Orders</Link>
            <Link to="/cart">🛒 Cart</Link>

            <button
              className="btn"
              style={{
                background: 'rgba(255,255,255,0.2)',
                color: 'white'
              }}
              onClick={handleLogout}
            >
              Logout
            </button>
          </>
        )}
      </nav>

      {/* 🔁 Routes */}
      <Routes>
        {/* Public */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Private */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <MedicineList />
            </ProtectedRoute>
          }
        />

        <Route
          path="/suggest"
          element={
            <ProtectedRoute>
              <Suggest />
            </ProtectedRoute>
          }
        />

        <Route
          path="/add"
          element={
            <ProtectedRoute>
              <AddMedicine />
            </ProtectedRoute>
          }
        />

        <Route
          path="/cart"
          element={
            <ProtectedRoute>
              <Cart />
            </ProtectedRoute>
          }
        />

        <Route
          path="/orders"
          element={
            <ProtectedRoute>
              <Orders />
            </ProtectedRoute>
          }
        />

        {/* ⚠️ Fallback */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </>
  )
}

